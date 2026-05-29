package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DatabaseDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    @Update
    suspend fun updateUserProfile(profile: UserProfile)

    @Query("SELECT * FROM game_records ORDER BY timestamp DESC")
    fun getAllGameRecordsFlow(): Flow<List<GameRecord>>

    @Query("SELECT * FROM game_records WHERE mode = 'daily' AND won = 1")
    fun getWonDailyGameRecordsFlow(): Flow<List<GameRecord>>

    @Query("SELECT * FROM game_records WHERE mode = 'daily'")
    suspend fun getDailyGameRecordsSync(): List<GameRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameRecord(record: GameRecord)

    @Query("SELECT * FROM saved_game WHERE id = 1 LIMIT 1")
    suspend fun getSavedGame(): SavedGame?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGame(savedGame: SavedGame)

    @Query("DELETE FROM saved_game WHERE id = 1")
    suspend fun clearSavedGame()

}
