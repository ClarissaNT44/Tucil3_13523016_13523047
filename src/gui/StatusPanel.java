package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


/**
 * Panel status untuk menampilkan informasi statistik dan status.
 */
public class StatusPanel extends JPanel {
    private JLabel statusLabel;

    public StatusPanel() {
        setLayout(new BorderLayout());
        statusLabel = new JLabel("Load a puzzle to start.");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLabel, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, 60);
    }
}