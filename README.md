# N-Queen Game

A classic N-Queen puzzle game built with Compose Multiplatform, featuring conflict visualization.

## Features

- **Interactive N-Queen Puzzle**: Place queens on a chessboard without conflicts
- **Conflict Visualization**: Visual feedback showing attacking lines and conflicting queens
- **Fixed 8x8 Board**: Standard chessboard dimensions
- **Cross-Platform**: apps for Android, iOS, and Desktop

## Requirements

- JDK 17
- Xcode 26 or newer for iOS (deployment target is iOS 26.0; Apple Silicon only)
- Android compile SDK 37, min SDK 24

## Architecture

At this stage didn't face the necessity of introducing view models. 
I've definitely was thinking about adding them, but decided to keep it simple.
I'm happy to focus on this part of project if needed.

### Fixed board size

The board is fixed at 8x8.

`BOARD_SIZE` is a single constant in `domain/chess/ChessBoardConfig.kt`,
with `FILE_RANGE` and `RANK_RANGE` derived from it, so restoring dynamic
sizing means widening one constant rather than unpicking a hardcoded 8.

## Walkthrough

[walkthrough.mov](walkthrough.mov)

## Quick Start

### Desktop
```bash
./gradlew :desktopApp:run
```

With hot reload, so edits to shared UI code appear without restarting:
```bash
./gradlew :desktopApp:hotRun --auto
```

`--auto` is required. Without it the app still starts with hot reload attached,
but nothing watches your files, so edits only appear when
`./gradlew :desktopApp:hotReloadMain` runs.

### Android
```bash
./gradlew :androidApp:installDebug
```

### iOS
iOS apps must be built and run through Xcode or the iOS Simulator directly.
Build through Xcode or build specific iOS targets:
```bash
# For device
./gradlew :composeApp:linkDebugFrameworkIosArm64

# For simulator
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

## Testing

### Unit Tests
Run unit tests for all platforms:
```bash
# All tests
./gradlew test

# Desktop unit tests
./gradlew :composeApp:desktopTest

# iOS tests
./gradlew :composeApp:iosSimulatorArm64Test
```

### Test Locations
- **Unit tests**: `composeApp/src/commonTest/kotlin/` - Kotlin multiplatform tests using kotlin.test
- **UI tests**: `composeApp/src/commonTest/kotlin/` - Simple UI component tests with mocked data

## Project Structure

- **`composeApp/`** - Shared UI code for all platforms (Kotlin Multiplatform library)
- **`androidApp/`** - Android application entry point (`MainActivity`, manifest, launcher icons)
- **`iosApp/`** - iOS application entry point (Xcode project)
- **`desktopApp/`** - Desktop application entry point (`main.kt`, native packaging)

Each platform's entry point lives in its own module, and `composeApp` holds only
shared code plus the platform `actual`s the shared code needs. Android's split is
forced - AGP 9 no longer allows the Android application plugin inside a Kotlin
Multiplatform module - and desktop follows the same shape for symmetry.

## TODO List

### Development Tasks
- [ ] **ChessBoard.kt:47** - Make abstract chess board