import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SudokuApp extends JFrame {

    private static final int SIZE = 9;
    private static final Color CLR_GIVEN     = new Color(30, 30, 30);
    private static final Color CLR_USER      = new Color(25, 100, 200);
    private static final Color CLR_ERROR     = new Color(220, 50, 50);
    private static final Color CLR_CELL_BG   = Color.WHITE;
    private static final Color CLR_GIVEN_BG  = new Color(235, 235, 235);

    private final JTextField[][] cells = new JTextField[SIZE][SIZE];
    private final boolean[][] isGiven  = new boolean[SIZE][SIZE];
    private final SudokuGenerator generator = new SudokuGenerator();

    private JLabel timerLabel;
    private Timer swingTimer;
    private int secondsElapsed;

    public SudokuApp() {
        setTitle("Sudoku");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildGrid(),      BorderLayout.CENTER);
        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildButtonBar(), BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(520, 560));
        setLocationRelativeTo(null);
    }

    // ── Top bar: timer ───────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timerLabel = new JLabel("Time: 0:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bar.add(timerLabel);
        return bar;
    }

    // ── 9×9 grid with thick borders around each 3×3 box ─────────────────────
    private JPanel buildGrid() {
        JPanel outer = new JPanel(new GridLayout(SIZE, SIZE, 0, 0));
        outer.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JTextField tf = new JTextField(1);
                tf.setHorizontalAlignment(JTextField.CENTER);
                tf.setFont(new Font("Arial", Font.BOLD, 22));
                tf.setBorder(cellBorder(i, j));

                // Allow only digits 1-9
                final int row = i, col = j;
                tf.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!Character.isDigit(c) || c == '0') {
                            e.consume();
                        } else {
                            // Replace current content
                            SwingUtilities.invokeLater(() -> {
                                String text = tf.getText();
                                if (text.length() > 1) tf.setText(String.valueOf(text.charAt(text.length() - 1)));
                                tf.setForeground(CLR_USER);
                            });
                        }
                    }
                });

                cells[i][j] = tf;
                outer.add(tf);
            }
        }
        return outer;
    }

    /**
     * Builds a cell border that draws thicker lines on the sides that border
     * a 3×3 box boundary, giving the classic Sudoku look.
     */
    private Border cellBorder(int row, int col) {
        int top    = (row % 3 == 0) ? 2 : 1;
        int left   = (col % 3 == 0) ? 2 : 1;
        int bottom = (row == SIZE - 1) ? 0 : 1;
        int right  = (col == SIZE - 1) ? 0 : 1;
        return new MatteBorder(top, left, bottom, right, Color.BLACK);
    }

    // ── Button bar ───────────────────────────────────────────────────────────
    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));

        // Difficulty chooser
        JComboBox<SudokuGenerator.Difficulty> diffBox =
                new JComboBox<>(SudokuGenerator.Difficulty.values());
        diffBox.setSelectedItem(SudokuGenerator.Difficulty.MEDIUM);

        JButton genBtn   = new JButton("New Game");
        JButton checkBtn = new JButton("Check Solution");
        JButton solveBtn = new JButton("Show Solution");

        genBtn.addActionListener((ActionEvent e) ->
                generateSudoku((SudokuGenerator.Difficulty) diffBox.getSelectedItem()));

        checkBtn.addActionListener((ActionEvent e) -> checkSolution());

        solveBtn.addActionListener((ActionEvent e) -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Show the solution? This will end the game.",
                    "Show Solution", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) revealSolution();
        });

        bar.add(new JLabel("Difficulty:"));
        bar.add(diffBox);
        bar.add(genBtn);
        bar.add(checkBtn);
        bar.add(solveBtn);
        return bar;
    }

    // ── Game logic ───────────────────────────────────────────────────────────
    private void generateSudoku(SudokuGenerator.Difficulty difficulty) {
        int[][] puzzle = generator.generate(difficulty);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JTextField tf = cells[i][j];
                tf.setEditable(true);
                if (puzzle[i][j] == 0) {
                    tf.setText("");
                    tf.setBackground(CLR_CELL_BG);
                    tf.setForeground(CLR_USER);
                    isGiven[i][j] = false;
                } else {
                    tf.setText(String.valueOf(puzzle[i][j]));
                    tf.setBackground(CLR_GIVEN_BG);
                    tf.setForeground(CLR_GIVEN);
                    tf.setEditable(false);
                    isGiven[i][j] = true;
                }
            }
        }
        resetTimer();
    }

    private void checkSolution() {
        // Build user grid; highlight errors in red
        int[][] userGrid = new int[SIZE][SIZE];
        boolean complete = true;
        boolean hasError = false;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (isGiven[i][j]) {
                    userGrid[i][j] = Integer.parseInt(cells[i][j].getText());
                } else {
                    String text = cells[i][j].getText().trim();
                    if (text.isEmpty()) {
                        complete = false;
                        cells[i][j].setBackground(CLR_CELL_BG); // reset any previous red
                    } else {
                        userGrid[i][j] = Integer.parseInt(text);
                    }
                }
            }
        }

        int[][] solution = generator.getSolution();

        // Colour-code each user cell
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (!isGiven[i][j]) {
                    String text = cells[i][j].getText().trim();
                    if (!text.isEmpty()) {
                        int val = Integer.parseInt(text);
                        if (val != solution[i][j]) {
                            cells[i][j].setBackground(new Color(255, 200, 200));
                            cells[i][j].setForeground(CLR_ERROR);
                            hasError = true;
                        } else {
                            cells[i][j].setBackground(new Color(200, 255, 200));
                            cells[i][j].setForeground(CLR_USER);
                        }
                    }
                }
            }
        }

        if (!complete) {
            JOptionPane.showMessageDialog(this,
                    "Some cells are still empty. Keep going!", "Not done yet",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (hasError) {
            JOptionPane.showMessageDialog(this,
                    "There are some mistakes — red cells are highlighted.", "Almost!",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            stopTimer();
            JOptionPane.showMessageDialog(this,
                    "🎉 Congratulations! Solved in " + timerLabel.getText().replace("Time: ", "") + "!",
                    "Puzzle Solved!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void revealSolution() {
        stopTimer();
        int[][] solution = generator.getSolution();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j].setText(String.valueOf(solution[i][j]));
                cells[i][j].setForeground(isGiven[i][j] ? CLR_GIVEN : CLR_USER);
                cells[i][j].setBackground(isGiven[i][j] ? CLR_GIVEN_BG : CLR_CELL_BG);
                cells[i][j].setEditable(false);
            }
        }
    }

    // ── Timer helpers ─────────────────────────────────────────────────────────
    private void resetTimer() {
        if (swingTimer != null) swingTimer.stop();
        secondsElapsed = 0;
        timerLabel.setText("Time: 0:00");
        swingTimer = new Timer(1000, e -> {
            secondsElapsed++;
            int mins = secondsElapsed / 60;
            int secs = secondsElapsed % 60;
            timerLabel.setText(String.format("Time: %d:%02d", mins, secs));
        });
        swingTimer.start();
    }

    private void stopTimer() {
        if (swingTimer != null) swingTimer.stop();
    }

    // ── Entry point ───────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SudokuApp().setVisible(true));
    }
}
