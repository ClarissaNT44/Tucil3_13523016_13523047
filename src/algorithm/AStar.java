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
        nodesVisited = 0;
        solutionStates.clear();
        State initialState = new State(initialBoard, null, null, 0);
        
        PriorityQueue<State> frontier = new PriorityQueue<>(
            Comparator.comparingInt(state -> state.getCost() + heuristic.calculate(state))
        );
        frontier.add(initialState);
        Map<String, Integer> costSoFar = new HashMap<>();
        costSoFar.put(initialState.getBoard().toString(), 0);
        Set<String> explored = new HashSet<>();
        
        while (!frontier.isEmpty()) {
            State currentState = frontier.poll();
            nodesVisited++;
            String boardRepresentation = currentState.getBoard().toString();
            
            if (currentState.getBoard().canPrimaryPieceExit()) {
                return reconstructPath(currentState);
            }
            
            explored.add(boardRepresentation);
            
            List<Move> possibleMoves = generatePossibleMoves(currentState.getBoard());
            
            for (Move move : possibleMoves) {
                Board newBoard = applyMove(currentState.getBoard(), move);
                String newBoardRepresentation = newBoard.toString();
                if (!explored.contains(newBoardRepresentation)) {
                    int newCost = currentState.getCost() + move.getSteps();
                    if (!costSoFar.containsKey(newBoardRepresentation) || 
                        newCost < costSoFar.get(newBoardRepresentation)) {
                        
                        costSoFar.put(newBoardRepresentation, newCost);
                        
                        State newState = new State(newBoard, currentState, move, currentState.getCost() + move.getSteps());
                        
                        frontier.add(newState);
                    }
                }
            }
        }
        
        return new ArrayList<>();
    }
    
    private List<Move> generatePossibleMoves(Board board) {
        List<Move> possibleMoves = new ArrayList<>();
        Map<Character, Piece> pieces = board.getPieces();
        
        for (Piece piece : pieces.values()) {
            char id = piece.getId();
            
            if (piece.isHorizontal()) {
                int maxRightSteps = getMaxStepsInDirection(board, piece, "right");
                for (int steps = 1; steps <= maxRightSteps; steps++) {
                    possibleMoves.add(new Move(id, "right", steps));
                }
                
                int maxLeftSteps = getMaxStepsInDirection(board, piece, "left");
                for (int steps = 1; steps <= maxLeftSteps; steps++) {
                    possibleMoves.add(new Move(id, "left", steps));
                }
            } else {
                int maxDownSteps = getMaxStepsInDirection(board, piece, "down");
                for (int steps = 1; steps <= maxDownSteps; steps++) {
                    possibleMoves.add(new Move(id, "down", steps));
                }
                
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
        
        Board tempBoard = board.copy();
        
        boolean canMove = true;
        while (canMove) {
            canMove = tempBoard.movePiece(piece.getId(), direction, 1);
            if (canMove) {
                steps++;
            }
        }
        
        return steps;
    }
    private Board applyMove(Board board, Move move) {
        Board newBoard = board.copy();
        
        newBoard.movePiece(move.getPieceId(), move.getDirection(), move.getSteps());
        
        return newBoard;
    }
    private List<Move> reconstructPath(State goalState) {
        List<Move> path = new ArrayList<>();
        List<State> statePath = new ArrayList<>();
        
        State current = goalState;
        while (current.getParent() != null) {
            path.add(0, current.getMoveMade()); 
            statePath.add(0, current);
            current = current.getParent();
        }
        
        statePath.add(0, current);
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
