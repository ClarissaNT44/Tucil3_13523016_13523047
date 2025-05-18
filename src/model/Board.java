package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the Rush Hour puzzle board.
 */
public class Board {
    private int width;
    private int height;
    private Map<Character, Piece> pieces;
    private int exitRow;
    private int exitCol;
    private boolean exitOnRight;
    private boolean exitOnBottom;
    private char primaryPieceId;

    /**
     * Creates a new board with the specified dimensions.
     * 
     * @param width  Width of the board
     * @param height Height of the board
     */
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.pieces = new HashMap<>();
    }

    /**
     * Creates a copy of this board.
     * 
     * @return A new board with the same configuration
     */
    public Board copy() {
        Board newBoard = new Board(width, height);
        newBoard.exitRow = exitRow;
        newBoard.exitCol = exitCol;
        newBoard.exitOnRight = exitOnRight;
        newBoard.exitOnBottom = exitOnBottom;
        newBoard.primaryPieceId = primaryPieceId;
        
        for (Piece piece : pieces.values()) {
            Piece newPiece = piece.copy();
            newBoard.pieces.put(newPiece.getId(), newPiece);
        }
        
        return newBoard;
    }

    /**
     * Adds a piece to the board.
     * 
     * @param piece The piece to add
     */
    public void addPiece(Piece piece) {
        pieces.put(piece.getId(), piece);
        if (piece.isPrimary()) {
            primaryPieceId = piece.getId();
        }
    }

    /**
     * Sets the exit position.
     * 
     * @param row Row of the exit
     * @param col Column of the exit
     */
    public void setExit(int row, int col) {
        exitRow = row;
        exitCol = col;
        
        // Determine if exit is on right or bottom edge
        exitOnRight = (col == width - 1);
        exitOnBottom = (row == height - 1);
    }

    /**
     * Moves a piece in the specified direction.
     * 
     * @param pieceId   ID of the piece to move
     * @param direction Direction to move ("up", "down", "left", or "right")
     * @param steps     Number of cells to move
     * @return True if the move was successful, false otherwise
     */
    public boolean movePiece(char pieceId, String direction, int steps) {
        Piece piece = pieces.get(pieceId);
        if (piece == null) {
            return false;
        }

        int newRow = piece.getRow();
        int newCol = piece.getCol();

        // Calculate new position based on direction
        switch (direction) {
            case "up":
                if (piece.isHorizontal()) return false;
                newRow -= steps;
                break;
            case "down":
                if (piece.isHorizontal()) return false;
                newRow += steps;
                break;
            case "left":
                if (!piece.isHorizontal()) return false;
                newCol -= steps;
                break;
            case "right":
                if (!piece.isHorizontal()) return false;
                newCol += steps;
                break;
            default:
                return false;
        }

        // Check if the new position is valid
        if (!isValidMove(piece, newRow, newCol)) {
            return false;
        }

        // Special case: if the piece is the primary piece and it's exiting the board
        if (piece.isPrimary() && piece.isHorizontal() && exitOnRight && newCol + piece.getLength() > width) {
            if (exitRow == piece.getRow()) {
                // Primary piece can exit, allow it
                piece.setCol(newCol);
                return true;
            }
            return false;
        } else if (piece.isPrimary() && !piece.isHorizontal() && exitOnBottom && newRow + piece.getLength() > height) {
            if (exitCol == piece.getCol()) {
                // Primary piece can exit, allow it
                piece.setRow(newRow);
                return true;
            }
            return false;
        }

        // Update piece position
        piece.setRow(newRow);
        piece.setCol(newCol);
        return true;
    }

    /**
     * Checks if a move is valid (not outside the board, not overlapping other pieces).
     * 
     * @param piece  The piece to move
     * @param newRow New row position
     * @param newCol New column position
     * @return True if the move is valid, false otherwise
     */
    private boolean isValidMove(Piece piece, int newRow, int newCol) {
        // Check if the piece would be outside the board
        if (newRow < 0 || newCol < 0) {
            return false;
        }
        
        if (piece.isHorizontal()) {
            if (newCol + piece.getLength() > width && 
                !(piece.isPrimary() && exitOnRight && piece.getRow() == exitRow)) {
                return false;
            }
        } else {
            if (newRow + piece.getLength() > height && 
                !(piece.isPrimary() && exitOnBottom && piece.getCol() == exitCol)) {
                return false;
            }
        }

        // Check if the piece would overlap with other pieces
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                // If this cell would be occupied by the moved piece
                boolean wouldOccupy = false;
                if (piece.isHorizontal()) {
                    wouldOccupy = r == newRow && c >= newCol && c < newCol + piece.getLength();
                } else {
                    wouldOccupy = c == newCol && r >= newRow && r < newRow + piece.getLength();
                }

                if (wouldOccupy) {
                    // Check if any other piece currently occupies this cell
                    for (Piece otherPiece : pieces.values()) {
                        if (otherPiece.getId() != piece.getId() && otherPiece.occupies(r, c)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Checks if the primary piece can exit the board.
     * 
     * @return True if the primary piece can exit, false otherwise
     */
    public boolean canPrimaryPieceExit() {
        Piece primaryPiece = pieces.get(primaryPieceId);
        
        if (primaryPiece.isHorizontal()) {
            // Check if the primary piece is aligned with the exit
            if (primaryPiece.getRow() != exitRow) {
                return false;
            }
            
            // Check if there's a clear path to the exit
            int exitDirection = exitOnRight ? 1 : -1;
            int startCol = primaryPiece.getCol() + (exitDirection > 0 ? primaryPiece.getLength() : -1);
            int endCol = exitDirection > 0 ? width : -1;
            
            for (int c = startCol; c != endCol; c += exitDirection) {
                for (Piece piece : pieces.values()) {
                    if (piece.getId() != primaryPieceId && piece.occupies(exitRow, c)) {
                        return false;
                    }
                }
            }
            
            return true;
        } else {
            // Check if the primary piece is aligned with the exit
            if (primaryPiece.getCol() != exitCol) {
                return false;
            }
            
            // Check if there's a clear path to the exit
            int exitDirection = exitOnBottom ? 1 : -1;
            int startRow = primaryPiece.getRow() + (exitDirection > 0 ? primaryPiece.getLength() : -1);
            int endRow = exitDirection > 0 ? height : -1;
            
            for (int r = startRow; r != endRow; r += exitDirection) {
                for (Piece piece : pieces.values()) {
                    if (piece.getId() != primaryPieceId && piece.occupies(r, exitCol)) {
                        return false;
                    }
                }
            }
            
            return true;
        }
    }

    /**
     * Gets the character at a specific cell.
     * 
     * @param row Row position
     * @param col Column position
     * @return The character representing the piece at this cell, or '.' if empty
     */
    public char getCell(int row, int col) {
        for (Piece piece : pieces.values()) {
            if (piece.occupies(row, col)) {
                return piece.getId();
            }
        }
        return '.';
    }

    /**
     * Creates a string representation of the board.
     * 
     * @return String representation of the board
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (r == exitRow && c == exitCol && (exitOnRight && c == width - 1) || (exitOnBottom && r == height - 1)) {
                    sb.append('K');
                } else {
                    sb.append(getCell(r, c));
                }
            }
            if (r < height - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    // Getters
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map<Character, Piece> getPieces() {
        return pieces;
    }

    public int getExitRow() {
        return exitRow;
    }

    public int getExitCol() {
        return exitCol;
    }

    public Piece getPrimaryPiece() {
        return pieces.get(primaryPieceId);
    }

    public char getPrimaryPieceId() {
        return primaryPieceId;
    }
}
