package com.example.chess.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class SparkParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val size: Float,
    var alpha: Float = 1.0f,
    val decay: Float = Random.nextFloat() * 0.015f + 0.012f
)

private data class ConfettiParticle(
    var x: Float,
    var y: Float,
    var vy: Float,
    var vx: Float,
    var rotation: Float,
    val color: Color,
    val width: Float,
    val height: Float
)

private val FireworkColors = listOf(
    ColorGoldBright,
    ColorCrimsonSoft,
    ColorEmeraldDeep,
    ColorRoyalBlueAlt,
    ColorRoyalPurple,
    ColorGoldAmber,
    ColorPinkBright,
    ColorRoyalCyan,
    Color.White
)

@Composable
fun FireworksOverlay(
    modifier: Modifier = Modifier
) {
    val sparks = remember { mutableStateListOf<SparkParticle>() }
    val confettis = remember { mutableStateListOf<ConfettiParticle>() }

    // Animation frame loop
    LaunchedEffect(Unit) {
        var lastSpawnTime = 0L

        while (true) {
            withFrameNanos { frameNanos ->
                val timeMs = frameNanos / 1_000_000L

                // Spawn new firework explosion every 350ms
                if (timeMs - lastSpawnTime > 350) {
                    lastSpawnTime = timeMs

                    val burstX = Random.nextFloat() * 800f + 100f
                    val burstY = Random.nextFloat() * 600f + 150f
                    val burstColor = FireworkColors.random()
                    val burstColorSecondary = FireworkColors.random()

                    // Spawn 45 spark particles per explosion
                    for (i in 0..45) {
                        val angle = Random.nextFloat() * 2f * PI.toFloat()
                        val speed = Random.nextFloat() * 12f + 3f
                        val particleColor = if (Random.nextBoolean()) burstColor else burstColorSecondary

                        sparks.add(
                            SparkParticle(
                                x = burstX,
                                y = burstY,
                                vx = cos(angle) * speed,
                                vy = sin(angle) * speed,
                                color = particleColor,
                                size = Random.nextFloat() * 6f + 3f
                            )
                        )
                    }

                    // Spawn confetti flakes falling from top
                    for (i in 0..8) {
                        confettis.add(
                            ConfettiParticle(
                                x = Random.nextFloat() * 1000f,
                                y = -20f,
                                vy = Random.nextFloat() * 4f + 3f,
                                vx = (Random.nextFloat() - 0.5f) * 2f,
                                rotation = Random.nextFloat() * 360f,
                                color = FireworkColors.random(),
                                width = Random.nextFloat() * 12f + 8f,
                                height = Random.nextFloat() * 8f + 6f
                            )
                        )
                    }
                }

                // Update spark physics
                val sparkIterator = sparks.iterator()
                while (sparkIterator.hasNext()) {
                    val s = sparkIterator.next()
                    s.x += s.vx
                    s.y += s.vy
                    s.vy += 0.22f // Gravity
                    s.vx *= 0.96f // Air resistance
                    s.vy *= 0.96f
                    s.alpha -= s.decay

                    if (s.alpha <= 0f) {
                        sparkIterator.remove()
                    }
                }

                // Update confetti physics
                val confettiIterator = confettis.iterator()
                while (confettiIterator.hasNext()) {
                    val c = confettiIterator.next()
                    c.y += c.vy
                    c.x += c.vx + sin(c.y * 0.02f) * 1.5f
                    c.rotation += 4f

                    if (c.y > 2200f) {
                        confettiIterator.remove()
                    }
                }
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        // Draw Sparks
        for (s in sparks) {
            drawCircle(
                color = s.color.copy(alpha = s.alpha.coerceIn(0f, 1f)),
                radius = s.size,
                center = Offset(s.x, s.y)
            )
            // Spark trail tail
            drawLine(
                color = s.color.copy(alpha = (s.alpha * 0.5f).coerceIn(0f, 1f)),
                start = Offset(s.x, s.y),
                end = Offset(s.x - s.vx * 2f, s.y - s.vy * 2f),
                strokeWidth = s.size * 0.8f,
                cap = StrokeCap.Round
            )
        }

        // Draw Confetti Flakes
        for (c in confettis) {
            drawRect(
                color = c.color,
                topLeft = Offset(c.x, c.y),
                size = androidx.compose.ui.geometry.Size(c.width, c.height)
            )
        }
    }
}
