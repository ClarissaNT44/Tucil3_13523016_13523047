package model;

public class Move {
    private char pieceId;
    private String direction;
    private int steps;

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

    @Override
    public String toString() {
        return pieceId + "-" + direction + (steps > 1 ? " " + steps : "");
    }
}
