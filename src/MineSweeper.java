import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MineSweeper extends JPanel {
    private static final int GRID_SIZE =10;
    private Cell[][] cells = new Cell[GRID_SIZE][GRID_SIZE];


    public MineSweeper() {
        this.setPreferredSize(new Dimension(450, 450));

        //cell class object creation
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cells[row][col] = new Cell();
            }
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int cellSize = Math.min(getWidth(), getHeight()) / GRID_SIZE;
                int col = e.getX() / cellSize;
                int row = e.getY() / cellSize;

                if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        cells[row][col].reveal();
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        cells[row][col].toggleFlag();
                    }
                    repaint();
                }
            }
        });

    }

    //-------Method to randomly place mines---------
    private void placeMines ( int mineCount){

        int placed = 0;
        int[][] mineCoordinates = new int[mineCount][2];

        while (placed < mineCount) {
            int row = (int) (Math.random() * GRID_SIZE);
            int col = (int) (Math.random() * GRID_SIZE);

            if (!cells[row][col].isMine()) {
                cells[row][col].setMine(true);
                placed++;
            }
        }
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // real time dimention of the pannel
        int realtime_width = getWidth();
        int realtime_height = getHeight();

        int cellSize = Math.min(realtime_width, realtime_height) / GRID_SIZE;

        // draw the grid
        for (int row = 1; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int x = col * cellSize;
                int y = row * cellSize;
                //g.drawRect(x, y, cellSize, cellSize);
                Cell cell = cells[row][col];

                if(cell.isRevealed()){
                    g.setColor(Color.yellow);
                }
                else{
                    g.setColor(Color.green);
                }
                g.fillRect(x, y, cellSize, cellSize);

                if (cell.isFlagged() && !cell.isRevealed()) {
                    g.setColor(Color.RED);
                    g.drawString("F", x + cellSize / 2 - 4, y + cellSize / 2 + 4);
                }
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);

            }
        }
    }


    public static void main (String[]args){
            JFrame frame = new JFrame("Mine Sweeper");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new MineSweeper());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

    }

}
