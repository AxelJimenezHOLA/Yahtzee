package com.example.yahtzee.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Dice {
	var value by mutableIntStateOf(0)
	var isHeld by mutableStateOf(false)
}
