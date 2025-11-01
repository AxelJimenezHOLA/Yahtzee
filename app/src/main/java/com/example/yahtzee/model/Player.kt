package com.example.yahtzee.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

class Player(
	val name: String,
	score: Int = 0
) {
	var score by mutableIntStateOf(score)
}

