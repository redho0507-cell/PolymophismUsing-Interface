public class Student implements Measurable {
    private String name;
    private double height;

    public Student(String name, double height) {
        this.name = name;
        this.height = height;
    }

    public double getHeight() {
        return height;
    }

    public double getMeasure() {
        return height;
    }

    public String getName() {
        return name;
    }
}