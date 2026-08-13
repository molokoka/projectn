# Analysis Board

A chess analysis board built with Compose Multiplatform, implementing the
exercise in [task_2.md](task_2.md): a move tree with variations, a delayed
computer move, and simulated asynchronous analysis.

## Status

- **Board rendering**: 8x8 board with file and rank labels, flippable
- **Part 1 - move variations**: not implemented
- **Part 2 - delayed computer move**: not implemented
- **Part 3 - asynchronous analysis**: not implemented

The board currently draws empty squares. Pieces, the starting position, and move
handling arrive with Part 1, so tapping a square does nothing yet.

## Features

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

Coverage is currently the board primitives - coordinate parsing and validation,
square colour, and draw order under both orientations - plus the analysis view
model's orientation state: flip, double flip, and reset.

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

### Verification Tasks
- [ ] Confirm the analysis view model is cleared on exit: Start, flip the board,
      Exit, then Start again - the board must come back white. This proves the
      navigation entry decorator cleared the entry's view model store on pop,
      which unit tests cannot reach.
- [ ] Decide whether screens should stay top-left or return to horizontally
      centred - `NavDisplay` defaults to `Alignment.TopStart`, and passing
      `contentAlignment = Alignment.TopCenter` restores the previous centring

### Development Tasks
- [ ] Pieces and the starting position from [task_2.md](task_2.md)
- [ ] Rook and queen move generation, LAN move application
- [ ] Move tree with variations and node selection
- [ ] Delayed computer move, cancelling any pending request
- [ ] Asynchronous analysis with out-of-order result handling