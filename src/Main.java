import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.event.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Créer un Canvas3D pour afficher le rendu
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas.setDoubleBufferEnable(true);
        canvas.setFocusable(true);
        canvas.requestFocus();

        // Configurer une fenêtre Swing pour l'application
        javax.swing.JFrame frame = new javax.swing.JFrame("Camera Movement with Adjustable Speed");
        frame.setSize(800, 600);
        frame.add(canvas);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Créer un SimpleUniverse pour gérer la scène
        SimpleUniverse universe = new SimpleUniverse(canvas);

        // Repositionner la caméra
        TransformGroup viewTransform = universe.getViewingPlatform().getViewPlatformTransform();
        Transform3D cameraRotation = new Transform3D();
        Transform3D cameraPosition = new Transform3D();
        Transform3D combinedTransform = new Transform3D();
        Vector3f cameraTranslation = new Vector3f(0.0f, 0.0f, 3.0f); // Position initiale

        cameraPosition.setTranslation(cameraTranslation);
        viewTransform.setTransform(cameraPosition);

        // Créer une branche principale pour la scène
        BranchGroup group = new BranchGroup();

        // Ajouter une sphère à la scène
        Sphere sphere = new Sphere(0.5f); // Sphère avec un rayon de 0.5
        group.addChild(sphere);

        // Ajouter une lumière ambiante
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        AmbientLight ambientLight = new AmbientLight(new Color3f(1.0f, 1.0f, 1.0f)); // Lumière ambiante blanche
        ambientLight.setInfluencingBounds(bounds);
        group.addChild(ambientLight);

        // Ajouter une lumière directionnelle
        DirectionalLight light = new DirectionalLight(new Color3f(1.0f, 1.0f, 1.0f), new Vector3f(-1.0f, -1.0f, -1.0f));
        light.setInfluencingBounds(bounds);
        group.addChild(light);

        // Ajouter un arrière-plan
        Background background = new Background(new Color3f(0.2f, 0.2f, 0.2f)); // Couleur gris foncé
        background.setApplicationBounds(bounds);
        group.addChild(background);

        // Ajouter la branche au SimpleUniverse
        universe.addBranchGraph(group);

        // Stocker les touches actuellement pressées
        ConcurrentHashMap<Integer, Boolean> keysPressed = new ConcurrentHashMap<>();
        final float[] speed = {0.05f}; // Vitesse initiale

        // Ajouter un écouteur clavier au Canvas3D
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keysPressed.put(e.getKeyCode(), true);
                if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_ADD) { // Augmenter la vitesse
                    speed[0] = speed[0] + 0.01f;
                    System.out.println("Vitesse augmentée : " + speed[0]);
                } else if (e.getKeyCode() == KeyEvent.VK_MINUS || e.getKeyCode() == KeyEvent.VK_SUBTRACT) { // Réduire la vitesse
                    speed[0] = Math.max(0.01f, speed[0] - 0.01f);
                    System.out.println("Vitesse réduite : " + speed[0]);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keysPressed.remove(e.getKeyCode());
            }
        });

        // Variables pour suivre l'état de la souris
        final boolean[] isRightMousePressed = {false};
        final int[] lastMouseX = {0};
        final int[] lastMouseY = {0};

        // Ajouter un écouteur de souris pour gérer les rotations de caméra
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    isRightMousePressed[0] = true;
                    lastMouseX[0] = e.getX();
                    lastMouseY[0] = e.getY();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    isRightMousePressed[0] = false;
                }
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isRightMousePressed[0]) {
                    int deltaX = e.getX() - lastMouseX[0];
                    int deltaY = e.getY() - lastMouseY[0];

                    // Mettre à jour la rotation de la caméra
                    Transform3D rotationX = new Transform3D();
                    Transform3D rotationY = new Transform3D();

                    rotationX.rotX(-deltaY * 0.005); // Rotation autour de X
                    rotationY.rotY(-deltaX * 0.005); // Rotation autour de Y

                    cameraRotation.mul(rotationY);
                    cameraRotation.mul(rotationX);

                    lastMouseX[0] = e.getX();
                    lastMouseY[0] = e.getY();
                }
            }
        });

        // Boucle de rendu à 240 FPS
        Thread animationThread = new Thread(() -> {
            long frameDuration = 1000 / 240; // Durée d'une frame en millisecondes (4.17ms)

            while (true) {
                // Calculer le déplacement relatif à la rotation actuelle
                Vector3f movement = new Vector3f();
                if (keysPressed.getOrDefault(KeyEvent.VK_Z, false)) { // Avancer
                    movement.z -= speed[0];
                }
                if (keysPressed.getOrDefault(KeyEvent.VK_S, false)) { // Reculer
                    movement.z += speed[0];
                }
                if (keysPressed.getOrDefault(KeyEvent.VK_Q, false)) { // Aller à gauche
                    movement.x -= speed[0];
                }
                if (keysPressed.getOrDefault(KeyEvent.VK_D, false)) { // Aller à droite
                    movement.x += speed[0];
                }

                // Appliquer la rotation actuelle à la direction de déplacement
                Transform3D movementTransform = new Transform3D();
                movementTransform.setTranslation(movement);

                Transform3D rotatedMovement = new Transform3D();
                rotatedMovement.mul(cameraRotation, movementTransform);

                Vector3f translatedMovement = new Vector3f();
                rotatedMovement.get(translatedMovement);
                cameraTranslation.add(translatedMovement);

                // Combiner translation et rotation
                combinedTransform.set(cameraRotation);
                combinedTransform.setTranslation(cameraTranslation);

                // Mettre à jour la position de la caméra dans le thread Swing
                SwingUtilities.invokeLater(() -> viewTransform.setTransform(combinedTransform));

                // Synchroniser la boucle à 240 FPS
                try {
                    Thread.sleep(frameDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        animationThread.setDaemon(true); // Assure que le thread se termine avec l'application
        animationThread.start();
    }
}
