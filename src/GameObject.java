public class GameObject {
    private final String name;

    public GameObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void update() {
        System.out.println("Mise à jour de : " + name);
    }

    @Override
    public String toString() {
        return "GameObject{" +
                "name='" + name + '\'' +
                '}';
    }
}
