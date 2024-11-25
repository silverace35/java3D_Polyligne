public class GameObjectViewModel extends AViewModel<GameObject> {
    public GameObjectViewModel(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void update() {
        System.out.println("Mise à jour du GameObjectViewModel pour : " + getObject().getName());
    }
}
