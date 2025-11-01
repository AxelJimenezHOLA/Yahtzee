package com.example.yahtzee.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.yahtzee.data.entities.GameResult

@Dao
interface GameResultDao {

	@Insert
	suspend fun insertResult(result: GameResult)

	@Query("SELECT * FROM game_results ORDER BY score DESC")
	suspend fun getAllResults(): List<GameResult>

	@Query("DELETE FROM game_results")
	suspend fun deleteAllResults()

	@Query("SELECT COUNT(*) FROM game_results")
	suspend fun getCount(): Int
}
