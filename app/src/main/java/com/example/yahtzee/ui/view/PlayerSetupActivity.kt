package com.example.yahtzee.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp

class PlayerSetupActivity : ComponentActivity() {

	companion object {
		const val EXTRA_PLAYERS = "extra_players"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			PlayerSetupScreen(onBack = { finish() })
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSetupScreen(onBack: () -> Unit) {
	val context = LocalContext.current
	var playerName by remember { mutableStateOf("") }
	val players = remember { mutableStateListOf<String>() }

	Scaffold(
		topBar = {
			TopAppBar(
				colors = topAppBarColors(
					containerColor = Color(0xFFFB7833),
					titleContentColor = Color(0xFFFFFFFF),
				),
				title = {
					Text("Setup Players")
				}
			)
		},
		floatingActionButton = {
			FloatingActionButton(onClick = onBack) {
				Icon(Icons.Default.ArrowBack, contentDescription = "Back")
			}
		}
	) { innerPadding ->
		Column(
			modifier = Modifier.padding(innerPadding),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			OutlinedTextField(
				value = playerName,
				onValueChange = { playerName = it },
				label = { Text("Player Name") },
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp)
			)

			Row {
				Button(
					onClick = {
						val trimmed = playerName.trim()
						if (trimmed.isNotEmpty() && !players.contains(trimmed)) {
							players.add(trimmed)
							playerName = ""
						}
					},
					modifier = Modifier.padding(8.dp),
					colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB7833))
				) { Text("Add Player") }

				Button(
					onClick = {
						val intent = Intent(context, GameActivity::class.java)
						intent.putExtra(PlayerSetupActivity.EXTRA_PLAYERS, players.toTypedArray())
						context.startActivity(intent)
					},
					modifier = Modifier.padding(8.dp),
					enabled = players.isNotEmpty(),
					colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF11A018))
				) { Text("Start Game") }
			}

			Text(
				text = "Current Players:",
				modifier = Modifier.padding(start = 4.dp, top = 6.dp),
				fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
				fontSize = 20.sp
			)
			if (players.isNotEmpty()) {
				LazyColumn {
					itemsIndexed(players) { index, name ->
						Text(
							text = "${index + 1}. ${name}",
							modifier = Modifier.padding(6.dp),
							fontSize = 16.sp
						)
					}
				}
			} else {
				Text("There are no players yet...")
			}
		}
	}
}
