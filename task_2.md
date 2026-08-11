# Chessboard Exercise: Analysis Tree

## Goal

Extend the chessboard from the previous exercise into a lightweight analysis
board. Users should be able to play moves, create and browse variations, request
a delayed computer move, and run simulated asynchronous analysis.

The focus is state modelling and correct behaviour while asynchronous work and
user interaction overlap. A chess engine and production-quality move-tree UI
are not expected.

## Starting position

Use the following initial position:

```text
8  . r . r . r . r
7  q . q . q . q .
6  . . . . . . . .
5  . . . . . . . .
4  . . . . . . . .
3  . . . . . . . .
2  . Q . Q . Q . Q
1  R . R . R . R .

   a b c d e f g h
```

Uppercase and lowercase pieces belong to opposing sides. You do not need to
parse FEN.

## Simplified chess model

- Moves use four-character LAN notation, such as `b2b4` or `a7a5`.
- Implement normal rook and queen movement. Pieces cannot jump over other
  pieces, may capture an opposing piece, and may not land on a piece of the
  same side.
- You do not need SAN, PGN, FEN, check, checkmate, castling, en passant, or
  other pieces.

## Part 1: Move variations

Represent move history as a tree whose root is the initial position. For
example:

```text
Start
├── b2b4
│   ├── a7a5
│   └── c7c5
└── d2d4
    └── e7e5
```

The user must be able to:

- Play a valid move from the selected position. This creates and selects a
  child node.
- Select an earlier node, including `Start`, and play a different move to
  create a variation.
- Select any visible node and see its position on the chessboard.
- Reset the complete tree, board, and analysis state to the initial position.

If the same LAN move already exists below the same parent, select and reuse
that child instead of creating a duplicate. The same resulting board position
may still occur through distinct paths in the tree. Reusing or revisiting a
node should preserve any evaluation already attached to it.

Display all branches in a simple tree or indented list. Each move row should
show its LAN move, and the selected node should be obvious. PGN-style formatting
and production-quality tree graphics are not expected.

## Part 2: Delayed computer move

The **Computer move** button requests a random valid move for the position
selected when the button is tapped. Return and apply the move after a random
delay of 1–3 seconds.

Only one computer-move request may be pending: starting another cancels the
previous one.

If the user has not changed the visible position while the request was
pending, add or reuse the resulting child, select it, and display its board
position. If the user has moved or navigated elsewhere, the delayed result must
not change the selected node or visible board. In that case, you may either
discard it or add it to its original branch without selecting it.

## Part 3: Asynchronous analysis

When **Analysis** is tapped:

1. Analyze all move nodes currently in the tree. The `Start` node
   does not need an evaluation.
2. Start one asynchronous operation with a random delay of 1–3 seconds.
3. Return a simulated evaluation of `+`, `-`, or `=` for every position in that
   snapshot and display it beside the corresponding move.

The evaluations may be random or deterministic; real chess analysis is out of
scope.

Analysis may be started again while an earlier request is running. Keep the
latest available evaluations visible and show a simple loading indication. An
older request may display its results while a newer one is still running. Once
the newer request completes, its results take precedence and no older request
may overwrite them.

Analysis must never change the selected node or visible board position.

Reset should cancel or invalidate pending computer-move and analysis work so
that delayed results cannot alter the reset state.

## Optional bonus

Preserve the board, move tree, selection, and evaluations across configuration
changes or activity recreation.
