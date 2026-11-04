public class Apple extends Fruit {
    public Apple(String name) {
        super(name);
    }

    public String howToEat() {
        return super.howToEat() + name + " can make apple pie.";
    }
}