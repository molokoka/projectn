# Analysis Board: a guide for reviewers

This is a chess analysis board built to the exercise in [task_2.md](task_2.md).
You can play moves, branch into variations at any earlier point in the line, ask
for a computer move that arrives after a delay, and run a simulated analysis pass
over the whole tree.

The exercise states its focus as "state modelling and correct behaviour while
asynchronous work and user interaction overlap", and notes that a chess engine and
a production-quality move-tree UI are not expected. The implementation is scoped
accordingly: the chess model covers rook and queen movement only, and the
substance sits in the ordering rules in
[`AnalysisState.kt`](composeApp/src/commonMain/kotlin/molokoka/project/n/analysis/AnalysisState.kt).

## Platform scope: read this first

This is a Kotlin Multiplatform project, but the three targets are not equally
supported.

| Platform | Status | What that means here |
|---|---|---|
| **Android** | **Supported, and the target** | The one platform the app is developed and verified on. Min SDK 24, compile SDK 37. State survives process death through a real `SavedStateHandle`; logging goes to `Log.d`. |
| Desktop (JVM) | Experimental | Exists as the fast feedback loop for UI work, because Compose hot reload makes a layout change visible in about a second. Not a shipping target. |
| iOS | Experimental | Builds and runs, but is not part of routine verification. arm64 device plus arm64 simulator only; iOS 26 deployment target; needs Xcode 26. |

The shared Compose code is genuinely shared, and desktop and iOS run it as
written. What differs is where the trade-offs were resolved: layout and
interaction were tuned against Android, and where a platform-specific decision had
to be made, Android is the platform it was made for. Process-death restore is the
clearest example -- it answers an Android lifecycle constraint, and it lives in
shared code only because that is where the state lives.

## What task_2 asked for, and where it lives

Paths below are relative to
`composeApp/src/commonMain/kotlin/molokoka/project/n/` for production code and
`composeApp/src/commonTest/kotlin/molokoka/project/n/` for tests.

| task_2 | Implementation | Pinned by |
|---|---|---|
| The starting position | `domain/BoardConfig.kt` (`INITIAL_POSITION`, one line of piece-square notation) | `domain/PositionTest.kt` |
| Four-character LAN moves | `domain/Move.kt`, `domain/Coordinates.kt` | `domain/MoveTest.kt`, `domain/CoordinatesTest.kt` |
| Rook and queen movement, no jumping, capture, no self-capture | `domain/pieces/Rook.kt`, `domain/pieces/Queen.kt`, `domain/pieces/Shared.kt`, applied by `domain/Chess.kt` | `domain/pieces/RookTest.kt`, `domain/pieces/QueenTest.kt`, `domain/ChessTest.kt` |
| Part 1: history as a tree rooted at the initial position | `domain/AnalysisTree.kt` | `domain/AnalysisTreeTest.kt` |
| Part 1: play from the selected node, select an earlier node, create a variation | `analysis/AnalysisState.kt` (`onSquareClick`, `selectNode`) | `analysis/AnalysisStateTest.kt` |
| Part 1: reuse an existing child rather than duplicating it | `AnalysisTree.add` returns the tree unchanged when the move is already there | `AnalysisTreeTest` "reuses a node that already carries the move" |
| Part 1: reusing a node preserves its evaluation | the same reuse path never rewrites `moveEvaluation` | `AnalysisStateTest` "replaying a move keeps the evaluation already attached to it" |
| Part 1: reset everything | `AnalysisIntent.Reset` returns a fresh `AnalysisState()` plus both cancel effects | `AnalysisStateTest.Resetting` |
| Part 2: computer move after a random 1-3s delay | `computer_move/DelayedRandomComputerMoveSource.kt` | `computer_move/DelayedRandomComputerMoveSourceTest.kt` |
| Part 2: only one pending request | a single `Job` field in `analysis/AnalysisViewModel.kt` | `AnalysisViewModelTest` "requesting another computer move discards the one under way" |
| Part 2: a result for a position no longer visible must not move the board | the `path != moves` guard in `playComputerMove` | `AnalysisStateTest.ReceivingAComputerMove` |
| Part 3: analyse a snapshot of the tree, one async operation, `+`/`-`/`=` per move | `move_evaluation/DelayedRandomMoveEvaluationSource.kt`, `move_evaluation/MoveEvaluation.kt` | `move_evaluation/*Test.kt`, `AnalysisViewModelTest` "a move played during a move evaluation is not in its snapshot" |
| Part 3: newer results win, older ones may not overwrite them | the generation counters described below | `AnalysisStateTest.ReceivingAMoveEvaluation`, `AnalysisViewModelTest` |
| Part 3: analysis never changes selection or the visible board | `movesEvaluationReady` copies only `tree` | `AnalysisStateTest` "a move evaluation never changes the selected node or the visible board position" |
| Part 3: reset invalidates delayed results | cancel effect plus the generation guard | `AnalysisStateTest` "evaluations arriving after reset cannot alter the reset state" |
| Bonus: survive activity recreation and process death | `analysis/AnalysisStateSaving.kt` over `SavedStateHandle` | `analysis/AnalysisStateSavingTest.kt`, `AnalysisViewModelTest` "a view model rebuilt on the same saved state comes up on the played moves" |

Everything in Parts 1 to 3 and the optional bonus is implemented.

## The State / ViewModel split

The state decides; the ViewModel runs. Every decision is a pure function of the
current state, and nothing that decides anything can see a clock, a dispatcher,
or a coroutine.

```
      user tap / delayed result
                 |
                 v
          AnalysisIntent
                 |
                 v
    AnalysisState.reduce(intent)          <-- pure; the whole rulebook
                 |
        +--------+---------+
        v                  v
   AnalysisState      List<AnalysisEffect>
        |                  |
        v                  v
   published to      AnalysisViewModel launches
   the UI           coroutines, cancels jobs
                           |
                    result comes back as
                    another AnalysisIntent
```

Reading it:

- **`AnalysisIntent`** -- one thing that happened: a tap, or a delayed result
  arriving.
- **`reduce`** -- the whole rulebook, pure. The same state and intent always give
  the same answer.
- **`AnalysisState`** -- everything the screen shows: the tree, the current path,
  the selected square, the loading flags.
- **`AnalysisEffect`** -- work to be done, described as a value.
  `CancelComputerMove` is a thing returned, not a call made.
- The loop closes at the bottom: a coroutine's result re-enters as another
  intent, so delayed work is judged by the same rulebook as a tap.

### What crosses the seam, and in which direction

The whole contract is one signature:

```
fun AnalysisState.reduce(intent: AnalysisIntent): AnalysisUpdate
                                       // = Pair<AnalysisState, List<AnalysisEffect>>
```

Intents go in, a next state and a list of effects come out. The dependency runs
one way only: `AnalysisState.kt` imports nothing from the ViewModel and could not
name it, while the ViewModel knows the state's entire vocabulary. That asymmetry
is what makes `AnalysisStateTest` possible at all -- the rulebook has no seam to
stub out, because it has no collaborators.

`AnalysisEffect`s are commands: `StartComputerMove` and `StartMovesEvaluation` are
asynchronous work that has to be carried out, and `CancelComputerMove` stops work
already under way. The reducer only requests them; the ViewModel performs the
asynchronous work and dispatches what comes back as a new intent -- the part
middleware plays in Redux.

### Results re-enter as intents, which is what the guards answer to

The connection is a cycle rather than a pipeline. The ViewModel feeds every result
back in as another intent, so `ComputerMoveReady` and `MovesEvaluationReady` arrive
through the same door as a screen tap.

Since the reducer consults nothing outside itself, each returning intent carries
what is needed to judge it against the current state -- `path` on
`ComputerMoveReady`, `generation` on `MovesEvaluationReady`. The four ordering
rules below all follow from this.

### The tests sit on the seam

`reduce` is a plain function -- a state and an intent in, a state and effects out --
so a test can call it directly instead of driving the whole screen to reach it.
`AnalysisStateTest` does exactly that, with no dispatcher and no clock, and covers
most of task_2: anything expressible as a sequence of intents belongs there.
`AnalysisViewModelTest` verifies the asynchronous behaviour -- the delay, job
cancellation, overlapping requests and out-of-order arrival. Needing `runTest` to
state a property is the signal it belongs there.

## The four ordering rules, and the mechanism for each

These are the requirements the exercise is actually about. Each one is worth
checking against the code.

**1. Only one computer move may be pending.** `computerMoveRequest` is a single
`Job?`. Starting one cancels whatever was there. The shape of the field *is* the
requirement, so there is no way to hold two.

**2. A computer move that arrives for a position the user has left must not move
the board.** The request carries the `path` it was made for.
`playComputerMove` returns the state untouched when `path != moves`. Cancellation
normally gets there first; the guard is what makes correctness independent of
that race. This build discards such a result rather than adding it unselected --
task_2 allows either.

**3. Analysis requests may overlap, and a newer result must never be overwritten
by an older one.** Two mechanisms answer this: a supervisor scope lets the requests
overlap, and two integers decide which results may be shown.

```
AnalysisState.pendingEvaluationGeneration   the number of requests ever started
AnalysisTree.evaluationGeneration           the generation currently displayed

isMoveEvaluationPending = pending > displayed        (that is the loading flag)

a result for generation g is applied only when
    g >= displayed        an older request may not overwrite newer results
    g <= pending          a result from before a reset cannot come back
```

Reset is what makes the second test matter: it puts `pending` back to zero, so a
result still in flight from before it fails `g <= pending` and is discarded. There
is also no stored "is loading" flag to keep in sync -- the comparison *is* the
flag.

Because analysis requests may legitimately run concurrently, they do not share
the single-job treatment of the computer move: they launch into
`moveEvaluationScope`, a `SupervisorJob` child of `viewModelScope`, so several
run at once and `cancelChildren()` stops all of them without killing the scope.
Contrast that with rule 1 and you can read the concurrency policy off the two
field declarations: one `Job?` means one at a time, a supervisor scope means many.

**4. Analysis must never change the selected node or the visible board.** Not
enforced by a check -- enforced by what `movesEvaluationReady` is able to do. It
returns `copy(tree = ...)`. `moves` and `selected` are not in reach. Structure is
not proof against a later edit, so the test
`` `a move evaluation never changes the selected node or the visible board position` ``
pins it: evaluations arrive for a whole line while an earlier node is selected, and
both the selected path and the board on screen have to come through unchanged.

Reset appears in rules 1, 3 and 4 at once: it returns a brand-new state and emits
both cancel effects, and because it resets `pendingEvaluationGeneration` to 0,
any result still in flight fails the `g <= pending` test if it somehow outlives
cancellation.

## Suggested reading order

Bottom-up, because each layer is expressed in the one below it. The whole chess
model is 438 lines.

### The domain

1. `domain/Coordinates.kt` (43 lines) -- a square: file and rank, with parsing and
   range validation. Everything else is expressed in these.
2. `domain/Move.kt` (33 lines) -- a from/to pair, parsed from four-character LAN.
3. `domain/pieces/Piece.kt` and `pieces/PieceType.kt` (20 lines together) -- side
   and type, and the symbols the notation uses.
4. `domain/Position.kt` (45 lines) -- a `Map<Coordinates, Piece>`, with `INITIAL`
   built from the single line of piece-square notation in `BoardConfig.kt`.
5. `domain/pieces/Shared.kt` (90 lines), then `Rook.kt` and `Queen.kt` (22 and 23)
   -- the movement rules, written as `Position` extensions so they can see what
   blocks a ray. Each piece exposes exactly two functions: one generating the
   squares it reaches, one validating a move. `Shared.kt` holds the geometry they
   have in common, and is the reference for how code in this project reads.
6. `domain/Chess.kt` (30 lines) -- `sideToMove`, and `Position.play(move, side)`,
   which dispatches on `PieceType` and returns the next position. This is where a
   move is accepted or refused. `domain/ReachableSquares.kt` is its counterpart for
   generation; both are exhaustive `when`s, so a new piece type breaks the build in
   exactly the places that need editing.
7. `domain/AnalysisTree.kt` (107 lines) -- the tree, which stands on all of the
   above. Positions are stored per node rather than replayed, and `add` is
   idempotent for a move that already exists.

### The analysis screen

1. `analysis/AnalysisState.kt` (216 lines) -- the whole rulebook. Read `reduce`
   first as a table of contents, then the private functions under it.
2. `analysis/AnalysisViewModel.kt` (117 lines) -- where the coroutines live:
   launching a request, cancelling one, and feeding the result back as an intent.
3. `analysis/view/AnalysisViewState.kt` -- the tree flattened into rows for
   display. Pure, so it is unit tested rather than screenshot tested.
4. `analysis/AnalysisScreen.kt` and `analysis/view/AnalysisView.kt` -- the UI.
   Composables take a view state and emit intents; they hold no rules.
5. `analysis/AnalysisStateTest.kt` -- the requirements as executable sentences.

## Tests

265 tests, all in `commonTest`, all green via `./gradlew :composeApp:desktopTest`.
Two conventions do most of the work.

**Test names aim to state one acceptance criterion as a sentence.**
`` `an older request may not overwrite the newer results` `` comes almost
verbatim from task_2. Where the spec and the code use different nouns, the
subject follows the code and the behaviour follows the spec, so a test is
findable from either side.

Where a name would need an "and" to join two criteria, it is two tests. Thirteen
names do contain one, but each joins a single subject rather than two outcomes:
`` `a rook reaches its whole rank and file` `` is one reachable set asserted as one
diagram, and `` `changing visible position and going back stops a computer move
already under way` `` names two steps of the given before a single criterion.

**Three diagram languages keep the assertions compact and the failures readable.**
They live in `commonTest/util/`, one per thing worth seeing, and each is the
assertion itself rather than a picture of one -- the test compares against the
string you are reading, so the diff on failure is the diagram.

Compactness is the other half of the point. One `reachableMovesDiagram` states all
64 squares in a single assertion -- every square the mover reaches and every square
it does not -- where the same coverage written square by square would run to dozens
of lines. It is also why no test here restates what a diagram already marks: the
diagram is the complete statement, so a further assertion holding a count would be
padding.

Diagrams are not the only check on the pieces, though. `RookTest` and `QueenTest`
each close with a `PlainAssertions` group that goes at the same rules directly --
comparing `rookReachableSquares` against a literal set of squares, and asserting
what `play` returns for a legal move or throws for a blocked one. That ground is
already covered by the diagrams, deliberately: the plain assertions are the smoke
check that the helper and the code agree.

### `positionDiagram()`

```
8 . . . . . . . .
7 . . . . . . . .
6 . . . . . . . .
5 . . . . . . . .
4 R . . . . . . .
3 . . . . . . . .
2 . . . . . . . .
1 . . . . . . . .
  a b c d e f g h
```

A board: each piece as its own symbol, `.` for an empty square, ranks descending
with file labels underneath.

Its inverse, `fromDiagram()`, reads a board back into a `Position`, so a test sets
up its given exactly the way it asserts its outcome. `ChessTest` shows it plainly:
`fromDiagram` builds the position, `play` runs, and `positionDiagram` states the
result -- one notation throughout.

### `reachableMovesDiagram()`

```
8 x x x . x x x x
7 x x x . x x x x
6 x x x . x x x x
5 x x x . x x x x
4 . . . R . . . .
3 x x x . x x x x
2 x x x . x x x x
1 x x x . x x x x
  a b c d e f g h
```

Where a piece can go, in five marks: `R` the mover, `.` empty and reachable, `x`
empty and not reachable, `o` occupied and reachable -- a capture -- and `#`
occupied and not reachable, a blocker. It is what makes `RookTest` and `QueenTest`
readable at 20 tests each.

That `o` and `#` are separate marks is the point: "may capture" and "is blocked by"
are the two rules distinguishing a rook from a piece that merely slides, and a
wrong one shows up at a glance instead of as a set difference in a failure message.

### `moveTreeDiagram()`

```
Start
└── b2b4+
    ├── a7a5=
    └── c7c5-
```

The tree, with each move's evaluation attached. Indentation is the branch, so
`a7a5` and `c7c5` are two replies to `b2b4`. The signs are White-relative by chess
convention -- `+` White is better, `-` Black is better, `=` equal -- never relative
to whoever just moved.

All three helpers carry their own tests -- 33 of the 265 -- on the grounds that an
assertion is only as trustworthy as the renderer it reads through. A broken
renderer that agreed with itself would pass every test that used it.

**A few properties are asserted on both sides of the split.** Beyond the State /
ViewModel division described above, note where it is deliberately crossed. Reset
both cancels the pending work and refuses a stale result, so it is tested twice:
cancellation in the ViewModel, invalidation in the reducer. They are separate
mechanisms and each has to hold on its own.

**What is deliberately not tested**, so its absence reads as a decision rather
than a gap: bare data classes and enums with no behaviour; the Compose
composables (checked by running the app and by `@Preview`, which is why the
preview states in `AnalysisScreenPreviewData.kt` are exhaustive); and the
randomness of the fakes -- a test whose truth depends on a constant inside a test
double is measuring the double.

## Running it

Android, the supported target:

```bash
./gradlew :androidApp:installDebug
```

Desktop, the quickest look at a UI change (experimental):

```bash
./gradlew :desktopApp:run
./gradlew :desktopApp:hotRun --auto    # hot reload; --auto is required to watch files
```

iOS, through Xcode (experimental):

```bash
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' \
  -configuration Debug build
```

Tests:

```bash
./gradlew :composeApp:desktopTest              # the JVM target is named desktop
./gradlew :composeApp:iosSimulatorArm64Test
```

Requires JDK 17. Module layout: `composeApp` holds all shared code as a KMP
*library*, and each platform's entry point sits in its own module
(`androidApp`, `desktopApp`, `iosApp`). The Android split is not a style choice --
AGP 9 no longer allows the Android application plugin inside a KMP module.
