package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance, low-latency SoundPool manager for tactile vinyl crate flip SFX.
 * Generates and loads an acoustic "plastic clack / vinyl sleeve flip" sound sample with zero external dependencies.
 */
class CrateSoundPoolManager(private val context: Context) {
    private var soundPool: SoundPool? = null
    private var soundId: Int = -1
    private var isLoaded: Boolean = false

    init {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build()

            soundPool?.setOnLoadCompleteListener { _, _, status ->
                isLoaded = (status == 0)
            }

            // Generate crisp acoustic "vinyl sleeve clack" WAV in cache
            val sfxFile = File(context.cacheDir, "vinyl_crate_flip.wav")
            if (!sfxFile.exists() || sfxFile.length() == 0L) {
                generateCrateFlipWav(sfxFile)
            }

            soundId = soundPool?.load(sfxFile.absolutePath, 1) ?: -1
        } catch (e: Exception) {
            Log.e("CrateSoundPoolManager", "Error initializing SoundPool", e)
        }
    }

    /**
     * Plays the physical crate flip sound with slight pitch variance for realism
     */
    fun playFlipSound(volume: Float = 0.65f) {
        if (soundPool != null && soundId != -1) {
            // Subtle pitch modulation (0.95x - 1.05x) mimics physical irregularity
            val pitch = 0.95f + (Math.random().toFloat() * 0.12f)
            soundPool?.play(soundId, volume, volume, 1, 0, pitch)
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }

    /**
     * Generates a 16-bit PCM 44.1kHz mono WAV with realistic vinyl jacket / jewel case clack resonance
     */
    private fun generateCrateFlipWav(file: File) {
        val sampleRate = 44100
        val durationSec = 0.075f // 75ms snappy mechanical clack
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate.toFloat()
            // High frequency initial transient click + woody body resonance (850Hz & 220Hz decay)
            val envelope = exp(-t * 85.0).toFloat()
            val transientNoise = ((Math.random() * 2.0 - 1.0) * exp(-t * 220.0)).toFloat()
            val woodResonance = (sin(2.0 * Math.PI * 720.0 * t) * 0.6 + sin(2.0 * Math.PI * 240.0 * t) * 0.4).toFloat()
            
            val sample = (envelope * (woodResonance * 0.7f + transientNoise * 0.3f))
                .coerceIn(-1.0f, 1.0f)

            buffer[i] = (sample * 32767).toInt().toShort()
        }

        FileOutputStream(file).use { fos ->
            val byteBuffer = ByteBuffer.allocate(44 + numSamples * 2).order(ByteOrder.LITTLE_ENDIAN)
            // RIFF header
            byteBuffer.put("RIFF".toByteArray())
            byteBuffer.putInt(36 + numSamples * 2)
            byteBuffer.put("WAVE".toByteArray())
            // fmt chunk
            byteBuffer.put("fmt ".toByteArray())
            byteBuffer.putInt(16) // Subchunk1Size
            byteBuffer.putShort(1.toShort()) // AudioFormat (PCM)
            byteBuffer.putShort(1.toShort()) // NumChannels (Mono)
            byteBuffer.putInt(sampleRate)
            byteBuffer.putInt(sampleRate * 2) // ByteRate
            byteBuffer.putShort(2.toShort()) // BlockAlign
            byteBuffer.putShort(16.toShort()) // BitsPerSample
            // data chunk
            byteBuffer.put("data".toByteArray())
            byteBuffer.putInt(numSamples * 2)

            for (s in buffer) {
                byteBuffer.putShort(s)
            }

            fos.write(byteBuffer.array())
        }
    }
}
