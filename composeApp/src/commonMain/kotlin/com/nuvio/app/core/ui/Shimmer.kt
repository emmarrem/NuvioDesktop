package com.nuvio.app.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp

@Stable
private class SkeletonAnimation {
    val progress = Animatable(0f)
    var consumers by mutableIntStateOf(0)
}

private val LocalSkeletonAnimation = staticCompositionLocalOf<SkeletonAnimation?> { null }

@Composable
internal fun SkeletonAnimationProvider(content: @Composable () -> Unit) {
    val animation = remember { SkeletonAnimation() }
    val isActive by remember { derivedStateOf { animation.consumers > 0 } }
    if (isActive) {
        LaunchedEffect(animation) {
            animation.progress.snapTo(0f)
            animation.progress.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing)),
            )
        }
    }
    CompositionLocalProvider(LocalSkeletonAnimation provides animation, content = content)
}

@Composable
internal fun rememberSkeletonProgress(): State<Float> {
    val animation = LocalSkeletonAnimation.current
    SkeletonAnimationConsumer(animation)
    return animation?.progress?.asState() ?: remember { mutableFloatStateOf(0f) }
}

@Composable
private fun SkeletonAnimationConsumer(animation: SkeletonAnimation?) {
    val active = LocalScreenActive.current && LocalWindowActive.current
    DisposableEffect(animation, active) {
        if (animation != null && active) animation.consumers++
        onDispose {
            if (animation != null &…47329 tokens truncated…Color, &borderColor, sizeof(borderColor));
        COLORREF captionColor = RGB(0, 0, 0);
        setDwmWindowAttribute(window, kDwmwaCaptionColor, &captionColor, sizeof(captionColor));
    } else {
        style &= ~(WS_THICKFRAME | WS_MAXIMIZEBOX | WS_MINIMIZEBOX);
        WNDPROC oldProc = reinterpret_cast<WNDPROC>(GetPropW(window, L"NuvioPipOldProc"));
        if (oldProc) {
            SetWindowLongPtrW(window, GWLP_WNDPROC, reinterpret_cast<LONG_PTR>(oldProc));
            RemovePropW(window, L"NuvioPipOldProc");
        }
        RemovePropW(window, L"NuvioPipPlayer");
        RemovePropW(window, L"NuvioPipRatio");
    }
    SetWindowLongPtrW(window, GWL_STYLE, style);
    SetWindowPos(window, nullptr, 0, 0, 0, 0,
        SWP_NOMOVE | SWP_NOSIZE | SWP_NOZORDER | SWP_NOACTIVATE | SWP_FRAMECHANGED);
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_setWindowAspectRatio(
    JNIEnv *, jobject, jlong windowHwnd, jfloat ratio
) {
    HWND window = (HWND)(intptr_t)windowHwnd;
    if (!window || !IsWindow(window)) return;
    if (ratio <= 0.05f) return;
    uintptr_t ratioInt = static_cast<uintptr_t>(std::round(ratio * 10000.0f));
    SetPropW(window, L"NuvioPipRatio", reinterpret_cast<HANDLE>(ratioInt));
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_layoutNativeSubviews(
    JNIEnv *, jobject, jlong handle
) {
    auto player = playerFromHandle(handle);
    if (player) {
        player->layoutNativeSubviewsAsync();
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_reparentSurfaceNative(JNIEnv *, jobject, jlong handle, jlong hostViewPtr) {
    auto player = playerFromHandle(handle);
    if (player) {
        HWND host = (HWND)(intptr_t)hostViewPtr;
        if (host && IsWindow(host)) {
            HWND rootWindow = GetAncestor(host, GA_ROOT);
            if (rootWindow && IsWindow(rootWindow) && GetPropW(rootWindow, L"NuvioPipOldProc")) {
                SetPropW(rootWindow, L"NuvioPipPlayer", reinterpret_cast<HANDLE>(static_cast<intptr_t>(handle)));
            }
        }
        player->reparentSurface(host);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_setPaused(JNIEnv *, jobject, jlong handle, jboolean paused) {
    auto player = playerFromHandle(handle);
    if (player) player->setPaused(paused == JNI_TRUE);
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_seekTo(JNIEnv *, jobject, jlong handle, jlong positionMs) {
    auto player = playerFromHandle(handle);
    if (player) player->seekToMilliseconds(positionMs);
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_seekBy(JNIEnv *, jobject, jlong handle, jlong offsetMs) {
    auto player = playerFromHandle(handle);
    if (player) player->seekByMilliseconds(offsetMs);
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_setSpeed(JNIEnv *, jobject, jlong handle, jfloat speed) {
    auto player = playerFromHandle(handle);
    if (player) player->setSpeed(speed);
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_adjustVolume(JNIEnv *, jobject, jlong handle, jfloat delta) {
    auto player = playerFromHandle(handle);
    if (player) player->adjustVolume(delta);
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_setVolume(JNIEnv *, jobject, jlong handle, jfloat level) {
    auto player = playerFromHandle(handle);
    if (player) player->setVolume(level);
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_durationMs(JNIEnv *, jobject, jlong handle) {
    auto player = playerFromHandle(handle);
    return player ? player->durationMs() : 0;
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_positionMs(JNIEnv *, jobject, jlong handle) {
    auto player = playerFromHandle(handle);
    return player ? player->positionMs() : 0;
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_bufferedPositionMs(JNIEnv *, jobject, jlong handle) {
    auto player = playerFromHandle(handle);
    return player ? player->bufferedPositionMs() : 0;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_isLoading(JNIEnv *, jobject, jlong handle) {
    auto player = playerFromHandle(handle);
    return player && player->isLoading() ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_isEnded(JNIEnv *, jobject, jlong handle) {
    auto player = playerFromHandle(handle);
    return player && player->isEnded() ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_isPaused(JNIEnv *, jobject, jlong handle) {
    auto player = playerFromHandle(handle);
    return !player || player->isPaused() ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jfloat JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_speed(JNIEnv *, jobject, jlong handle) {
    auto player = playerFromHandle(handle);
    return player ? (jfloat)player->speed() : 1.0f;
}

extern "C" JNIEXPORT jfloat JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_volume(JNIEnv *, jobject, jlong handle) {
    auto player = playerFromHandle(handle);
    return player ? (jfloat)player->volume() : 1.0f;
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_setResizeMode(JNIEnv *, jobject, jlong handle, jint mode) {
    auto player = playerFromHandle(handle);
    if (player) player->setResizeMode(mode);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_audioTracksJson(JNIEnv *env, jobject, jlong handle) {
    auto player = playerFromHandle(handle);
    return newJavaStringUtf8(env, player ? player->audioTracksJson() : "[]");
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_subtitleTracksJson(JNIEnv *env, jobject, jlong handle) {
    auto player = playerFromHandle(handle);
    return newJavaStringUtf8(env, player ? player->subtitleTracksJson() : "[]");
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_selectAudioTrack(JNIEnv *, jobject, jlong handle, jint trackId) {
    auto player = playerFromHandle(handle);
    if (player) player->selectAudioTrackId(trackId);
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_selectSubtitleTrack(JNIEnv *, jobject, jlong handle, jint trackId) {
    auto player = playerFromHandle(handle);
    if (player) player->selectSubtitleTrackId(trackId);
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_addSubtitleUrl(JNIEnv *env, jobject, jlong handle, jstring url) {
    auto player = playerFromHandle(handle);
    if (player) player->addSubtitleUrl(jstringToUtf8(env, url));
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_clearExternalSubtitles(JNIEnv *, jobject, jlong handle) {
    auto player = playerFromHandle(handle);
    if (player) player->removeExternalSubtitles();
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_clearExternalSubtitlesAndSelect(JNIEnv *, jobject, jlong handle, jint trackId) {
    auto player = playerFromHandle(handle);
    if (player) player->removeExternalSubtitlesAndSelect(trackId);
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_applyWindowChrome(
    JNIEnv *,
    jobject,
    jlong windowHwnd,
    jboolean darkMode,
    jint captionColorRgb,
    jint borderColorRgb,
    jint textColorRgb
) {
    HWND hwnd = (HWND)(intptr_t)windowHwnd;
    applyDwmWindowChrome(
        hwnd,
        darkMode == JNI_TRUE,
        rgbIntToColorRef(captionColorRgb),
        rgbIntToColorRef(borderColorRgb),
        rgbIntToColorRef(textColorRgb)
    );

    // The window's default class brush is white, so any region Windows erases before Skia
    // repaints it flashes white. Erase to black instead; against the near-black UI it is
    // invisible even if a repaint lags.
    if (hwnd && IsWindow(hwnd)) {
        SetClassLongPtrW(hwnd, GCLP_HBRBACKGROUND, (LONG_PTR)GetStockObject(BLACK_BRUSH));
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_setWindowBorderlessFullscreen(
    JNIEnv *,
    jobject,
    jlong windowHwnd,
    jboolean fullscreen,
    jint x,
    jint y,
    jint width,
    jint height
) {
    setBorderlessFullscreen(
        (HWND)(intptr_t)windowHwnd,
        fullscreen == JNI_TRUE,
        x,
        y,
        width,
        height
    );
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_setSubtitleDelayMs(JNIEnv *, jobject, jlong handle, jint delayMs) {
    auto player = playerFromHandle(handle);
    if (player) player->setSubtitleDelayMs(delayMs);
}

extern "C" JNIEXPORT void JNICALL
Java_com_nuvio_app_features_player_desktop_NativePlayerBridge_applySubtitleStyle(
    JNIEnv *env,
    jobject,
    jlong handle,
    jstring textColor,
    jstring backgroundColor,
    jstring outlineColor,
    jfloat outlineSize,
    jboolean bold,
    jfloat fontSize,
    jint subPos,
    jboolean useLibass,
    jboolean stripSdh
) {
    auto player = playerFromHandle(handle);
    if (!player) return;
    player->applySubtitleStyle(
        jstringToUtf8(env, textColor),
        jstringToUtf8(env, backgroundColor),
        jstringToUtf8(env, outlineColor),
        outlineSize,
        bold == JNI_TRUE,
        fontSize,
        subPos,
        useLibass == JNI_TRUE,
        stripSdh == JNI_TRUE
    );
}
