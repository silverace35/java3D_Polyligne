import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AParentViewModel<T, C extends AViewModel<?>> extends AViewModel<T> {
    private final List<C> children;

    public AParentViewModel(T object) {
        super(object);
        this.children = new ArrayList<>();
    }

    // Ajouter un enfant
    public void addChild(C child) {
        children.add(child);
    }

    // Supprimer un enfant
    public void removeChild(C child) {
        children.remove(child);
    }

    // Obtenir une liste non modifiable des enfants
    public List<C> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public String toString() {
        return "AParentViewModel{" +
                "id='" + getId() + '\'' +
                ", object=" + getObject() +
                ", children=" + children +
                '}';
    }
}
