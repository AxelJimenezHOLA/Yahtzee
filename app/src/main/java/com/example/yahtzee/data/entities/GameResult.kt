package com.example.yahtzee.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "game_results")
data class GameResult(
	@PrimaryKey(autoGenerate = true) val id: Int = 0,
	val winnerName: String,
	val score: Int,
	val date: Date
)
