# Analysis Board

A chess analysis board built with Compose Multiplatform, implementing the
exercise in [task_2.md](task_2.md): a move tree with variations, a delayed
computer move, and simulated asynchronous analysis.

## Status

- **Board rendering**: 8x8 board with file and rank labels, flippable
- **Part 1 - move variations**: implemented
- **Part 2 - delayed computer move**: implemented, against a scripted move source
- **Part 3 - asynchronous analysis**: not implemented

Tapping a piece of the side to move selects it; tapping a legal target plays the
move and records it in the move tree. Playing from an earlier node creates a
variation rather than overwriting the line.

The computer move applies after a random 1-3 second delay, and a pending request
is cancelled when the board is reset, when a move is played, or when a different
node is selected. **The move it plays is scripted, not generated** - nothing in
the codebase enumerates legal moves yet, so `ScriptedComputerMoveSource` knows a
single four-move line and returns nothing from any other position. Everything
around it - the delay, cancellation, and stale-result handling - is real.

## Features

- **Fixed 8x8 Board**: Standard chessboard dimensions
- **Move tree**: variations, node selection, and board playback per node
- **Cross-Platform**: apps for Android, iOS, and Desktop

## Requirements

- JDK 17
- Xcode 26 or newer for iOS (deployment target is iOS 26.0; Apple Silicon only)
- Android compile SDK 37, min SDK 24

## Architecture

The analysis screen follows MVI. `AnalysisState` holds the whole screen state and
owns the reducer:

```kotlin
fun AnalysisState.reduce(intent: AnalysisIntent): AnalysisUpdate
```

`AnalysisUpdate` pairs the next state with an optional `AnalysisEffect`. The
reducer is pure - no coroutines, no clock - so every decision, including
cancelling a pending computer move, is a value that can be asserted directly.
`AnalysisViewModel` reduces the intent, publishes the state, and runs the effect;
that is all it does. View models are provided by Koin.

The computer move sits behind `ComputerMoveSource`, so the view model knows
nothing about how a move is chosen or how long it takes. Replacing the scripted
implementation with real generation is a one-class change.

### Fixed board size

The board is fixed at 8x8.

`BOARD_SIZE` is a single constant in `domain/BoardConfig.kt`, with `FILE_RANGE`
and `RANK_RANGE` derived from it, so restoring dynamic sizing means widening one
constant rather than unpicking a hardcoded 8.

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

`./gradlew test` does not cover the shared code - `composeApp` is a Kotlin
Multiplatform library, so its JVM target is `desktopTest`.

Coverage is the board primitives - coordinate parsing and validation, square
colour, and draw order under both orientations - the rook and queen move rules,
the move tree, and the analysis screen. The analysis tests are split by unit
under test: `AnalysisStateTest` drives the pure reducer with no coroutines, and
`AnalysisViewModelTest` covers only what needs a dispatcher - the delay, the
result round trip, and cancellation.

Board positions and move trees are asserted as diagrams rather than object
graphs, so a failure prints a readable board or tree.

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
- [x] Pieces and the starting position from [task_2.md](task_2.md)
- [x] Rook and queen move rules, LAN move application
- [x] Move tree with variations and node selection
- [x] Delayed computer move, cancelling any pending request
- [ ] Generate a random valid move, replacing `ScriptedComputerMoveSource`
- [ ] Asynchronous analysis with out-of-order result handling

### Refactoring Tasks
- [ ] Unify board and analytics sizing and scrolling: both should sit in one
      wrapper column that owns the width (`uiConfig.squareSize * BOARD_SIZE`) and
      the padding, so neither states it again, and the board's horizontal pan
      should be shared with the analytics panel rather than being its own scroll
      state