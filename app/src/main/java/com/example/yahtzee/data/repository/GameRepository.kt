package com.example.yahtzee.data.repository

import com.example.yahtzee.data.dao.GameResultDao
import com.example.yahtzee.data.entities.GameResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
	private val dao: GameResultDao
) {
	suspend fun insertResult(result: GameResult) = dao.insertResult(result)
	suspend fun getAllResults(): List<GameResult> = dao.getAllResults()

	suspend fun deleteAllResults() = dao.deleteAllResults()

	suspend fun getCount(): Int = dao.getCount()
}
