package com.example.chess.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.chess.model.PieceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

object SoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 22050

    /**
     * Play synthesized move sound depending on piece type, capture, or check
     */
    fun playMoveSound(pieceType: PieceType, isCapture: Boolean, isCheck: Boolean) {
        scope.launch {
            try {
                val samples = generatePieceMoveSamples(pieceType, isCapture, isCheck)
                playAudioSamples(samples)
            } catch (e: Exception) {
                // Silently fallback if audio output is unavailable
            }
        }
    }

    /**
     * Play celebratory victory fanfare sound
     */
    fun playVictorySound() {
        scope.launch {
            try {
                val samples = generateVictoryFanfareSamples()
                playAudioSamples(samples)
            } catch (e: Exception) {
                // Silently fallback
            }
        }
    }

    private fun playAudioSamples(samples: ShortArray) {
        if (samples.isEmpty()) return
        val bufferSize = samples.size * 2
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(samples, 0, samples.size)
        audioTrack.play()

        // Wait for sound to finish then release
        val durationMs = (samples.size * 1000L / SAMPLE_RATE) + 60L
        Thread.sleep(durationMs)
        try {
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {}
    }

    private fun generatePieceMoveSamples(
        pieceType: PieceType,
        isCapture: Boolean,
        isCheck: Boolean
    ): ShortArray {
        // Base sound duration per piece (70ms to 120ms)
        val numSamples = when (pieceType) {
            PieceType.PAWN -> (SAMPLE_RATE * 0.065).toInt()
            PieceType.KNIGHT -> (SAMPLE_RATE * 0.110).toInt()
            PieceType.BISHOP -> (SAMPLE_RATE * 0.090).toInt()
            PieceType.ROOK -> (SAMPLE_RATE * 0.100).toInt()
            PieceType.QUEEN -> (SAMPLE_RATE * 0.120).toInt()
            PieceType.KING -> (SAMPLE_RATE * 0.130).toInt()
        }

        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / numSamples
            val decay = (1.0 - progress) * (1.0 - progress)

            var wave = 0.0

            when (pieceType) {
                PieceType.PAWN -> {
                    // Soft wood tap (240 Hz)
                    val freq = 240.0
                    wave = sin(2.0 * PI * freq * t) * decay
                    // Add slight noise transient at impact start
                    if (i < 80) wave += (Math.random() - 0.5) * 0.6 * (1.0 - i / 80.0)
                }
                PieceType.KNIGHT -> {
                    // Double clip-clop horse step (260 Hz then 380 Hz)
                    val isSecondStep = progress > 0.45
                    val freq = if (isSecondStep) 380.0 else 260.0
                    val localProgress = if (isSecondStep) (progress - 0.45) / 0.55 else progress / 0.45
                    val stepDecay = (1.0 - localProgress) * (1.0 - localProgress)
                    wave = sin(2.0 * PI * freq * t) * stepDecay
                }
                PieceType.BISHOP -> {
                    // Smooth diagonal pitch glide (280 Hz to 520 Hz)
                    val freq = 280.0 + (240.0 * progress)
                    wave = sin(2.0 * PI * freq * t) * decay
                }
                PieceType.ROOK -> {
                    // Deep solid wood thump (150 Hz)
                    val freq = 150.0
                    wave = (sin(2.0 * PI * freq * t) + 0.4 * sin(2.0 * PI * freq * 0.5 * t)) * decay
                    if (i < 120) wave += (Math.random() - 0.5) * 0.8 * (1.0 - i / 120.0)
                }
                PieceType.QUEEN -> {
                    // Elegant dual-tone chime (523 Hz C5 + 659 Hz E5)
                    wave = (0.6 * sin(2.0 * PI * 523.25 * t) + 0.4 * sin(2.0 * PI * 659.25 * t)) * decay
                }
                PieceType.KING -> {
                    // Noble majestic warmth (220 Hz A3 + 440 Hz A4)
                    wave = (0.7 * sin(2.0 * PI * 220.0 * t) + 0.3 * sin(2.0 * PI * 440.0 * t)) * decay
                }
            }

            // Capture strike enhancement (sharp wood pop)
            if (isCapture) {
                val captureImpact = if (i < 150) (Math.random() - 0.5) * 1.2 * (1.0 - i / 150.0) else 0.0
                wave = wave * 0.7 + captureImpact
            }

            // Scale to 16-bit PCM amplitude
            val amp = (wave * 26000.0).toInt().coerceIn(-32000, 32000)
            samples[i] = amp.toShort()
        }

        // Check warning suffix tone (880 Hz double beep if king is placed in check)
        if (isCheck) {
            val checkBeepSamples = (SAMPLE_RATE * 0.12).toInt()
            val combined = ShortArray(samples.size + checkBeepSamples)
            System.arraycopy(samples, 0, combined, 0, samples.size)

            for (j in 0 until checkBeepSamples) {
                val t = j.toDouble() / SAMPLE_RATE
                val prog = j.toDouble() / checkBeepSamples
                val beepDecay = (1.0 - prog)
                val isSecondBeep = prog > 0.5
                val beepWave = if ((prog in 0.0..0.4) || (prog in 0.5..0.9)) {
                    sin(2.0 * PI * (if (isSecondBeep) 1046.5 else 880.0) * t) * beepDecay
                } else 0.0

                combined[samples.size + j] = (beepWave * 20000.0).toInt().coerceIn(-30000, 30000).toShort()
            }
            return combined
        }

        return samples
    }

    private fun generateVictoryFanfareSamples(): ShortArray {
        // C5 (523.25), E5 (659.25), G5 (783.99), C6 (1046.50) ascending fanfare
        val noteDur = (SAMPLE_RATE * 0.15).toInt()
        val totalSamples = noteDur * 4
        val samples = ShortArray(totalSamples)

        val freqs = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)

        for (n in 0..3) {
            val freq = freqs[n]
            val offset = n * noteDur
            for (i in 0 until noteDur) {
                val t = i.toDouble() / SAMPLE_RATE
                val prog = i.toDouble() / noteDur
                val decay = if (n == 3) (1.0 - prog * 0.5) else (1.0 - prog)
                val wave = (0.7 * sin(2.0 * PI * freq * t) + 0.3 * sin(2.0 * PI * (freq * 2) * t)) * decay
                samples[offset + i] = (wave * 28000.0).toInt().coerceIn(-32000, 32000).toShort()
            }
        }

        return samples
    }
}
