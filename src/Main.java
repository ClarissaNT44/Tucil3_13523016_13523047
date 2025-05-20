import algorithm.*;
import model.*;
import utility.*;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("        RUSH HOUR PUZZLE SOLVER");
        System.out.println("============================================");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the puzzle file path: ");
        String filePath = scanner.nextLine();
        
        try {
            Board board = FileHandler.loadBoardFromFile(filePath);
                        
            System.out.println("\nInitial Board:");
            FileHandler.printBoard(board);
            
            System.out.println("\nSelect the search algorithm:");
            System.out.println("1. Uniform Cost Search (UCS)");
            System.out.println("2. Greedy Best First Search (GBFS)");
            System.out.println("3. A* Search");
            System.out.print("Enter your choice (1-3): ");
            
            int algorithmChoice = scanner.nextInt();
            Pathfinder pathfinder;
            
            int heuristicChoice = 0;
            if (algorithmChoice == 2 || algorithmChoice == 3) {
                System.out.println("\nSelect heuristic:");
                System.out.println("1. Distance to Exit");
                System.out.println("2. Blocking Vehicles");
                System.out.println("3. Combined (Distance + Blocking Vehicles)");
                System.out.print("Enter your choice (1-3): ");
                heuristicChoice = scanner.nextInt();
            }
            
            long startTime = System.currentTimeMillis();
            switch (algorithmChoice) {
                case 1:
                    pathfinder = new UCS();
                    break;
                case 2:
                    Heuristic gbfsHeuristic = HeuristicFactory.createHeuristic(heuristicChoice);
                    pathfinder = new GreedyBestFirst(gbfsHeuristic);
                    break;
                case 3:
                    Heuristic aStarHeuristic = HeuristicFactory.createHeuristic(heuristicChoice);
                    pathfinder = new AStar(aStarHeuristic);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid algorithm choice");
            }
            
            List<Move> solution = pathfinder.findPath(board);
            long endTime = System.currentTimeMillis();
            
            System.out.println("\n============================================");
            System.out.println("              SOLUTION FOUND");
            System.out.println("============================================");
            
            if (solution.isEmpty()) {
                System.out.println("No solution found!");
            } else {
                System.out.println("Solution path contains " + solution.size() + " moves:");
                
                Board currentBoard = board.copy();
                System.out.println("\nInitial state:");
                FileHandler.printBoard(currentBoard);
                
                System.out.println("\nStep by step solution:");
                for (int i = 0; i < solution.size(); i++) {
                    Move move = solution.get(i);
                    System.out.println("\nStep " + (i + 1) + ": " + move);
                    currentBoard.movePiece(move.getPieceId(), move.getDirection(), move.getSteps());
                    FileHandler.printBoard(currentBoard);
                }
                
                System.out.println("\n============================================");
                System.out.println("               STATISTICS");
                System.out.println("============================================");
                System.out.println("Path length: " + solution.size() + " moves");
                System.out.println("Nodes visited: " + pathfinder.getNodesVisited());
                System.out.println("Execution time: " + (endTime - startTime) + " ms");
            }
            
        } catch (IOException e) {
            System.err.println("Error reading the puzzle file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
