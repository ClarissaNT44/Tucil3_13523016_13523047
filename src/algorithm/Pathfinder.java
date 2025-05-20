package algorithm;

import model.Board;
import model.Move;
import model.State;

import java.util.List;

public interface Pathfinder {
    
    List<Move> findPath(Board initialBoard);

    int getNodesVisited();
    
    List<State> getSolutionStates();
}
