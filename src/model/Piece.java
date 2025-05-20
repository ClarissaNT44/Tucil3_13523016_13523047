package model;

public class Piece {
    private char id;
    private int row;
    private int col;
    private int length;
    private boolean isHorizontal;
    private boolean isPrimary;

    public Piece(char id, int row, int col, int length, boolean isHorizontal, boolean isPrimary) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.length = length;
        this.isHorizontal = isHorizontal;
        this.isPrimary = isPrimary;
    }

    public Piece copy() {
        return new Piece(id, row, col, length, isHorizontal, isPrimary);
    }

    public char getId() {
        return id;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getLength() {
        return length;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public boolean occupies(int r, int c) {
        if (isHorizontal) {
            return r == row && c >= col && c < col + length;
        } else {
            return c == col && r >= row && r < row + length;
        }
    }

    @Override
    public String toString() {
        return id + (isPrimary ? "(P)" : "") + "[" + row + "," + col + "]";
    }
}
