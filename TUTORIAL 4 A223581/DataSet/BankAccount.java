public class BankAccount implements Measurable {
    private int accNumber;
    private double balance;
    private static int LastAssignedNumber = 1000;

    public BankAccount() {
        this(0);
    }

    public BankAccount(double balance) {
        LastAssignedNumber++;
        this.accNumber = LastAssignedNumber;
        this.balance = balance;
    }

    public int getAccountNumber() {
        return accNumber;
    }

    public double getMeasure() {
        return balance;
    }

    public String getName() {
        return "Account " + accNumber;
    }
}