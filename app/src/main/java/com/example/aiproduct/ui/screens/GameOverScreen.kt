package com.example.aiproduct.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameOverScreen(score: Int, onPlayAgain: () -> Unit, onHome: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "gameOver")
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("emoji_dodge_prefs", android.content.Context.MODE_PRIVATE) }
    val highScore by remember { mutableIntStateOf(prefs.getInt("high_score", 0)) }
    val titlePulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titlePulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF020617), Color(0xFF111827), Color(0xFF1E293B))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "GAME OVER",
                style = TextStyle(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFF87171), Color(0xFFFBBF24), Color(0xFFFB7185))
                    ),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    shadow = Shadow(color = Color(0xFFF97316), blurRadius = 18f)
                ),
                modifier = Modifier.graphicsLayer {
                    scaleX = titlePulse
                    scaleY = titlePulse
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Score: $score",
                fontSize = 20.sp,
                color = Color(0xFFCBD5F5),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "High Score: $highScore",
                style = TextStyle(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF38BDF8), Color(0xFFA855F7))
                    ),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onPlayAgain,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(12.dp))
            ) {
                Text(text = "PLAY AGAIN", color = Color(0xFF0B1020), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onHome,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
            ) {
                Text(text = "HOME", color = Color(0xFFE2E8F0))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Tip: Keep moving as speed ramps up",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )
        }
    }
}
