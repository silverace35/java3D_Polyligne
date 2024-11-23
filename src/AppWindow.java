import javax.swing.*;

public class AppWindow extends JFrame {
    public AppWindow() {
        super("Java3D Game Engine");
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Créer les panneaux principaux
        Scene3D scene3D = new Scene3D();
        ConfigPanel configPanel = new ConfigPanel();

        // Ajouter les panneaux dans un JSplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scene3D.getCanvas(), configPanel);
        splitPane.setDividerLocation(Constants.INITIAL_SCENE_WIDTH);
        splitPane.setResizeWeight(0.8);
        splitPane.setOneTouchExpandable(true);

        // Ajouter le splitPane à la fenêtre principale
        getContentPane().add(splitPane);

        // Lancer le contrôleur pour gérer les entrées utilisateur
        new CameraController(scene3D, configPanel);
    }
}
