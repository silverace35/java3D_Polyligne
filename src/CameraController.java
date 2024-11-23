import javax.swing.*;
import javax.vecmath.*;
import javax.media.j3d.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ConcurrentHashMap;

public class CameraController {
    private final Scene3D scene3D;
    private final ConfigPanel configPanel;
    private final Robot robot;
    private final ConcurrentHashMap<Integer, Boolean> keysPressed = new ConcurrentHashMap<>();
    private float speedMultiplier = 1.0f;
    private final float baseSpeed = 0.05f;
    private int lastMouseX, lastMouseY;
    private boolean isDragging = false;

    // Angles pour maintenir la rotation de la caméra
    private float cameraPitch = 0.0f; // Rotation autour de l'axe X (haut-bas)
    private float cameraYaw = 0.0f;   // Rotation autour de l'axe Y (gauche-droite)

    public CameraController(Scene3D scene3D, ConfigPanel configPanel) {
        this.scene3D = scene3D;
        this.configPanel = configPanel;

        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException("Failed to initialize robot for mouse control", e);
        }

        initializeMouseListeners();
        initializeKeyboardListeners();
        startAnimationLoop();
    }

    private void initializeMouseListeners() {
        Canvas3D canvas = scene3D.getCanvas();

        // Gestion de la molette pour ajuster la vitesse
        canvas.addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            if (notches < 0) { // Molette vers le haut
                speedMultiplier = Math.min(speedMultiplier + 0.1f, 10.0f);
            } else { // Molette vers le bas
                speedMultiplier = Math.max(speedMultiplier - 0.1f, 0.1f);
            }
            configPanel.updateSpeedDisplay(speedMultiplier);
        });

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    isDragging = true;
                    lastMouseX = e.getXOnScreen();
                    lastMouseY = e.getYOnScreen();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    isDragging = false;
                }
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    int currentMouseX = e.getXOnScreen();
                    int currentMouseY = e.getYOnScreen();

                    int deltaX = currentMouseX - lastMouseX;
                    int deltaY = currentMouseY - lastMouseY;

                    // Gestion des bordures de l'écran
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    if (currentMouseX <= 0) {
                        robot.mouseMove(screenSize.width - 2, currentMouseY);
                        currentMouseX = screenSize.width - 2;
                    } else if (currentMouseX >= screenSize.width - 1) {
                        robot.mouseMove(1, currentMouseY);
                        currentMouseX = 1;
                    }

                    if (currentMouseY <= 0) {
                        robot.mouseMove(currentMouseX, screenSize.height - 2);
                        currentMouseY = screenSize.height - 2;
                    } else if (currentMouseY >= screenSize.height - 1) {
                        robot.mouseMove(currentMouseX, 1);
                        currentMouseY = 1;
                    }

                    // Mise à jour des angles de rotation
                    float sensitivity = 0.2f; // Sensibilité de la rotation
                    cameraYaw -= deltaX * sensitivity * 0.01f;
                    cameraPitch = Math.max(-90, Math.min(90, cameraPitch - deltaY * sensitivity * 0.01f)); // Limiter le pitch

                    updateCameraRotation();

                    lastMouseX = currentMouseX;
                    lastMouseY = currentMouseY;
                }
            }
        });
    }

    private void initializeKeyboardListeners() {
        Canvas3D canvas = scene3D.getCanvas();

        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keysPressed.put(e.getKeyCode(), true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keysPressed.remove(e.getKeyCode());
            }
        });
    }

    private void startAnimationLoop() {
        new Thread(() -> {
            while (true) {
                Vector3f movement = new Vector3f();
                float currentSpeed = baseSpeed * speedMultiplier;

                if (keysPressed.getOrDefault(KeyEvent.VK_Z, false)) {
                    movement.z -= currentSpeed; // Avancer
                }
                if (keysPressed.getOrDefault(KeyEvent.VK_S, false)) {
                    movement.z += currentSpeed; // Reculer
                }
                if (keysPressed.getOrDefault(KeyEvent.VK_Q, false)) {
                    movement.x -= currentSpeed; // Aller à gauche
                }
                if (keysPressed.getOrDefault(KeyEvent.VK_D, false)) {
                    movement.x += currentSpeed; // Aller à droite
                }

                // Appliquer la rotation et la translation
                Transform3D movementTransform = new Transform3D();
                movementTransform.setTranslation(movement);

                Transform3D rotatedMovement = new Transform3D();
                rotatedMovement.mul(scene3D.getCameraRotation(), movementTransform);

                Vector3f translatedMovement = new Vector3f();
                rotatedMovement.get(translatedMovement);
                scene3D.getCameraTranslation().add(translatedMovement);

                updateCamera();

                // Boucle à 240 FPS
                try {
                    Thread.sleep(1000 / 240);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private void updateCameraRotation() {
        // Réinitialiser la rotation
        Transform3D yawRotation = new Transform3D();
        Transform3D pitchRotation = new Transform3D();

        yawRotation.rotY(cameraYaw);
        pitchRotation.rotX(cameraPitch);

        Transform3D combinedRotation = new Transform3D();
        combinedRotation.mul(yawRotation);
        combinedRotation.mul(pitchRotation);

        scene3D.getCameraRotation().set(combinedRotation);

        updateCamera();
    }

    private void updateCamera() {
        Transform3D combinedTransform = new Transform3D(scene3D.getCameraRotation());
        combinedTransform.setTranslation(scene3D.getCameraTranslation());
        scene3D.getViewTransform().setTransform(combinedTransform);
    }
}
