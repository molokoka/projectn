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

**Check for an already-running desktop app before launching another one.** A
hot-reload session started with `--auto` already picks up code changes, so a
second launch is redundant, and the two instances race over the same build
directory:

```bash
pgrep -fl molokoka.project.n.MainKt
```

Two matches means a hot-reload session is live - the app JVM plus the
`org.jetbrains.compose.devtools.Main` sidecar, which only exists under `hotRun`.
One match is a plain `:desktopApp:run`. Nothing means the field is clear. If a
hot-reload session is running, edit the source and let it reload rather than
starting a second app. `pgrep -f hotRun` does not work - the Gradle process
carries the task name `:desktopApp:hotReloadMain`, not `hotRun`.

**A passing `linkDebugFramework*` does not mean the iOS app builds.** The Gradle
framework link and the Xcode project fail independently. After touching iOS
targets, Compose, or AGP, verify the real app:

```bash
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' \
  -configuration Debug build
```

**Testing policy**: Claude may run any test command without asking.

Tests live in `composeApp/src/commonTest/kotlin/` and cover the board primitives
only - coordinate parsing, square colour, draw order - not rendering. To check UI
changes, run `./gradlew :desktopApp:run` and look at the app.

**Name a test as a backticked sentence stating one acceptance criterion** -
`` fun `rejects a rank below one`() ``, not `fun rejectsRankBelowOne()`. If the
name needs an "and" to join two criteria, it is two tests. Backticked names with
spaces compile for both the JVM and Kotlin/Native targets, so they are safe in
`commonTest`; they would only break in an Android instrumented test, which this
project does not have.

**Verification is phase 0 by default.** Run the suite once and confirm it is
green. Deliberately breaking a guard to prove a test goes red is phase 1, and
only on request - it costs four builds to check one branch. Do not pass
`--rerun-tasks` for routine checks; it defeats the build cache.

### Writing tests

**Name the requirement, not the implementation.** Take the wording from
`task_2.md`: "shows loading", "visible position", "take precedence", "cannot
alter the reset state". `` `changing the visible position hides the computer move
loading` `` says what a user gets; `` `selecting another node stops waiting for
the computer` `` describes a boolean field. Where the spec and the code disagree
on a noun, the subject follows the code (`move evaluation`) and the behaviour
follows the spec, so the test stays findable from both.

**Each test carries its own given.** Name every value the assertion depends on
as a local `val` and interpolate it into the expected output:

```kotlin
val olderAnswer = MoveEvaluation.WHITE_BETTER
val newerAnswer = MoveEvaluation.BLACK_BETTER
...
assertEquals(
    """
    Start
    └── b2b4$olderAnswer
    """.trimIndent(),
    viewModel.state.value.tree.moveTreeDiagram()
)
```

`└── b2b4+` forces the reader to trace a bare symbol back through a helper to
find out whose answer it was. Prefer duplicated setup in each test over a shared
builder that hides which value belongs to which request.

**Assert something only the behaviour under test can change.** A test that still
passes with that behaviour deleted is not a test. Two that looked fine here and
were not: asserting an empty tree after Reset could not detect a leaked
evaluation, because evaluations attach to nothing on an empty tree; and asserting
"no move was played" after navigating away could not detect broken cancellation,
because the `path != moves` guard discards the move anyway.

**Do not test the fakes.** `` `plays no move before the delay elapses` `` only
proved that a fake's `delay()` had not elapsed. Anything whose truth depends on a
constant inside a test double is measuring the double.

**Split by layer.** `AnalysisStateTest` covers `reduce` as a pure function - no
dispatcher, no clock. `AnalysisViewModelTest` covers what only exists in time:
job lifecycle, cancellation, overlapping requests, out-of-order arrival. If a
property is expressible as a sequence of `reduce` calls, it belongs in the state
test. The exception is job isolation - that one feature's coroutine does not
cancel another's is invisible to the reducer, which emits no effect for it.

**Test a defensive guard at the layer that owns it**, and only if removing the
guard would break a stated requirement should the other mechanism fail. Reset
both cancels the pending work and refuses stale results; cancellation is testable
in the view model, invalidation only in the reducer, because cancellation always
intervenes first. An untested guard is indistinguishable from dead code.

## Readable Code

`domain/pieces/Shared.kt` is the reference for how code here should
read. Match it.

```kotlin
internal fun squaresBetweenOnRank(move: Move): List<Coordinates> {
    val start = minOf(move.from.file, move.to.file) + 1
    val end = maxOf(move.from.file, move.to.file)

    return (start until end)
        .map { fileStep ->
            Coordinates(fileStep, move.from.rank)
        }
}

internal fun squaresBetweenOnDiagonal(move: Move): List<Coordinates> {
    val fileDirection = if (move.from.file < move.to.file) 1 else -1
    val rankDirection = if (move.from.rank < move.to.rank) 1 else -1

    val start = 1
    val end = abs(move.to.file - move.from.file)

    return (start until end)
        .map { diagonalStep ->
            Coordinates(
                move.from.file + fileDirection * diagonalStep,
                move.from.rank + rankDirection * diagonalStep
            )
        }
}
```

What to copy from it:

- **Name every intermediate value, then compute with the names.** `start` and
  `end` on their own lines beat `(minOf(a, b) + 1 until maxOf(a, b))` inline. A
  reader checks one bound at a time instead of unpacking a nested expression.
- **Name the lambda parameter for the thing it is** - `fileStep`, `diagonalStep`,
  not `it`.
- **Keep sibling functions structurally identical.** All three read
  `start`, `end`, `(start until end).map { ... }`. When they match, a function
  that differs is visible; when each is written its own way, a bug hides in the
  difference. A real one did: one helper walked from the origin while its twin
  walked from one square past it, and the asymmetry was invisible until the
  tests caught it.
- **No comments.** If a line needs one, name something instead.
- **Build a result independently rather than relying on two results lining up.**
  The diagonal used to `zip` a file list with a rank list, which was only correct
  while both happened to be ordered the same way - a coupling neither signature
  declared. Computing each square from the origin removed it.

Prefer clarity over cleverness where the cost is a few lines and no measurable
performance difference. These functions run on a click, over at most six squares.

## Development Notes

- Base package is `molokoka.project.n`, with platform-specific subpackages
- `domain/pieces/` holds the piece model - `Piece`, `PieceType` - and the
  movement rules built on it. Each piece there exposes exactly two functions:
  `<piece>ReachableSquares(origin): Set<Coordinates>` generates, and
  `requireValid<Piece>Move(move)` validates. `Shared.kt` holds what they have in
  common - the geometry predicates, the ray builders, and `reachableAlong`.
  Three exhaustive `when`s dispatch on `PieceType` - `domain/ReachableSquares.kt`
  (generation), `Chess.kt` (validation), and `ui/PieceImage.kt` (display) - so a
  new entry in the enum breaks the build in exactly the places that need editing
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
- **Piece artwork in `composeResources/drawable` must be XML vector drawables,
  not SVG.** Compose Multiplatform generates a `Res.drawable` accessor for an
  `.svg` and renders it on desktop and iOS, but Android throws
  `IllegalStateException: Android platform doesn't support SVG format` the
  moment `painterResource` loads it. Nothing catches this before runtime -
  `assembleDebug` succeeds, because the failure is in the resource decoder, not
  the build. Vector XML renders on all three targets. It has no `<circle>`
  element, so round shapes are two arc subpaths in `pathData`.
- **Launch the Android app, do not just build it.** `:androidApp:assembleDebug`
  passing says nothing about whether the app runs; the SVG crash above shipped a
  green build. `./gradlew :androidApp:installDebug`, start it, then
  `adb logcat -d -b crash`.
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