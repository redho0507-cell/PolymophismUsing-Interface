public class Laptop extends Product {
    private String nameL;
    private double priceL;

    public Laptop(String type, String nameL, double priceL) {
        super(type);
        this.nameL = nameL;
        this.priceL = priceL;
    }

    @Override
    public String toString() {
        return super.toString() + nameL + ".";
    }
}