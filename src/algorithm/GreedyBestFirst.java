package algorithm;

import java.util.*;
import model.*;
/**
 * Greedy Best First Search algorithm implementation.
 */
public class GreedyBestFirst implements Pathfinder {
    private int nodesVisited;
    private List<State> solutionStates;
    private Heuristic heuristic;
    
    /**
     * Creates a new Greedy Best First Search instance with the specified heuristic.
     * 
     * @param heuristic The heuristic to use for evaluation
     */
    public GreedyBestFirst(Heuristic heuristic) {
        this.nodesVisited = 0;
        this.solutionStates = new ArrayList<>();
        this.heuristic = heuristic;
    }
    
    @Override
   public List<Move> findPath(Board initialBoard) {
        // Reset counters and solution
        nodesVisited = 0;
        solutionStates.clear();
        
        // Create the initial state
        State initialState = new State(initialBoard, null, null, 0);
        
        // Priority queue to store states based on their heuristic value
        PriorityQueue<State> frontier = new PriorityQueue<>(
            Comparator.comparingInt(state -> heuristic.calculate(state))
        );
        frontier.add(initialState);
        
        // Set to track visited states to avoid cycles
        Set<String> explored = new HashSet<>();
        
        while (!frontier.isEmpty()) {
            // Get the state with the lowest heuristic value
            State currentState = frontier.poll();
            nodesVisited++;
            
            // Add the current board's representation to explored set
            String boardRepresentation = currentState.getBoard().toString();
            explored.add(boardRepresentation);
            
            // Check if we've reached the goal state
            if (currentState.getBoard().canPrimaryPieceExit()) {
                // Reconstruct the path and return the moves
                return reconstructPath(currentState);
            }
            
            // Generate all possible moves from the current state
            List<Move> possibleMoves = generatePossibleMoves(currentState.getBoard());
            
            for (Move move : possibleMoves) {
                // Apply the move to get the new board configuration
                Board newBoard = applyMove(currentState.getBoard(), move);
                String newBoardRepresentation = newBoard.toString();
                
                // If this board configuration hasn't been explored yet
                if (!explored.contains(newBoardRepresentation)) {
                    // Create a new state with incremented cost
                    State newState = new State(newBoard, currentState, move, currentState.getCost() + 1);
                    
                    // Add the new state to the frontier
                    frontier.add(newState);
                }
            }
        }
        
        // No solution found
        return new ArrayList<>();
    }
    private List<Move> generatePossibleMoves(Board board) {
        List<Move> possibleMoves = new ArrayList<>();
        Map<Character, Piece> pieces = board.getPieces();
        
        for (Piece piece : pieces.values()) {
            char id = piece.getId();
            
            if (piece.isHorizontal()) {
                // Check right movement
                int maxRightSteps = getMaxStepsInDirection(board, piece, "right");
                for (int steps = 1; steps <= maxRightSteps; steps++) {
                    possibleMoves.add(new Move(id, "right", steps));
                }
                
                // Check left movement
                int maxLeftSteps = getMaxStepsInDirection(board, piece, "left");
                for (int steps = 1; steps <= maxLeftSteps; steps++) {
                    possibleMoves.add(new Move(id, "left", steps));
                }
            } else {
                // Check down movement
                int maxDownSteps = getMaxStepsInDirection(board, piece, "down");
                for (int steps = 1; steps <= maxDownSteps; steps++) {
                    possibleMoves.add(new Move(id, "down", steps));
                }
                
                // Check up movement
                int maxUpSteps = getMaxStepsInDirection(board, piece, "up");
                for (int steps = 1; steps <= maxUpSteps; steps++) {
                    possibleMoves.add(new Move(id, "up", steps));
                }
            }
        }
        
        return possibleMoves;
    }
    private int getMaxStepsInDirection(Board board, Piece piece, String direction) {
        int steps = 0;
        
        // Create a temporary board for testing moves
        Board tempBoard = board.copy();
        
        // Try moving the piece one step at a time until it can't move further
        while (tempBoard.movePiece(piece.getId(), direction, 1)) {
            steps++;
            // Reset to a fresh copy for the next test
            tempBoard = board.copy();
            // Try moving the accumulated number of steps
            if (!tempBoard.movePiece(piece.getId(), direction, steps + 1)) {
                break;
            }
        }
        
        return steps;
    }
    
    /**
     * Applies a move to a board and returns the new board state.
     * 
     * @param board The current board state
     * @param move The move to apply
     * @return The new board state after applying the move
     */
    private Board applyMove(Board board, Move move) {
        // Create a deep copy of the board
        Board newBoard = board.copy();
        
        // Apply the move to the new board
        newBoard.movePiece(move.getPieceId(), move.getDirection(), move.getSteps());
        
        return newBoard;
    }
    
    /**
     * Reconstructs the path from the goal state back to the initial state.
     * 
     * @param goalState The goal state
     * @return List of moves to reach the goal state
     */
    private List<Move> reconstructPath(State goalState) {
        List<Move> path = new ArrayList<>();
        List<State> statePath = new ArrayList<>();
        
        // Traverse from goal state back to the initial state
        State current = goalState;
        while (current.getParent() != null) {
            path.add(0, current.getMoveMade());  // Add to front of list to get correct order
            statePath.add(0, current);
            current = current.getParent();
        }
        
        // Add the initial state
        statePath.add(0, current);
        
        // Store the solution states for visualization
        this.solutionStates = statePath;
        
        return path;
    }

    @Override
    public int getNodesVisited() {
        return nodesVisited;
    }
    
    @Override
    public List<State> getSolutionStates() {
        return solutionStates;
    }
    
}
