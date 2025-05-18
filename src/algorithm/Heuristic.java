package algorithm;

import model.Board;
import model.Piece;
import model.State;

/**
 * Interface for heuristic functions.
 */
public interface Heuristic {
    /**
     * Calculates the heuristic value for a state.
     * 
     * @param state The state to evaluate
     * @return The heuristic value
     */
    int calculate(State state);
}

/**
 * Distance heuristic: measures the distance between the primary piece and the exit.
 */
class DistanceHeuristic implements Heuristic {
    @Override
    public int calculate(State state) {
        Board board = state.getBoard();
        Piece primaryPiece = board.getPrimaryPiece();
        
        int distanceToExit = 0;
        
        if (primaryPiece.isHorizontal()) {
            // For horizontal pieces, measure horizontal distance to exit
            int primaryRight = primaryPiece.getCol() + primaryPiece.getLength() - 1;
            
            // If exit is on the right edge
            if (board.getExitCol() == board.getWidth() - 1) {
                distanceToExit = board.getWidth() - 1 - primaryRight;
            } 
            // If exit is on the left edge
            else if (board.getExitCol() == 0) {
                distanceToExit = primaryPiece.getCol();
            }
        } else {
            // For vertical pieces, measure vertical distance to exit
            int primaryBottom = primaryPiece.getRow() + primaryPiece.getLength() - 1;
            
            // If exit is on the bottom edge
            if (board.getExitRow() == board.getHeight() - 1) {
                distanceToExit = board.getHeight() - 1 - primaryBottom;
            } 
            // If exit is on the top edge
            else if (board.getExitRow() == 0) {
                distanceToExit = primaryPiece.getRow();
            }
        }
        
        return distanceToExit;
    }
}

/**
 * Blocking vehicles heuristic: counts the number of vehicles blocking the primary piece's path to exit.
 */
class BlockingVehiclesHeuristic implements Heuristic {
    @Override
    public int calculate(State state) {
        Board board = state.getBoard();
        Piece primaryPiece = board.getPrimaryPiece();
        int blockingCount = 0;
        
        if (primaryPiece.isHorizontal()) {
            int row = primaryPiece.getRow();
            int startCol, endCol;
            
            // If exit is on the right edge
            if (board.getExitCol() == board.getWidth() - 1) {
                startCol = primaryPiece.getCol() + primaryPiece.getLength();
                endCol = board.getWidth();
            } 
            // If exit is on the left edge
            else {
                startCol = 0;
                endCol = primaryPiece.getCol();
            }
            
            for (int col = startCol; col < endCol; col++) {
                if (board.getCell(row, col) != '.') {
                    blockingCount++;
                }
            }
        } else {
            int col = primaryPiece.getCol();
            int startRow, endRow;
            
            // If exit is on the bottom edge
            if (board.getExitRow() == board.getHeight() - 1) {
                startRow = primaryPiece.getRow() + primaryPiece.getLength();
                endRow = board.getHeight();
            } 
            // If exit is on the top edge
            else {
                startRow = 0;
                endRow = primaryPiece.getRow();
            }
            
            for (int row = startRow; row < endRow; row++) {
                if (board.getCell(row, col) != '.') {
                    blockingCount++;
                }
            }
        }
        
        return blockingCount;
    }
}

/**
 * Combined heuristic: combines distance and blocking vehicles heuristics.
 */
class CombinedHeuristic implements Heuristic {
    private DistanceHeuristic distanceHeuristic = new DistanceHeuristic();
    private BlockingVehiclesHeuristic blockingHeuristic = new BlockingVehiclesHeuristic();
    
    @Override
    public int calculate(State state) {
        int distance = distanceHeuristic.calculate(state);
        int blocking = blockingHeuristic.calculate(state);
        
        // Combine the two heuristics, weighting blocking vehicles higher
        return distance + (2 * blocking);
    }
}

/**
 * Factory class to create heuristic instances.
 */
class HeuristicFactory { // harusnya public
    public static final int DISTANCE_HEURISTIC = 1;
    public static final int BLOCKING_VEHICLES_HEURISTIC = 2;
    public static final int COMBINED_HEURISTIC = 3;
    
    /**
     * Creates a heuristic based on the specified type.
     * 
     * @param type The type of heuristic to create
     * @return The heuristic instance
     */
    public static Heuristic createHeuristic(int type) {
        switch (type) {
            case DISTANCE_HEURISTIC:
                return new DistanceHeuristic();
            case BLOCKING_VEHICLES_HEURISTIC:
                return new BlockingVehiclesHeuristic();
            case COMBINED_HEURISTIC:
                return new CombinedHeuristic();
            default:
                return new CombinedHeuristic(); // Default to combined heuristic
        }
    }
}
