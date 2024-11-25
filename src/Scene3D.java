import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Scene3D {
    private final Canvas3D canvas;
    private final SimpleUniverse universe;
    private final TransformGroup cameraTransformGroup;
    private final List<GameObject> gameObjects; // Liste des objets dans la scène

    public Scene3D() {
        // Canvas pour le rendu
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas.setDoubleBufferEnable(true);

        // Univers Java3D
        universe = new SimpleUniverse(canvas);
        cameraTransformGroup = universe.getViewingPlatform().getViewPlatformTransform();

        // Liste des objets de la scène
        gameObjects = new ArrayList<>();

        initializeCamera(); // Initialiser la caméra
        BranchGroup sceneGraph = createSceneGraph(); // Créer les objets de la scène
        universe.addBranchGraph(sceneGraph);

        configureRenderDistance(); // Configurer la distance de rendu
    }

    private void initializeCamera() {
        Transform3D initialTransform = new Transform3D();
        Vector3f initialPosition = new Vector3f(0.0f, 5.0f, 15.0f); // Position initiale de la caméra
        initialTransform.setTranslation(initialPosition);
        cameraTransformGroup.setTransform(initialTransform);

        // Configurer un FOV par défaut
        setFieldOfView(Math.toRadians(60.0)); // 60° par défaut
    }

    private BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();

        // Ajouter une lumière directionnelle
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0); // Distance d'influence
        DirectionalLight light = new DirectionalLight(new Color3f(1.0f, 1.0f, 1.0f), new Vector3f(-1.0f, -1.0f, -1.0f));
        light.setInfluencingBounds(bounds);
        root.addChild(light);

        // Ajouter une lumière ambiante
        AmbientLight ambientLight = new AmbientLight(new Color3f(0.4f, 0.4f, 0.4f)); // Lumière douce
        ambientLight.setInfluencingBounds(bounds);
        root.addChild(ambientLight);

        // Ajouter une sphère par défaut comme objet de référence
        Sphere sphere = new Sphere(1.0f);
        TransformGroup sphereTransformGroup = new TransformGroup();
        Transform3D sphereTransform = new Transform3D();
        sphereTransform.setTranslation(new Vector3f(0, 1, 0)); // Positionner la sphère au-dessus de la grille
        sphereTransformGroup.setTransform(sphereTransform);
        sphereTransformGroup.addChild(sphere);
        root.addChild(sphereTransformGroup);

        // Ajouter la grille
        root.addChild(createGrid(20, 1.0f, new Color3f(0.7f, 0.7f, 0.7f))); // Grille de 20x20 avec espacement de 1 unité

        // Ajouter un arrière-plan (gris foncé)
        Background background = new Background(new Color3f(0.2f, 0.2f, 0.2f)); // Gris foncé
        background.setApplicationBounds(bounds);
        root.addChild(background);

        return root;
    }

    private void configureRenderDistance() {
        View view = universe.getViewer().getView();
        view.setBackClipDistance(500.0); // Distance maximale de rendu
        view.setFrontClipDistance(0.1); // Distance minimale de rendu
    }

    private Node createGrid(int size, float spacing, Color3f color) {
        LineArray gridLines = new LineArray((size * 4 + 4) * 2, LineArray.COORDINATES | LineArray.COLOR_3);
        int index = 0;
        for (int i = -size; i <= size; i++) {
            float coord = i * spacing;

            // Lignes parallèles à Z
            gridLines.setCoordinate(index, new Point3f(coord, 0.0f, -size * spacing)); // Début de la ligne
            gridLines.setCoordinate(index + 1, new Point3f(coord, 0.0f, size * spacing)); // Fin de la ligne

            // Lignes parallèles à X
            gridLines.setCoordinate(index + 2, new Point3f(-size * spacing, 0.0f, coord)); // Début de la ligne
            gridLines.setCoordinate(index + 3, new Point3f(size * spacing, 0.0f, coord)); // Fin de la ligne

            gridLines.setColor(index, color);
            gridLines.setColor(index + 1, color);
            gridLines.setColor(index + 2, color);
            gridLines.setColor(index + 3, color);

            index += 4;
        }

        return new Shape3D(gridLines);
    }

    public Canvas3D getCanvas() {
        return canvas;
    }

    public TransformGroup getCameraTransformGroup() {
        return cameraTransformGroup;
    }

    public void setFieldOfView(double fovRadians) {
        View view = universe.getViewer().getView();
        view.setFieldOfView(fovRadians);
    }

    public double getFieldOfView() {
        View view = universe.getViewer().getView();
        return view.getFieldOfView();
    }

    public List<GameObject> getGameObjects() {
        return Collections.unmodifiableList(gameObjects); // Retourne une liste immuable des objets
    }

    public void addGameObject(GameObject gameObject) {
        gameObjects.add(gameObject);
        System.out.println("GameObject ajouté : " + gameObject.getName());
    }

    public boolean removeGameObject(GameObject gameObject) {
        if (gameObjects.remove(gameObject)) {
            System.out.println("GameObject supprimé : " + gameObject.getName());
            return true;
        }
        System.out.println("GameObject introuvable : " + gameObject.getName());
        return false;
    }

    public void update() {
        for (GameObject obj : gameObjects) {
            obj.update(); // Appelle la mise à jour de chaque objet
        }
    }
}
