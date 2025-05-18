package model;

import java.util.ArrayList;
// import java.util.HashMap;
import java.util.List;
// import java.util.Map;

/**
 * Represents a state of the Rush Hour puzzle board.
 */
public class State {
    private Board board;
    private State parent;
    private Move moveMade;
    private int cost;
    private int heuristicValue;
    
    /**
     * Creates a new state.
     * 
     * @param board The board configuration for this state
     * @param parent The parent state
     * @param moveMade The move that led to this state from parent
     * @param cost The cost to reach this state
     */
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
    
    /**
     * Gets the total evaluation function value (f = g + h).
     * 
     * @return The sum of cost and heuristic value
     */
    public int getF() {
        return cost + heuristicValue;
    }

    /**
     * Generates all possible next states from this state.
     * 
     * @return List of possible next states
     */
    public List<State> generateNextStates() {
        List<State> nextStates = new ArrayList<>();
        
        for (Piece piece : board.getPieces().values()) {
            // Try moving horizontally if piece is horizontal
            if (piece.isHorizontal()) {
                // Try moving left
                for (int steps = 1; steps <= board.getWidth(); steps++) {
                    Board newBoard = board.copy();
                    if (newBoard.movePiece(piece.getId(), "left", steps)) {
                        Move move = new Move(piece.getId(), "left", steps);
                        nextStates.add(new State(newBoard, this, move, cost + 1));
                    } else {
                        break; // Can't move further in this direction
                    }
                }
                
                // Try moving right
                for (int steps = 1; steps <= board.getWidth(); steps++) {
                    Board newBoard = board.copy();
                    if (newBoard.movePiece(piece.getId(), "right", steps)) {
                        Move move = new Move(piece.getId(), "right", steps);
                        nextStates.add(new State(newBoard, this, move, cost + 1));
                    } else {
                        break; // Can't move further in this direction
                    }
                }
            } 
            // Try moving vertically if piece is vertical
            else {
                // Try moving up
                for (int steps = 1; steps <= board.getHeight(); steps++) {
                    Board newBoard = board.copy();
                    if (newBoard.movePiece(piece.getId(), "up", steps)) {
                        Move move = new Move(piece.getId(), "up", steps);
                        nextStates.add(new State(newBoard, this, move, cost + 1));
                    } else {
                        break; // Can't move further in this direction
                    }
                }
                
                // Try moving down
                for (int steps = 1; steps <= board.getHeight(); steps++) {
                    Board newBoard = board.copy();
                    if (newBoard.movePiece(piece.getId(), "down", steps)) {
                        Move move = new Move(piece.getId(), "down", steps);
                        nextStates.add(new State(newBoard, this, move, cost + 1));
                    } else {
                        break; // Can't move further in this direction
                    }
                }
            }
        }
        
        return nextStates;
    }

    /**
     * Checks if this state is a solution (primary piece can exit).
     * 
     * @return True if this state is a solution, false otherwise
     */
    public boolean isSolution() {
        return board.canPrimaryPieceExit();
    }

    /**
     * Returns a unique string representation of this state's board configuration,
     * to be used as a key for visited states.
     * 
     * @return String representation of the board
     */
    public String getStateKey() {
        return board.toString();
    }
    
    /**
     * Compares this state with another state for equality.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        State other = (State) obj;
        return getStateKey().equals(other.getStateKey());
    }
    
    /**
     * Returns a hash code for this state.
     */
    @Override
    public int hashCode() {
        return getStateKey().hashCode();
    }
}
