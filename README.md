 
<div align="center">

  <img src="https://nuvio.tv/assets/nuvio-app-logo-wordmark.webp" alt="Nuvio" width="320" />

  <p>
    A free, open-source media app for Windows, macOS, and Linux.
    <br />
    Bring your own sources. Nuvio turns them into a library with artwork, ratings, subtitles, and your place saved on every screen.
  </p>

  [Website](https://nuvio.tv) · [Official GitHub Releases](https://github.com/NuvioMedia/NuvioDesktop/releases/latest) · [Support Nuvio](https://nuvio.tv/support)

</div>

## Windows ARM64 Fork

This repository is an **unofficial fork of [Nuvio Desktop](https://github.com/NuvioMedia/NuvioDesktop)**, created specifically to bring native Windows ARM64 support to the application.

The official Nuvio Desktop project currently does not provide a dedicated Windows ARM64 build. This fork aims to address that gap by adapting the existing application and its build configuration to support Windows on ARM devices, including Snapdragon-powered PCs.

**This fork has been developed with the assistance of OpenAI's ChatGPT Codex.**

The goal is to maintain compatibility with the original Nuvio Desktop application while enabling native ARM64 builds, without relying on x64 emulation.

This is an independent community effort and is not officially maintained or endorsed by the Nuvio development team.

## ⚠️ Alpha Software - Testers Only

Nuvio Desktop is currently in alpha and is intended only for testers. It is under active development and is not suitable for daily use.

The Windows ARM64 implementation is also experimental. Expect potential compatibility issues, build failures, and breaking changes.

Features, settings, stored data, and compatibility may change or stop working without notice.

Please report any ARM64-specific issues encountered during testing.

## Get Nuvio Desktop

### Windows ARM64

This fork focuses on building Nuvio Desktop for Windows ARM64.

Check this fork's **GitHub Releases** section for ARM64 builds, if available.

For instructions on building a native Windows ARM64 version, see [Windows ARM64](docs/windows-arm64.md).

### Official Builds

For officially supported platforms, download Nuvio Desktop from the [official GitHub Releases](https://github.com/NuvioMedia/NuvioDesktop/releases/latest).

- Windows x64: MSI installer
- macOS: DMG installer
- Linux: DEB, RPM, Flatpak, or AppImage

## Build from Source

Clone this fork and navigate to the project directory:

```bash
git clone https://github.com/emmarrem/NuvioDesktop/tree/Dev
cd NuvioDesktop
```

### Run the App

```bash
./gradlew :composeApp:run
```

On Windows PowerShell:

```powershell
.\gradlew.bat :composeApp:run
```

For a dedicated native Windows ARM64 build, see [Windows ARM64](docs/windows-arm64.md).

### Package the App

Build a release package for the current host:

```bash
./gradlew :composeApp:packageReleaseDistributionForCurrentOS
```

For platform-specific packaging:

**Windows PowerShell:**

```powershell
.\gradlew.bat :composeApp:packageReleaseMsi --rerun-tasks
```

**macOS:**

```bash
./scripts/build-macos-release-dmgs.sh --package-only
```

**Linux:**

```bash
./gradlew :composeApp:packageReleaseDeb
```

**Note:** The standard Windows packaging command does not automatically guarantee a native ARM64 installer. Refer to the ARM64-specific build instructions for the required configuration.

The shared application is built with Kotlin Multiplatform and Compose Multiplatform.

## Credits and Acknowledgements

- **[NuvioMedia](https://github.com/NuvioMedia/NuvioDesktop)** — For developing and maintaining the original open-source Nuvio Desktop application.
- **[OpenAI ChatGPT Codex](https://openai.com/codex/)** — Used to assist with the development, code modifications, and build configuration changes needed for Windows ARM64 compatibility.

This fork builds upon the work of the original Nuvio Desktop contributors. All credit for the original application belongs to its respective developers.

## License

[GNU General Public License v3.0](./LICENSE)

This fork retains the original project's GPL v3.0 license.
