package com.nuvio.app.core.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WindowActivityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun loadingAnimationStopsWhenWindowIsInactiveAndResumes() {
        val active = mutableStateOf(true)
        lateinit var frame: State<Float>
        compose.mainClock.autoAdvance = false
        compose.setContent {
            CompositionLocalProvider(LocalWindowActive provides active.value) {
                frame = rememberLoadingIndicatorFrame(true)
            }
        }
        compose.mainClock.advanceTimeBy(500)
        compose.runOnIdle { assertTrue(frame.value > 0f); active.value = false }
        compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeBy(1000)
        compose.runOnIdle { assertEquals(0f, frame.value); active.value = true }
        compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeBy(500)
        compose.runOnIdle { assertTrue(frame.value > 0f) }
    }
}
