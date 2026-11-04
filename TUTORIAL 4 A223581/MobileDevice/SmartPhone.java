public class SmartPhone extends MobileDevice {
    private String type;

    public SmartPhone(String manufacturer, String model, String type) {
        super(manufacturer, model);
        this.type = type;
    }

    @Override
    public void printDetails() {
        System.out.println("SmartPhone Detail:");
        super.printDetails();
        System.out.println("Type: " + type);
    }
}