import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class MineSweeper extends JPanel {
    int GRID_SIZE;
    Cell[][] cells;
    int[][] mineCoordinates;
    int mineCount;
    boolean gameOver = false;
    boolean gameWon = false;
    boolean gameStarted = false;

    // UI Components
    private JFrame parentFrame;
    private JLabel timerLabel;
    private JLabel flagCountLabel;
    private JButton restartButton;
    private JButton backToMenuButton;
    private Timer gameTimer;
    private int elapsedTime = 0;
    private int flagsUsed = 0;

    // Images
    private BufferedImage mineImage;
    private BufferedImage flagImage;

    private static final String MINE_IMAGE_PATH = "images\\blast.png";
    private static final String FLAG_IMAGE_PATH = "images\\flag.png";

    public MineSweeper(JFrame frame) {
        this.parentFrame = frame;
        this.setPreferredSize(new Dimension(450, 450));

        loadImages();
        setupUI();
        initializeGame();
    }

    private void loadImages() {
        try {
            mineImage = javax.imageio.ImageIO.read(new java.io.File(MINE_IMAGE_PATH));
            flagImage = javax.imageio.ImageIO.read(new java.io.File(FLAG_IMAGE_PATH));
        } catch (Exception e) {
            System.out.println("Warning: Could not load images. Using text fallback.");
            // Images will be null and fallback text will be used
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Top panel for game info
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(Color.LIGHT_GRAY);

        timerLabel = new JLabel("Time: 0");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));

        flagCountLabel = new JLabel("Flags: 0/" + mineCount);
        flagCountLabel.setFont(new Font("Arial", Font.BOLD, 16));

        restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> restartGame());

        backToMenuButton = new JButton("Back to Menu");
        backToMenuButton.addActionListener(e -> backToMenu());

        topPanel.add(timerLabel);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(flagCountLabel);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(restartButton);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(backToMenuButton);

        add(topPanel, BorderLayout.SOUTH);

        // Game timer
        gameTimer = new Timer(1000, e -> {
            if (!gameOver && !gameWon && gameStarted) {
                elapsedTime++;
                timerLabel.setText("Time: " + elapsedTime);
            }
        });
    }
    private void backToMenu() {
        // Stop the game timer
        if (gameTimer != null) {
            gameTimer.stop();
        }

        // Show confirmation dialog
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to go back to the menu? Current game will be lost.",
                "Back to Menu",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            // Reset game state
            gameOver = false;
            gameWon = false;
            gameStarted = false;
            elapsedTime = 0;
            flagsUsed = 0;

            // Remove mouse listeners
            removeMouseListeners();

            // Show difficulty selection again
            initializeGame();
        } else {
            // Resume the timer if game was in progress
            if (gameStarted && !gameOver && !gameWon) {
                gameTimer.start();
            }
        }
    }

    public void initializeGame() {
        String[] options = {"Easy", "Medium", "Hard"};
        int choice = JOptionPane.showOptionDialog(this, "Select difficulty level:", "Minesweeper - Choose Difficulty",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == -1) {
            System.exit(0);
        }

        changeDifficultyLevel(choice);
        restartGame();
    }

    private void changeDifficultyLevel(int mode) {
        switch (mode) {
            case 0:
                GRID_SIZE = 10;
                mineCount = 10;
                break;
            case 1:
                GRID_SIZE = 16;
                mineCount = 40;
                break;
            case 2:
                GRID_SIZE = 24;
                mineCount = 99;
                break;
            default:
                GRID_SIZE = 10;
                mineCount = 10;
        }

        cells = new Cell[GRID_SIZE][GRID_SIZE];
        mineCoordinates = new int[mineCount][2];

        // Adjust window size based on difficulty
        int panelSize = Math.min(600, GRID_SIZE * 25);
        setPreferredSize(new Dimension(panelSize, panelSize + 50));

        if (parentFrame != null) {
            parentFrame.pack();
            parentFrame.setLocationRelativeTo(null);
        }

    }

    //--------Restart the Game-----------
    private void restartGame() {
        gameOver = false;
        gameWon = false;
        gameStarted = false;
        elapsedTime = 0;
        flagsUsed = 0;

        // Initialize cells
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cells[row][col] = new Cell();
            }
        }

        placeMines();
        AdjMineCount();
        updateUI();

        // Setup mouse listener
        removeMouseListeners();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e);
            }
        });

        gameTimer.stop();
        repaint();
    }

    //---------Will not allow more clicks after game ends-------
    private void removeMouseListeners() {
        MouseListener[] listeners = getMouseListeners();
        for (MouseListener listener : listeners) {
            removeMouseListener(listener);
        }
    }

    //------------Mouse clicker------------
    private void handleMouseClick(MouseEvent e) {
        if (gameOver || gameWon) return;

        if (!gameStarted) {
            gameStarted = true;
            gameTimer.start();
        }

        int cellSize = Math.min(getWidth(), getHeight()) / GRID_SIZE;
        int col = e.getX() / cellSize;
        int row = e.getY() / cellSize;

        if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (!cells[row][col].isFlagged()) {
                    if (cells[row][col].isMine()) {
                        gameOver = true;
                        gameTimer.stop();
                        repaint();
                        JOptionPane.showMessageDialog(this, "Game Over!" + elapsedTime);
                    }
                    else if (cells[row][col].getAdjacentMines() == 0) {
                        floodReveal(row, col);
                    }

                    cells[row][col].reveal();

                    if (checkWin()) {
                        gameWon = true;
                        gameTimer.stop();
                        JOptionPane.showMessageDialog(this, "Congratulations, You won!\nTime taker:" + elapsedTime);
                    }
                }
//                else if()
            }
            else if (SwingUtilities.isRightMouseButton(e)) {
                if (!cells[row][col].isRevealed()) {
                    if (cells[row][col].isFlagged()) {
                        flagsUsed--;
                    } else {
                        if(flagsUsed < mineCount){
                            flagsUsed++;
                        }
                        else{
                            JOptionPane.showMessageDialog(this,"No flags available\nTime taker:" + elapsedTime);
                        }
                    }
                    cells[row][col].toggleFlag();
                    updateUI();
                }
            }

            repaint();
        }
    }

    //------------Method to randomly place mines-------------
    private void placeMines() {

        mineCoordinates = new int[mineCount][2];
        int placed = 0;
        Random random = new Random();

        while (placed < mineCount) {
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);

            if (!cells[row][col].isMine()) {
                cells[row][col].setMine(true);
                mineCoordinates[placed][0] = row;
                mineCoordinates[placed][1] = col;
                placed++;
            }
        }
    }

    //------Method to count Adjcent mines-----------
    private void AdjMineCount() {
        for (int i = 0; i < mineCount; i++) {
            int row = mineCoordinates[i][0];
            int col = mineCoordinates[i][1];

            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {

                    if (j == 0 && k == 0) {
                        continue;
                    }

                    int adjRow = row + j;
                    int adjCol = col + k;

                    if (adjRow >= 0 && adjRow < GRID_SIZE &&
                            adjCol >= 0 && adjCol < GRID_SIZE) {

                        int currentCount = cells[adjRow][adjCol].getAdjacentMines();
                        cells[adjRow][adjCol].setAdjacentMines(currentCount + 1);
                    }
                }
            }
        }
    }

    private boolean checkWin() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {

                if (!cells[row][col].isMine() && !cells[row][col].isRevealed()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void floodReveal(int row, int col) {
        // Fix out of bounds problem
        if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE) {
            return;
        }

        // Stop recursion if it is revealed/flagged/mine
        if (cells[row][col].isRevealed() || cells[row][col].isFlagged() || cells[row][col].isMine()) {
            return;
        }

        cells[row][col].reveal();

        if (cells[row][col].getAdjacentMines() > 0) {
            return;
        }

        // Flooding to all adjacent cells
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {

                if (i == 0 && j == 0) {
                    continue;
                }

                // Recursively flood reveal the neighbor
                floodReveal(row + i, col + j);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // real time dimention of the pannel
        int availableWidth = getWidth();
        int availableHeight = getHeight() - 50; // Account for the top panel
        int cellSize = Math.min(availableWidth, availableHeight) / GRID_SIZE;

        int gridWidth = GRID_SIZE * cellSize;
        int gridHeight = GRID_SIZE * cellSize;
        int offsetX = (availableWidth - gridWidth) / 2;
        int offsetY = (availableHeight - gridHeight) / 2;

        // draw the grid
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int x = col * cellSize;
                int y = row * cellSize;
                Cell cell = cells[row][col];

                //background color
                if (cell.isRevealed()) {
                    if (cell.isMine()) {
                        g.setColor(new Color(144, 238, 144));
                    } else {
                        g.setColor(new Color(255, 255, 153));
                    }
                } else {
                    g.setColor(new Color(144, 238, 144));
                }
                g.fillRect(x, y, cellSize, cellSize);

                //Draw flag
                if (cell.isFlagged() && !cell.isRevealed()) {
                    if (flagImage != null) {
                        // Scale and draw the flag image
                        int imageSize = (int)(cellSize);
                        int imageX = x + (cellSize - imageSize) / 2;
                        int imageY = y + (cellSize - imageSize) / 2;
                        g.drawImage(flagImage, imageX, imageY, imageSize, imageSize, this);
                    } else {
                        // Fallback to text if image not loaded
                        g.setColor(Color.RED);
                        g.setFont(new Font("Arial", Font.BOLD, cellSize/2));
                        FontMetrics fm = g.getFontMetrics();
                        String text = "F";
                        int textX = x + (cellSize - fm.stringWidth(text)) / 2;
                        int textY = y + (cellSize + fm.getAscent()) / 2;
                        g.drawString(text, textX, textY);
                    }
                }

                //Draw mine or number
                if (cell.isRevealed()) {
                    if (cell.isMine()) {
                        if (mineImage != null) {
                            int imageSize = (int)(cellSize * 1.5);
                            int imageX = x + (cellSize - imageSize) / 2;
                            int imageY = y + (cellSize - imageSize) / 2;
                            g.drawImage(mineImage, imageX, imageY, imageSize, imageSize, this);
                        } else {
                            // Fallback to text if image not loaded
                            g.setColor(Color.BLACK);
                            g.setFont(new Font("Arial", Font.BOLD, cellSize/2));
                            FontMetrics fm = g.getFontMetrics();
                            String text = "*";
                            int textX = x + (cellSize - fm.stringWidth(text)) / 2;
                            int textY = y + (cellSize + fm.getAscent()) / 2;
                            g.drawString(text, textX, textY);
                        }
                    }
                    else if (cell.getAdjacentMines() > 0) {
                        g.setColor(Color.BLACK);
                        g.drawString(String.valueOf(cell.getAdjacentMines()),
                                x + cellSize / 2 - 4, y + cellSize / 2 + 4);
                    }
                }

                //Draw border
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Mine Sweeper");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            MineSweeper game = new MineSweeper(frame);
            frame.add(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }

}