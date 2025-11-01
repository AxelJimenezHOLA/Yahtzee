package com.example.yahtzee.ui.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.yahtzee.model.YahtzeeScorer
import com.example.yahtzee.model.YahtzeeScorer.Category

class PossibleScoresActivity : ComponentActivity() {

	companion object {
		const val EXTRA_DICE_VALUES = "extra_dice_values"
		const val EXTRA_CURRENT_PLAYER = "extra_current_player"
		const val EXTRA_AVAILABLE_CATEGORIES = "extra_available_categories"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		val diceValues = intent?.getIntArrayExtra(EXTRA_DICE_VALUES) ?: IntArray(5)
		val currentPlayer = intent?.getStringExtra(EXTRA_CURRENT_PLAYER) ?: "Player"
		val availableCategories = intent?.getStringArrayExtra(EXTRA_AVAILABLE_CATEGORIES)
			?.map { Category.valueOf(it) }
			?: Category.entries

		setContent {
			PossibleScoresScreen(
				diceValues = diceValues.toList(),
				currentPlayer = currentPlayer,
				availableCategories = availableCategories,
				onBack = { finish() }
			)
		}
	}

}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ContextCastToActivity")
@Composable
fun PossibleScoresScreen(
	diceValues: List<Int>,
	currentPlayer: String,
	availableCategories: List<Category>,
	onBack: () -> Unit = {}
) {
	val activity = LocalContext.current as Activity
	val scores = availableCategories.map { cat ->
		val score = YahtzeeScorer.calculateScore(diceValues, cat)
		Pair(cat, score)
	}

	Scaffold(
		topBar = {
			TopAppBar(
				colors = topAppBarColors(
					containerColor = Color(0xFF2B67FF),
					titleContentColor = Color(0xFFFFFFFF),
				),
				title = { Text("Select a Category for $currentPlayer") }
			)
		},
		floatingActionButton = {
			FloatingActionButton(onClick = onBack) {
				Icon(Icons.Default.ArrowBack, contentDescription = "Back")
			}
		}
	) { innerPadding ->
		LazyColumn(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Top
		) {
			items(scores) { (cat, sc) ->
				Button(
					onClick = {
						val resultIntent = Intent().apply {
							putExtra(GameActivity.RESULT_CATEGORY, cat.name)
						}
						activity.setResult(Activity.RESULT_OK, resultIntent)
						activity.finish()
					},
					modifier = Modifier
						.padding(vertical = 2.dp)
						.fillMaxWidth(0.7f),
					colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B67FF))
				) {
					Text("${cat.name.replace("_", " ")}: $sc pts")
				}
			}
		}
	}
}

