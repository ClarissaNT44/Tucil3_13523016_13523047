package utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Board;
import model.Move;
import model.Piece;

public class FileHandler {
    public static Board loadBoardFromFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        
        try {
            // Read board dimensions
            line = reader.readLine();
            if (line == null) {
                throw new IOException("File is empty or incomplete");
            }
            
            String[] dimensions = line.split(" ");
            if (dimensions.length < 2) {
                throw new IOException("Invalid board dimensions format. Expected 'width height'");
            }
            
            int width = Integer.parseInt(dimensions[0]);
            int height = Integer.parseInt(dimensions[1]);
            
            // Skip the number of pieces line (not needed for grid format)
            line = reader.readLine();
            
            // Create the board
            Board board = new Board(width, height);
            
            // Read the grid representation
            char[][] grid = new char[height][width];
            for (int r = 0; r < height; r++) {
                line = reader.readLine();
                if (line == null || line.length() < width) {
                    throw new IOException("Invalid grid row " + (r + 1) + ": insufficient length");
                }
                for (int c = 0; c < width; c++) {
                    grid[r][c] = line.charAt(c);
                }
            }
            
            // Find exit position (marked by 'K')
            int exitRow = -1;
            int exitCol = -1;
            
            // First, find the primary piece position (P)
            int primaryRow = -1;
            
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    if (grid[r][c] == 'P') {
                        primaryRow = r;
                        break;
                    }
                }
                if (primaryRow >= 0) break;
            }
            
            // Now find 'K' in the same row as 'P'
            if (primaryRow >= 0) {
                for (int c = 0; c < width; c++) {
                    if (grid[primaryRow][c] == 'K') {
                        exitRow = primaryRow;
                        exitCol = c;
                        // Replace 'K' with '.' since it's not a piece
                        grid[exitRow][exitCol] = '.';
                        break;
                    }
                }
            }
            
            // If 'K' not found in the same row, look elsewhere
            if (exitRow < 0) {
                for (int r = 0; r < height; r++) {
                    for (int c = 0; c < width; c++) {
                        if (grid[r][c] == 'K') {
                            exitRow = r;
                            exitCol = c;
                            // Replace 'K' with '.' since it's not a piece
                            grid[r][c] = '.';
                            break;
                        }
                    }
                    if (exitRow >= 0) break;
                }
            }
            
            // Set default exit if 'K' was not found
            if (exitRow < 0) {
                // Default exit is at the right edge of the row with the primary piece
                exitRow = (primaryRow >= 0) ? primaryRow : height / 2;
                exitCol = width - 1;
            }
            
            // Set the exit position
            board.setExit(exitRow, exitCol);
            
            // Map from ID to information: [startRow, startCol, endRow, endCol]
            Map<Character, int[]> pieceBounds = new HashMap<>();
            
            // Find all pieces and their boundaries
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    char id = grid[r][c];
                    if (id != '.') {
                        if (!pieceBounds.containsKey(id)) {
                            // Initialize with [startRow, startCol, endRow, endCol]
                            pieceBounds.put(id, new int[] {r, c, r, c});
                        } else {
                            // Update the end positions
                            int[] bounds = pieceBounds.get(id);
                            bounds[2] = Math.max(bounds[2], r);
                            bounds[3] = Math.max(bounds[3], c);
                        }
                    }
                }
            }
            
            // Create pieces from boundaries
            for (Map.Entry<Character, int[]> entry : pieceBounds.entrySet()) {
                char id = entry.getKey();
                int[] bounds = entry.getValue();
                
                int startRow = bounds[0];
                int startCol = bounds[1];
                int endRow = bounds[2];
                int endCol = bounds[3];
                
                boolean isHorizontal = (startRow == endRow);
                int length = isHorizontal ? (endCol - startCol + 1) : (endRow - startRow + 1);
                
                // Determine if this is the primary piece (P)
                boolean isPrimary = (id == 'P');
                
                Piece piece = new Piece(id, startRow, startCol, length, isHorizontal, isPrimary);
                board.addPiece(piece);
            }
            
            return board;
        } finally {
            reader.close();
        }
    }

    public static void printBoard(Board board, char movingPieceId) {
        int height = board.getHeight();
        int width = board.getWidth();
        int exitRow = board.getExitRow();
        int exitCol = board.getExitCol();
        
        final String RESET = "\u001B[0m";
        final String RED = "\u001B[91m";
        final String GREEN = "\u001B[92m";

        // Print top border
        System.out.print("+");
        for (int c = 0; c < width; c++) {
            System.out.print("---+");
        }
        System.out.println();
        
        // Print board rows
        for (int r = 0; r < height; r++) {
            System.out.print("|");
            for (int c = 0; c < width; c++) {
                char cell = board.getCell(r, c);
                String color = "";
                if (cell == movingPieceId) {
                    color = GREEN; // Highlight the moving piece
                } else if (cell == 'P') {
                    color = RED; // Primary piece
                }
                System.out.print(" " + color + cell + RESET + " ");

                // Right border - only leave it open if this is the exit position
                if (c == width - 1 && r == exitRow) {
                    System.out.print(" ");
                } else {
                    System.out.print("|");
                }
            }
            System.out.println();
            
            // Print row separator (bottom border for this row)
            System.out.print("+");
            for (int c = 0; c < width; c++) {
                System.out.print("---+");
            }
            System.out.println();
        }
    }
    public static void saveSolutionToFile(List<Move> solution, List<Board> boardStates, String filePath, long timeTaken, int nodesVisited) throws IOException {
        // Ensure the file has .txt extension
        if (!filePath.endsWith(".txt")) {
            throw new IOException("Invalid file format. The file must be a .txt file.");
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("============================================");
            writer.write("\n              SOLUTION FOUND");
            writer.write("\n============================================");
            if (solution.isEmpty()) {
                writer.write("\nNo solution found.");
                return;
            } else {
                writer.write("\nSolution path contains " + solution.size() + " moves.\n");
            }
            // Write the solution steps
            writer.write("Step by step solution:\n");
            for (int i = 0; i < solution.size(); i++) {
                Move move = solution.get(i);
                writer.write("\nStep " + (i + 1) + ": " + move + "\n");
                Board currentBoard = boardStates.get(i);
                int height = currentBoard.getHeight();
                int width = currentBoard.getWidth();
                int exitRow = currentBoard.getExitRow();
                int exitCol = currentBoard.getExitCol();
                
                // Print top border
                writer.write("+");
                for (int c = 0; c < width; c++) {
                    writer.write("---+");
                }
                writer.write("\n");

                // Print board rows
                for (int r = 0; r < height; r++) {
                    writer.write("|");
                    for (int c = 0; c < width; c++) {
                        char cell = currentBoard.getCell(r, c);
                        writer.write(" " + cell + " ");
                        // Right border - only leave it open if this is the exit position
                        if (c == width - 1 && r == exitRow) {
                            writer.write(" ");
                        } else {
                            writer.write("|");
                        }
                    }
                    writer.write("\n");
                    // Print row separator (bottom border for this row)
                    writer.write("+");
                    for (int c = 0; c < width; c++) {
                        writer.write("---+");
                    }
                    writer.write("\n");
                }
            }
            
            // Write the statistics in the format you requested
            writer.write("\n============================================\n");
            writer.write("               STATISTICS\n");
            writer.write("============================================\n");
            writer.write("Path length: " + solution.size() + " moves\n");
            writer.write("Nodes visited: " + nodesVisited + "\n");
            writer.write("Execution time: " + timeTaken + " ms\n");
            writer.write("============================================\n");
        }
    }
}