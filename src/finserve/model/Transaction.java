package finserve.model;

public class Transaction {
    private final String txnId;
    private final String fromAccount;
    private final String toAccount;
    private final double amount;
    private final String description;
    private final String timestamp;
    private final String currency;
    private String status;

    public Transaction(String txnId, String fromAccount, String toAccount, double amount,
                       String description, String timestamp, String currency, String status) {
        this.txnId = txnId;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
        this.currency = currency;
        this.status = status;
    }

    public String getTxnId() { return txnId; }
    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getTimestamp() { return timestamp; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("%-10s %-12s -> %-12s %10.2f %-4s %-9s %s",
                txnId, fromAccount, toAccount, amount, currency, status, description);
    }
}
