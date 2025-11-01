package com.example.yahtzee.model

object YahtzeeScorer {

	enum class Category {
		ONES, TWOS, THREES, FOURS, FIVES, SIXES,
		THREE_OF_A_KIND, FOUR_OF_A_KIND, FULL_HOUSE,
		SMALL_STRAIGHT, LARGE_STRAIGHT, YAHTZEE, CHANCE
	}

	fun calculateScore(dice: List<Int>, category: Category): Int {
		val counts = dice.groupingBy { it }.eachCount()
		return when (category) {
			Category.ONES -> dice.filter { it == 1 }.sum()
			Category.TWOS -> dice.filter { it == 2 }.sum()
			Category.THREES -> dice.filter { it == 3 }.sum()
			Category.FOURS -> dice.filter { it == 4 }.sum()
			Category.FIVES -> dice.filter { it == 5 }.sum()
			Category.SIXES -> dice.filter { it == 6 }.sum()

			Category.THREE_OF_A_KIND ->
				if (counts.values.any { it >= 3 }) dice.sum() else 0
			Category.FOUR_OF_A_KIND ->
				if (counts.values.any { it >= 4 }) dice.sum() else 0
			Category.FULL_HOUSE ->
				if (counts.values.sorted() == listOf(2, 3)) 25 else 0
			Category.SMALL_STRAIGHT ->
				if (hasStraight(dice, 4)) 30 else 0
			Category.LARGE_STRAIGHT ->
				if (hasStraight(dice, 5)) 40 else 0
			Category.YAHTZEE ->
				if (counts.values.any { it == 5 }) 50 else 0
			Category.CHANCE ->
				dice.sum()
		}
	}

	private fun hasStraight(dice: List<Int>, length: Int): Boolean {
		val distinct = dice.toSet().sorted()
		val consecutive = distinct.fold(1 to 1) { (max, curr), next ->
			if (next == curr + 1) max + 1 to next else 1 to next
		}.first
		return consecutive >= length
	}

	fun upperBonus(upperScores: Map<Category, Int>): Int {
		val sum = upperScores.filterKeys {
			it in listOf(
				Category.ONES, Category.TWOS, Category.THREES,
				Category.FOURS, Category.FIVES, Category.SIXES
			)
		}.values.sum()
		return if (sum >= 63) 35 else 0
	}
}
