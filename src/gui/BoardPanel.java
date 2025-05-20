package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import model.Board;

public class BoardPanel extends JPanel {
    private Board board;
    private static final int CELL_SIZE = 50;
    private Map<Character, Color> pieceColors = new HashMap<>();

    public BoardPanel() {
        setPreferredSize(new Dimension(600, 600));
    }

    public void setBoard(Board board) {
        this.board = board;
        assignColors();
        int width = board != null ? board.getWidth() : 0;
        int height = board != null ? board.getHeight() : 0;
        setPreferredSize(new Dimension(width * CELL_SIZE, height * CELL_SIZE));
        revalidate();
        repaint();
    }

    private void assignColors() {
        pieceColors.clear();
        if (board == null) return;
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN.darker(), Color.ORANGE, Color.MAGENTA, Color.CYAN.darker(), Color.PINK, Color.YELLOW.darker()};
        int idx = 0;
        for (char id : board.getPieces().keySet()) {
            pieceColors.put(id, colors[idx % colors.length]);
            idx++;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) return;
        int width = board.getWidth();
        int height = board.getHeight();

        Graphics2D g2 = (Graphics2D) g;
        Stroke oldStroke = g2.getStroke();
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                char cellChar = board.getCell(r, c);
                Color bgColor = Color.LIGHT_GRAY;
                if (cellChar == '.') {
                    bgColor = Color.WHITE;
                } else if (cellChar == 'P') {
                    bgColor = Color.RED;
                } else if (pieceColors.containsKey(cellChar)) {
                    bgColor = pieceColors.get(cellChar);
                }
                g.setColor(bgColor);
                g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                if (cellChar != '.') {
                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD, 20f));
                    FontMetrics fm = g.getFontMetrics();
                    String text = String.valueOf(cellChar);
                    int textWidth = fm.stringWidth(text);
                    int textHeight = fm.getAscent();
                    int x = c * CELL_SIZE + (CELL_SIZE - textWidth) / 2;
                    int y = r * CELL_SIZE + (CELL_SIZE + textHeight) / 2 - 4;
                    g.drawString(text, x, y);
                }
            }
        }
        int exitRow = board.getExitRow();
        int exitCol = board.getExitCol();
        g2.setStroke(new BasicStroke(5));
        g2.setColor(Color.GREEN.darker());
        if (exitRow >= 0 && exitRow < height && exitCol == -1) {
            int x = 0;
            int y = exitRow * CELL_SIZE;
            g2.drawLine(x, y, x, y + CELL_SIZE);
        } else if (exitRow >= 0 && exitRow < height && exitCol == width) {
            int x = width * CELL_SIZE - 1;
            int y = exitRow * CELL_SIZE;
            g2.drawLine(x, y, x, y + CELL_SIZE);
        } else if (exitCol >= 0 && exitCol < width && exitRow == -1) {
            int x = exitCol * CELL_SIZE;
            int y = 0;
            g2.drawLine(x, y, x + CELL_SIZE, y);
        } else if (exitCol >= 0 && exitCol < width && exitRow == height) {
            int x = exitCol * CELL_SIZE;
            int y = height * CELL_SIZE - 1;
            g2.drawLine(x, y, x + CELL_SIZE, y);
        }
        g2.setStroke(oldStroke);
    }
}