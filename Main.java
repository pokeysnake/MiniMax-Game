import java.util.Scanner;


public class Main {
    // Game board constants
    private static final int BOARD_SIZE = 8;        // 8x8 board
    private static final int EMPTY = 0;             // Empty cell
    private static final int COMPUTER = 1;          // Computer player (X)
    private static final int HUMAN = 2;             // Human player (O)
    private static final int MAX_DEPTH = 10;        // Maximum search depth
    
    // Game state variables
    private int[][] board;                          // 8x8 game board
    private boolean computerFirst;                  // Whether computer goes first
    private int timeLimitSeconds;                   // Time limit for computer moves
    private long startTime;                         // Start time for move calculation
    
    //Main method
    public Main() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        setup();
    }
    
    //Main Method that starts the game
    public static void main(String[] args) {
        Main game = new Main();
        game.play();
    }
    
    //Creates the board with empty cells
    private void setup() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = EMPTY;
            }
        }
    }
    
    /**
     * Main game loop
     * Handles turn order and game flow
     */
    private void play() {
        Scanner scanner = new Scanner(System.in);
        
        // Get user preferences
        System.out.print("Would you like to go first? (y/n): ");
        String choice = scanner.nextLine().trim();
        computerFirst = !choice.equalsIgnoreCase("y");
        
        System.out.print("How long should the computer think about its moves (in seconds)? : ");
        timeLimitSeconds = Integer.parseInt(scanner.nextLine().trim());
        
        printBoard();
        
        // Main game loop
        while (true) {
            if (computerFirst) {
                // Computer goes first
                makeMove();
                printBoard();
                checkGameOver();
                
                getAMove(scanner);
                printBoard();
                checkGameOver();
            } else {
                // Human goes first
                getAMove(scanner);
                printBoard();
                checkGameOver();
                
                makeMove();
                printBoard();
                checkGameOver();
            }
        }
    }
    
    //Makes the computer move
    private void makeMove() {
        startTime = System.currentTimeMillis();
        
        int best = Integer.MIN_VALUE;
        int score, mi = -1, mj = -1;
        
        // Iterative deepening where start from depth 1 and go deeper until time runs out
        for (int depth = 1; depth <= MAX_DEPTH; depth++) {
            if (isTimeUp()) break; // Stop if time limit reached
            
            int currentBest = Integer.MIN_VALUE;
            int currentMi = -1, currentMj = -1;
            
            // Evaluate all possible moves at current depth
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] == EMPTY && !isTimeUp()) {
                        board[i][j] = COMPUTER; // make move on board
                        score = min(depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
                        if (score > currentBest) {
                            currentMi = i;
                            currentMj = j;
                            currentBest = score;
                        }
                        board[i][j] = EMPTY; // undo move
                    }
                }
            }
            
            // Update best move if we found a better one at this depth
            if (!isTimeUp() && currentMi != -1 && currentMj != -1) {
                mi = currentMi;
                mj = currentMj;
                best = currentBest;
            }
        }
        
        // Make the best move found
        if (mi != -1 && mj != -1) {
            board[mi][mj] = COMPUTER;
            System.out.println((char)('A' + mi) + "" + (mj + 1));
        } else {
            // Fallback: make any available move if no best move found
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = COMPUTER;
                        System.out.println((char)('A' + i) + "" + (j + 1));
                        return;
                    }
                }
            }
        }
    }
    
    //Checks if the time limit has been reached
    private boolean isTimeUp() {
        return (System.currentTimeMillis() - startTime) > (timeLimitSeconds * 1000);
    }
    
    //Minimax function
    private int min(int depth, int alpha, int beta) {
        int best = Integer.MAX_VALUE;
        int score;
        
        // Cut-off test: check for terminal state or time limit
        if (check4Winner() != 0) return check4Winner();
        if (depth == 0 || isTimeUp()) return evaluate();
        
        // Try all possible moves for the human player
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY && !isTimeUp()) {
                    board[i][j] = HUMAN; // make move on board
                    score = max(depth - 1, alpha, beta);
                    if (score < best) best = score;
                    board[i][j] = EMPTY; // undo move
                    
                    // Alpha-beta pruning
                    beta = Math.min(beta, score);
                    if (beta <= alpha) break; // Beta cutoff
                }
            }
        }
        return best;
    }
    
    //Max function
    private int max(int depth, int alpha, int beta) {
        int best = Integer.MIN_VALUE;
        int score;
        
        // Cut-off test: check for terminal state or time limit
        if (check4Winner() != 0) return check4Winner();
        if (depth == 0 || isTimeUp()) return evaluate();
        
        // Try all possible moves for the computer player
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY && !isTimeUp()) {
                    board[i][j] = COMPUTER; // make move on board
                    score = min(depth - 1, alpha, beta);
                    if (score > best) best = score;
                    board[i][j] = EMPTY; // undo move
                    
                    // Alpha-beta pruning
                    alpha = Math.max(alpha, score);
                    if (beta <= alpha) break; // Beta cutoff
                }
            }
        }
        return best;
    }
    
    //Evaluates the board
    private int evaluate() {
        int computerScore = 0;
        int humanScore = 0;
        
        // Check rows for potential 4-in-a-line
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j <= BOARD_SIZE - 4; j++) {
                computerScore += evaluateLine(i, j, 0, 1, COMPUTER);
                humanScore += evaluateLine(i, j, 0, 1, HUMAN);
            }
        }
        
        // Check columns for potential 4-in-a-line
        for (int i = 0; i <= BOARD_SIZE - 4; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                computerScore += evaluateLine(i, j, 1, 0, COMPUTER);
                humanScore += evaluateLine(i, j, 1, 0, HUMAN);
            }
        }
        
        return computerScore - humanScore;
    }
    
    //Evaluates a specific line of 4 positions for a player
    private int evaluateLine(int startRow, int startCol, int deltaRow, int deltaCol, int player) {
        int count = 0;
        int empty = 0;
        
        // Count player pieces and empty spaces in this line
        for (int i = 0; i < 4; i++) {
            int row = startRow + i * deltaRow;
            int col = startCol + i * deltaCol;
            
            if (board[row][col] == player) {
                count++;
            } else if (board[row][col] == EMPTY) {
                empty++;
            }
        }
        
        // Score based on how close to winning
        if (count == 4) return 1000;  // Win
        if (count == 3 && empty == 1) return 100;  // Near win
        if (count == 2 && empty == 2) return 10;   // Potential
        if (count == 1 && empty == 3) return 1;    // Start
        
        return 0;
    }
    
    //Checks if there's a winner or if the game is a draw, returns =-5000 depending on who wins 
    private int check4Winner() {
        // Check rows for 4-in-a-line
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j <= BOARD_SIZE - 4; j++) {
                if (checkLine(i, j, 0, 1, COMPUTER)) return 5000;  // computer wins
                if (checkLine(i, j, 0, 1, HUMAN)) return -5000;    // human wins
            }
        }
        
        // Check columns for 4-in-a-line
        for (int i = 0; i <= BOARD_SIZE - 4; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (checkLine(i, j, 1, 0, COMPUTER)) return 5000;  // computer wins
                if (checkLine(i, j, 1, 0, HUMAN)) return -5000;    // human wins
            }
        }
        
        // Check for draw (board is full)
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY) return 0; // game not over
            }
        }
        return 1; // draw
    }
    
    //Checks if a specific line of 4 positions contains all the same player's pieces
    private boolean checkLine(int startRow, int startCol, int deltaRow, int deltaCol, int player) {
        for (int i = 0; i < 4; i++) {
            int row = startRow + i * deltaRow;
            int col = startCol + i * deltaCol;
            if (board[row][col] != player) {
                return false;
            }
        }
        return true;
    }
    
    //Gets the human player move
    private void getAMove(Scanner scanner) {
        while (true) {
            System.out.print("Choose your next move: ");
            String move = scanner.nextLine().trim().toUpperCase();
            
            // Check for empty input
            if (move.isEmpty()) {
                System.out.println("Please enter a move (e.g., A1).");
                continue;
            }
            
            // Check format
            if (move.length() != 2) {
                System.out.println("Invalid format. Use format like A1 (row A-H, column 1-8).");
                continue;
            }
            
            char rowChar = move.charAt(0);
            char colChar = move.charAt(1);
            
            // Check bounds
            if (rowChar < 'A' || rowChar > 'H') {
                System.out.println("Invalid row. Row must be A-H.");
                continue;
            }
            
            if (colChar < '1' || colChar > '8') {
                System.out.println("Invalid column. Column must be 1-8.");
                continue;
            }
            
            int row = rowChar - 'A';
            int col = colChar - '1';
            
            // Check if position is already occupied
            if (board[row][col] != EMPTY) {
                System.out.println("Position " + move + " is already occupied. Try again.");
                continue;
            }
            
            board[row][col] = HUMAN;
            break;
        }
    }
    
    //Prints the board
    private void printBoard() {
        System.out.println("  1 2 3 4 5 6 7 8");
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print((char)('A' + i) + " ");
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    System.out.print("- ");
                } else if (board[i][j] == COMPUTER) {
                    System.out.print("X ");
                } else {
                    System.out.print("O ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
    
    //Checks if the game is over and displays the appropriate message
    private void checkGameOver() {
        if (check4Winner() == -5000) {
            System.out.println("Game Over!");
            System.out.println("You win");
            System.exit(0);
        }
        if (check4Winner() == 5000) {
            System.out.println("Game Over!");
            System.out.println("Computer Wins");
            System.exit(0);
        }
        if (check4Winner() == 1) {
            System.out.println("Game Over!");
            System.out.println("Draw");
            System.exit(0);
        }
    }
}
