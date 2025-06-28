public class Cell {
    private boolean isMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private int adjacentMines;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void incrementNumber() {
        this.number++;
    }

    private int number = 0;

    //-------constructor-----------
    // initaially nothing is known
    public Cell() {
        isMine = false;
        isRevealed = false;
        isFlagged = false;
        adjacentMines = 0;
        number = 0;
    }

    // ------Getters and Setters-------
    //is a mine or not
    public boolean isMine(){
        return isMine;
    }
    public void setMine(boolean mine){
        isMine = mine;
    }

    //revealed or not
    public boolean isRevealed(){
        return isRevealed;
    }
    public void reveal(){
        isRevealed = true;
    }

    //flagged or not
    public boolean isFlagged(){
        return isFlagged;
    }
    public void toggleFlag(){
        isFlagged = !isFlagged;
    }

    //no. of mines
    public int getAdjacentMines(){
        return adjacentMines;
    }
    public void setAdjacentMines(int count){
        adjacentMines = count;
    }
}
