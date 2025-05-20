package model;

import java.util.HashMap;
import java.util.Map;

public class Board {
    private int width;
    private int height;
    private Map<Character, Piece> pieces;
    private int exitRow;
    private int exitCol;
    private boolean exitOnRight;
    private boolean exitOnBottom;
    private char primaryPieceId;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.pieces = new HashMap<>();
    }

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

    public void addPiece(Piece piece) {
        pieces.put(piece.getId(), piece);
        if (piece.isPrimary()) {
            primaryPieceId = piece.getId();
        }
    }

    public void setExit(int row, int col) {
        exitRow = row;
        exitCol = col;
        
        exitOnRight = (col == width - 1);
        exitOnBottom = (row == height - 1);
    }

    public boolean movePiece(char pieceId, String direction, int steps) {
        Piece piece = pieces.get(pieceId);
        if (piece == null) {
            return false;
        }

        int newRow = piece.getRow();
        int newCol = piece.getCol();

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

        if (!isValidMove(piece, newRow, newCol)) {
            return false;
        }
        if (piece.isPrimary() && piece.isHorizontal() && exitOnRight && newCol + piece.getLength() > width) {
            if (exitRow == piece.getRow()) {
                piece.setCol(newCol);
                return true;
            }
            return false;
        } else if (piece.isPrimary() && !piece.isHorizontal() && exitOnBottom && newRow + piece.getLength() > height) {
            if (exitCol == piece.getCol()) {
                piece.setRow(newRow);
                return true;
            }
            return false;
        }

        piece.setRow(newRow);
        piece.setCol(newCol);
        return true;
    }

    private boolean isValidMove(Piece piece, int newRow, int newCol) {
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

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                boolean wouldOccupy = false;
                if (piece.isHorizontal()) {
                    wouldOccupy = r == newRow && c >= newCol && c < newCol + piece.getLength();
                } else {
                    wouldOccupy = c == newCol && r >= newRow && r < newRow + piece.getLength();
                }

                if (wouldOccupy) {
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

    public boolean canPrimaryPieceExit() {
        Piece primaryPiece = getPrimaryPiece();
        
        if (primaryPiece.isHorizontal()) {
            int row = primaryPiece.getRow();
            int pieceRight = primaryPiece.getCol() + primaryPiece.getLength() - 1;
            int pieceLeft = primaryPiece.getCol();
            
            if (exitCol == width && exitRow == row) {
                return pieceRight == width - 1;
            }
            else if (exitCol > pieceRight && exitRow == row) {
                return pieceRight == exitCol - 1;
            }
            else if (exitCol == -1 && exitRow == row) {
                return pieceLeft == 0;
            }
        }
        else {
            int col = primaryPiece.getCol();
            int pieceBottom = primaryPiece.getRow() + primaryPiece.getLength() - 1;
            int pieceTop = primaryPiece.getRow();
            
            if (exitRow == height && exitCol == col) {
                return pieceBottom == height - 1;
            }
            else if (exitRow == -1 && exitCol == col) {
                return pieceTop == 0;
            }
        }
        
        return false;
    }

    public char getCell(int row, int col) {
        for (Piece piece : pieces.values()) {
            if (piece.occupies(row, col)) {
                return piece.getId();
            }
        }
        return '.';
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                sb.append(getCell(r, c));
            }
            if (r < height - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

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
