# N-Queen Game

A classic N-Queen puzzle game built with Compose Multiplatform, featuring conflict visualization and leaderboard functionality.

## Features

- **Interactive N-Queen Puzzle**: Place queens on a chessboard without conflicts
- **Conflict Visualization**: Visual feedback showing attacking lines and conflicting queens
- **Multiple Board Sizes**: Play with different board dimensions
- **Leaderboard**: Track your best times and compete with yourself
- **Cross-Platform**: apps for Android, iOS, and Desktop

## Requirements

- JDK 17
- Xcode 26 or newer for iOS (deployment target is iOS 26.0; Apple Silicon only)
- Android compile SDK 37, min SDK 24

## Architecture

At this stage didn't face the necessity of introducing view models. 
I've definitely was thinking about adding them, but decided to keep it simple.
I'm happy to focus on this part of project if needed.

## Walkthrough

[walkthrough.mov](walkthrough.mov)

## Quick Start

### Desktop
```bash
./gradlew :composeApp:run
```

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

Android's entry point lives in its own module because AGP 9 no longer allows
the Android application plugin inside a Kotlin Multiplatform module.

## TODO List

### Development Tasks
- [ ] **ChessBoard.kt:47** - Make abstract chess board
- [ ] **WinScreen.kt:44** - Extract logic into view model