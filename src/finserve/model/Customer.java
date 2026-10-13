package finserve.model;

public class Customer {
    private final String id;
    private final String name;
    private final String city;
    private final int riskScore;

    public Customer(String id, String name, String city, int riskScore) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.riskScore = riskScore;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public int getRiskScore() { return riskScore; }

    @Override
    public String toString() {
        return String.format("%-8s %-18s %-14s risk=%d", id, name, city, riskScore);
    }
}
