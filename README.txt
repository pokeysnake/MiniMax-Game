PROJECT 3
NICHOLAS PEREZ
CS4200


HOW TO PLAY:
- The game is played on an 8x8 board (A1-H8)
- Players take turns placing pieces (X = Computer, O = Human)
- First to get 4 pieces in a line (rows or columns only) wins
- No diagonals count for winning

GAME SETUP:
- Choose who goes first (Computer or Human)
- Set time limit for computer moves (default 5 seconds)

MAKING MOVES:
- Enter moves in format: A1, B3, C5, etc.
- Row: A-H, Column: 1-8
- Program validates all input and asks for re-entry if invalid

PROGRAM FEATURES:
- Alpha-beta pruning for efficient search
- Iterative deepening (searches 6-8 plies deep)
- Time management (5-second limit)
- Illegal move detection and validation
- Clear board display after each move

EXAMPLE GAME FLOW:
1. "Would you like to go first? (y/n): " - Enter y or n
2. "How long should the computer think about its moves (in seconds)? : " - Enter time limit
3. Board displays with empty positions (-)
4. Take turns entering moves (A1, B2, etc.)
5. Game ends when someone gets 4 in a line or board is full

ERROR HANDLING:
- Invalid format: "Invalid format. Use format like A1 (row A-H, column 1-8)."
- Out of bounds: "Invalid row. Row must be A-H." or "Invalid column. Column must be 1-8."
- Occupied position: "Position A1 is already occupied. Try again."

The computer uses MINIMAX algorithm with alpha-beta pruning to find optimal moves within the time limit. 