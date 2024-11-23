import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;

public class Scene3D {
    private final Canvas3D canvas;
    private final SimpleUniverse universe;
    private final TransformGroup viewTransform;
    private final Transform3D cameraRotation;
    private final Vector3f cameraTranslation;

    public Scene3D() {
        // Canvas3D
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas.setDoubleBufferEnable(true);

        // Universe et caméra
        universe = new SimpleUniverse(canvas);
        viewTransform = universe.getViewingPlatform().getViewPlatformTransform();
        cameraRotation = new Transform3D();
        cameraTranslation = new Vector3f(0.0f, 0.0f, 3.0f);

        // Initialiser la position de la caméra
        Transform3D cameraPosition = new Transform3D();
        cameraPosition.setTranslation(cameraTranslation);
        viewTransform.setTransform(cameraPosition);

        // Augmenter la distance de rendu
        universe.getViewingPlatform().getViewers()[0].getView().setBackClipDistance(1000.0);

        // Configurer la scène
        universe.addBranchGraph(createSceneGraph());
    }

    private BranchGroup createSceneGraph() {
        BranchGroup group = new BranchGroup();

        // Objet 3D (Sphère)
        Sphere sphere = new Sphere(0.5f);
        group.addChild(sphere);

        // Lumière ambiante
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0); // Augmenté à 1000
        AmbientLight ambientLight = new AmbientLight(new Color3f(1.0f, 1.0f, 1.0f));
        ambientLight.setInfluencingBounds(bounds);
        group.addChild(ambientLight);

        // Lumière directionnelle
        DirectionalLight directionalLight = new DirectionalLight(
                new Color3f(1.0f, 1.0f, 1.0f), new Vector3f(-1.0f, -1.0f, -1.0f));
        directionalLight.setInfluencingBounds(bounds);
        group.addChild(directionalLight);

        // Arrière-plan
        Background background = new Background(new Color3f(0.2f, 0.2f, 0.2f));
        background.setApplicationBounds(bounds);
        group.addChild(background);

        // Ajouter une grille au sol
        group.addChild(createGrid(50, 1.0f, new Color3f(0.5f, 0.5f, 0.5f)));

        return group;
    }

    /**
     * Crée une grille sur le plan XZ à Y = 0.
     * @param size Le demi-taille de la grille (en unités).
     * @param spacing L'espacement entre les lignes.
     * @param color La couleur des lignes.
     * @return Un Node contenant la grille.
     */
    private Node createGrid(int size, float spacing, Color3f color) {
        int numLines = (size * 2) + 1;
        LineArray gridLines = new LineArray(numLines * 4, LineArray.COORDINATES | LineArray.COLOR_3);

        // Générer les lignes de la grille
        int index = 0;
        for (int i = -size; i <= size; i++) {
            float coord = i * spacing;

            // Ligne verticale (parallèle à Z)
            gridLines.setCoordinate(index, new Point3f(coord, 0.0f, -size * spacing));
            gridLines.setCoordinate(index + 1, new Point3f(coord, 0.0f, size * spacing));

            // Ligne horizontale (parallèle à X)
            gridLines.setCoordinate(index + 2, new Point3f(-size * spacing, 0.0f, coord));
            gridLines.setCoordinate(index + 3, new Point3f(size * spacing, 0.0f, coord));

            // Couleur pour chaque segment
            gridLines.setColor(index, color);
            gridLines.setColor(index + 1, color);
            gridLines.setColor(index + 2, color);
            gridLines.setColor(index + 3, color);

            index += 4;
        }

        Shape3D gridShape = new Shape3D(gridLines);
        return gridShape;
    }

    public Canvas3D getCanvas() {
        return canvas;
    }

    public TransformGroup getViewTransform() {
        return viewTransform;
    }

    public Transform3D getCameraRotation() {
        return cameraRotation;
    }

    public Vector3f getCameraTranslation() {
        return cameraTranslation;
    }
}
