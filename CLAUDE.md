# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Kotlin Multiplatform project targeting Android, iOS, and Desktop using
Compose Multiplatform for UI.

Local toolchain: JDK 17 and Xcode 26 for iOS.

## Project Architecture

The project follows a modular Kotlin Multiplatform structure:

- **`/composeApp`** - Compose Multiplatform **library** targeting Android, iOS, and Desktop
  - Applies `com.android.kotlin.multiplatform.library`, configured via `kotlin { android { } }`
  - `commonMain` - Shared UI code across all platforms
  - `androidMain` - Android-specific code (platform `actual`s only; no app entry point)
  - `desktopMain` - Desktop-specific code (platform `actual`s only; no app entry point)
  - `iosMain` - iOS-specific code

- **`/androidApp`** - Android application module (`com.android.application`)
  - Holds `MainActivity`, `AndroidManifest.xml`, and launcher `res/`; depends on `/composeApp`
  - Exists because AGP 9 does not allow `com.android.application` in a Kotlin Multiplatform module

- **`/iosApp`** - iOS application entry point (Xcode project)
  - Swift entry point that integrates with Kotlin Multiplatform framework

- **`/desktopApp`** - Desktop application module (`org.jetbrains.kotlin.jvm`)
  - Holds `main.kt` (`molokoka.project.n.MainKt`) and the `compose.desktop { application { } }` block; depends on `/composeApp`
  - A plain JVM module, not a KMP one - `composeApp` keeps the `jvm("desktop")` target it consumes

## Development Commands

**Run Gradle tasks one per invocation.** Do not batch several tasks into a
single `./gradlew` call - a batched run gives no feedback until it finishes,
hides which target failed, and tends to exceed command timeouts. Run them in
fast-to-slow order so failures surface early.

Standard Gradle task names work as expected (`build`, `run`, `assembleDebug`);
run `./gradlew tasks` rather than trusting a list here. The non-obvious ones:

| Task | Note |
|---|---|
| `:composeApp:desktopTest` | JVM target is named `desktop`, so it is not `:composeApp:test` |
| `:composeApp:iosSimulatorArm64Test` | iOS unit tests |
| `:androidApp:*` | All Android tasks live here, not in `:composeApp` |
| `:desktopApp:run` | Desktop app; `:composeApp:run` no longer exists |
| `:desktopApp:hotRun --auto` | Desktop app with hot reload; `hotRunAsync` launches it detached |
| `:desktopApp:packageDmg` | Native distribution; also `packageMsi`, `packageDeb` |

`:composeApp:test` and `:composeApp:testDebugUnitTest` do not exist -
`composeApp` is a KMP library with no Android build variants.

**`hotRun` does not watch files unless you pass `--auto`** (alias `--autoReload`).

**A passing `linkDebugFramework*` does not mean the iOS app builds.** The Gradle
framework link and the Xcode project fail independently. After touching iOS
targets, Compose, or AGP, verify the real app:

```bash
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' \
  -configuration Debug build
```

**Testing policy**: Claude may run any test command without asking.

Tests live in `composeApp/src/commonTest/kotlin/` and cover n-queen conflict
logic only - not rendering. To check UI changes, run `./gradlew :desktopApp:run`
and look at the app.

## Development Notes

- Base package is `molokoka.project.n`, with platform-specific subpackages
- All dependency versions belong in `gradle/libs.versions.toml`, never inline in
  a build script
- JVM target is Java 11 for Android compilations (set in the
  `kotlin { android { } }` block of `composeApp`, plus `compileOptions` in
  `androidApp`) and Java 17 for the desktop ones (`composeApp`'s `desktop`
  target and `desktopApp`). Both are pinned deliberately - do not remove them.
  Unpinned, desktop bytecode follows whatever JDK runs Gradle, and Compose Hot
  Reload runs the app on its own provisioned JetBrains Runtime, which is older
  than the JDK an IDE may build with. That mismatch fails at launch with
  `UnsupportedClassVersionError`, not at compile time.
- iOS targets are arm64 (device) and simulator arm64 only. iosX64 (Intel
  simulator) was removed: Compose Multiplatform publishes no iosX64 artifacts,
  so it could never build the UI. `EXCLUDED_ARCHS[sdk=iphonesimulator*]` in the
  Xcode project keeps Xcode from requesting it.
- **Do not use Material components** - prefer basic Compose components and custom
  styling. The `compose.material3` dependency has been removed from the build, so
  a Material import will fail to compile rather than slip through.
- Gradle configuration cache and build cache are enabled (`gradle.properties`).
  A killed Gradle client can leave a daemon holding cache locks; if a build hangs
  waiting on `journal-1.lock` or `fileContent.lock`, kill the stale daemon rather
  than waiting it out.
- AGP 10 will remove the legacy KMP/Android APIs entirely. The `androidApp` split
  and `com.android.kotlin.multiplatform.library` usage are what keep this project
  ready for it - do not reintroduce `com.android.application` or
  `com.android.library` into a KMP module.