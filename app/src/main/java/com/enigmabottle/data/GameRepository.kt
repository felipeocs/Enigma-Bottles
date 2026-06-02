package com.enigmabottle.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GameRepository(private val dao: DatabaseDao) {
    val userProfileFlow: Flow<UserProfile> = dao.getUserProfileFlow()
        .map { it ?: UserProfile() }

    val allGameRecords: Flow<List<GameRecord>> = dao.getAllGameRecordsFlow()
    val wonDailyRecords: Flow<List<GameRecord>> = dao.getWonDailyGameRecordsFlow()

    suspend fun getOrInitializeProfile(): UserProfile = withContext(Dispatchers.IO) {
        var profile = dao.getUserProfile()
        if (profile == null) {
            profile = UserProfile()
            dao.insertUserProfile(profile)
        }
        profile
    }

    suspend fun updateProfile(transform: (UserProfile) -> UserProfile) = withContext(Dispatchers.IO) {
        val p = getOrInitializeProfile()
        val newProfile = transform(p)
        dao.updateUserProfile(newProfile)
    }

    suspend fun addCoins(amount: Int) = updateProfile { p ->
        p.copy(coins = p.coins + amount)
    }

    suspend fun spendCoins(amount: Int): Boolean = withContext(Dispatchers.IO) {
        var success = false
        updateProfile { p ->
            if (p.coins >= amount) {
                success = true
                p.copy(coins = p.coins - amount)
            } else {
                p
            }
        }
        success
    }

    suspend fun changeLives(delta: Int) = updateProfile { p ->
        val newLives = (p.lives + delta).coerceAtLeast(0)
        p.copy(
            lives = newLives,
            lastLifeRegenTimeMillis = if (newLives < 5 && p.lives >= 5) System.currentTimeMillis() else p.lastLifeRegenTimeMillis
        )
    }

    suspend fun restoreLives() = updateProfile { p ->
        val newLives = p.lives + 1
        p.copy(
            lives = newLives,
            lastLifeRegenTimeMillis = if (newLives < 5) p.lastLifeRegenTimeMillis else System.currentTimeMillis()
        )
    }

    suspend fun saveGameRecord(record: GameRecord) = withContext(Dispatchers.IO) {
        dao.insertGameRecord(record)
    }

    suspend fun getSavedGame(): SavedGame? = withContext(Dispatchers.IO) {
        dao.getSavedGame()
    }

    suspend fun saveGameState(savedGame: SavedGame) = withContext(Dispatchers.IO) {
        dao.saveGame(savedGame)
    }

    suspend fun clearSavedGame() = withContext(Dispatchers.IO) {
        dao.clearSavedGame()
    }

    suspend fun getDailyGameRecordsSync(): List<GameRecord> = withContext(Dispatchers.IO) {
        dao.getDailyGameRecordsSync()
    }
}
