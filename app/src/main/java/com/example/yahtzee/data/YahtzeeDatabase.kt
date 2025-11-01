package com.example.yahtzee.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.yahtzee.data.dao.GameResultDao
import com.example.yahtzee.data.entities.GameResult

@Database(entities = [GameResult::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class YahtzeeDatabase : RoomDatabase() {
	abstract fun gameResultDao(): GameResultDao
}
