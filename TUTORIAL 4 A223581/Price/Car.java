public class Car implements Price {
    private String brand;
    private double priceCar;
    private Customer buy;

    public Car(String brand, double priceCar) {
        this.brand = brand;
        this.priceCar = priceCar;
    }

    public String getBrand() {
        return brand;
    }

    @Override
    public String toString() {
        return "The car brand is " + brand + " with price RM" + priceCar + ".";
    }

    @Override
    public void getPrice(Customer c) {
        buy = c;
        System.out.println(this);
        if (c.getSalary() > 8000) {
            System.out.println("The customer's name is " + c.getName()
                + " with salary RM" + c.getSalary() + " can buy this car. "
                + "Your salary is higher than the price's car.");
        } else {
            System.out.println("The customer's name is " + c.getName()
                + " with salary RM" + c.getSalary() + " cannot buy this car. "
                + "The car price is more than your salary.");
        }
    }
}