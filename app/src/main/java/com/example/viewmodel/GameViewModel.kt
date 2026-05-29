package com.example.viewmodel

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class Screen {
    SPLASH,
    HOME,
    GAME_PLAY,
    STORE,
    STATS,
    SETTINGS,
    DAILY_CALENDAR
}

data class SwapHistory(
    val moveNumber: Int,
    val swappedIndices: Pair<Int, Int>,
    val correctCount: Int,
    val text: String
)

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    // Central user profile state observed reactively from DB
    val userProfile: StateFlow<UserProfile> = repository.userProfileFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile()
        )

    val gameRecords: StateFlow<List<GameRecord>> = repository.allGameRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val wonDailyRecords: StateFlow<List<GameRecord>> = repository.wonDailyRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Visual theme selection (Settings on-the-fly change)
    var currentLanguage by mutableStateOf("pt")
    var isMuted by mutableStateOf(false)

    // Reactive active saved game tracking
    var activeSavedGame by mutableStateOf<SavedGame?>(null)
    var showClassicConfirmDialog by mutableStateOf(false)
    var pendingDifficultyToStart by mutableStateOf("")
    var unlockedNewDifficultyThisTurn by mutableStateOf(false)
    var lastCalculatedScore by mutableStateOf(0)
    var lastCoinsEarned by mutableStateOf(0)

    // Current screen navigation state
    var currentScreen by mutableStateOf(Screen.SPLASH)
        private set

    // Game play states
    var isDailyChallenge by mutableStateOf(false)
        private set
    var currentDifficulty by mutableStateOf("Médio")
        private set
    var dailyDayKey by mutableStateOf("")
        private set

    var targetSequence by mutableStateOf<List<String>>(emptyList())
        private set
    var boardSequence by mutableStateOf<List<String>>(emptyList())
        private set
    var initialSequence by mutableStateOf<List<String>>(emptyList())
        private set

    var selectedIndex by mutableStateOf<Int?>(null)
    var movesCount by mutableStateOf(0)
    var elapsedTimeSeconds by mutableStateOf(0)
    var isTimerActive by mutableStateOf(false)
    var isGameOver by mutableStateOf(false)
    var isGameWon by mutableStateOf(false)

    // Notification message overlays (e.g. Toast, dialogs)
    var toastMessage by mutableStateOf<String?>(null)
    var quitDialogVisible by mutableStateOf(false)
    var adSimulationVisible by mutableStateOf(false)
    var adTargetReward by mutableStateOf("") // "coins", "lives", "hint" or "xray"
    var interstitialAdVisible by mutableStateOf(false)
    var interstitialAdCountdown by mutableStateOf(5)

    // Power-up indicators
    var activeHintIndex by mutableStateOf<Int?>(null)
    var isRevealActive by mutableStateOf(false)
    var isXRayActive by mutableStateOf(false)
    var isFreezeActive by mutableStateOf(false)
    var freezeTimeRemaining by mutableStateOf(0)
    var xRayFeedbackText by mutableStateOf<String?>(null)

    // Daily Rewards Status properties
    var showDailyRewardDialog by mutableStateOf(false)
    var currentStreakDay by mutableStateOf(1)
    var dailyRewardClaimedThisTurn by mutableStateOf(false)
    var claimMessage by mutableStateOf("")

    // Swap actions history for logic table
    var swapHistoryList = mutableStateOf<List<SwapHistory>>(emptyList())

    // Calendar month selector
    var calendarYear by mutableStateOf(2026)
    var calendarMonth by mutableStateOf(Calendar.MAY) // Default to May 2026 as per local clock

    // Timer Job reference
    private var timerJob: Job? = null
    // Lives regeneration ticker
    private var regenJob: Job? = null
    var livesRegenCountDown by mutableStateOf("00:00")

    init {
        // Fetch current localized profile adjustments
        viewModelScope.launch {
            var prof = repository.getOrInitializeProfile()
            if (prof.activeBgId == "wood") {
                val bgs = prof.getPurchasedBgs().toMutableList()
                if (!bgs.contains("sleek_interface")) {
                    bgs.add("sleek_interface")
                }
                prof = prof.copy(
                    activeBgId = "sleek_interface",
                    purchasedBgsCsv = bgs.joinToString(",")
                )
                repository.updateProfile(prof)
            }
            currentLanguage = if (prof.easyWins == 0 && prof.mediumWins == 0) "pt" else "pt" // default pt
            activeSavedGame = repository.getSavedGame()
            startLivesRegenTicker()
            
            // Process daily login reward flow
            processDailyLogin()
        }
    }

    fun navigateTo(screen: Screen) {
        currentScreen = screen
        selectedIndex = null
        if (screen == Screen.DAILY_CALENDAR) {
            val calendar = Calendar.getInstance()
            calendarYear = calendar.get(Calendar.YEAR)
            calendarMonth = calendar.get(Calendar.MONTH)
        }
    }

    fun setLanguage(lang: String) {
        currentLanguage = lang
    }

    // Timer controlling
    private fun startTimer() {
        timerJob?.cancel()
        isTimerActive = true
        timerJob = viewModelScope.launch(Dispatchers.Main) {
            while (isTimerActive && !isGameOver) {
                delay(1000)
                if (isFreezeActive) {
                    freezeTimeRemaining--
                    if (freezeTimeRemaining <= 0) {
                        isFreezeActive = false
                    }
                } else {
                    elapsedTimeSeconds++
                }
            }
        }
    }

    private fun stopTimer() {
        isTimerActive = false
        timerJob?.cancel()
    }

    // Lives regenerator ticker (increases life every 15 minutes)
    private fun startLivesRegenTicker() {
        regenJob?.cancel()
        regenJob = viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                val p = userProfile.value
                if (p.lives < 5) {
                    val diff = System.currentTimeMillis() - p.lastLifeRegenTimeMillis
                    val fifteenMinutesMs = 15 * 60 * 1000L
                    if (diff >= fifteenMinutesMs) {
                        val livesToRegen = (diff / fifteenMinutesMs).toInt()
                        val newLives = (p.lives + livesToRegen).coerceAtMost(5)
                        val extraTime = diff % fifteenMinutesMs
                        repository.updateProfile(p.copy(
                            lives = newLives,
                            lastLifeRegenTimeMillis = System.currentTimeMillis() - extraTime
                        ))
                        showToast(TextRes.get("ad_completed", currentLanguage))
                    } else {
                        // Calculate countdown text
                        val remainingMs = fifteenMinutesMs - diff
                        val minutes = (remainingMs / 1000) / 60
                        val seconds = (remainingMs / 1000) % 60
                        livesRegenCountDown = String.format("%02d:%02d", minutes, seconds)
                    }
                } else {
                    livesRegenCountDown = "00:00"
                }
                delay(1000)
            }
        }
    }

    fun checkPendingMatchSuspended(): Boolean {
        return activeSavedGame != null && activeSavedGame?.activeGame == true
    }

    // Start a normal random game
    fun startNewGame(difficulty: String) {
        viewModelScope.launch {
            val saved = repository.getSavedGame()
            if (saved != null && saved.activeGame && !saved.isDailyUniverse) {
                // Intercept to warn and request confirmation (subtending 1 life to overwrite)
                pendingDifficultyToStart = difficulty
                showClassicConfirmDialog = true
            } else {
                initiateNewClassicGame(difficulty)
            }
        }
    }

    // Resume a standard suspended game
    fun resumeSavedProgress() {
        viewModelScope.launch {
            val saved = repository.getSavedGame()
            if (saved != null && saved.activeGame) {
                currentDifficulty = saved.difficulty
                isDailyChallenge = saved.isDailyUniverse
                dailyDayKey = saved.dailyDayKey
                selectedIndex = null
                movesCount = saved.movesCount
                elapsedTimeSeconds = saved.elapsedTimeSeconds
                isGameOver = false
                isGameWon = false
                swapHistoryList.value = emptyList()

                activeHintIndex = null
                isRevealActive = false
                isXRayActive = false
                isFreezeActive = false
                freezeTimeRemaining = 0
                xRayFeedbackText = null

                targetSequence = saved.getTargetColors()
                boardSequence = saved.getCurrentColors()
                initialSequence = saved.getInitialColors()

                currentScreen = Screen.GAME_PLAY
                // Note: timer is delayed; starts only after selecting the first bottle
            } else {
                showToast("Nenhuma partida salva encontrada.")
            }
        }
    }

    // Start Daily Challenge
    fun startDailyChallenge(dayKey: String, difficulty: String) {
        viewModelScope.launch {
            val p = userProfile.value
            if (p.lives <= 0) {
                showToast(TextRes.get("lose", currentLanguage))
                return@launch
            }

            // Check if there is a saved game for this daily challenge
            val saved = repository.getSavedGame()
            if (saved != null && saved.activeGame && saved.isDailyUniverse && saved.dailyDayKey == dayKey) {
                currentDifficulty = saved.difficulty
                isDailyChallenge = true
                dailyDayKey = saved.dailyDayKey
                selectedIndex = null
                movesCount = saved.movesCount
                elapsedTimeSeconds = saved.elapsedTimeSeconds
                isGameOver = false
                isGameWon = false
                swapHistoryList.value = emptyList()

                activeHintIndex = null
                isRevealActive = false
                isXRayActive = false
                isFreezeActive = false
                freezeTimeRemaining = 0
                xRayFeedbackText = null

                targetSequence = saved.getTargetColors()
                boardSequence = saved.getCurrentColors()
                initialSequence = saved.getInitialColors()

                currentScreen = Screen.GAME_PLAY
                return@launch
            }

            currentDifficulty = difficulty
            isDailyChallenge = true
            dailyDayKey = dayKey
            selectedIndex = null
            movesCount = 0
            elapsedTimeSeconds = 0
            isGameOver = false
            isGameWon = false
            swapHistoryList.value = emptyList()

            activeHintIndex = null
            isRevealActive = false
            isXRayActive = false
            isFreezeActive = false
            freezeTimeRemaining = 0
            xRayFeedbackText = null

            // Deterministic dynamic generator
            val pColors = generateDailyColors(dayKey, difficulty)
            targetSequence = pColors.first
            boardSequence = pColors.second
            initialSequence = pColors.second

            // Save daily progression on start so they can leave and resume it anytime
            saveCurrentGameProgress()

            currentScreen = Screen.GAME_PLAY
            // Note: timer is delayed; starts only after selecting the first bottle
        }
    }

    private fun generateDailyColors(dayKey: String, difficulty: String): Pair<List<String>, List<String>> {
        val allPossibleColors = getAvailableColorsList()
        val size = getBottleCountForDifficulty(difficulty)

        // Generate deterministic seed using dayKey string
        val seed = dayKey.hashCode().toLong()
        val random = Random(seed)

        val gameColors = allPossibleColors.shuffled(random).take(size)
        val target = gameColors.shuffled(random)
        
        var init = gameColors.shuffled(random)
        var matches = countMatches(init, target)
        var limit = 0
        while (matches == size && size > 1 && limit < 10) {
            init = gameColors.shuffled(random)
            matches = countMatches(init, target)
            limit++
        }
        return Pair(target, init)
    }

    fun exitGameVoluntarily() {
        viewModelScope.launch {
            stopTimer()
            val p = userProfile.value
            val hasInfinite = p.isAdFree && p.infiniteLivesEndTime > System.currentTimeMillis()
            if (!hasInfinite) {
                // Lose a life as warning
                repository.changeLives(-1)
                showToast(TextRes.get("exit_confirm", currentLanguage) + " (-1 Vida)")
            } else {
                showToast("Vidas Infinitas Ativas! Nenhuma vida perdida. 👑")
            }
            repository.clearSavedGame() // invalidate standard progress
            activeSavedGame = null
            quitDialogVisible = false
            currentScreen = Screen.HOME
        }
    }

    private suspend fun saveCurrentGameProgress() {
        val saved = SavedGame(
            difficulty = currentDifficulty,
            targetColorsCsv = targetSequence.joinToString(","),
            currentColorsCsv = boardSequence.joinToString(","),
            initialColorsCsv = initialSequence.joinToString(","),
            movesCount = movesCount,
            elapsedTimeSeconds = elapsedTimeSeconds,
            activeGame = true,
            isDailyUniverse = isDailyChallenge,
            dailyDayKey = dailyDayKey
        )
        repository.saveGameState(saved)
        activeSavedGame = saved
    }

    private fun countMatches(listA: List<String>, listB: List<String>): Int {
        var count = 0
        for (i in listA.indices) {
            if (listA[i] == listB[i]) count++
        }
        return count
    }

    // Swap interaction logic
    fun selectBottle(index: Int) {
        if (isGameOver || isGameWon) return

        // Start timer only upon the first bottle interaction
        if (!isTimerActive) {
            startTimer()
        }

        // Play quick auditory beep
        playTone(ToneGenerator.TONE_PROP_BEEP)

        // If X-Ray active, analyze this bottle, do NOT swap
        if (isXRayActive) {
            viewModelScope.launch {
                val isCorrect = boardSequence[index] == targetSequence[index]
                val p = userProfile.value
                val usesXRayCount = p.xRayCount > 0
                val success = if (usesXRayCount) {
                    repository.updateProfile(p.copy(xRayCount = p.xRayCount - 1))
                    true
                } else {
                    repository.spendCoins(20)
                }

                if (success) {
                    val resultText = if (isCorrect) "✓ Correta!" else "✗ Incorreta."
                    xRayFeedbackText = if (usesXRayCount) {
                        "Garrafa ${index + 1}: $resultText (Bônus usado)"
                    } else {
                        "Garrafa ${index + 1}: $resultText"
                    }
                    isXRayActive = false
                    delay(3000)
                    xRayFeedbackText = null
                } else {
                    showToast(TextRes.get("no_coins", currentLanguage))
                    isXRayActive = false
                }
            }
            return
        }

        val currentSelected = selectedIndex
        if (currentSelected == null) {
            selectedIndex = index
        } else if (currentSelected == index) {
            selectedIndex = null // click again to deselect
        } else {
            // Perform SWAP
            performSwap(currentSelected, index)
            selectedIndex = null
        }
    }

    private fun performSwap(indexA: Int, indexB: Int) {
        val list = boardSequence.toMutableList()
        val temp = list[indexA]
        list[indexA] = list[indexB]
        list[indexB] = temp
        boardSequence = list
        movesCount++

        // Play confirming double-beeps swap sound
        playTone(ToneGenerator.TONE_CDMA_CONFIRM)

        val matches = countMatches(boardSequence, targetSequence)
        val shortDifficulty = getCurrentDifficultyShort()
        val swapLog = SwapHistory(
            moveNumber = movesCount,
            swappedIndices = Pair(indexA, indexB),
            correctCount = matches,
            text = "$movesCount. Swap #${indexA + 1} ⇄ #${indexB + 1} ➜ $matches / ${boardSequence.size} (${shortDifficulty})"
        )
        swapHistoryList.value = listOf(swapLog) + swapHistoryList.value

        viewModelScope.launch {
            if (matches == boardSequence.size) {
                // Victory!
                handleVictory()
            } else {
                saveCurrentGameProgress()
            }
        }
    }

    private suspend fun handleVictory() {
        stopTimer()
        isGameOver = true
        isGameWon = true

        // Play victory synthesizer sound chord
        viewModelScope.launch(Dispatchers.IO) {
            try {
                playTone(ToneGenerator.TONE_PROP_ACK)
                delay(150)
                playTone(ToneGenerator.TONE_CDMA_HIGH_L)
            } catch (e: Exception) {}
        }

        val recordCoins = getAwardCoinsForDifficulty(currentDifficulty)
        val p = userProfile.value
        val coinsReward = if (p.isAdFree) recordCoins * 2 else recordCoins
        lastCoinsEarned = coinsReward
        
        // Save statistics in Room
        val scoreCalculated = calculateScore(movesCount, elapsedTimeSeconds, currentDifficulty)
        lastCalculatedScore = scoreCalculated
        val modeKey = if (isDailyChallenge) "daily" else "standard"
        val record = GameRecord(
            mode = modeKey,
            difficulty = currentDifficulty,
            dayKey = dailyDayKey,
            moves = movesCount,
            timeInSeconds = elapsedTimeSeconds,
            score = scoreCalculated,
            won = true
        )
        repository.saveGameRecord(record)

        // Clear saved standard progression in case they solved it
        repository.clearSavedGame()
        activeSavedGame = null

        // Update profile
        val easyW = (p.easyWins + (if (currentDifficulty == "Fácil") 1 else 0)).coerceAtMost(3)
        val medW = (p.mediumWins + (if (currentDifficulty == "Médio") 1 else 0)).coerceAtMost(5)
        val hardW = (p.hardWins + (if (currentDifficulty == "Difícil") 1 else 0)).coerceAtMost(5)
        val expW = (p.expertWins + (if (currentDifficulty == "Especialista") 1 else 0)).coerceAtMost(5)
        val epicW = (p.epicWins + (if (currentDifficulty == "Épico") 1 else 0)).coerceAtMost(5)

        // Handle tier unlock progression
        var currentUnlock = p.unlockedDifficulty
        if (currentUnlock == 0 && easyW >= 3) currentUnlock = 1 // Fácil -> Médio
        if (currentUnlock == 1 && medW >= 5) currentUnlock = 2 // Médio -> Difícil
        if (currentUnlock == 2 && hardW >= 5) currentUnlock = 3 // Difícil -> Especialista
        if (currentUnlock == 3 && expW >= 5) currentUnlock = 4 // Especialista -> Épico
        if (currentUnlock == 4 && epicW >= 5) currentUnlock = 5 // Épico -> Mestre (Unlocked free tier choice)

        unlockedNewDifficultyThisTurn = currentUnlock > p.unlockedDifficulty

        val newLevelsCount = p.completedLevelsCount + 1

        repository.updateProfile(p.copy(
            coins = p.coins + coinsReward,
            unlockedDifficulty = currentUnlock,
            easyWins = easyW,
            mediumWins = medW,
            hardWins = hardW,
            expertWins = expW,
            epicWins = epicW,
            completedLevelsCount = newLevelsCount
        ))

        showToast("+ $coinsReward " + TextRes.get("coins", currentLanguage) + "!")

        if (!p.isAdFree && (newLevelsCount % 5 == 0)) {
            launchInterstitialAd()
        }
    }

    // Power-up buys
    fun buyHintPowerUp() {
        if (isGameOver) return
        viewModelScope.launch {
            val p = userProfile.value
            val usesHintCount = p.hintCount > 0
            val success = if (usesHintCount) {
                repository.updateProfile(p.copy(hintCount = p.hintCount - 1))
                true
            } else {
                repository.spendCoins(30)
            }

            if (success) {
                // Find one bottle that is incorrect and highlight it
                val incorrectIndices = mutableListOf<Int>()
                for (i in boardSequence.indices) {
                    if (boardSequence[i] != targetSequence[i]) {
                        incorrectIndices.add(i)
                    }
                }
                if (incorrectIndices.isNotEmpty()) {
                    val targetIndex = incorrectIndices.random()
                    activeHintIndex = targetIndex
                    if (usesHintCount) {
                        showToast(TextRes.get("hint_btn", currentLanguage) + ": Garrafa #${targetIndex + 1} revelada! (Bônus usado)")
                    } else {
                        showToast(TextRes.get("hint_btn", currentLanguage) + ": Garrafa #${targetIndex + 1} revelada!")
                    }
                    delay(3000)
                    activeHintIndex = null
                } else {
                    showToast("Todas as garrafas estão de fato corretas!")
                }
            } else {
                showToast(TextRes.get("no_coins", currentLanguage))
            }
        }
    }

    fun buyRevealPowerUp() {
        if (isGameOver) return
        viewModelScope.launch {
            if (repository.spendCoins(50)) {
                isRevealActive = true
                showToast(TextRes.get("reveal_btn", currentLanguage) + "!")
                delay(2000)
                isRevealActive = false
            } else {
                showToast(TextRes.get("no_coins", currentLanguage))
            }
        }
    }

    fun buyXRayPowerUp() {
        if (isGameOver) return
        val p = userProfile.value
        if (p.xRayCount > 0) {
            isXRayActive = true
            showToast("Clique em uma garrafa para fazer o Raio-X. (Grátis via Bônus!)")
        } else {
            if (p.coins < 20) {
                showToast(TextRes.get("no_coins", currentLanguage))
                return
            }
            isXRayActive = true
            showToast("Clique em uma garrafa para fazer o Raio-X.")
        }
    }

    fun buyFreezePowerUp() {
        if (isGameOver) return
        viewModelScope.launch {
            if (repository.spendCoins(15)) {
                isFreezeActive = true
                freezeTimeRemaining = 15
                showToast(TextRes.get("freeze_btn", currentLanguage) + " por 15 segundos!")
            } else {
                showToast(TextRes.get("no_coins", currentLanguage))
            }
        }
    }

    // simulated Rewarded Ad engine
    fun launchAdRewardFlow(rewardType: String) {
        adTargetReward = rewardType
        adSimulationVisible = true
    }

    fun completeAdRewardSimulation() {
        viewModelScope.launch {
            adSimulationVisible = false
            if (adTargetReward == "coins") {
                repository.addCoins(75)
                showToast("Anúncio concluído! +75 Moedas.")
            } else if (adTargetReward == "lives") {
                repository.restoreLives()
                showToast("Anúncio concluído! +1 Vida ganha.")
            } else if (adTargetReward == "hint") {
                val up = repository.getOrInitializeProfile()
                repository.updateProfile(up.copy(hintCount = up.hintCount + 1))
                showToast("Anúncio concluído! +1 Dica Adicionada.")
            } else if (adTargetReward == "xray") {
                val up = repository.getOrInitializeProfile()
                repository.updateProfile(up.copy(xRayCount = up.xRayCount + 1))
                showToast("Anúncio concluído! +1 Raio-X Adicionado.")
            }
        }
    }

    // Interstitial compulsory ads (every 5 levels)
    fun launchInterstitialAd() {
        interstitialAdCountdown = 5
        interstitialAdVisible = true
        viewModelScope.launch {
            while (interstitialAdCountdown > 0) {
                delay(1000)
                interstitialAdCountdown--
            }
        }
    }

    fun forceCloseInterstitial() {
        interstitialAdVisible = false
    }

    fun buyAdFreePlan() {
        viewModelScope.launch {
            val p = repository.getOrInitializeProfile()
            repository.updateProfile(p.copy(isAdFree = true))
            showToast("Sucesso! Plano Sem Anúncios Vitalício Ativado! 👑")
        }
    }

    fun activateInfiniteLives() {
        viewModelScope.launch {
            val p = repository.getOrInitializeProfile()
            if (!p.isAdFree) {
                showToast("Função exclusiva para usuários do plano Vitalício!")
                return@launch
            }
            val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
            if (p.lastInfiniteLivesActivationDate == todayStr) {
                showToast("Você já ativou as vidas infinitas hoje!")
                return@launch
            }
            val durationMs = 30 * 60 * 1000L
            val endTime = System.currentTimeMillis() + durationMs
            
            repository.updateProfile(p.copy(
                infiniteLivesEndTime = endTime,
                lastInfiniteLivesActivationDate = todayStr,
                lives = p.lives.coerceAtLeast(5)
            ))
            showToast("Vidas infinitas ativadas por 30 minutos! ❤️👑")
        }
    }

    // cosmetic Store transactions
    fun buySkin(skinId: String, cost: Int) {
        viewModelScope.launch {
            val p = userProfile.value
            val purchased = p.getPurchasedSkins().toMutableSet()
            if (purchased.contains(skinId)) {
                // Already purchased, simply equip
                repository.updateProfile(p.copy(activeSkinId = skinId))
                showToast("Skin Equipada!")
            } else {
                if (repository.spendCoins(cost)) {
                    purchased.add(skinId)
                    val listString = purchased.joinToString(",")
                    val up = repository.getOrInitializeProfile()
                    repository.updateProfile(up.copy(
                        purchasedSkinsCsv = listString,
                        activeSkinId = skinId
                    ))
                    showToast("Skin Comprada e Equipada!")
                } else {
                    showToast(TextRes.get("no_coins", currentLanguage))
                }
            }
        }
    }

    fun buyBgTheme(bgId: String, cost: Int) {
        viewModelScope.launch {
            val p = userProfile.value
            val purchased = p.getPurchasedBgs().toMutableSet()
            if (purchased.contains(bgId)) {
                repository.updateProfile(p.copy(activeBgId = bgId))
                showToast("Fundo de Tela Equipado!")
            } else {
                if (repository.spendCoins(cost)) {
                    purchased.add(bgId)
                    val listString = purchased.joinToString(",")
                    val up = repository.getOrInitializeProfile()
                    repository.updateProfile(up.copy(
                        purchasedBgsCsv = listString,
                        activeBgId = bgId
                    ))
                    showToast("Tema de Fundo Comprado e Equipado!")
                } else {
                    showToast(TextRes.get("no_coins", currentLanguage))
                }
            }
        }
    }

    fun toggleVibration(enabled: Boolean) {
        viewModelScope.launch {
            val p = userProfile.value
            repository.updateProfile(p.copy(isVibrationEnabled = enabled))
        }
    }

    // Tone Generator sound engine implementation
    private var toneGenerator: ToneGenerator? = null

    private fun getToneGenerator(): ToneGenerator? {
        if (toneGenerator == null) {
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
            } catch (e: Exception) {
                // fall-safe
            }
        }
        return toneGenerator
    }

    fun playTone(toneType: Int) {
        if (isMuted) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getToneGenerator()?.startTone(toneType, 120)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            // ignore
        }
    }

    // Pause and exit game voluntarily without losing a life
    fun pauseAndExitGame() {
        viewModelScope.launch {
            stopTimer()
            saveCurrentGameProgress()
            quitDialogVisible = false
            showToast("Progresso salvo! Até a próxima.")
            currentScreen = Screen.HOME
        }
    }

    // Abandon saved classic match and start a new one (subtends 1 life cost)
    fun abandonSavedAndStartNewClassic() {
        viewModelScope.launch {
            val p = userProfile.value
            val hasInfinite = p.isAdFree && p.infiniteLivesEndTime > System.currentTimeMillis()
            if (p.lives <= 1 && !hasInfinite) {
                showToast("Vidas insuficientes para iniciar novo jogo e descartar o atual!")
                return@launch
            }
            if (!hasInfinite) {
                repository.changeLives(-1)
            }
            repository.clearSavedGame()
            activeSavedGame = null
            showClassicConfirmDialog = false
            initiateNewClassicGame(pendingDifficultyToStart)
        }
    }

    // Launch classic match directly
    fun initiateNewClassicGame(difficulty: String) {
        viewModelScope.launch {
            val p = userProfile.value
            val hasInfinite = p.isAdFree && p.infiniteLivesEndTime > System.currentTimeMillis()
            if (p.lives <= 0 && !hasInfinite) {
                showToast(TextRes.get("lose", currentLanguage))
                return@launch
            }

            currentDifficulty = difficulty
            isDailyChallenge = false
            dailyDayKey = ""
            selectedIndex = null
            movesCount = 0
            elapsedTimeSeconds = 0
            isGameOver = false
            isGameWon = false
            swapHistoryList.value = emptyList()

            // Power-up resets
            activeHintIndex = null
            isRevealActive = false
            isXRayActive = false
            isFreezeActive = false
            freezeTimeRemaining = 0
            xRayFeedbackText = null

            // Generate sequence
            val size = getBottleCountForDifficulty(difficulty)
            val colors = getAvailableColorsList().shuffled().take(size)
            targetSequence = colors.shuffled()
            
            // Ensure initial is not exactly equal
            var scm = colors.shuffled()
            var matches = countMatches(scm, targetSequence)
            var attempts = 0
            while (matches == size && size > 1 && attempts < 10) {
                scm = colors.shuffled()
                scm = colors.shuffled()
                matches = countMatches(scm, targetSequence)
                attempts++
            }
            boardSequence = scm
            initialSequence = scm

            // Save state immediately
            saveCurrentGameProgress()

            currentScreen = Screen.GAME_PLAY
        }
    }

    // Calendar navigation for next challenges
    fun isNextDailyChallengeAvailable(): Boolean {
        if (!isDailyChallenge) return false
        try {
            val parts = dailyDayKey.split("-")
            if (parts.size != 3) return false
            val year = parts[0].toInt()
            val month = parts[1].toInt() - 1
            val day = parts[2].toInt()

            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

            if (year == currentYear && month == currentMonth) {
                return day < currentDay
            }
            val maxDaysInThatMonth = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
            }.getActualMaximum(Calendar.DAY_OF_MONTH)
            
            if (day < maxDaysInThatMonth) {
                return true
            }
        } catch (e: Exception) {
            // fail-safe
        }
        return false
    }

    fun startNextDailyChallenge() {
        try {
            val parts = dailyDayKey.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            var day = parts[2].toInt()

            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, day)
            }
            cal.add(Calendar.DAY_OF_MONTH, 1)

            val nextYear = cal.get(Calendar.YEAR)
            val nextMonth = cal.get(Calendar.MONTH) + 1
            val nextDay = cal.get(Calendar.DAY_OF_MONTH)
            
            val nextDayKey = String.format("%04d-%02d-%02d", nextYear, nextMonth, nextDay)
            
            val calIndex = cal.get(Calendar.DAY_OF_WEEK)
            val nextDifficulty = when (calIndex) {
                Calendar.MONDAY, Calendar.FRIDAY -> "Difícil"
                Calendar.TUESDAY, Calendar.SATURDAY -> "Especialista"
                Calendar.WEDNESDAY -> "Épico"
                else -> "Mestre"
            }
            
            startDailyChallenge(nextDayKey, nextDifficulty)
        } catch (e: Exception) {
            showToast("Erro ao avançar para o próximo desafio.")
        }
    }

    // Math scoring formula (guarantees strictly higher rewards for faster time and fewer moves)
    private fun calculateScore(moves: Int, time: Int, difficulty: String): Int {
        val baseScore = when(difficulty) {
            "Fácil" -> 100
            "Médio" -> 250
            "Difícil" -> 500
            "Especialista" -> 800
            "Épico" -> 1200
            else -> 1800 // Mestre
        }

        val expectedMoves = when(difficulty) {
            "Fácil" -> 3
            "Médio" -> 6
            "Difícil" -> 10
            "Especialista" -> 15
            "Épico" -> 20
            else -> 25
        }
        val movesBonus = if (moves <= expectedMoves) {
            300 + (expectedMoves - moves) * 50
        } else {
            (300 - (moves - expectedMoves) * 20).coerceAtLeast(50)
        }

        val expectedTimeInSeconds = when(difficulty) {
            "Fácil" -> 15
            "Médio" -> 30
            "Difícil" -> 60
            "Especialista" -> 100
            "Épico" -> 150
            else -> 200
        }
        val timeBonus = if (time <= expectedTimeInSeconds) {
            200 + (expectedTimeInSeconds - time) * 10
        } else {
            (200 - (time - expectedTimeInSeconds) * 2).coerceAtLeast(20)
        }

        return baseScore + movesBonus + timeBonus
    }

    fun showToast(msg: String) {
        toastMessage = msg
        viewModelScope.launch {
            delay(2500)
            if (toastMessage == msg) {
                toastMessage = null
            }
        }
    }

    // UI helpers
    fun getBottleCountForDifficulty(difficulty: String): Int {
        return when(difficulty) {
            "Fácil" -> 3
            "Médio" -> 5
            "Difícil" -> 8
            "Especialista" -> 10
            "Épico" -> 13
            else -> 16
        }
    }

    fun getUnlockedTiersCount(profile: UserProfile): Int {
        return profile.unlockedDifficulty
    }

    fun getWinsForDifficulty(diffName: String, profile: UserProfile): Int {
        return when(diffName) {
            "Fácil" -> profile.easyWins.coerceAtMost(3)
            "Médio" -> profile.mediumWins.coerceAtMost(5)
            "Difícil" -> profile.hardWins.coerceAtMost(5)
            "Especialista" -> profile.expertWins.coerceAtMost(5)
            "Épico" -> profile.epicWins.coerceAtMost(5)
            else -> 0
        }
    }

    fun getWinsTargetForDifficulty(diffName: String): Int {
        return when(diffName) {
            "Fácil" -> 3
            "Médio" -> 5
            "Difícil" -> 5
            "Especialista" -> 5
            "Épico" -> 5
            else -> 0
        }
    }

    fun getAwardCoinsForDifficulty(difficulty: String): Int {
        val base = when(difficulty) {
            "Fácil" -> 10
            "Médio" -> 20
            "Difícil" -> 35
            "Especialista" -> 50
            "Épico" -> 75
            else -> 100 // Mestre
        }
        return if (isDailyChallenge) base + 50 else base
    }

    private fun getCurrentDifficultyShort(): String {
        return when(currentDifficulty) {
            "Fácil" -> TextRes.get("easy_short", currentLanguage)
            "Médio" -> TextRes.get("medium_short", currentLanguage)
            "Difícil" -> TextRes.get("hard_short", currentLanguage)
            "Especialista" -> TextRes.get("expert_short", currentLanguage)
            "Épico" -> TextRes.get("epic_short", currentLanguage)
            else -> TextRes.get("master_short", currentLanguage)
        }
    }

    // list of all game colors
    fun getAvailableColorsList(): List<String> {
        return listOf(
            "red", "green", "blue", "yellow", "orange", "purple", "pink", "teal",
            "cyan", "magenta", "lime", "brown", "lavender", "coral", "mint", "gold"
        )
    }

    // helper colors parser
    fun getColorHex(colorName: String): androidx.compose.ui.graphics.Color {
        return when(colorName) {
            "red" -> androidx.compose.ui.graphics.Color(0xFFE53935)
            "green" -> androidx.compose.ui.graphics.Color(0xFF43A047)
            "blue" -> androidx.compose.ui.graphics.Color(0xFF1E88E5)
            "yellow" -> androidx.compose.ui.graphics.Color(0xFFFFEB3B)
            "orange" -> androidx.compose.ui.graphics.Color(0xFFFB8C00)
            "purple" -> androidx.compose.ui.graphics.Color(0xFF8E24AA)
            "pink" -> androidx.compose.ui.graphics.Color(0xFFD81B60)
            "teal" -> androidx.compose.ui.graphics.Color(0xFF00897B)
            "cyan" -> androidx.compose.ui.graphics.Color(0xFF00ACC1)
            "magenta" -> androidx.compose.ui.graphics.Color(0xFFE91E63)
            "lime" -> androidx.compose.ui.graphics.Color(0xFFC0CA33)
            "brown" -> androidx.compose.ui.graphics.Color(0xFF795548)
            "lavender" -> androidx.compose.ui.graphics.Color(0xFFB39DDB)
            "coral" -> androidx.compose.ui.graphics.Color(0xFFFF7043)
            "mint" -> androidx.compose.ui.graphics.Color(0xFF80CBC4)
            "gold" -> androidx.compose.ui.graphics.Color(0xFFFFB300)
            else -> androidx.compose.ui.graphics.Color.Gray
        }
    }

    // --- DAILY LOGIN & REWARD SYSTEM METHOD LOGIC ---
    fun processDailyLogin() {
        viewModelScope.launch {
            val p = repository.getOrInitializeProfile()
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            
            if (p.lastLoginDateKey == todayStr) {
                // Already logged in today, syncing state
                currentStreakDay = if (p.consecutiveLogins <= 0) 1 else p.consecutiveLogins
                return@launch
            }

            // Determine if consecutive login days
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)

            val newStreak = if (p.lastLoginDateKey == yesterdayStr) {
                if (p.consecutiveLogins >= 7) {
                    1 // completed full week, dynamic restart!
                } else {
                    p.consecutiveLogins + 1
                }
            } else {
                1 // missed a day or first-ever tracker check
            }

            currentStreakDay = newStreak
            showDailyRewardDialog = true
            dailyRewardClaimedThisTurn = false
        }
    }

    fun claimDailyReward() {
        viewModelScope.launch {
            val p = repository.getOrInitializeProfile()
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val streak = currentStreakDay
            
            val coinsReward: Int
            val hintsReward: Int
            val xRaysReward: Int
            
            when(streak) {
                1 -> { coinsReward = 20; hintsReward = 0; xRaysReward = 0 }
                2 -> { coinsReward = 35; hintsReward = 0; xRaysReward = 0 }
                3 -> { coinsReward = 50; hintsReward = 1; xRaysReward = 0 }
                4 -> { coinsReward = 65; hintsReward = 0; xRaysReward = 0 }
                5 -> { coinsReward = 80; hintsReward = 0; xRaysReward = 1 }
                6 -> { coinsReward = 110; hintsReward = 0; xRaysReward = 0 }
                else -> { coinsReward = 250; hintsReward = 2; xRaysReward = 2 } // Day 7 bonus!
            }

            val rewardTextParts = mutableListOf<String>()
            rewardTextParts.add(String.format(TextRes.get("coins_reward_piece", currentLanguage), coinsReward))
            if (hintsReward > 0) {
                rewardTextParts.add(String.format(TextRes.get("hints_reward_piece", currentLanguage), hintsReward))
            }
            if (xRaysReward > 0) {
                rewardTextParts.add(String.format(TextRes.get("xrays_reward_piece", currentLanguage), xRaysReward))
            }

            val message = String.format(TextRes.get("reward_day_label", currentLanguage), streak) + rewardTextParts.joinToString(" • ")
            claimMessage = if (streak == 7) {
                String.format(TextRes.get("super_reward_weekly", currentLanguage), message)
            } else {
                message
            }

            // Save updated profile
            repository.updateProfile(p.copy(
                coins = p.coins + coinsReward,
                hintCount = p.hintCount + hintsReward,
                xRayCount = p.xRayCount + xRaysReward,
                consecutiveLogins = streak,
                lastLoginDateKey = todayStr
            ))

            dailyRewardClaimedThisTurn = true
            showToast(String.format(TextRes.get("bonus_claimed_toast", currentLanguage), streak))
        }
    }

}

// Factory pattern for initializing VM with local constructor arguments
class GameViewModelFactory(private val gameRepository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(gameRepository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}
