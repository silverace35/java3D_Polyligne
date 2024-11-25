import java.util.UUID;

public abstract class AViewModel<T> {
    private final String id;
    private T object; // L'objet associé au ViewModel

    public AViewModel(T object) {
        this.id = UUID.randomUUID().toString(); // Génère un identifiant unique
        this.object = object;
    }

    public String getId() {
        return id;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    // Méthode abstraite pour être implémentée par les sous-classes
    public abstract void update();

    @Override
    public String toString() {
        return "AViewModel{" +
                "id='" + id + '\'' +
                ", object=" + object +
                '}';
    }
}
