package utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import model.Board;
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

    public static void printBoard(Board board) {
        int height = board.getHeight();
        int width = board.getWidth();
        int exitRow = board.getExitRow();
        int exitCol = board.getExitCol();
        
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
                System.out.print(" " + cell + " ");
                
                if (c == width - 1) {
                    if (exitCol == width && exitRow == r) {
                        System.out.print(" ");  
                    } else {
                        System.out.print("|");
                    }
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
}