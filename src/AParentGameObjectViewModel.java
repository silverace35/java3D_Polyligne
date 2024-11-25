import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AParentGameObjectViewModel extends AGameObjectViewModel {
    private final List<AGameObjectViewModel> children;

    public AParentGameObjectViewModel(GameObject gameObject) {
        super(gameObject);
        this.children = new ArrayList<>();
    }

    // Ajouter un enfant
    public void addChild(AGameObjectViewModel child) {
        children.add(child);
    }

    // Supprimer un enfant
    public void removeChild(AGameObjectViewModel child) {
        children.remove(child);
    }

    // Obtenir une liste non modifiable des enfants
    public List<AGameObjectViewModel> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public String toString() {
        return "AParentGameObjectViewModel{" +
                "id='" + getId() + '\'' +
                ", gameObject=" + getObject() +
                ", children=" + children +
                '}';
    }
}
