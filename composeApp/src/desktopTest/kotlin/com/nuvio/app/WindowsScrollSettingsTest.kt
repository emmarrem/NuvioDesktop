@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package com.nuvio.app

import androidx.compose.foundation.gestures.ScrollConfig
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WindowsScrollSettingsTest {
    @Test fun scaleKeepsDirectionAndFractionalInput() {
        val platform = object : ScrollConfig {
            override val isSmoothScrollingEnabled = false
            override fun Density.calculateMouseWheelScroll(event: PointerEvent, bounds: IntSize) = Offset(0.5f, -200f)
        }
        val config = WindowsScrollConfig(platform, 0.45f)
        val result = with(config) { with(Density(1f)) { calculateMouseWheelScroll(PointerEvent(emptyList()), IntSize(800, 600)) } }
        assertEquals(Offset(0.225f, -90f), result)
        assertFalse(config.isSmoothScrollingEnabled)
    }

    @Test fun precisionDetectionDoesNotTreatWholeWheelNotchesAsTouchpad() {
        assertTrue(isPrecisionWheelRotation(0.125, 0))
        assertTrue(isPrecisionWheelRotation(-1.5, -1))
        assertFalse(isPrecisionWheelRotation(1.0, 1))
        assertFalse(isPrecisionWheelRotation(Double.NaN, 0))
    }

    @Test fun invalidOverridesUseSafeDefault() {
        listOf(null, "bad", "NaN", "Infinity", "0", "-1", "9").forEach { assertEquals(0.45f, windowsScrollScale(it)) }
        assertEquals(0.7f, windowsScrollScale("0.7"))
    }
}
