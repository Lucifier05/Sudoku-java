import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SudokuGeneratorTest {

    private SudokuGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new SudokuGenerator();
    }

    @Test
    void generatedPuzzleHasCorrectSize() {
        int[][] puzzle = generator.generate();
        assertEquals(9, puzzle.length);
        for (int[] row : puzzle) assertEquals(9, row.length);
    }

    @Test
    void generatedPuzzleContainsOnlyValidValues() {
        int[][] puzzle = generator.generate();
        for (int[] row : puzzle) {
            for (int val : row) {
                assertTrue(val >= 0 && val <= 9, "Value out of range: " + val);
            }
        }
    }

    @Test
    void easyDifficultyRemovesFewerCells() {
        int[][] easy = generator.generate(SudokuGenerator.Difficulty.EASY);
        int emptyEasy = countEmpty(easy);

        generator = new SudokuGenerator();
        int[][] hard = generator.generate(SudokuGenerator.Difficulty.HARD);
        int emptyHard = countEmpty(hard);

        assertTrue(emptyEasy < emptyHard,
                "EASY should have fewer empty cells than HARD");
    }

    @Test
    void solutionIsComplete() {
        generator.generate();
        int[][] solution = generator.getSolution();
        for (int[] row : solution) {
            for (int val : row) {
                assertTrue(val >= 1 && val <= 9, "Solution has invalid value: " + val);
            }
        }
    }

    @Test
    void solutionRowsContainAllDigits() {
        generator.generate();
        int[][] solution = generator.getSolution();
        for (int i = 0; i < 9; i++) {
            boolean[] seen = new boolean[10];
            for (int j = 0; j < 9; j++) {
                int v = solution[i][j];
                assertFalse(seen[v], "Duplicate " + v + " in row " + i);
                seen[v] = true;
            }
        }
    }

    @Test
    void solutionColumnsContainAllDigits() {
        generator.generate();
        int[][] solution = generator.getSolution();
        for (int j = 0; j < 9; j++) {
            boolean[] seen = new boolean[10];
            for (int i = 0; i < 9; i++) {
                int v = solution[i][j];
                assertFalse(seen[v], "Duplicate " + v + " in col " + j);
                seen[v] = true;
            }
        }
    }

    @Test
    void validateSolutionReturnsTrueForCorrectAnswer() {
        generator.generate();
        int[][] solution = generator.getSolution();
        assertTrue(generator.validateSolution(solution));
    }

    @Test
    void validateSolutionReturnsFalseForWrongAnswer() {
        generator.generate();
        int[][] solution = generator.getSolution();
        // Corrupt one cell
        solution[0][0] = (solution[0][0] % 9) + 1; // change to a different digit
        // May accidentally stay the same if wrapping — brute force a guaranteed change
        int[][] wrong = generator.getSolution();
        wrong[4][4] = wrong[4][4] == 1 ? 2 : 1;
        assertFalse(generator.validateSolution(wrong));
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private int countEmpty(int[][] grid) {
        int count = 0;
        for (int[] row : grid) for (int v : row) if (v == 0) count++;
        return count;
    }
}
