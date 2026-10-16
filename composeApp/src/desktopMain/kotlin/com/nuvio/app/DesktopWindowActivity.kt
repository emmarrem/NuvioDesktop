package com.nuvio.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.nuvio.app.core.ui.LocalWindowActive
import java.awt.Window
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

@Composable
internal fun DesktopWindowActivity(
    window: Window,
    minimized: Boolean,
    content: @Composable () -> Unit,
) {
    var focused by remember(window) { mutableStateOf(window.isFocused) }
    DisposableEffect(window) {
        val listener = object : WindowAdapter() {
            override fun windowGainedFocus(event: WindowEvent) { focused = true }
            override fun windowLostFocus(event: WindowEvent) { focused = false }
        }
        window.addWindowFocusListener(listener)
        onDispose { window.removeWindowFocusListener(listener) }
    }
    // Screen effects/decorative animations may pause; the native player and
    // download services stay mounted and continue running in the background.
    CompositionLocalProvider(LocalWindowActive provides (focused && !minimized), content = content)
}
