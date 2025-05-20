package algorithm;

import model.Board;
import model.Piece;
import model.State;

public interface Heuristic {
    int calculate(State state);
}

class DistanceHeuristic implements Heuristic {
    @Override
    public int calculate(State state) {
        Board board = state.getBoard();
        Piece primaryPiece = board.getPrimaryPiece();
        
        if (primaryPiece.isHorizontal()) {
            int row = primaryPiece.getRow();
            int exitRow = board.getExitRow();
            int exitCol = board.getExitCol();
            if (exitRow == row) {
                int primaryRight = primaryPiece.getCol() + primaryPiece.getLength() - 1;
                
                if (exitCol > primaryRight) {
                    return exitCol - primaryRight; 
                }
                else if (exitCol < primaryPiece.getCol()) {
                    return primaryPiece.getCol() - exitCol;
                }
            }
        }
        return board.getWidth() + board.getHeight(); 
    }
}

class BlockingVehiclesHeuristic implements Heuristic {
    @Override
    public int calculate(State state) {
        Board board = state.getBoard();
        Piece primaryPiece = board.getPrimaryPiece();
        int blockingCount = 0;
        
        int exitRow = board.getExitRow();
        int exitCol = board.getExitCol();
        
        if (primaryPiece.isHorizontal()) {
            int row = primaryPiece.getRow();
            
            if (exitRow == row) {
                int startCol, endCol;
                
                if (exitCol > primaryPiece.getCol()) {
                    startCol = primaryPiece.getCol() + primaryPiece.getLength();
                    endCol = exitCol;
                } else {
                    startCol = exitCol + 1;
                    endCol = primaryPiece.getCol();
                }
                
                for (int col = startCol; col < endCol; col++) {
                    if (board.getCell(row, col) != '.') {
                        blockingCount++;
                    }
                }
            } else {
                return Integer.MAX_VALUE / 2; 
            }
        } else {
            int col = primaryPiece.getCol();
            
            if (exitCol == col) {
                int startRow, endRow;
                
                if (exitRow > primaryPiece.getRow()) {
                    startRow = primaryPiece.getRow() + primaryPiece.getLength();
                    endRow = exitRow;
                } else {
                    startRow = exitRow + 1;
                    endRow = primaryPiece.getRow();
                }
                
                for (int row = startRow; row < endRow; row++) {
                    if (board.getCell(row, col) != '.') {
                        blockingCount++;
                    }
                }
            } else {
                return Integer.MAX_VALUE / 2;
            }
        }
        
        return blockingCount;
    }
}


class CombinedHeuristic implements Heuristic {
    private DistanceHeuristic distanceHeuristic = new DistanceHeuristic();
    private BlockingVehiclesHeuristic blockingHeuristic = new BlockingVehiclesHeuristic();
    
    @Override
    public int calculate(State state) {
        int distance = distanceHeuristic.calculate(state);
        int blocking = blockingHeuristic.calculate(state);
        
        return distance + (2 * blocking);
    }
}
