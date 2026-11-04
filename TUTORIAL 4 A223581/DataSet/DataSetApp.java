import java.text.DecimalFormat;

public class DataSetApp {

    public static void main(String[] args) {
        DecimalFormat df = new DecimalFormat("0.00");

        // --- Testing BankAccount ---
        DataSet bankData = new DataSet();
        bankData.add(new BankAccount(0));
        bankData.add(new BankAccount(10000));
        bankData.add(new BankAccount(2000));

        System.out.println("Bank Average Balance: " + bankData.getAverage());
        Measurable max = bankData.getMax();
        System.out.println("Bank Highest Balance: " + max.getMeasure());

        // --- Testing Coin ---
        DataSet coinData = new DataSet();
        coinData.add(new Coin(0.25, "Quarter"));
        coinData.add(new Coin(0.1, "Dime"));
        coinData.add(new Coin(0.05, "Nickel"));

        System.out.println("Average Coin Value: " + df.format(coinData.getAverage()));

        max = coinData.getMax();
        System.out.println("Highest Coin Balance: " + df.format(max.getMeasure()));
        System.out.println("Highest Coin's Name: " + max.getName());

        // --- Testing Student ---
        DataSet student1 = new DataSet();
        student1.add(new Student("Ali", 157.50));
        student1.add(new Student("Alex", 165.00));
        student1.add(new Student("Maria", 147.00));

        System.out.println("Average Student's Height: " + df.format(student1.getAverage()));

        max = student1.getMax();
        System.out.println("Tallest Height: " + df.format(max.getMeasure()));
        System.out.println("Tallest Student's Name: " + max.getName());
    }
}