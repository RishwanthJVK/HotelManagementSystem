package hotel.model;

import java.time.LocalDate;

public class Customer {
    private static int idCounter = 1000;

    private String customerId;
    private String name;
    private String contactNumber;
    private Integer allocatedRoomNumber;  // Wrapper class
    private LocalDate checkInDate;
    private int daysStayed;

    public Customer(String name, String contactNumber) {
        this.customerId = "C" + (idCounter++);
        this.name = name;
        this.contactNumber = contactNumber;
        this.allocatedRoomNumber = null;
        this.checkInDate = LocalDate.now();
        this.daysStayed = 0;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public Integer getAllocatedRoomNumber() { return allocatedRoomNumber; }
    public void setAllocatedRoomNumber(Integer roomNumber) { this.allocatedRoomNumber = roomNumber; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate date) { this.checkInDate = date; }
    public int getDaysStayed() { return daysStayed; }
    public void setDaysStayed(int days) { this.daysStayed = days; }

    @Override
    public String toString() {
        return customerId + " - " + name + " | Contact: " + contactNumber + " | Room: " + (allocatedRoomNumber != null ? allocatedRoomNumber : "None");
    }
}
