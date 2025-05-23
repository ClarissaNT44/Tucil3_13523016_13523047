package gui;

import algorithm.AStar;
import algorithm.GreedyBestFirst;
import algorithm.HeuristicFactory;
import algorithm.Pathfinder;
import algorithm.UCS;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.Board;
import model.Move;
import utility.FileHandler;

public class MainFrame extends JFrame {
    private ControlPanel controlPanel;
    private BoardPanel boardPanel;
    private StatusPanel statusPanel;

    private Board currentBoard;
    private List<Move> solutionMoves;
    private List<Board> boardStates;
    private Pathfinder pathfinder;

    private Timer animationTimer;
    private int animationStep = 0;

    private JButton saveButton;

    private long startTime;
    private long endTime;

    public MainFrame() {
        setTitle("Rush Hour Puzzle Solver");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(900, 700);
        setLocationRelativeTo(null);

        saveButton = new JButton("Save Solution");
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> onSaveClicked());
        controlPanel = new ControlPanel(this::onBrowseClicked, this::onSolveClicked,
                this::onPlayClicked, this::onPauseClicked,
                this::onPrevClicked, this::onNextClicked, saveButton);

        boardPanel = new BoardPanel();
        statusPanel = new StatusPanel(this::onPrevClicked, this::onNextClicked, this::onSaveClicked);

        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(boardPanel), BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        animationTimer = new Timer(800, e -> {
            if (boardStates != null && animationStep < boardStates.size() - 1) {
                animationStep++;
                updateBoardState(animationStep);
            } else {
                animationTimer.stop();
                controlPanel.setPlaying(false);
            }
        });
    }

    private void onSaveClicked() {
        if (solutionMoves == null || solutionMoves.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No solution to save!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        chooser.setDialogTitle("Save Solution");
        int result = chooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = chooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".txt")) {
                filePath += ".txt";
            }

            try {
                long timeTaken = endTime - startTime;
                FileHandler.saveSolutionToFile(solutionMoves, boardStates, filePath, timeTaken, pathfinder.getNodesVisited());
                JOptionPane.showMessageDialog(this, "Solution saved successfully to " + filePath,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to save solution: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onBrowseClicked() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            controlPanel.setFilePath(file.getAbsolutePath());
            try {
                currentBoard = FileHandler.loadBoardFromFile(file.getAbsolutePath());
                boardPanel.setBoard(currentBoard);
                statusPanel.setStatus("Puzzle loaded. Ready to solve.");
                resetAnimation();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to load puzzle:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onSolveClicked() {
        if (currentBoard == null) {
            JOptionPane.showMessageDialog(this, "Please load a puzzle file first.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int algoIndex = controlPanel.getSelectedAlgorithmIndex();
        int heuristicIndex = controlPanel.getSelectedHeuristicIndex() + 1;

        switch (algoIndex) {
            case 0:
                pathfinder = new UCS();
                break;
            case 1:
                pathfinder = new GreedyBestFirst(HeuristicFactory.createHeuristic(heuristicIndex));
                break;
            case 2:
                pathfinder = new AStar(HeuristicFactory.createHeuristic(heuristicIndex));
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid algorithm selection.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        statusPanel.setStatus("Solving...");
        controlPanel.setControlsEnabled(false);
        saveButton.setEnabled(false);
        statusPanel.setPrevEnabled(false);
        statusPanel.setNextEnabled(false);
        statusPanel.setSaveEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            List<Move> solution;
            List<model.State> states;

            @Override
            protected Void doInBackground() {
                startTime = System.currentTimeMillis();
                solution = pathfinder.findPath(currentBoard);
                endTime = System.currentTimeMillis();
                states = pathfinder.getSolutionStates();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    solutionMoves = solution;
                    boardStates = states.stream().map(model.State::getBoard).toList();

                    if (solutionMoves.isEmpty()) {
                        statusPanel.setStatus("No solution found.");
                        JOptionPane.showMessageDialog(MainFrame.this, "No solution found!",
                                "Info", JOptionPane.INFORMATION_MESSAGE);
                        resetAnimation();
                    } else {
                        statusPanel.setStatus("Solution found with " + solutionMoves.size() + " moves, nodes visited: " +
                                pathfinder.getNodesVisited() + ", execution time: " + (endTime - startTime) + " ms");
                        animationStep = 0;
                        boardPanel.setBoard(boardStates.get(0));
                        controlPanel.setControlsEnabled(true);
                        controlPanel.setPlaying(false);
                        saveButton.setEnabled(true);
                        statusPanel.setPrevEnabled(false);
                        statusPanel.setNextEnabled(boardStates.size() > 1);
                        statusPanel.setSaveEnabled(true);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Error during solving:\n" + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    resetAnimation();
                }
            }
        };

        worker.execute();
    }

    private void onPlayClicked() {
        animationTimer.start();
        controlPanel.setPlaying(true);
        statusPanel.setPrevEnabled(false);
        statusPanel.setNextEnabled(false);
    }

    private void onPauseClicked() {
        animationTimer.stop();
        controlPanel.setPlaying(false);
        statusPanel.setPrevEnabled(animationStep > 0);
        statusPanel.setNextEnabled(boardStates != null && animationStep < boardStates.size() - 1);
    }

    private void onNextClicked() {
        if (boardStates == null || animationStep >= boardStates.size() - 1) return;
        animationStep++;
        updateBoardState(animationStep);
    }

    private void onPrevClicked() {
        if (boardStates == null || animationStep <= 0) return;
        animationStep--;
        updateBoardState(animationStep);
    }

    private void updateBoardState(int step) {
        boardPanel.setBoard(boardStates.get(step));
        statusPanel.setStatus("Step " + step + " / " + (boardStates.size() - 1));
        statusPanel.setPrevEnabled(step > 0);
        statusPanel.setNextEnabled(step < boardStates.size() - 1);
    }

    private void resetAnimation() {
        animationTimer.stop();
        animationStep = 0;
        solutionMoves = null;
        boardStates = null;
        controlPanel.setControlsEnabled(true);
        controlPanel.setPlaying(false);
        saveButton.setEnabled(false);
        statusPanel.setPrevEnabled(false);
        statusPanel.setNextEnabled(false);
        statusPanel.setSaveEnabled(false);
        if (currentBoard != null) {
            boardPanel.setBoard(currentBoard);
        } else {
            boardPanel.setBoard(null);
        }
        statusPanel.setStatus("Load a puzzle to start.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}