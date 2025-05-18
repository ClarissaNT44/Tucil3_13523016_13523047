package model;

/**
 * Represents a vehicle piece on the Rush Hour puzzle board.
 */
public class Piece {
    private char id;
    private int row;
    private int col;
    private int length;
    private boolean isHorizontal;
    private boolean isPrimary;

    /**
     * Creates a new piece with the specified properties.
     * 
     * @param id          Character identifier for this piece
     * @param row         Starting row position
     * @param col         Starting column position
     * @param length      Length of the piece
     * @param isHorizontal Whether the piece is oriented horizontally
     * @param isPrimary   Whether this is the primary piece
     */
    public Piece(char id, int row, int col, int length, boolean isHorizontal, boolean isPrimary) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.length = length;
        this.isHorizontal = isHorizontal;
        this.isPrimary = isPrimary;
    }

    /**
     * Creates a copy of this piece.
     * 
     * @return A new piece with the same properties
     */
    public Piece copy() {
        return new Piece(id, row, col, length, isHorizontal, isPrimary);
    }

    // Getters and setters
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

    /**
     * Checks if this piece occupies a specific cell.
     * 
     * @param r Row to check
     * @param c Column to check
     * @return True if this piece occupies the cell, false otherwise
     */
    public boolean occupies(int r, int c) {
        if (isHorizontal) {
            return r == row && c >= col && c < col + length;
        } else {
            return c == col && r >= row && r < row + length;
        }
    }

    /**
     * Returns a string representation of this piece.
     */
    @Override
    public String toString() {
        return id + (isPrimary ? "(P)" : "") + "[" + row + "," + col + "]";
    }
}
