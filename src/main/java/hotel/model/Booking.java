package hotel.model;

import java.time.LocalDate;

public class Booking {
    private static int bookingCounter = 5000;

    private String bookingId;
    private String customerId;
    private String customerName;
    private int roomNumber;
    private RoomType roomType;
    private LocalDate checkInDate;
    private int days;
    private double totalAmount;
    private boolean isActive;

    public Booking(String customerId, String customerName, int roomNumber, RoomType roomType, int days, double totalAmount) {
        this.bookingId = "B" + (bookingCounter++);
        this.customerId = customerId;
        this.customerName = customerName;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkInDate = LocalDate.now();
        this.days = days;
        this.totalAmount = totalAmount;
        this.isActive = true;
    }

    public String getBookingId() { return bookingId; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public int getRoomNumber() { return roomNumber; }
    public RoomType getRoomType() { return roomType; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public int getDays() { return days; }
    public double getTotalAmount() { return totalAmount; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return bookingId + " | Room " + roomNumber + " | " + customerName + " | " + days + " days | ₹" + String.format("%.2f", totalAmount);
    }
}
