public class Product implements Price {
    private String type;
    private Customer buy;

    public Product(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "The product is ";
    }

    @Override
    public void getPrice(Customer c) {
        buy = c;
        System.out.println(this);
        if (c.getSalary() > 4000) {
            System.out.println("The customer's name is " + c.getName()
                + " with salary RM" + c.getSalary() + " can buy this product.");
            System.out.println("Your salary is higher than the price's product.");
        } else {
            System.out.println("The customer's name is " + c.getName()
                + " with salary RM" + c.getSalary() + " cannot buy this product.");
            System.out.println("The product price is more than your salary.");
        }
    }
}