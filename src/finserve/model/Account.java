package finserve.model;

public class Account {
    private final String accountNumber;
    private final String customerId;
    private double balance;
    private final String type;

    public Account(String accountNumber, String customerId, double balance, String type) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.balance = balance;
        this.type = type;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getCustomerId() { return customerId; }
    public double getBalance() { return balance; }
    public String getType() { return type; }
    public void setBalance(double balance) { this.balance = balance; }

    @Override
    public String toString() {
        return String.format("%-14s cust=%-8s %-9s balance=%.2f",
                accountNumber, customerId, type, balance);
    }
}
