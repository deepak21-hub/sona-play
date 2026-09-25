package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.TrackCatalog
import com.example.model.AudioEffectSettings
import com.example.model.AudioFormat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Sona", appName)
  }

  @Test
  fun `catalog contains city pop classics`() {
    val tracks = TrackCatalog.sampleTracks
    assertTrue(tracks.isNotEmpty())
    val plasticLove = tracks.find { it.id == "track_plastic_love" }
    assertEquals("Plastic Love", plasticLove?.title)
    assertEquals("Mariya Takeuchi", plasticLove?.artist)
    assertTrue(plasticLove?.format?.isLossless == true)
  }

  @Test
  fun `audio effect settings initialize with correct vinyl defaults`() {
    val effects = AudioEffectSettings()
    assertTrue(effects.vinylCrackleEnabled)
    assertTrue(effects.analogWarmth)
    assertFalse(effects.is45RpmMode)
    assertEquals(1.0f, effects.playbackSpeedRpm)
  }
}
