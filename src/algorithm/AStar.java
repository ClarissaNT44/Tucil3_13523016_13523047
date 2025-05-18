package algorithm;

import java.util.*;
import model.*;

public class AStar implements Pathfinder {
    
    private int nodesVisited;
    private List<State> solutionStates;
    private Heuristic heuristic;
    
    public AStar(Heuristic heuristic) {
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
        
        // Priority queue to store states based on f(n) = g(n) + h(n)
        PriorityQueue<State> frontier = new PriorityQueue<>(
            Comparator.comparingInt(state -> state.getCost() + heuristic.calculate(state))
        );
        frontier.add(initialState);
        
        // Map to track the cost so far to reach each state
        Map<String, Integer> costSoFar = new HashMap<>();
        costSoFar.put(initialState.getBoard().toString(), 0);
        
        // Set to track visited states to avoid cycles
        Set<String> explored = new HashSet<>();
        
        while (!frontier.isEmpty()) {
            // Get the state with the lowest f(n) value
            State currentState = frontier.poll();
            nodesVisited++;
            
            // Add the current board's representation to explored set
            String boardRepresentation = currentState.getBoard().toString();
            
            // Check if we've reached the goal state
            if (currentState.getBoard().canPrimaryPieceExit()) {
                // Reconstruct the path and return the moves
                return reconstructPath(currentState);
            }
            
            // Mark as explored after goal check
            explored.add(boardRepresentation);
            
            // Generate all possible moves from the current state
            List<Move> possibleMoves = generatePossibleMoves(currentState.getBoard());
            
            for (Move move : possibleMoves) {
                // Apply the move to get the new board configuration
                Board newBoard = applyMove(currentState.getBoard(), move);
                String newBoardRepresentation = newBoard.toString();
                
                // If this board configuration hasn't been explored yet
                if (!explored.contains(newBoardRepresentation)) {
                    // Calculate new cost (g value)
                    int newCost = currentState.getCost() + 1;
                    
                    // Check if we found a better path to this state
                    if (!costSoFar.containsKey(newBoardRepresentation) || 
                        newCost < costSoFar.get(newBoardRepresentation)) {
                        
                        // Update cost
                        costSoFar.put(newBoardRepresentation, newCost);
                        
                        // Create a new state
                        State newState = new State(newBoard, currentState, move, newCost);
                        
                        // Add the new state to the frontier
                        frontier.add(newState);
                    }
                }
            }
        }
        
        // No solution found
        return new ArrayList<>();
    }
    
    /**
     * Generates all possible moves from the current board state.
     * 
     * @param board The current board state
     * @return List of all possible moves
     */
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
    
    /**
     * Determines the maximum number of steps a piece can move in a given direction.
     * 
     * @param board The current board state
     * @param piece The piece to move
     * @param direction Direction to move ("up", "down", "left", or "right")
     * @return The maximum number of steps the piece can move
     */
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
