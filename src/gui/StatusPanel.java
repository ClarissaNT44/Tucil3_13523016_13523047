package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Panel status untuk menampilkan informasi statistik dan status,
 * sekaligus tombol navigasi dan simpan solusi.
 */
public class StatusPanel extends JPanel {
    private JLabel statusLabel;
    private JButton prevButton;
    private JButton nextButton;
    private JButton saveButton;

    public StatusPanel(Runnable prevCallback, Runnable nextCallback, Runnable saveCallback) {
        setLayout(new BorderLayout(10, 0));  // gap horizontal 10 px
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Label status di kiri
        statusLabel = new JLabel("Load a puzzle to start.");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusLabel.setPreferredSize(new Dimension(500, 25));

        // Panel khusus untuk tombol-tombol di kanan
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        prevButton = new JButton("Prev");
        prevButton.setEnabled(false);
        prevButton.addActionListener(e -> prevCallback.run());

        nextButton = new JButton("Next");
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> nextCallback.run());

        saveButton = new JButton("Save");
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> saveCallback.run());

        // Tambahkan tombol ke panel tombol
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(saveButton);

        // Tambahkan komponen ke panel utama
        add(statusLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    public void setPrevEnabled(boolean enabled) {
        prevButton.setEnabled(enabled);
    }

    public void setNextEnabled(boolean enabled) {
        nextButton.setEnabled(enabled);
    }

    public void setSaveEnabled(boolean enabled) {
        saveButton.setEnabled(enabled);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, 40);
    }
}