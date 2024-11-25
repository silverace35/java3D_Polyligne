public abstract class AGameObjectViewModel extends AViewModel<GameObject> {
    public AGameObjectViewModel(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public abstract void update();
}
