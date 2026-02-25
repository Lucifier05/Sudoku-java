import java.util.Random;

public class SudokuGenerator {
    private static final int SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private final int[][] board;
    private final int[][] solution;

    public enum Difficulty {
        EASY(36),       // remove 36 cells (~40 clues)
        MEDIUM(46),     // remove 46 cells (~35 clues)
        HARD(52);       // remove 52 cells (~29 clues)

        final int cellsToRemove;
        Difficulty(int cellsToRemove) { this.cellsToRemove = cellsToRemove; }
    }

    public SudokuGenerator() {
        board = new int[SIZE][SIZE];
        solution = new int[SIZE][SIZE];
    }

    public int[][] generate(Difficulty difficulty) {
        clearBoard();
        fillDiagonal();
        fillRemaining(0, SUBGRID_SIZE);
        // Save solution before removing digits
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(board[i], 0, solution[i], 0, SIZE);
        }
        removeDigits(difficulty.cellsToRemove);
        return board;
    }

    /** Backwards-compatible overload defaulting to MEDIUM */
    public int[][] generate() {
        return generate(Difficulty.MEDIUM);
    }

    public int[][] getSolution() {
        int[][] copy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(solution[i], 0, copy[i], 0, SIZE);
        }
        return copy;
    }

    private void clearBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = 0;
            }
        }
    }

    private void fillDiagonal() {
        for (int i = 0; i < SIZE; i += SUBGRID_SIZE) {
            fillSubgrid(i, i);
        }
    }

    private void fillSubgrid(int row, int col) {
        Random random = new Random();
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                int num;
                do {
                    num = random.nextInt(SIZE) + 1;
                } while (!isSafeInSubgrid(row, col, num));
                board[row + i][col + j] = num;
            }
        }
    }

    private boolean isSafeInSubgrid(int row, int col, int num) {
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                if (board[row + i][col + j] == num) return false;
            }
        }
        return true;
    }

    private boolean fillRemaining(int i, int j) {
        if (j >= SIZE && i < SIZE - 1) { i++; j = 0; }
        if (i >= SIZE && j >= SIZE) return true;

        if (i < SUBGRID_SIZE) {
            if (j < SUBGRID_SIZE) j = SUBGRID_SIZE;
        } else if (i < SIZE - SUBGRID_SIZE) {
            if (j == (i / SUBGRID_SIZE) * SUBGRID_SIZE) j += SUBGRID_SIZE;
        } else {
            if (j == SIZE - SUBGRID_SIZE) {
                i++;
                j = 0;
                if (i >= SIZE) return true;
            }
        }

        for (int num = 1; num <= SIZE; num++) {
            if (isSafe(i, j, num)) {
                board[i][j] = num;
                if (fillRemaining(i, j + 1)) return true;
                board[i][j] = 0;
            }
        }
        return false;
    }

    private boolean isSafe(int i, int j, int num) {
        return isSafeInRow(i, num)
            && isSafeInCol(j, num)
            && isSafeInSubgrid(i - i % SUBGRID_SIZE, j - j % SUBGRID_SIZE, num);
    }

    private boolean isSafeInRow(int i, int num) {
        for (int j = 0; j < SIZE; j++) {
            if (board[i][j] == num) return false;
        }
        return true;
    }

    private boolean isSafeInCol(int j, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (board[i][j] == num) return false;
        }
        return true;
    }

    private void removeDigits(int count) {
        Random random = new Random();
        while (count > 0) {
            int i = random.nextInt(SIZE);
            int j = random.nextInt(SIZE);
            if (board[i][j] != 0) {
                board[i][j] = 0;
                count--;
            }
        }
    }

    /**
     * Validates a completed user grid against the stored solution.
     * Returns true only if every cell matches.
     */
    public boolean validateSolution(int[][] userGrid) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (userGrid[i][j] != solution[i][j]) return false;
            }
        }
        return true;
    }

    public void printBoard() {
        for (int r = 0; r < SIZE; r++) {
            for (int d = 0; d < SIZE; d++) {
                System.out.print(board[r][d] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        SudokuGenerator sudoku = new SudokuGenerator();
        sudoku.generate(Difficulty.MEDIUM);
        sudoku.printBoard();
    }
}
