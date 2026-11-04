public class Animal implements Edible {
    private String name;

    public Animal(String name) {
        this.name = name;
    }

    public String howToEat() {
        return name + ". " + name + " needs to be cooked first before eating.";
    }
}