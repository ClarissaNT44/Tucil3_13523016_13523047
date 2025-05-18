package algorithm;

import model.Board;
import model.Move;
import model.State;

import java.util.List;

/**
 * Interface for pathfinding algorithms used to solve the Rush Hour puzzle.
 */
public interface Pathfinder {
    
    /**
     * Finds a path from the initial state to a goal state.
     * 
     * @param initialBoard The initial configuration of the board
     * @return A list of moves leading to the solution, or an empty list if no solution exists
     */
    List<Move> findPath(Board initialBoard);
    
    /**
     * Gets the number of nodes visited during the search.
     * 
     * @return The number of nodes visited
     */
    int getNodesVisited();
    
    /**
     * Gets the states explored during the search for visualization or debugging.
     * 
     * @return The list of states in the solution path
     */
    List<State> getSolutionStates();
}
