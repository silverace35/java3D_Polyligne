import java.awt.*;
import java.awt.event.*;
import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.util.concurrent.ConcurrentHashMap;

public class CameraController {
    private final TransformGroup cameraTransformGroup;
    private final Transform3D cameraTransform;
    private final AppWindow appWindow;

    private float speedMultiplier = 1.0f; // Multiplicateur de vitesse
    private float baseSpeed = 0.05f; // Vitesse de base
    private float cameraSensitivity = 0.005f; // Sensibilité de la souris

    private final Vector3f cameraPosition = new Vector3f(0, 2, 10);
    private float yaw = 0f; // Rotation autour de Y
    private float pitch = 0f; // Rotation autour de X

    private final ConcurrentHashMap<Integer, Boolean> keysPressed = new ConcurrentHashMap<>();
    private boolean isDragging = false;
    private int lastMouseX, lastMouseY;
    private Dimension screenSize;

    public CameraController(Scene3D scene3D, TransformGroup cameraTransformGroup, AppWindow appWindow) {
        this.cameraTransformGroup = cameraTransformGroup;
        this.cameraTransform = new Transform3D();
        this.appWindow = appWindow;

        // Obtenir les dimensions de l'écran
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        initializeInputListeners(scene3D.getCanvas());
        updateCameraTransform();
    }

    private void initializeInputListeners(Canvas3D canvas) {
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
                    try {
                        Robot robot = new Robot();

                        int currentMouseX = e.getXOnScreen();
                        int currentMouseY = e.getYOnScreen();

                        int deltaX = currentMouseX - lastMouseX;
                        int deltaY = currentMouseY - lastMouseY;

                        yaw += -deltaX * cameraSensitivity; // Rotation horizontale
                        pitch += -deltaY * cameraSensitivity; // Rotation verticale

                        pitch = (float) Math.max(-Math.PI / 2 + 0.01, Math.min(Math.PI / 2 - 0.01, pitch)); // Limiter la rotation verticale
                        updateCameraTransform();

                        // Vérifier si la souris atteint un bord et la téléporter
                        if (currentMouseX <= 0) {
                            robot.mouseMove(screenSize.width - 2, currentMouseY);
                            lastMouseX = screenSize.width - 2;
                        } else if (currentMouseX >= screenSize.width - 1) {
                            robot.mouseMove(1, currentMouseY);
                            lastMouseX = 1;
                        } else {
                            lastMouseX = currentMouseX;
                        }

                        if (currentMouseY <= 0) {
                            robot.mouseMove(currentMouseX, screenSize.height - 2);
                            lastMouseY = screenSize.height - 2;
                        } else if (currentMouseY >= screenSize.height - 1) {
                            robot.mouseMove(currentMouseX, 1);
                            lastMouseY = 1;
                        } else {
                            lastMouseY = currentMouseY;
                        }
                    } catch (AWTException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        canvas.addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            if (notches < 0) { // Molette vers le haut
                speedMultiplier = Math.min(speedMultiplier + 0.1f, 10.0f);
            } else if (notches > 0) { // Molette vers le bas
                speedMultiplier = Math.max(0.1f, speedMultiplier - 0.1f);
            }
            appWindow.updateCameraSpeedLabel(speedMultiplier);
        });
    }

    public void update() {
        Vector3f movement = new Vector3f();

        if (keysPressed.getOrDefault(KeyEvent.VK_Z, false)) { // Avancer
            movement.z -= baseSpeed * speedMultiplier;
        }
        if (keysPressed.getOrDefault(KeyEvent.VK_S, false)) { // Reculer
            movement.z += baseSpeed * speedMultiplier;
        }
        if (keysPressed.getOrDefault(KeyEvent.VK_Q, false)) { // Gauche
            movement.x -= baseSpeed * speedMultiplier;
        }
        if (keysPressed.getOrDefault(KeyEvent.VK_D, false)) { // Droite
            movement.x += baseSpeed * speedMultiplier;
        }

        applyRelativeMovement(movement);
    }

    private void applyRelativeMovement(Vector3f movement) {
        Transform3D rotationTransform = new Transform3D();
        rotationTransform.rotY(yaw); // Rotation horizontale
        Transform3D pitchTransform = new Transform3D();
        pitchTransform.rotX(pitch); // Rotation verticale
        rotationTransform.mul(pitchTransform); // Combiner les deux rotations

        Transform3D movementTransform = new Transform3D();
        movementTransform.setTranslation(movement);

        Transform3D combinedTransform = new Transform3D();
        combinedTransform.mul(rotationTransform, movementTransform);

        Vector3f transformedMovement = new Vector3f();
        combinedTransform.get(transformedMovement);

        cameraPosition.add(transformedMovement);
        updateCameraTransform();
    }

    private void updateCameraTransform() {
        Transform3D rotationY = new Transform3D();
        Transform3D rotationX = new Transform3D();

        rotationY.rotY(yaw); // Rotation autour de l'axe Y
        rotationX.rotX(pitch); // Rotation autour de l'axe X

        Transform3D combinedRotation = new Transform3D();
        combinedRotation.mul(rotationY);
        combinedRotation.mul(rotationX);

        combinedRotation.setTranslation(cameraPosition);

        // Appliquer la transformation finale au groupe de transformation de la caméra
        cameraTransformGroup.setTransform(combinedRotation);
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }
}
