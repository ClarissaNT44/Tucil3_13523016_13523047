import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import algorithm.AStar;
import algorithm.GreedyBestFirst;
import algorithm.Heuristic;
import algorithm.HeuristicFactory;
import algorithm.Pathfinder;
import algorithm.UCS;
import model.Board;
import model.Move;
import utility.FileHandler;

/**
 * Main class for the Rush Hour puzzle solver application.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("        RUSH HOUR PUZZLE SOLVER");
        System.out.println("============================================");

        // Ask user for the puzzle file
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the puzzle file path: ");
        String filePath = scanner.nextLine();
        
        try {
            // Load the puzzle from file
            Board board = FileHandler.loadBoardFromFile(filePath);
            
            // Display the initial board
            System.out.println("\nInitial Board:");
            FileHandler.printBoard(board, ' ');
            
            // Ask user for the algorithm choice
            System.out.println("\nSelect the search algorithm:");
            System.out.println("1. Uniform Cost Search (UCS)");
            System.out.println("2. Greedy Best First Search (GBFS)");
            System.out.println("3. A* Search");
            System.out.print("Enter your choice (1-3): ");
            
            int algorithmChoice = scanner.nextInt();
            Pathfinder pathfinder;
            
            // If choosing GBFS or A*, ask for heuristic
            int heuristicChoice = 0;
            if (algorithmChoice == 2 || algorithmChoice == 3) {
                System.out.println("\nSelect heuristic:");
                System.out.println("1. Distance to Exit");
                System.out.println("2. Blocking Vehicles");
                System.out.println("3. Combined (Distance + Blocking Vehicles)");
                System.out.print("Enter your choice (1-3): ");
                heuristicChoice = scanner.nextInt();
            }
            scanner.nextLine(); 

            // Create the selected pathfinder
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
            
            // Execute search
            List<Move> solution = pathfinder.findPath(board);
            long endTime = System.currentTimeMillis();
            
            // Display results
            System.out.println("\n============================================");
            System.out.println("              SOLUTION FOUND");
            System.out.println("============================================");
            
            if (solution.isEmpty()) {
                System.out.println("No solution found!");
            } else {
                System.out.println("Solution path contains " + solution.size() + " moves:");
                
                // Apply each move to the board and display steps
                Board currentBoard = board.copy();
                System.out.println("\nInitial state:");
                FileHandler.printBoard(currentBoard, ' ');
                
                // List to store all board states
                List<Board> boardStates = new ArrayList<>();

                // Display each step
                System.out.println("\nStep by step solution:");
                for (int i = 0; i < solution.size(); i++) {
                    Move move = solution.get(i);
                    System.out.println("\nStep " + (i + 1) + ": " + move);
                    currentBoard.movePiece(move.getPieceId(), move.getDirection(), move.getSteps());
                    FileHandler.printBoard(currentBoard, move.getPieceId());
                    boardStates.add(currentBoard.copy());
                }
                
                System.out.println("\n============================================");
                System.out.println("               STATISTICS");
                System.out.println("============================================");
                System.out.println("Path length: " + solution.size() + " moves");
                System.out.println("Nodes visited: " + pathfinder.getNodesVisited());
                System.out.println("Execution time: " + (endTime - startTime) + " ms");

                System.out.print("\nEnter the saving file path (must end with .txt): ");
                String saveFilePath = scanner.nextLine();
                try {
                    FileHandler.saveSolutionToFile(solution, boardStates, saveFilePath, endTime - startTime, pathfinder.getNodesVisited());
                    System.out.println("Solution saved to " + saveFilePath);
                } catch (IOException e) {
                    System.err.println("Error saving the solution: " + e.getMessage());
                }
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
    
    /**
     * Loads a puzzle board from a file.
     * 
     * @param filePath Path to the puzzle file
     * @return The loaded board
     * @throws IOException If file reading fails
     */
    // private static Board loadBoardFromFile(String filePath) throws IOException {
    //     BufferedReader reader = new BufferedReader(new FileReader(filePath));
    //     String line;
        
    //     // Read board dimensions
    //     line = reader.readLine();
    //     String[] dimensions = line.split(" ");
    //     int width = Integer.parseInt(dimensions[0]);
    //     int height = Integer.parseInt(dimensions[1]);
        
    //     Board board = new Board(width, height);
        
    //     // Read exit position
    //     line = reader.readLine();
    //     String[] exitPos = line.split(" ");
    //     int exitRow = Integer.parseInt(exitPos[0]);
    //     int exitCol = Integer.parseInt(exitPos[1]);
    //     board.setExit(exitRow, exitCol);
        
    //     // Read number of pieces
    //     int numPieces = Integer.parseInt(reader.readLine());
        
    //     // Read each piece
    //     for (int i = 0; i < numPieces; i++) {
    //         line = reader.readLine();
    //         String[] pieceData = line.split(" ");
            
    //         char id = pieceData[0].charAt(0);
    //         int row = Integer.parseInt(pieceData[1]);
    //         int col = Integer.parseInt(pieceData[2]);
    //         int length = Integer.parseInt(pieceData[3]);
    //         boolean isHorizontal = Integer.parseInt(pieceData[4]) == 0;
    //         boolean isPrimary = Integer.parseInt(pieceData[5]) == 1;
            
    //         Piece piece = new Piece(id, row, col, length, isHorizontal, isPrimary);
    //         board.addPiece(piece);
    //     }
        
    //     reader.close();
    //     return board;
    // }
    
    /**
     * Prints the board in a readable format.
     * 
     * @param board The board to print
     */
    // private static void printBoard(Board board) {
    //     int height = board.getHeight();
    //     int width = board.getWidth();
        
    //     // Print top border
    //     System.out.print("+");
    //     for (int c = 0; c < width; c++) {
    //         System.out.print("---+");
    //     }
    //     System.out.println();
        
    //     // Print board rows
    //     for (int r = 0; r < height; r++) {
    //         System.out.print("|");
    //         for (int c = 0; c < width; c++) {
    //             char cell = board.getCell(r, c);
    //             System.out.print(" " + cell + " |");
    //         }
    //         System.out.println();
            
    //         // Print row separator
    //         System.out.print("+");
    //         for (int c = 0; c < width; c++) {
    //             // Mark the exit
    //             if ((r == board.getExitRow() && (c == 0 || c == width - 1)) || 
    //                 (c == board.getExitCol() && (r == 0 || r == height - 1))) {
    //                 System.out.print("   +");
    //             } else {
    //                 System.out.print("---+");
    //             }
    //         }
    //         System.out.println();
    //     }
    // }
}
