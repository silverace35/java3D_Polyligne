import javax.swing.*;
import java.awt.*;

public class AppWindow extends JFrame {
    private Scene3D scene3D;
    private CameraController cameraController;
    private JLabel cameraSpeedLabel;

    public AppWindow() {
        setTitle("3D Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout());

        // Initialiser la scène et la caméra
        initializeScene();

        // Ajouter le Canvas3D de la scène
        add(scene3D.getCanvas(), BorderLayout.CENTER);

        // Ajouter le panneau latéral
        add(createControlPanel(), BorderLayout.EAST);

        // Démarrer la boucle de mise à jour
        startUpdateLoop();
    }

    private void initializeScene() {
        scene3D = new Scene3D();
        cameraController = new CameraController(scene3D, scene3D.getCameraTransformGroup(), this);
        scene3D.setFieldOfView(Math.toRadians(110)); //fov par défaut
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(200, getHeight()));
        panel.setBackground(new Color(40, 40, 40)); // Gris foncé

        JLabel titleLabel = new JLabel("Configuration");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.WHITE); // Texte blanc

        cameraSpeedLabel = new JLabel("Speed: " + cameraController.getSpeedMultiplier());
        cameraSpeedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cameraSpeedLabel.setForeground(Color.LIGHT_GRAY); // Texte gris clair

        JLabel fovLabel = new JLabel("FOV: 110°");
        fovLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fovLabel.setForeground(Color.LIGHT_GRAY); // Texte gris clair

        JSlider fovSlider = new JSlider(30, 120, 110); // FOV de 30° à 120°
        fovSlider.setBackground(new Color(40, 40, 40));
        fovSlider.setForeground(Color.WHITE);
        fovSlider.addChangeListener(e -> {
            int fov = fovSlider.getValue();
            fovLabel.setText("FOV: " + fov + "°");
            scene3D.setFieldOfView(Math.toRadians(fov)); // Convertir en radians
        });

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(cameraSpeedLabel);
        panel.add(fovLabel);
        panel.add(fovSlider);

        return panel;
    }

    public void updateCameraSpeedLabel(float newSpeed) {
        cameraSpeedLabel.setText("Camera Speed: " + String.format("%.1f", newSpeed));
    }

    private void startUpdateLoop() {
        Thread updateThread = new Thread(() -> {
            long frameDuration = 1000 / 240; // 240 FPS
            while (true) {
                cameraController.update(); // Mise à jour de la caméra
                scene3D.update(); // Mise à jour de la scène

                try {
                    Thread.sleep(frameDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        updateThread.setDaemon(true);
        updateThread.start();
    }
}
