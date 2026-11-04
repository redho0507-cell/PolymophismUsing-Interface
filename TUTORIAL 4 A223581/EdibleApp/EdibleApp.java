// EdibleApp.java
public class EdibleApp {

    public static void main(String[] args) {
        Edible[] objects = {
            new Animal("Chicken"),
            new Apple("Red Apple"),
            new Orange("Navel Orange")
        };

        Person p1 = new Person("Ali");
        Person p2 = new Person("Sarah");

        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof Animal) {
                System.out.println(p1.eating(objects[i]));
            } else {
                System.out.println(p2.eating(objects[i]));
            }
        }
    }
}