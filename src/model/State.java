package model;

import java.util.ArrayList;
import java.util.List;

public class State {
    private Board board;
    private State parent;
    private Move moveMade;
    private int cost;
    private int heuristicValue;
    
    public State(Board board, State parent, Move moveMade, int cost) {
        this.board = board;
        this.parent = parent;
        this.moveMade = moveMade;
        this.cost = cost;
        this.heuristicValue = 0;
    }

    public Board getBoard() {
        return board;
    }

    public State getParent() {
        return parent;
    }

    public Move getMoveMade() {
        return moveMade;
    }

    public int getCost() {
        return cost;
    }
    
    public int getHeuristicValue() {
        return heuristicValue;
    }
    
    public void setHeuristicValue(int value) {
        this.heuristicValue = value;
    }
    
    public int getF() {
        return cost + heuristicValue;
    }

    public List<State> generateNextStates() {
        List<State> nextStates = new ArrayList<>();
        
        for (Piece piece : board.getPieces().values()) {
            if (piece.isHorizontal()) {
                for (int steps = 1; steps <= board.getWidth(); steps++) {
                    Board newBoard = board.copy();
                    if (newBoard.movePiece(piece.getId(), "left", steps)) {
                        Move move = new Move(piece.getId(), "left", steps);
                        nextStates.add(new State(newBoard, this, move, cost + move.getSteps()));
                    } else {
                        break; 
                    }
                }
                for (int steps = 1; steps <= board.getWidth(); steps++) {
                    Board newBoard = board.copy();
                    if (newBoard.movePiece(piece.getId(), "right", steps)) {
                        Move move = new Move(piece.getId(), "right", steps);
                        nextStates.add(new State(newBoard, this, move, cost + move.getSteps()));
                    } else {
                        break;
                    }
                }
            } 
            else {
                for (int steps = 1; steps <= board.getHeight(); steps++) {
                    Board newBoard = board.copy();
                    if (newBoard.movePiece(piece.getId(), "up", steps)) {
                        Move move = new Move(piece.getId(), "up", steps);
                        nextStates.add(new State(newBoard, this, move, cost + move.getSteps()));
                    } else {
                        break; 
                    }
                }
                for (int steps = 1; steps <= board.getHeight(); steps++) {
                    Board newBoard = board.copy();
                    if (newBoard.movePiece(piece.getId(), "down", steps)) {
                        Move move = new Move(piece.getId(), "down", steps);
                        nextStates.add(new State(newBoard, this, move, cost + move.getSteps()));
                    } else {
                        break; 
                    }
                }
            }
        }
        
        return nextStates;
    }

    public boolean isSolution() {
        return board.canPrimaryPieceExit();
    }
    public String getStateKey() {
        return board.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        State other = (State) obj;
        return getStateKey().equals(other.getStateKey());
    }
    
    @Override
    public int hashCode() {
        return getStateKey().hashCode();
    }
}
