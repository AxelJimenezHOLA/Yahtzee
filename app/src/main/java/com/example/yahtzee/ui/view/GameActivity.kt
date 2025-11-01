package com.example.yahtzee.ui.view

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yahtzee.R
import com.example.yahtzee.model.YahtzeeScorer
import com.example.yahtzee.ui.viewmodel.GameViewModel
import dagger.hilt.android.AndroidEntryPoint

/* ---------- Activity ---------- */

@AndroidEntryPoint
class GameActivity : ComponentActivity() {

	companion object {
		const val REQUEST_CATEGORY = 1001
		const val RESULT_CATEGORY = "selected_category"
	}

	private lateinit var soundMediaPlayer: MediaPlayer
	private val viewModel : GameViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		soundMediaPlayer = MediaPlayer.create(this, R.raw.dice_throw)

		enableEdgeToEdge()
		setContent {
			GameScreen(soundMediaPlayer, viewModel)
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		listOf(soundMediaPlayer).forEach {
			try {
				if (it.isPlaying) it.stop()
				it.release()
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == REQUEST_CATEGORY && resultCode == RESULT_OK && data != null) {
			val categoryName = data.getStringExtra(RESULT_CATEGORY)
			val currentPlayer = viewModel.players.getOrNull(viewModel.currentPlayerIndex)
			val category = YahtzeeScorer.Category.valueOf(categoryName ?: return)
			if (currentPlayer != null) {
				viewModel.applyScore(currentPlayer, category)
				viewModel.nextTurn()
			}
		}
	}

}

/* ---------- Composables ---------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(soundMediaPlayer: MediaPlayer, viewModel: GameViewModel) {
	val context = LocalContext.current
	val activity = context as? Activity

	LaunchedEffect(Unit) {
		val playersFromIntent =
			activity?.intent?.getStringArrayExtra(PlayerSetupActivity.EXTRA_PLAYERS)
		if (!playersFromIntent.isNullOrEmpty()) {
			playersFromIntent.forEach { name -> viewModel.addPlayer(name) }
			viewModel.resetDice()
			activity.intent.removeExtra(PlayerSetupActivity.EXTRA_PLAYERS)
		}
	}

	val gameFinished by viewModel.gameFinished.collectAsState()
	LaunchedEffect(gameFinished) {
		if (gameFinished) {
			val winnerName = viewModel.getWinner()?.name
			val winnerScore = viewModel.getWinner()?.score
			Toast.makeText(
				context,
				"Winner: $winnerName with $winnerScore points!",
				Toast.LENGTH_SHORT
			).show()
			val intent = Intent(context, MainActivity::class.java)
			context.startActivity(intent)
			activity?.finish()
		}
	}

	Scaffold(
		topBar = {
			TopAppBar(
				colors = topAppBarColors(
					containerColor = Color(0xFF11A018),
					titleContentColor = Color(0xFFFFFFFF),
				),
				title = {
					Column {
						val roundNumber = viewModel.round
						val currentPlayerName = viewModel.players
							.getOrNull(viewModel.currentPlayerIndex)?.name
						Text("Round $roundNumber")
						Text("Turn of $currentPlayerName")
					}
				}
			)
		}
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				.padding(innerPadding),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
		) {
			Text(
				"Remaining Throws: ${viewModel.rollsLeft}",
				modifier = Modifier.padding(bottom = 8.dp),
				fontSize = 18.sp
			)
			DiceRow(viewModel)
			ThrowDiceButton(viewModel, soundMediaPlayer)
			Spacer(modifier = Modifier.height(10.dp))
			ScoreBoard(viewModel)
		}
	}
}


/* ---------- Subcomposables ---------- */

@Composable
fun DiceRow(viewModel: GameViewModel) {
	val context = LocalContext.current

	Row(
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.CenterVertically
	) {
		viewModel.diceList.forEachIndexed { idx, dice ->
			Surface(
				modifier = Modifier.size(64.dp)
			) {
				Column(
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center,
					modifier = Modifier.fillMaxSize()
				) {
					Image(
						painter = painterResource(
							id = context.resources.getIdentifier(
								"dice_${dice.value}",
								"drawable",
								context.packageName
							)
						),
						contentDescription = "Dice ${dice.value}",
						modifier = Modifier.size(36.dp)
					)

					Checkbox(
						checked = dice.isHeld,
						onCheckedChange = { checked ->
							if (dice.value != 0) {
								viewModel.toggleHold(idx)
							}
						},
						modifier = Modifier.padding(top = 2.dp)
					)
				}
			}
		}
	}
}


@Composable
fun ThrowDiceButton(viewModel: GameViewModel, soundMediaPlayer: MediaPlayer) {
	val context = LocalContext.current

	Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.Center) {
		Button(
			onClick = {
				viewModel.rollDice()
				try {
					soundMediaPlayer.start()
				} catch (e: Exception) {
					e.printStackTrace()
				}
			},
			enabled = viewModel.rollsLeft > 0,
			colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF11A018))
		) {
			Text("Throw Dice")
		}
	}

	Button(
		onClick = {
			val diceValues = viewModel.diceList.map { it.value }.toIntArray()
			val currentPlayer = viewModel.players.getOrNull(viewModel.currentPlayerIndex)
			val availableCategories = currentPlayer?.let { player ->
				viewModel.availableCategories(player).map { it.name }.toTypedArray()
			} ?: emptyArray()

			val intent = Intent(context, PossibleScoresActivity::class.java).apply {
				putExtra(PossibleScoresActivity.EXTRA_DICE_VALUES, diceValues)
				putExtra(PossibleScoresActivity.EXTRA_CURRENT_PLAYER, currentPlayer?.name ?: "Player")
				putExtra(PossibleScoresActivity.EXTRA_AVAILABLE_CATEGORIES, availableCategories)
			}
			(context as Activity).startActivityForResult(intent, GameActivity.REQUEST_CATEGORY)
		},
		colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B67FF)),
		enabled = viewModel.rollsLeft != 3
	) {
		Text("Check Possible Scores")
	}

}

@Composable
fun ScoreBoard(viewModel: GameViewModel) {
	Column(modifier = Modifier.padding(top = 12.dp)) {
		Text("Current Score:", fontSize = 18.sp)
		viewModel.players.forEachIndexed { idx, p ->
			Text("${p.name}: ${p.score} points")
		}
	}
}
