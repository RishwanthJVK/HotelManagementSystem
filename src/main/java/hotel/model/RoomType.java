package hotel.model;

// Week 5: Enum with constructor and methods
public enum RoomType {
    STANDARD("Standard", 1500.0),
    DELUXE("Deluxe", 2500.0),
    SUITE("Suite", 5000.0);

    private final String displayName;
    private final double baseTariff;

    RoomType(String displayName, double baseTariff) {
        this.displayName = displayName;
        this.baseTariff = baseTariff;
    }

    public double getBaseTariff() {
        return baseTariff;
    }

    public double calculateTotalCost(int days) {
        return baseTariff * days;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
