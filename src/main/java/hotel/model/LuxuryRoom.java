package hotel.model;

public class LuxuryRoom extends Room implements Amenities {
    private boolean hasPrivatePool;

    public LuxuryRoom(int roomNumber, boolean hasPrivatePool) {
        super(roomNumber, RoomType.SUITE.getBaseTariff(), RoomType.SUITE);
        this.hasPrivatePool = hasPrivatePool;
    }

    @Override
    public double calculateTariff(int days) {
        double tariff = (basePrice + 1500) * days; // unboxing
        if (hasPrivatePool) tariff += 2000 * days;
        return tariff;
    }

    @Override
    public boolean provideWifi() { return true; }

    @Override
    public boolean provideBreakfast() { return true; }

    @Override
    public String getAmenitiesDescription() {
        return "WiFi, AC, Breakfast, Premium Services" + (hasPrivatePool ? ", Private Pool" : "");
    }

    public boolean isHasPrivatePool() { return hasPrivatePool; }
}
