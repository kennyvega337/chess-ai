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
        // Wooden impact duration (shorter for a "tap" feel)
        val durationSec = when (pieceType) {
            PieceType.PAWN -> 0.08
            PieceType.KNIGHT, PieceType.BISHOP -> 0.10
            PieceType.ROOK, PieceType.QUEEN -> 0.12
            PieceType.KING -> 0.14
        }
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / numSamples
            
            // Fast exponential decay for percussive wood sound
            val decay = Math.exp(-progress * 8.0)
            
            // Wood resonance frequencies (simulating wooden board body)
            // Primary resonance ~180-300Hz, higher overtones for "hardness"
            val baseFreq = when (pieceType) {
                PieceType.PAWN -> 280.0
                PieceType.KNIGHT, PieceType.BISHOP -> 240.0
                else -> 200.0 // Heavier pieces have lower thud
            }
            
            // Mix multiple sine waves for a complex wooden tone
            var wave = sin(2.0 * PI * baseFreq * t) 
            wave += 0.5 * sin(2.0 * PI * (baseFreq * 2.2) * t)
            wave += 0.25 * sin(2.0 * PI * (baseFreq * 4.1) * t)
            
            // Apply decay
            wave *= decay

            // Initial impact "click" (high frequency white noise transient)
            if (i < (SAMPLE_RATE * 0.015).toInt()) {
                val impactNoise = (Math.random() * 2.0 - 1.0) * (1.0 - i / (SAMPLE_RATE * 0.015))
                wave = wave * 0.4 + impactNoise * 0.6
            }

            // Capture enhancement: makes the "click" louder and adds more noise
            if (isCapture) {
                if (i < (SAMPLE_RATE * 0.03).toInt()) {
                    wave += (Math.random() * 2.0 - 1.0) * 0.5 * (1.0 - i / (SAMPLE_RATE * 0.03))
                }
            }

            // Scale to 16-bit PCM amplitude
            val amp = (wave * 24000.0).toInt().coerceIn(-32000, 32000)
            samples[i] = amp.toShort()
        }

        // Check warning suffix tone (Subtle double wood tap if king is in check)
        if (isCheck) {
            val pauseSamples = (SAMPLE_RATE * 0.05).toInt()
            val secondTap = generatePieceMoveSamples(pieceType, false, false)
            val combined = ShortArray(samples.size + pauseSamples + secondTap.size)
            System.arraycopy(samples, 0, combined, 0, samples.size)
            // Silence in between
            System.arraycopy(secondTap, 0, combined, samples.size + pauseSamples, secondTap.size)
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
