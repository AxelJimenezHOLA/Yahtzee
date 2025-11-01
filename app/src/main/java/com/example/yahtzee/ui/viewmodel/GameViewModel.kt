package com.example.yahtzee.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yahtzee.data.entities.GameResult
import com.example.yahtzee.data.repository.GameRepository
import com.example.yahtzee.model.Dice
import com.example.yahtzee.model.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import com.example.yahtzee.model.YahtzeeScorer
import com.example.yahtzee.model.YahtzeeScorer.Category
import com.example.yahtzee.model.ScoreSheet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class GameViewModel @Inject constructor(
	private val repository: GameRepository
) : ViewModel() {

	
	var diceList = mutableStateListOf<Dice>().apply { repeat(5) { add(Dice()) } }
		private set

	var players = mutableStateListOf<Player>()
		private set

	var currentPlayerIndex by mutableIntStateOf(0)
		private set

	var rollsLeft by mutableIntStateOf(3)

	var round by mutableIntStateOf(1)
		private set

	private val _gameFinished = MutableStateFlow(false)
	val gameFinished: StateFlow<Boolean> = _gameFinished

	fun addPlayer(name: String) {
		val trimmed = name.trim()
		if (trimmed.isNotEmpty()) {
			players.add(Player(trimmed))
		}
	}

	fun toggleHold(index: Int) {
		if (index in 0 until diceList.size) {
			diceList[index].isHeld = !diceList[index].isHeld
			
		}
	}

	fun rollDice() {
		if (rollsLeft <= 0) return
		diceList.forEach { if (!it.isHeld) it.value = (1..6).random() }
		rollsLeft--
	}

	fun nextTurn() {
		if (players.isEmpty()) return

		val allDone = players.all { availableCategories(it).isEmpty() }
		if (allDone) {
			val winner = getWinner()
			if (winner != null) saveResult(winner)
			return
		}

		if (currentPlayerIndex < players.lastIndex) currentPlayerIndex++
		else {
			currentPlayerIndex = 0
			round++
		}

		resetDice()
		rollsLeft = 3
	}

	fun getWinner(): Player? = players.maxByOrNull { it.score }

	fun saveResult(winner: Player) {
		viewModelScope.launch {
			repository.insertResult(
				GameResult(
					winnerName = winner.name,
					score = winner.score,
					date = Date()
				)
			)
			_gameFinished.value = true
		}
	}

	var scoreSheets = mutableMapOf<String, ScoreSheet>()

	fun availableCategories(player: Player): List<Category> {
		val sheet = scoreSheets.getOrPut(player.name) { ScoreSheet() }
		return Category.entries.filter { sheet.scores[it] == null }
	}

	fun applyScore(player: Player, category: Category) {
		val diceValues = diceList.map { it.value }
		val score = YahtzeeScorer.calculateScore(diceValues, category)
		val sheet = scoreSheets.getOrPut(player.name) { ScoreSheet() }
		sheet.scores[category] = score
		player.score = totalScore(player)
	}

	private fun totalScore(player: Player): Int {
		val sheet = scoreSheets[player.name] ?: return 0
		val upperBonus = YahtzeeScorer.upperBonus(sheet.scores.filterValues { it != null } as Map<Category, Int>)
		return sheet.scores.values.filterNotNull().sum() + upperBonus
	}

	fun resetDice() {
		diceList.forEach {
			it.isHeld = false
			it.value = 0
		}
	}
}
