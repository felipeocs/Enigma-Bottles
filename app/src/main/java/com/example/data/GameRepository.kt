package com.example.data

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

    suspend fun updateProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        dao.updateUserProfile(profile)
    }

    suspend fun addCoins(amount: Int) = withContext(Dispatchers.IO) {
        val p = getOrInitializeProfile()
        dao.updateUserProfile(p.copy(coins = p.coins + amount))
    }

    suspend fun spendCoins(amount: Int): Boolean = withContext(Dispatchers.IO) {
        val p = getOrInitializeProfile()
        if (p.coins >= amount) {
            dao.updateUserProfile(p.copy(coins = p.coins - amount))
            true
        } else {
            false
        }
    }

    suspend fun changeLives(delta: Int) = withContext(Dispatchers.IO) {
        val p = getOrInitializeProfile()
        val newLives = (p.lives + delta).coerceAtLeast(0)
        dao.updateUserProfile(p.copy(
            lives = newLives,
            lastLifeRegenTimeMillis = if (newLives < 5 && p.lives >= 5) System.currentTimeMillis() else p.lastLifeRegenTimeMillis
        ))
    }

    suspend fun restoreLives() = withContext(Dispatchers.IO) {
        val p = getOrInitializeProfile()
        val newLives = p.lives + 1
        dao.updateUserProfile(p.copy(
            lives = newLives,
            lastLifeRegenTimeMillis = if (newLives < 5) p.lastLifeRegenTimeMillis else System.currentTimeMillis()
        ))
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
