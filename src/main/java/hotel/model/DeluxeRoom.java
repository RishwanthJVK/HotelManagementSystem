package hotel.model;

public class DeluxeRoom extends Room implements Amenities {
    public DeluxeRoom(int roomNumber) {
        super(roomNumber, RoomType.DELUXE.getBaseTariff(), RoomType.DELUXE);
    }

    @Override
    public double calculateTariff(int days) {
        return (basePrice + 500) * days; // unboxing
    }

    @Override
    public boolean provideWifi() { return true; }

    @Override
    public boolean provideBreakfast() { return true; }

    @Override
    public String getAmenitiesDescription() {
        return "WiFi, AC, Breakfast";
    }
}
