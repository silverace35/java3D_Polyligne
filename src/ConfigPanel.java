import javax.swing.*;
import java.awt.*;

public class ConfigPanel extends JPanel {
    private final JLabel speedDisplay;

    public ConfigPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.DARK_GRAY);

        JLabel configLabel = new JLabel("Configuration");
        configLabel.setFont(new Font("Arial", Font.BOLD, 16));
        configLabel.setForeground(Color.WHITE);
        configLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(configLabel);

        add(Box.createRigidArea(new Dimension(0, 20)));

        speedDisplay = new JLabel("Multiplicateur : 1.0");
        speedDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
        speedDisplay.setForeground(Color.WHITE);
        speedDisplay.setAlignmentX(CENTER_ALIGNMENT);
        add(speedDisplay);

        add(Box.createVerticalGlue());
    }

    public void updateSpeedDisplay(float speedMultiplier) {
        speedDisplay.setText(String.format("Multiplicateur : %.1f", speedMultiplier));
    }
}
