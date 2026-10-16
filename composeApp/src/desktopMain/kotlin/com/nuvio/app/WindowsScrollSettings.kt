@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package com.nuvio.app

import androidx.compose.foundation.gestures.LocalScrollConfig
import androidx.compose.foundation.gestures.ScrollConfig
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.awt.awtEventOrNull
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import java.awt.event.MouseWheelEvent
import kotlin.math.abs

private const val DefaultWindowsScrollScale = 0.45f

internal fun windowsScrollScale(value: String?): Float =
    value?.toFloatOrNull()?.takeIf { it.isFinite() && it in 0.1f..2f } ?: DefaultWindowsScrollScale

internal fun isPrecisionWheelRotation(rotation: Double, wholeRotation: Int): Boolean =
    rotation.isFinite() && abs(rotation - wholeRotation.toDouble()) > 0.001

private fun platformWheelDelta(config: ScrollConfig, density: Density, event: PointerEvent, bounds: IntSize): Offset =
    with(config) { density.calculateMouseWheelScroll(event, bounds) }

// Keep this adapter isolated: Compose's desktop scroll policy is an internal
// API. The port's compilation/tests must pass before a new Compose version ships.
internal class WindowsScrollConfig(
    private val platform: ScrollConfig,
    private val scale: Float,
) : ScrollConfig by platform {
    override fun isPreciseWheelScroll(event: PointerEvent): Boolean {
        if (event.type == PointerEventType.PanMove) return true
        val wheel = event.awtEventOrNull as? MouseWheelEvent
        return (wheel != null && isPrecisionWheelRotation(wheel.preciseWheelRotation, wheel.wheelRotation)) ||
            platform.isPreciseWheelScroll(event)
    }

    override fun Density.calculateMouseWheelScroll(event: PointerEvent, bounds: IntSize): Offset {
        val offset = platformWheelDelta(platform, this, event, bounds)
        val wheel = event.awtEventOrNull as? MouseWheelEvent
        // Respect the user's explicit OS "one screen at a time" preference.
        return if (wheel?.scrollType == MouseWheelEvent.WHEEL_BLOCK_SCROLL) offset else offset * scale
    }
}

@Composable
internal fun DesktopScrollSettings(content: @Composable () -> Unit) {
    val platform = LocalScrollConfig.current
    val config = remember(platform) {
        if (System.getProperty("os.name", "").startsWith("Windows", ignoreCase = true)) {
            WindowsScrollConfig(platform, windowsScrollScale(System.getProperty("nuvio.desktop.scrollScale")))
        } else {
            platform
        }
    }
    CompositionLocalProvider(LocalScrollConfig provides config, content = content)
}
