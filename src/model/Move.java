package model;

/**
 * Represents a move in the Rush Hour puzzle.
 */
public class Move {
    private char pieceId;
    private String direction;
    private int steps;

    /**
     * Creates a new move.
     * 
     * @param pieceId    Character identifier for the moved piece
     * @param direction  Direction of movement ("up", "down", "left", or "right")
     * @param steps      Number of cells to move
     */
    public Move(char pieceId, String direction, int steps) {
        this.pieceId = pieceId;
        this.direction = direction;
        this.steps = steps;
    }

    public char getPieceId() {
        return pieceId;
    }

    public String getDirection() {
        return direction;
    }

    public int getSteps() {
        return steps;
    }

    /**
     * Returns a string representation of this move.
     */
    @Override
    public String toString() {
        return pieceId + "-" + direction + (steps > 1 ? " " + steps : "");
    }
}
