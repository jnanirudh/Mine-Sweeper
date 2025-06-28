import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MineSweeper extends JPanel {
    int GRID_SIZE;
    Cell[][] cells;
    int[][] mineCoordinates;
    int mineCount;
    boolean gameOver = false;
    boolean gameWon = false;
    MineSweeper myOwnReference = null;

    public MineSweeper() {
        this.setPreferredSize(new Dimension(450, 450));

        myOwnReference = this;
        changeDifficultyLevel();

        //cell class object creation
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cells[row][col] = new Cell();
            }
        }

        placeMines();
        AdjMineCount();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int cellSize = Math.min(getWidth(), getHeight()) / GRID_SIZE;
                int col = e.getX() / cellSize;
                int row = e.getY() / cellSize;

                if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
                    if (SwingUtilities.isLeftMouseButton(e) && !SwingUtilities.isRightMouseButton(e)) {
                        if (!cells[row][col].isFlagged()) {
                            cells[row][col].reveal();

                            if (cells[row][col].isMine()) {
                                gameOver = true;

                                JOptionPane.showMessageDialog(this, "Game Over!");
                            }
                            else if (cells[row][col].getAdjacentMines() == 0) {
                                floodReveal(row, col);
                            }

                            if (checkWin()) {
                                gameWon = true;
                                JOptionPane.showMessageDialog(this, "Congratulations, You won!");
                            }
                        }
                    }
                    else if (SwingUtilities.isRightMouseButton(e)) {
                        if (!cells[row][col].isRevealed()) {
                            cells[row][col].toggleFlag();
                        }
                    }

                    repaint();
                }
            }
        });
    }

    //-------Method to randomly place mines---------
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
        for( int i = 0 ; i < mineCount; i++) {
            int row = mineCoordinates[i][0];
            int col = mineCoordinates[i][1];

            for (int j = -1; j <= 1; j++) {
                for (int k = 1; k >= -1; k++) {
                    // now the coordinates of the cell is [row+i][col+j]
                    cells[row+i][col+j].incrementNumber();
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // real time dimention of the pannel
        int realtime_width = getWidth();
        int realtime_height = getHeight();
        int cellSize = Math.min(realtime_width, realtime_height) / GRID_SIZE;

        // draw the grid
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int x = col * cellSize;
                int y = row * cellSize;
                Cell cell = cells[row][col];

                //background color
                if (cell.isRevealed()) {
                    if (cell.isMine()) {
                        g.setColor(Color.RED);
                    } else {
                        g.setColor(new Color(255, 255, 153));
                    }
                } else {
                    g.setColor(new Color(144, 238, 144));
                }
                g.fillRect(x, y, cellSize, cellSize);

                //Draw flag
                if (cell.isFlagged() && !cell.isRevealed()) {
                    g.setColor(Color.RED);
                    g.drawString("F", x + cellSize / 2 - 4, y + cellSize / 2 + 4);
                }

                //Draw mine or number
                if (cell.isRevealed()) {
                    if (cell.isMine()) {
                        g.setColor(Color.BLACK);
                        g.drawString("*", x + cellSize / 2 - 4, y + cellSize / 2 + 4);
                    } else if (cell.getAdjacentMines() > 0) {
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
        JFrame frame = new JFrame("Mine Sweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new MineSweeper());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void floodReveal(int row , int col){
        // fix out of bounds problem
        if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE) {
            return;
        }

        //stop recursion if it is revealed/flagged/mine
        if(cells[row][col].isRevealed() || cells[row][col].isFlagged() || cells[row][col].isMine() ){
            return;
        }

        cells[row][col].reveal();

        if (cells[row][col].getAdjacentMines() > 0) {
            return;
        }

        //flooding to all adjacent cells
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {

                if (i == 0 && j == 0) {
                    continue;
                }
                // recursively flood reveal the neighbor
                floodReveal(row + i, col + j);

            }
        }
    }


    private void changeDifficultyLevel()
    {
        System.out.println("The game can be played in three modes:\n 1. Easy\n 2. Medium \n 3. Hard \n Enter the mode of difficulty:");
        Scanner sc = new Scanner(System.in);
        int mode = sc.nextInt();
        sc.close();

        switch (mode) {
            case 1:
                GRID_SIZE = 10;
                mineCount = 10;
                break;
            case 2:
                GRID_SIZE = 16;
                mineCount = 40;
                break;
            case 3:
                GRID_SIZE = 24;
                mineCount = 99;
                break;
            default:
                GRID_SIZE = 10;
                mineCount = 10;
        }

        cells = new Cell[GRID_SIZE][GRID_SIZE];
        mineCoordinates = new int[mineCount][2];
    }
}
