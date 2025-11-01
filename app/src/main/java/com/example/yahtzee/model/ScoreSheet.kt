package com.example.yahtzee.model

data class ScoreSheet(
	val scores: MutableMap<YahtzeeScorer.Category, Int?> = mutableMapOf()
)