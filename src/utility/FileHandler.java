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
            
            line = reader.readLine();
            
            int exitRow = -1;
            int exitCol = -1;
            String[] allLines = new String[height + 2]; 
            int lineCount = 0;
            int gridStartIndex = -1;
            
            while ((line = reader.readLine()) != null && lineCount < height + 2) {
                allLines[lineCount++] = line;
            }
            
            for (int i = 0; i < lineCount; i++) {
                String currentLine = allLines[i];
                int kPos = currentLine.indexOf('K');
                
                if (kPos >= 0) {
                    if (i == 0) {
                        exitRow = -1;
                        gridStartIndex = 1; 
                        
                        exitCol = kPos;
                        System.out.println("DEBUG: K found above grid at column position " + kPos); 
                        
                    } else if (i >= height) {
                        exitRow = height;
                        
                        exitCol = kPos;
                        System.out.println("DEBUG: K found below grid at column position " + kPos);
                        
                    } else {
                        exitRow = i - (gridStartIndex == -1 ? 0 : gridStartIndex);
                        
                        int colBeforeK = 0;
                        for (int c = 0; c < kPos; c++) {
                            if (currentLine.charAt(c) != ' ') {
                                colBeforeK++;
                            }
                        }
                        
                        if (colBeforeK == 0) {
                            exitCol = -1;
                        } else if (colBeforeK >= width) {
                            exitCol = width;
                        } else {
                            exitCol = colBeforeK - 1;
                        }
                    }
                    
                    allLines[i] = allLines[i].replace("K", "");
                    break;
                }
            }
            
            if (exitRow == -1 && exitCol == -1) {
                throw new IOException("No exit position (K) found in the puzzle");
            }
            if (gridStartIndex == -1) {
                gridStartIndex = 0;
            }
            
            char[][] grid = new char[height][width];
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    grid[r][c] = '.';
                }
            }
            
            for (int r = 0; r < height; r++) {
                if (gridStartIndex + r >= lineCount) {
                    break; 
                }
                
                String currentLine = allLines[gridStartIndex + r];
                
                int gridCol = 0;
                for (int c = 0; c < currentLine.length() && gridCol < width; c++) {
                    char ch = currentLine.charAt(c);
                    if (ch != ' ') {
                        grid[r][gridCol++] = ch;
                    }
                }
            }
            
            Board board = new Board(width, height);
            board.setExit(exitRow, exitCol);
            
            Map<Character, int[]> pieceBounds = new HashMap<>();
            
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    char id = grid[r][c];
                    if (id != '.') {
                        if (!pieceBounds.containsKey(id)) {
                            pieceBounds.put(id, new int[] {r, c, r, c});
                        } else {
                            int[] bounds = pieceBounds.get(id);
                            bounds[2] = Math.max(bounds[2], r);
                            bounds[3] = Math.max(bounds[3], c);
                        }
                    }
                }
            }
            
            for (Map.Entry<Character, int[]> entry : pieceBounds.entrySet()) {
                char id = entry.getKey();
                int[] bounds = entry.getValue();
                
                int startRow = bounds[0];
                int startCol = bounds[1];
                int endRow = bounds[2];
                int endCol = bounds[3];
                
                boolean isHorizontal = (startRow == endRow);
                int length = isHorizontal ? (endCol - startCol + 1) : (endRow - startRow + 1);
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

        System.out.print("+");
        for (int c = 0; c < width; c++) {
            if (exitRow == -1 && exitCol == c) {
                System.out.print("   +");
            } else {
                System.out.print("---+");
            }
        }
        System.out.println();
        
        for (int r = 0; r < height; r++) {
            if (exitCol == -1 && exitRow == r) {
                System.out.print(" ");  
            } else {
                System.out.print("|");
            }
            
            for (int c = 0; c < width; c++) {
                char cell = board.getCell(r, c);
              
                String color = "";
                if (cell == movingPieceId) {
                    color = GREEN; 
                } else if (cell == 'P') {
                    color = RED; 
                }
                System.out.print(" " + color + cell + RESET + " ");
                if (c == width - 1 && r == exitRow) {
                    System.out.print(" ");

                } else {
                    System.out.print("|");
                }
            }
            System.out.println();
            System.out.print("+");
            for (int c = 0; c < width; c++) {
                if (r == height - 1 && exitRow == height && exitCol == c) {
                    System.out.print("   +"); 
                } else {
                    System.out.print("---+");
                }
            }
            System.out.println();
        }
    }

    public static void saveSolutionToFile(List<Move> solution, List<Board> boardStates, String filePath, long timeTaken, int nodesVisited) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("============================================");
            writer.write("\n      *** RUSH HOUR PUZZLE SOLVER ***");
            writer.write("\n============================================");
            
            // Write initial state
            writer.write("\n\nInitial Board:\n");
            writeBoard(writer, boardStates.get(0));
            
            writer.write("\n============================================");
            writer.write("\n              SOLUTION FOUND");
            writer.write("\n============================================");
            
            if (solution.isEmpty()) {
                writer.write("\nNo solution found!");
            } else {
                writer.write("\nSolution path contains " + solution.size() + " moves:\n");
            }
            
            // Write the solution steps
            writer.write("\nStep by step solution:\n");
            for (int i = 0; i < solution.size(); i++) {
                Move move = solution.get(i);
                writer.write("\nStep " + (i + 1) + ": " + move + "\n");
                
                // Write the board state after this move
                writeBoard(writer, boardStates.get(i + 1));
            }
            
            writer.write("\n============================================");
            writer.write("\n               STATISTICS");
            writer.write("\n============================================");
            writer.write("\nPath length: " + solution.size() + " moves");
            writer.write("\nNodes visited: " + nodesVisited);
            writer.write("\nExecution time: " + timeTaken + " ms");
            writer.write("\n============================================");
        }
    }

    // Helper method to write a board state
    private static void writeBoard(BufferedWriter writer, Board board) throws IOException {
        int height = board.getHeight();
        int width = board.getWidth();
        int exitRow = board.getExitRow();
        int exitCol = board.getExitCol();
        
        // Print top border with possible exit at top
        writer.write("+");
        for (int c = 0; c < width; c++) {
            if (exitRow == -1 && exitCol == c) {
                writer.write("   +"); // Exit at top
            } else {
                writer.write("---+");
            }
        }
        writer.write("\n");
        
        // Print board rows
        for (int r = 0; r < height; r++) {
            // Left border with possible exit at left
            if (exitCol == -1 && exitRow == r) {
                writer.write(" ");  // Exit at left side
            } else {
                writer.write("|");
            }
            
            // Cell contents
            for (int c = 0; c < width; c++) {
                char cell = board.getCell(r, c);
                writer.write(" " + cell + " ");
                
                // Right border
                if (c == width - 1) {
                    if (exitCol == width && exitRow == r) {
                        writer.write(" ");  // Exit at right side
                    } else {
                        writer.write("|");
                    }
                } else {
                    writer.write("|");
                }
            }
            writer.write("\n");
            
            // Print row separator with possible exit at bottom
            writer.write("+");
            for (int c = 0; c < width; c++) {
                if (r == height - 1 && exitRow == height && exitCol == c) {
                    writer.write("   +"); // Exit at bottom
                } else {
                    writer.write("---+");
                }
            }
            writer.write("\n");
        }
    }
}