package com.example.aiproduct.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

private data class Obstacle(
    val id: Int,
    val x: Float,
    val y: Float,
    val speed: Float,
    val emoji: String,
    val size: Float
)

@Composable
fun GameScreen(onGameOver: (Int) -> Unit, onExit: () -> Unit) {
    val onGameOverState by rememberUpdatedState(onGameOver)
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("emoji_dodge_prefs", android.content.Context.MODE_PRIVATE) }
    var highScore by remember { mutableIntStateOf(prefs.getInt("high_score", 0)) }
    val emojis = remember {
        listOf("😈", "👾", "💣", "🧨", "🔥", "⚡", "☠️", "🦠", "💥")
    }

    val shake = remember { Animatable(0f) }
    val flash = remember { Animatable(0f) }
    val hitScale = remember { Animatable(0.6f) }
    val hitAlpha = remember { Animatable(0f) }
    val highScoreScale = remember { Animatable(1f) }
    val highScoreGlow = remember { Animatable(0f) }
    val scoreScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    var showExitConfirm by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var hitHandled by remember { mutableStateOf(false) }
    var hitPosition by remember { mutableStateOf<Offset?>(null) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0B1020), Color(0xFF111827), Color(0xFF1E293B))
                )
            )
            .graphicsLayer { translationX = shake.value }
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val playerSizePx = 52f
        val obstacleSizePx = 40f
        val playerY = heightPx - playerSizePx - 40f

        var playerX by remember(widthPx) { mutableStateOf(widthPx / 2f) }
        var obstacles by remember { mutableStateOf(emptyList<Obstacle>()) }
        var isRunning by remember { mutableStateOf(true) }
        var elapsedMs by remember { mutableLongStateOf(0L) }
        var score by remember { mutableIntStateOf(0) }
        var lastScore by remember { mutableIntStateOf(0) }

        LaunchedEffect(isRunning, widthPx, heightPx, isPaused) {
            if (!isRunning) return@LaunchedEffect
            var lastFrameNanos = 0L
            while (isRunning) {
                if (isPaused) {
                    lastFrameNanos = 0L
                    delay(16)
                    continue
                }
                val frameTimeNanos = androidx.compose.runtime.withFrameNanos { it }
                if (lastFrameNanos == 0L) {
                    lastFrameNanos = frameTimeNanos
                    continue
                }
                val dt = (frameTimeNanos - lastFrameNanos) / 1_000_000_000f
                lastFrameNanos = frameTimeNanos
                elapsedMs += (dt * 1000).toLong()
                score = (elapsedMs / 1000L).toInt()

                obstacles = obstacles
                    .map { obstacle -> obstacle.copy(y = obstacle.y + obstacle.speed * dt) }
                    .filter { obstacle -> obstacle.y < heightPx + obstacle.size }

                val playerLeft = playerX - playerSizePx / 2f
                val playerRight = playerX + playerSizePx / 2f
                val playerTop = playerY
                val playerBottom = playerY + playerSizePx

                val hit = obstacles.any { obstacle ->
                    val oLeft = obstacle.x - obstacle.size / 2f
                    val oRight = obstacle.x + obstacle.size / 2f
                    val oTop = obstacle.y
                    val oBottom = obstacle.y + obstacle.size
                    oRight > playerLeft && oLeft < playerRight && oBottom > playerTop && oTop < playerBottom
                }

                if (hit && !hitHandled) {
                    hitHandled = true
                    isRunning = false
                    hitPosition = Offset(playerX, playerY + playerSizePx / 2f)
                    scope.launch {
                        flash.snapTo(0.7f)
                        hitAlpha.snapTo(1f)
                        hitScale.snapTo(0.6f)
                        shake.animateTo(
                            targetValue = 0f,
                            animationSpec = keyframes {
                                durationMillis = 320
                                0f at 0
                                12f at 50
                                -12f at 110
                                8f at 180
                                -6f at 240
                                0f at 320
                            }
                        )
                    }
                    scope.launch {
                        hitScale.animateTo(
                            targetValue = 1.7f,
                            animationSpec = tween(320)
                        )
                    }
                    scope.launch {
                        hitAlpha.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(320)
                        )
                    }
                    scope.launch {
                        flash.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(380)
                        )
                        delay(200)
                        onGameOverState(score)
                    }
                }
            }
        }

        LaunchedEffect(isRunning, widthPx, isPaused) {
            if (!isRunning) return@LaunchedEffect
            var nextId = 0
            while (isRunning) {
                if (isPaused) {
                    delay(120)
                    continue
                }
                val difficulty = (elapsedMs / 4_000L).toInt()
                val intervalMs = (820 - difficulty * 70).coerceAtLeast(260)
                val speed = (290 + difficulty * 32).toFloat()
                val x = Random.nextFloat() * (widthPx - obstacleSizePx) + obstacleSizePx / 2f
                val emoji = emojis.random()

                obstacles = obstacles + Obstacle(
                    id = nextId++,
                    x = x,
                    y = -obstacleSizePx,
                    speed = speed,
                    emoji = emoji,
                    size = obstacleSizePx
                )
                delay(intervalMs.toLong())
            }
        }

        LaunchedEffect(score) {
            if (score > lastScore) {
                lastScore = score
                scoreScale.snapTo(1.15f)
                scoreScale.animateTo(1f, animationSpec = tween(140))
            }
            if (score > highScore) {
                highScore = score
                prefs.edit().putInt("high_score", highScore).apply()
                scope.launch {
                    highScoreScale.snapTo(1f)
                    highScoreGlow.snapTo(0.9f)
                    highScoreScale.animateTo(
                        targetValue = 1.2f,
                        animationSpec = tween(200)
                    )
                    highScoreScale.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(220)
                    )
                    highScoreGlow.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(700)
                    )
                }
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            repeat(40) { index ->
                val seedX = (index * 97 % 100) / 100f
                val seedY = (index * 57 % 100) / 100f
                drawCircle(
                    color = Color.White.copy(alpha = 0.2f),
                    radius = 2.2f,
                    center = Offset(size.width * seedX, size.height * seedY)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isPaused) {
                    if (!isPaused) {
                        detectTapGestures { offset ->
                            val clamped = offset.x.coerceIn(playerSizePx / 2f, widthPx - playerSizePx / 2f)
                            playerX = clamped
                        }
                    }
                }
                .pointerInput(isPaused) {
                    if (!isPaused) {
                        detectDragGestures { _, dragAmount ->
                            val clamped = (playerX + dragAmount.x)
                                .coerceIn(playerSizePx / 2f, widthPx - playerSizePx / 2f)
                            playerX = clamped
                        }
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "SCORE",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                )
                Text(
                    text = score.toString(),
                    style = TextStyle(
                        color = Color(0xFFF8FAFC),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        shadow = Shadow(color = Color(0xFF0EA5E9), blurRadius = 12f)
                    ),
                    modifier = Modifier.graphicsLayer {
                        scaleX = scoreScale.value
                        scaleY = scoreScale.value
                    }
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "HIGH ${highScore}",
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF38BDF8), Color(0xFFA855F7))
                        ),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.graphicsLayer {
                        scaleX = highScoreScale.value
                        scaleY = highScoreScale.value
                    }
                )
                if (highScoreGlow.value > 0f) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "NEW HIGH SCORE!",
                        color = Color(0xFFFFE082).copy(alpha = highScoreGlow.value),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isPaused) "PAUSED" else "",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    letterSpacing = 2.sp
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { isPaused = !isPaused },
                    modifier = Modifier
                        .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                        contentDescription = if (isPaused) "Play" else "Pause",
                        tint = Color(0xFFE2E8F0)
                    )
                }
                IconButton(
                    onClick = { showExitConfirm = true },
                    modifier = Modifier
                        .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        contentDescription = "Exit",
                        tint = Color(0xFFE2E8F0)
                    )
                }
            }

            Text(
                text = "🙂",
                fontSize = 36.sp,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (playerX - playerSizePx / 2f).roundToInt(),
                            playerY.roundToInt()
                        )
                    }
            )

            obstacles.forEach { obstacle ->
                Text(
                    text = obstacle.emoji,
                    fontSize = 28.sp,
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (obstacle.x - obstacle.size / 2f).roundToInt(),
                                obstacle.y.roundToInt()
                            )
                        }
                )
            }

            hitPosition?.let { center ->
                Text(
                    text = "💥",
                    fontSize = 46.sp,
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (center.x - 26f).roundToInt(),
                                (center.y - 26f).roundToInt()
                            )
                        }
                        .graphicsLayer {
                            scaleX = hitScale.value
                            scaleY = hitScale.value
                            alpha = hitAlpha.value
                        }
                )
            }
        }

        if (flash.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFF3B30).copy(alpha = flash.value))
            )
        }

        if (isPaused && isRunning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0B1020).copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "PAUSED",
                    color = Color(0xFFF8FAFC),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }

        if (showExitConfirm) {
            AlertDialog(
                onDismissRequest = { showExitConfirm = false },
                title = { Text(text = "Exit Game?") },
                text = { Text(text = "Your current run will end.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showExitConfirm = false
                            onExit()
                        }
                    ) {
                        Text(text = "Exit")
                    }
                },
                dismissButton = {
                    Button(onClick = { showExitConfirm = false }) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    }
}
