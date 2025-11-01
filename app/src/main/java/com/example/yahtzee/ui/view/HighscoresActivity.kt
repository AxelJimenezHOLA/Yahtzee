package com.example.yahtzee.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.yahtzee.ui.viewmodel.HighscoresViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HighscoresActivity : ComponentActivity() {

	private val highscoresViewModel: HighscoresViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			ScoreScreenContent(
				viewModel = highscoresViewModel,
				onBack = { finish() },
				onClearResults = { highscoresViewModel.deleteHighscores(); finish() }
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScoreScreenContent(
	viewModel: HighscoresViewModel,
	onBack: () -> Unit,
	onClearResults: () -> Unit
) {
	val count by viewModel.count.collectAsState()
	viewModel.getCount()

	Scaffold(
		topBar = {
			TopAppBar(
				colors = topAppBarColors(
					containerColor = Color(0xFFFB3333),
					titleContentColor = Color(0xFFFFFFFF),
				),
				title = {
					Text("Highscores")
				}
			)
		},
		bottomBar = {
			BottomAppBar {
				Row(
					horizontalArrangement = Arrangement.Center,
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically,
				) {
					Button(
						onClick = onClearResults,
						colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB3333)),
					) { Text("Clear Highscores") }

				}
			}
		},
		floatingActionButton = {
			FloatingActionButton(onClick = onBack) {
				Icon(Icons.Default.ArrowBack, contentDescription = "Back")
			}
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

			if (count != 0) {
				HighscoresScreen(viewModel, Modifier.weight(1f).padding(8.dp))
			} else {
				CircularProgressIndicator()
			}
		}
	}
}

@Composable
fun HighscoresScreen(viewModel: HighscoresViewModel, modifier: Modifier = Modifier) {
	val scores by viewModel.highscores.collectAsState()

	LazyColumn(modifier = modifier) {
		itemsIndexed(scores) { index, score ->
			val cardColor = when (index) {
				0 -> Color(0xFFFFDB64) // Oro
				1 -> Color(0xFF9A9A9A) // Plata
				2 -> Color(0xFFFFAD6E) // Bronce
				else -> Color(0xFFFFFFFF) // Normal
			}

			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(4.dp),
				colors = CardDefaults.cardColors(containerColor = cardColor),
				border = BorderStroke(2.dp, Color.Black)
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {

					Column {
						Text(
							text = "🏆 ${score.winnerName}",
							style = MaterialTheme.typography.titleMedium
						)
						Text(
							text = "📅 ${score.date}",
							style = MaterialTheme.typography.bodySmall
						)
					}


					Text(
						text = score.score.toString(),
						style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
					)
				}
			}
		}
	}
}
