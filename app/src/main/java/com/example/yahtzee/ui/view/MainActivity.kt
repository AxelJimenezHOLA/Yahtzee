package com.example.yahtzee.ui.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yahtzee.R

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			TitleScreen()
		}
	}
}

@Composable
fun ChangeActivityButton(
	text: String,
	cls : Class<*>,
	modifier : Modifier,
	colorHex : Long,
	fontSize: TextUnit = TextUnit.Unspecified
) {
	val context = LocalContext.current
	Button(
		onClick = {
			val intent = Intent(context, cls)
			context.startActivity(intent)
		},
		modifier = modifier,
		colors = ButtonDefaults.buttonColors(containerColor = Color(colorHex)),
	) { Text(text = text, fontSize = fontSize) }
}

@Composable
fun ActionsColumn() {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {

		ChangeActivityButton(
			text = "Start Game",
			cls = PlayerSetupActivity::class.java,
			modifier = Modifier.fillMaxWidth(0.5f),
			colorHex = 0xFFFB7833,
			fontSize = 28.sp
		)

		ChangeActivityButton(
			text = "View Top Scores",
			cls = HighscoresActivity::class.java,
			modifier = Modifier.padding(6.dp),
			colorHex = 0xFFFB3333
		)
	}
}

@Composable
fun VerticalTitle() {
	Column(
		modifier = Modifier.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Image(
			painter = painterResource(id = R.drawable.dice_logo),
			contentDescription = "Image of two dice",
			modifier = Modifier.size(260.dp)
		)
		Text(
			text = "Yahtzee",
			modifier = Modifier.padding(18.dp),
			fontSize = 64.sp,
			fontWeight = FontWeight.ExtraBold,
			color = Color(0xFFA54F21)
		)
		ActionsColumn()
	}
}

@Composable
fun HorizontalTitle() {
	Row(
		modifier = Modifier.fillMaxSize(),
		horizontalArrangement = Arrangement.SpaceEvenly,
		verticalAlignment = Alignment.CenterVertically
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Image(
				painter = painterResource(R.drawable.dice_logo),
				contentDescription = "Image of two dice",
				modifier = Modifier.size(180.dp)
			)
			Text(
				text = "Yahtzee",
				modifier = Modifier.padding(16.dp),
				fontSize = 50.sp,
				fontWeight = FontWeight.ExtraBold,
				color = Color(0xFFA54F21)
			)
		}

		ActionsColumn()
	}
}

@Composable
fun TitleScreen() {
	val configuration = LocalConfiguration.current

	when (configuration.orientation) {
		Configuration.ORIENTATION_LANDSCAPE -> HorizontalTitle()
		else -> VerticalTitle()
	}
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
	TitleScreen()
}