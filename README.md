# Analysis Board

A chess analysis board built with Compose Multiplatform, implementing the
exercise in [task_2.md](task_2.md): a move tree with variations, a delayed
computer move, and simulated asynchronous analysis.

Reviewing this? Start with [REVIEWERS.md](REVIEWERS.md), which maps every task_2
requirement to the code that implements it and the test that pins it, and
explains the four ordering rules the exercise is really about. The same guide as
a web page is [docs/analysis-board-guide.html](docs/analysis-board-guide.html) -
open it in a browser if you would rather read it laid out than in Markdown.

## Platform scope

This is a Kotlin Multiplatform project, and all three apps work: Android,
desktop and iOS run the same shared code. **Android is where the focus is** - it
is the platform the app is developed against, and where a decision that can only
go one way goes. Desktop doubles as the fast feedback loop for UI work, because
Compose hot reload shows a layout change in about a second.

## Status

Parts 1 to 3 and the optional bonus are implemented.

- **Part 1 - move variations**: play a move from any selected node; playing from
  an earlier node creates a variation rather than overwriting the line
- **Part 2 - delayed computer move**: applies after a random 1-3 second delay;
  a pending request is cancelled by a reset, a played move, or a new selection
- **Part 3 - asynchronous analysis**: requests may overlap, the newest results
  win, and nothing in flight before a reset can come back afterwards
- **Optional bonus**: board, tree, selection and evaluations survive activity
  recreation and process death

## Requirements

- JDK 17
- Android compile SDK 37, min SDK 24 - the focus platform
- Xcode 26 or newer for the iOS app (deployment target is iOS 26.0; Apple
  Silicon only)

## Quick Start

### Android (the focus platform)
```bash
./gradlew :androidApp:installDebug
```

### Desktop
```bash
./gradlew :desktopApp:run
./gradlew :desktopApp:hotRun --auto
```

`--auto` is required for hot reload. Without it the app still starts with hot
reload attached, but nothing watches your files, so edits only appear when
`./gradlew :desktopApp:hotReloadMain` runs.

### iOS
Build and run through Xcode, or link the framework directly:
```bash
./gradlew :composeApp:linkDebugFrameworkIosArm64            # device
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64   # simulator
```

## Testing

```bash
./gradlew :composeApp:desktopTest
./gradlew :composeApp:iosSimulatorArm64Test
```

Tests live in `composeApp/src/commonTest/kotlin/`. Note that `./gradlew test`
does not cover the shared code - `composeApp` is a Kotlin Multiplatform library,
so its JVM target is named `desktopTest`.

Board positions and move trees are asserted as diagrams rather than object
graphs, so a failure prints a readable board or tree.
[REVIEWERS.md](REVIEWERS.md) covers how the suite is split and what it
deliberately leaves untested.

## Project Structure

- **`composeApp/`** - all shared code, as a Kotlin Multiplatform library
- **`androidApp/`** - Android entry point (`MainActivity`, manifest, launcher
  icons) - the focus platform
- **`desktopApp/`** - desktop entry point (`main.kt`, native packaging)
- **`iosApp/`** - iOS entry point (Xcode project)

Each platform's entry point lives in its own module, and `composeApp` holds only
shared code plus the platform `actual`s it needs. The Android split is forced -
AGP 9 no longer allows the Android application plugin inside a Kotlin
Multiplatform module - and desktop follows the same shape for symmetry.
