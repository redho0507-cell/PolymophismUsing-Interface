public class SmartPhone extends Product {
    private String nameSP;
    private double priceSP;

    public SmartPhone(String type, String nameSP, double priceSP) {
        super(type);
        this.nameSP = nameSP;
        this.priceSP = priceSP;
    }

    @Override
    public String toString() {
        return super.toString() + nameSP + ".";
    }
}