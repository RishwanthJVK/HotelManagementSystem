package hotel.model;

// Week 2: Inheritance + Interface implementation
public class StandardRoom extends Room implements Amenities {
    private boolean hasAC;

    public StandardRoom(int roomNumber, boolean hasAC) {
        super(roomNumber, RoomType.STANDARD.getBaseTariff(), RoomType.STANDARD);
        this.hasAC = hasAC;
    }

    // Week 2: Runtime polymorphism override
    @Override
    public double calculateTariff(int days) {
        double tariff = basePrice * days;  // unboxing
        if (hasAC) tariff += 200 * days;
        return tariff;
    }

    @Override
    public boolean provideWifi() { return true; }

    @Override
    public boolean provideBreakfast() { return false; }

    @Override
    public String getAmenitiesDescription() {
        return "WiFi" + (hasAC ? ", AC" : "");
    }

    public boolean isHasAC() { return hasAC; }
    public void setHasAC(boolean hasAC) { this.hasAC = hasAC; }
}
