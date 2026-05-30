package com.enigmabottle.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val coins: Int = 100, // Inicia com algumas moedas para encorajar o uso dos power-ups
    val lives: Int = 5,
    val lastLifeRegenTimeMillis: Long = System.currentTimeMillis(),
    val unlockedDifficulty: Int = 0, // 0 = Fácil, 1 = Médio, 2 = Difícil, 3 = Especialista, 4 = Épico, 5 = Mestre (libera modo livre)
    val easyWins: Int = 0,
    val mediumWins: Int = 0,
    val hardWins: Int = 0,
    val expertWins: Int = 0,
    val epicWins: Int = 0,
    val activeSkinId: String = "classic",
    val activeBgId: String = "sleek_interface",
    val purchasedSkinsCsv: String = "classic", // Armazenado como CSV simples: "classic,neon"
    val purchasedBgsCsv: String = "sleek_interface,wood", // Armazenado como CSV simples: "wood,lab"
    
    // Novas propriedades solicitadas: Daily streak, login dates e inventário de power-ups
    val consecutiveLogins: Int = 0,
    val lastLoginDateKey: String = "",
    val hintCount: Int = 0,
    val xRayCount: Int = 0,
    val isVibrationEnabled: Boolean = true,
    val isAdFree: Boolean = false,
    val completedLevelsCount: Int = 0,
    val infiniteLivesEndTime: Long = 0L,
    val lastInfiniteLivesActivationDate: String = ""
) {
    fun getPurchasedSkins(): List<String> = purchasedSkinsCsv.split(",").filter { it.isNotBlank() }
    fun getPurchasedBgs(): List<String> = purchasedBgsCsv.split(",").filter { it.isNotBlank() }
}

@Entity(tableName = "game_records")
data class GameRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: String, // "standard" ou "daily"
    val difficulty: String, // "Fácil", "Médio", "Difícil", "Especialista", "Épico", "Mestre"
    val dayKey: String = "", // Para desafio diário, ex: "2026-05-28"
    val moves: Int = 0,
    val timeInSeconds: Int = 0,
    val score: Int = 0,
    val won: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_game")
data class SavedGame(
    @PrimaryKey val id: Int = 1,
    val difficulty: String, // "Fácil", "Médio", "Difícil", etc.
    val targetColorsCsv: String, // Ex: "red,green,blue"
    val currentColorsCsv: String, // Ex: "green,red,blue"
    val initialColorsCsv: String, // Ex: "blue,green,red"
    val movesCount: Int = 0,
    val elapsedTimeSeconds: Int = 0,
    val activeGame: Boolean = false,
    val isDailyUniverse: Boolean = false,
    val dailyDayKey: String = ""
) {
    fun getTargetColors(): List<String> = targetColorsCsv.split(",").filter { it.isNotBlank() }
    fun getCurrentColors(): List<String> = currentColorsCsv.split(",").filter { it.isNotBlank() }
    fun getInitialColors(): List<String> = initialColorsCsv.split(",").filter { it.isNotBlank() }
}
