package com.nuvio.app.features.updater

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class Arm64UpdateAssetTest {
    private val selector = AppUpdateAssetSelector(
        fileExtensions = listOf(".msi", ".exe"),
        preferredNameFragments = listOf("arm64", "aarch64", "windows"),
        requiredNameFragments = listOf("arm64", "aarch64"),
    )

    private fun asset(name: String) = AppUpdateAssetCandidate(name, "https://example.com/$name")

    @Test
    fun arm64NeverFallsBackToTheOnlyX64Installer() {
        assertNull(selectBestUpdateAsset(listOf(asset("Nuvio-Windows-x64-1.0.msi")), selector))
    }

    @Test
    fun arm64SelectsItsOwnInstallerFromAMixedRelease() {
        val arm = asset("Nuvio-Windows-arm64-1.0.msi")
        assertEquals(arm, selectBestUpdateAsset(listOf(asset("Nuvio-Windows-x64-1.0.msi"), arm), selector))
    }

    @Test
    fun aarch64AliasIsAcceptedButNonInstallersAreExcluded() {
        val arm = asset("Nuvio-Windows-aarch64-1.0.exe")
        assertEquals(arm, selectBestUpdateAsset(listOf(asset("Nuvio-Windows-arm64-1.0.zip"), arm), selector))
    }
}
