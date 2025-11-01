package com.example.yahtzee.di

import android.content.Context
import androidx.room.Room
import com.example.yahtzee.data.YahtzeeDatabase
import com.example.yahtzee.data.dao.GameResultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

	@Provides
	@Singleton
	fun provideDatabase(@ApplicationContext context: Context): YahtzeeDatabase {
		return Room.databaseBuilder(
			context.applicationContext,
			YahtzeeDatabase::class.java,
			"yahtzee.db"
		).build()
	}

	@Provides
	@Singleton
	fun provideGameResultDao(db: YahtzeeDatabase): GameResultDao {
		return db.gameResultDao()
	}
}
