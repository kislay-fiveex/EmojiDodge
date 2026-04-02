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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(onStart: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "home")
    val titlePulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titlePulse"
    )
    val buttonPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF111827), Color(0xFF030712))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "EMOJI DODGE",
                style = TextStyle(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF38BDF8), Color(0xFFA855F7), Color(0xFF22D3EE))
                    ),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    shadow = Shadow(color = Color(0xFF0EA5E9), blurRadius = 18f)
                ),
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = titlePulse
                        scaleY = titlePulse
                    }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Avoid the falling emojis!",
                fontSize = 16.sp,
                color = Color(0xFFCBD5F5),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onStart,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = buttonPulse
                        scaleY = buttonPulse
                    }
                    .shadow(14.dp, RoundedCornerShape(14.dp))
            ) {
                Text(
                    text = "START GAME",
                    color = Color(0xFF0B1020),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Drag or tap to move",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
