package finserve.util;

import finserve.model.Account;
import finserve.model.Customer;
import finserve.model.Document;
import finserve.model.Transaction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * In-memory seed data for the FinServe platform. Everything the demo needs
 * (customers, accounts, transactions, documents, fraud patterns) lives here.
 */
public final class DataStore {

    private static final List<Customer> CUSTOMERS = new ArrayList<Customer>();
    private static final List<Account> ACCOUNTS = new ArrayList<Account>();
    private static final List<Transaction> TRANSACTIONS = new ArrayList<Transaction>();
    private static final List<Document> DOCUMENTS = new ArrayList<Document>();
    private static final List<String> FRAUD_PATTERNS = new ArrayList<String>();

    static {
        seed();
    }

    private DataStore() { }

    private static void seed() {
        CUSTOMERS.add(new Customer("C001", "Aarav Sharma", "Hyderabad", 12));
        CUSTOMERS.add(new Customer("C002", "Diya Patel", "Mumbai", 45));
        CUSTOMERS.add(new Customer("C003", "Vivaan Rao", "Bengaluru", 71));
        CUSTOMERS.add(new Customer("C004", "Ananya Nair", "Chennai", 8));
        CUSTOMERS.add(new Customer("C005", "Ishaan Gupta", "Delhi", 63));
        CUSTOMERS.add(new Customer("C006", "Meera Iyer", "Pune", 27));
        CUSTOMERS.add(new Customer("C007", "Kabir Singh", "Jaipur", 88));
        CUSTOMERS.add(new Customer("C008", "Riya Desai", "Surat", 19));

        ACCOUNTS.add(new Account("ACC1001", "C001", 152340.75, "SAVINGS"));
        ACCOUNTS.add(new Account("ACC1002", "C002", 89211.10, "CURRENT"));
        ACCOUNTS.add(new Account("ACC1003", "C003", 405512.00, "SAVINGS"));
        ACCOUNTS.add(new Account("ACC1004", "C004", 12900.40, "SALARY"));
        ACCOUNTS.add(new Account("ACC1005", "C005", 76320.00, "SAVINGS"));
        ACCOUNTS.add(new Account("ACC1006", "C006", 54200.90, "NRI"));
        ACCOUNTS.add(new Account("ACC1007", "C007", 998001.25, "CURRENT"));
        ACCOUNTS.add(new Account("ACC1008", "C008", 33000.00, "SALARY"));

        addTxn("TXN0001", "ACC1001", "ACC1002", 25000.00, "NEFT transfer salary payment", "2026-01-03 09:14", "INR", "COMPLETED");
        addTxn("TXN0002", "ACC1002", "ACC1003", 120000.50, "IMPS transfer invoice settlement", "2026-01-03 11:02", "INR", "COMPLETED");
        addTxn("TXN0003", "ACC1007", "ACC1008", 999999.00, "Cash deposit structured withdrawal test", "2026-01-04 01:47", "INR", "FLAGGED");
        addTxn("TXN0004", "ACC1003", "ACC1006", 45000.00, "UPI transfer utility bill payment", "2026-01-04 15:30", "INR", "COMPLETED");
        addTxn("TXN0005", "ACC1005", "ACC1001", 7500.00, "NEFT transfer rent payment", "2026-01-05 08:05", "INR", "COMPLETED");
        addTxn("TXN0006", "ACC1004", "ACC1007", 500000.00, "International wire high risk transfer", "2026-01-05 23:58", "USD", "FLAGGED");
        addTxn("TXN0007", "ACC1006", "ACC1005", 32000.00, "IMPS transfer education fees", "2026-01-06 10:22", "INR", "COMPLETED");
        addTxn("TXN0008", "ACC1008", "ACC1002", 15000.00, "UPI transfer mobile recharge", "2026-01-06 18:44", "INR", "COMPLETED");
        addTxn("TXN0009", "ACC1002", "ACC1004", 88000.00, "NEFT transfer vendor payment", "2026-01-07 12:10", "INR", "PENDING");
        addTxn("TXN0010", "ACC1007", "ACC1003", 250000.00, "Cash deposit repeated deposit pattern", "2026-01-07 02:15", "INR", "FLAGGED");

        DOCUMENTS.add(new Document("DOC001", "Quarterly Fraud Report",
                "The bank observed repeated transfer structure across multiple accounts. "
              + "Customer C007 performed cash deposit and structured withdrawal repeatedly. "
              + "Anomaly detection flagged a high risk transfer to an offshore account."));
        DOCUMENTS.add(new Document("DOC002", "AML Compliance Notes",
                "Anti money laundering review covers transfer patterns, cash deposit limits "
              + "and high risk transfer monitoring. Suspicious structured deposit sequences require reporting."));
        DOCUMENTS.add(new Document("DOC003", "Loan Policy Handbook",
                "Salary payment history and rent payment records are used to evaluate credit worthiness. "
              + "Utility bill payment and education fees show financial responsibility."));

        FRAUD_PATTERNS.add("structured withdrawal");
        FRAUD_PATTERNS.add("cash deposit");
        FRAUD_PATTERNS.add("high risk transfer");
        FRAUD_PATTERNS.add("anomaly detection");
        FRAUD_PATTERNS.add("offshore account");
    }

    private static void addTxn(String id, String from, String to, double amt,
                               String desc, String ts, String cur, String status) {
        TRANSACTIONS.add(new Transaction(id, from, to, amt, desc, ts, cur, status));
    }

    public static List<Customer> customers() { return CUSTOMERS; }
    public static List<Account> accounts() { return ACCOUNTS; }
    public static List<Transaction> transactions() { return TRANSACTIONS; }
    public static List<Document> documents() { return DOCUMENTS; }
    public static List<String> fraudPatterns() { return FRAUD_PATTERNS; }

    /** All transaction descriptions joined by a separator, used as corpus. */
    public static String transactionCorpus() {
        StringBuilder sb = new StringBuilder();
        for (Transaction t : TRANSACTIONS) {
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            sb.append(t.getDescription());
        }
        return sb.toString();
    }

    /** Concatenated document text. */
    public static String documentCorpus() {
        StringBuilder sb = new StringBuilder();
        for (Document d : DOCUMENTS) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(d.getContent());
        }
        return sb.toString();
    }

    /** Distinct list of account numbers (used as flow-network nodes). */
    public static List<String> accountNumbers() {
        List<String> out = new ArrayList<String>();
        for (Account a : ACCOUNTS) {
            out.add(a.getAccountNumber());
        }
        return out;
    }

    /** Maps account number -> index for quick lookup in graph algorithms. */
    public static Map<String, Integer> accountIndex() {
        Map<String, Integer> map = new LinkedHashMap<String, Integer>();
        int i = 0;
        for (String a : accountNumbers()) {
            map.put(a, i++);
        }
        return map;
    }

    /**
     * Builds a random 1-based weighted directed graph representing inter-bank
     * transaction capacity. Returns int[][] adjacency (0 means no edge).
     */
    public static int[][] randomCapacityGraph(int n, int maxCap, long seed) {
        Random rnd = new Random(seed);
        int[][] cap = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && rnd.nextInt(100) < 35) {
                    cap[i][j] = 1 + rnd.nextInt(maxCap);
                }
            }
        }
        return cap;
    }
}
