package gui;

import java.awt.FlowLayout;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Panel kontrol yang berisi input file, pilihan algoritma, heuristic, dan tombol animasi.
 */
public class ControlPanel extends JPanel {
    private JTextField filePathField;
    private JButton browseButton;
    private JComboBox<String> algorithmComboBox;
    private JComboBox<String> heuristicComboBox;
    private JButton solveButton;
    private JButton playButton;
    private JButton pauseButton;
    private JButton prevButton;
    private JButton nextButton;

    // Tambahan: tombol Save disimpan agar mudah di-manage
    private JButton saveButton;

    private Consumer<Void> browseAction;
    private Consumer<Void> solveAction;
    private Consumer<Void> playAction;
    private Consumer<Void> pauseAction;
    private Consumer<Void> prevAction;
    private Consumer<Void> nextAction;

    public ControlPanel(Runnable browseCallback, Runnable solveCallback,
                        Runnable playCallback, Runnable pauseCallback,
                        Runnable prevCallback, Runnable nextCallback, JButton saveButton) {
        this.saveButton = saveButton;
        this.browseAction = v -> browseCallback.run();
        this.solveAction = v -> solveCallback.run();
        this.playAction = v -> playCallback.run();
        this.pauseAction = v -> pauseCallback.run();
        this.prevAction = v -> prevCallback.run();
        this.nextAction = v -> nextCallback.run();
        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        filePathField = new JTextField(30);
        filePathField.setEditable(false);

        browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> browseAction.accept(null));

        algorithmComboBox = new JComboBox<>(new String[] {
                "Uniform Cost Search (UCS)",
                "Greedy Best First Search (GBFS)",
                "A* Search"
        });
        algorithmComboBox.addActionListener(e -> {
            int idx = algorithmComboBox.getSelectedIndex();
            heuristicComboBox.setEnabled(idx == 1 || idx == 2);
        });

        heuristicComboBox = new JComboBox<>(new String[] {
                "Distance to Exit",
                "Blocking Vehicles",
                "Combined (Distance + Blocking Vehicles)"
        });
        heuristicComboBox.setEnabled(false);

        solveButton = new JButton("Solve");
        solveButton.addActionListener(e -> solveAction.accept(null));

        playButton = new JButton("Play");
        playButton.setEnabled(false);
        playButton.addActionListener(e -> playAction.accept(null));

        pauseButton = new JButton("Pause");
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(e -> pauseAction.accept(null));

        prevButton = new JButton("Prev");
        prevButton.setEnabled(false);
        prevButton.addActionListener(e -> prevAction.accept(null));

        nextButton = new JButton("Next");
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> nextAction.accept(null));
    }

    private void layoutComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new JLabel("Puzzle file:"));
        add(filePathField);
        add(browseButton);
        add(new JLabel("Algorithm:"));
        add(algorithmComboBox);
        add(new JLabel("Heuristic:"));
        add(heuristicComboBox);
        add(solveButton);
        add(playButton);
        add(pauseButton);
        add(prevButton);
        add(nextButton);

        // Jika tombol Save sudah di-set, tambahkan juga
        if (saveButton != null) {
            add(saveButton);
        }
    }

    /**
     * Tambahkan tombol Save ke panel.
     */
    public void addSaveButton(JButton button) {
        this.saveButton = button;
        add(saveButton);
        revalidate();
        repaint();
    }

    public void setFilePath(String path) {
        filePathField.setText(path);
    }

    public int getSelectedAlgorithmIndex() {
        return algorithmComboBox.getSelectedIndex();
    }

    public int getSelectedHeuristicIndex() {
        return heuristicComboBox.getSelectedIndex();
    }

    public void setControlsEnabled(boolean enabled) {
        solveButton.setEnabled(enabled);
        playButton.setEnabled(enabled);
        pauseButton.setEnabled(false);
        prevButton.setEnabled(enabled);
        nextButton.setEnabled(enabled);

        // Save button ikut disable kalau controls disable
        if (saveButton != null) {
            saveButton.setEnabled(enabled);
        }
    }

    public void setPlaying(boolean isPlaying) {
        playButton.setEnabled(!isPlaying);
        pauseButton.setEnabled(isPlaying);
        solveButton.setEnabled(!isPlaying);
        browseButton.setEnabled(!isPlaying);
        prevButton.setEnabled(!isPlaying);
        nextButton.setEnabled(!isPlaying);

        if (saveButton != null) {
            saveButton.setEnabled(!isPlaying);
        }
    }
}