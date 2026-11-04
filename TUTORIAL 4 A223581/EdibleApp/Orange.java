public class Orange extends Fruit {
    public Orange(String name) {
        super(name);
    }

    public String howToEat() {
        return super.howToEat() + name + " can make orange butter cake.";
    }
}