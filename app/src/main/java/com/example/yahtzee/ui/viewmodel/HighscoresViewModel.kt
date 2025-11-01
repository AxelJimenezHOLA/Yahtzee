package com.example.yahtzee.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yahtzee.data.entities.GameResult
import com.example.yahtzee.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HighscoresViewModel @Inject constructor(
	private val repository: GameRepository
) : ViewModel() {

	private val _highscores = MutableStateFlow<List<GameResult>>(emptyList())
	val highscores: StateFlow<List<GameResult>> = _highscores

	private val _count = MutableStateFlow(0)
	val count: StateFlow<Int> = _count


	init {
		loadHighscores()
	}

	fun loadHighscores() {
		viewModelScope.launch {
			_highscores.value = repository.getAllResults()
		}
	}

	fun deleteHighscores() {
		viewModelScope.launch {
			repository.deleteAllResults()
			_highscores.value = emptyList()
		}
	}

	fun getCount() {
		viewModelScope.launch {
			repository.getCount()
			_count.value = repository.getCount()
		}
	}
}
