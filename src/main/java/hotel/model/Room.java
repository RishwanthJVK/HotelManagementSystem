package hotel.model;

// Week 2: Abstract class
public abstract class Room {
    protected Integer roomNumber;  // Week 5: Wrapper class
    protected Double basePrice;    // Week 5: Wrapper class
    protected RoomType roomType;
    protected boolean isAvailable;

    public Room(int roomNumber, double basePrice, RoomType roomType) {
        this.roomNumber = roomNumber;   // autoboxing
        this.basePrice = basePrice;     // autoboxing
        this.roomType = roomType;
        this.isAvailable = true;
    }

    // Week 2: Abstract method
    public abstract double calculateTariff(int days);

    // Concrete method
    public void displayRoomDetails() {
        System.out.println("Room #" + roomNumber + " | Type: " + roomType + " | Base: ₹" + basePrice + " | Available: " + isAvailable);
    }

    // Getters and setters
    public int getRoomNumber() { return roomNumber; }  // unboxing
    public double getBasePrice() { return basePrice; } // unboxing
    public RoomType getRoomType() { return roomType; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    @Override
    public String toString() {
        return "Room #" + roomNumber + " (" + roomType.getDisplayName() + ") - ₹" + basePrice + "/day - " + (isAvailable ? "Available" : "Occupied");
    }
}
