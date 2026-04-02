package com.example.aiproduct

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aiproduct.ui.screens.GameOverScreen
import com.example.aiproduct.ui.screens.GameScreen
import com.example.aiproduct.ui.screens.HomeScreen
import com.example.aiproduct.ui.theme.AIProductTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIProductTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    EmojiDodgeApp()
                }
            }
        }
    }
}

@Serializable
sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object Game : Screen

    @Serializable
    data object GameOver : Screen
}

@Composable
fun EmojiDodgeApp() {
    val navController = rememberNavController()
    var lastScore by remember { mutableIntStateOf(0) }

    Scaffold() { it->

        NavHost(navController = navController, startDestination = Screen.Home, modifier = Modifier.padding(it)) {
            composable<Screen.Home> {
                HomeScreen(
                    onStart = { navController.navigate(Screen.Game) }
                )
            }
            composable<Screen.Game> {
                GameScreen(
                    onGameOver = { score ->
                        lastScore = score
                        navController.navigate(Screen.GameOver)
                    },
                    onExit = { navController.popBackStack(Screen.Home, inclusive = false) }
                )
            }
            composable<Screen.GameOver> {
                GameOverScreen(
                    score = lastScore,
                    onPlayAgain = {
                        navController.navigate(Screen.Game) {
                            popUpTo(Screen.Home) { inclusive = false }
                        }
                    },
                    onHome = { navController.popBackStack(Screen.Home, inclusive = false) }
                )
            }
        }
    }
}
