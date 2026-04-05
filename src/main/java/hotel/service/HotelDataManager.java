package hotel.service;

import hotel.model.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

// Week 8 & 9: Collections Framework
public class HotelDataManager {
    private static HotelDataManager instance;

    // Week 8: ArrayList and HashMap for storage
    private final ArrayList<Room> rooms = new ArrayList<>();
    private final ArrayList<Customer> customers = new ArrayList<>();
    private final ArrayList<Booking> bookings = new ArrayList<>();
    private final HashMap<Integer, String> roomToCustomerMap = new HashMap<>(); // roomNum -> customerId

    private static final String DATA_DIR = System.getProperty("user.home") + "/HotelData/";
    private static final String ROOMS_FILE = DATA_DIR + "rooms.dat";
    private static final String CUSTOMERS_FILE = DATA_DIR + "customers.dat";
    private static final String BOOKINGS_FILE = DATA_DIR + "bookings.dat";

    private HotelDataManager() {
        new File(DATA_DIR).mkdirs();
        loadData();
        if (rooms.isEmpty()) seedDefaultRooms();
    }

    public static HotelDataManager getInstance() {
        if (instance == null) instance = new HotelDataManager();
        return instance;
    }

    // ---- ROOM OPERATIONS ----

    public void addRoom(Room room) {
        rooms.add(room);
        saveRooms();
    }

    public boolean removeRoom(int roomNumber) {
        boolean removed = rooms.removeIf(r -> r.getRoomNumber() == roomNumber && r.isAvailable());
        if (removed) saveRooms();
        return removed;
    }

    public List<Room> getAllRooms() {
        return Collections.unmodifiableList(rooms);
    }

    public List<Room> getAvailableRooms() {
        // Week 8: Iterator usage internally
        List<Room> available = new ArrayList<>();
        Iterator<Room> it = rooms.iterator();
        while (it.hasNext()) {
            Room r = it.next();
            if (r.isAvailable()) available.add(r);
        }
        return available;
    }

    public Optional<Room> findRoom(int roomNumber) {
        return rooms.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst();
    }

    public boolean roomNumberExists(int roomNumber) {
        return rooms.stream().anyMatch(r -> r.getRoomNumber() == roomNumber);
    }

    public List<Room> getRoomsSortedByPrice() {
        List<Room> sorted = new ArrayList<>(rooms);
        Collections.sort(sorted, Comparator.comparingDouble(Room::getBasePrice));
        return sorted;
    }

    // ---- CUSTOMER OPERATIONS ----

    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomers();
    }

    public boolean removeCustomer(String customerId) {
        boolean removed = customers.removeIf(c -> c.getCustomerId().equals(customerId)
                && c.getAllocatedRoomNumber() == null);
        if (removed) saveCustomers();
        return removed;
    }

    public List<Customer> getAllCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public Optional<Customer> findCustomer(String customerId) {
        return customers.stream().filter(c -> c.getCustomerId().equals(customerId)).findFirst();
    }

    public boolean customerIdExists(String id) {
        return customers.stream().anyMatch(c -> c.getCustomerId().equals(id));
    }

    // ---- BOOKING OPERATIONS ----

    // Week 6: Synchronized for thread safety (Week 7 concept)
    public synchronized BookingResult bookRoom(String customerId, int roomNumber, int days) {
        Optional<Room> roomOpt = findRoom(roomNumber);
        Optional<Customer> custOpt = findCustomer(customerId);

        if (roomOpt.isEmpty()) return new BookingResult(false, "Room not found.");
        if (custOpt.isEmpty()) return new BookingResult(false, "Customer not found.");

        Room room = roomOpt.get();
        Customer customer = custOpt.get();

        if (!room.isAvailable()) return new BookingResult(false, "Room " + roomNumber + " is already occupied.");
        if (customer.getAllocatedRoomNumber() != null) return new BookingResult(false, "Customer already has a booking.");

        // Week 5: Autoboxing/Unboxing in tariff calculation
        Double tariff = room.calculateTariff(days); // result stored as wrapper
        double amount = tariff; // unboxing

        Booking booking = new Booking(customerId, customer.getName(), roomNumber, room.getRoomType(), days, amount);
        bookings.add(booking);

        room.setAvailable(false);
        customer.setAllocatedRoomNumber(roomNumber);  // autoboxing
        customer.setDaysStayed(days);
        roomToCustomerMap.put(roomNumber, customerId);

        saveAll();
        return new BookingResult(true, "Booking confirmed! ID: " + booking.getBookingId() + " | Total: ₹" + String.format("%.2f", amount));
    }

    public synchronized CheckoutResult checkoutRoom(int roomNumber) {
        Optional<Room> roomOpt = findRoom(roomNumber);
        if (roomOpt.isEmpty()) return new CheckoutResult(false, "Room not found.", null);

        Room room = roomOpt.get();
        if (room.isAvailable()) return new CheckoutResult(false, "Room is not currently booked.", null);

        String customerId = roomToCustomerMap.get(roomNumber);
        Optional<Customer> custOpt = findCustomer(customerId);

        // Find active booking
        Optional<Booking> bookingOpt = bookings.stream()
                .filter(b -> b.getRoomNumber() == roomNumber && b.isActive())
                .findFirst();

        room.setAvailable(true);
        roomToCustomerMap.remove(roomNumber);

        Bill bill = null;
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setActive(false);
            // Week 5: Wrapper class arithmetic (Integer, Double)
            Integer days = booking.getDays();
            Double roomTariff = room.getBasePrice();
            Double serviceCharge = 200.0;
            Double totalBill = (roomTariff * days) + serviceCharge; // unboxing arithmetic
            bill = new Bill(booking.getCustomerName(), roomNumber, room.getRoomType(), days, roomTariff, serviceCharge, totalBill);
        }

        custOpt.ifPresent(c -> {
            c.setAllocatedRoomNumber(null);
            c.setDaysStayed(0);
        });

        saveAll();
        return new CheckoutResult(true, "Checkout successful for Room " + roomNumber, bill);
    }

    public List<Booking> getAllBookings() { return Collections.unmodifiableList(bookings); }
    public List<Booking> getActiveBookings() {
        return bookings.stream().filter(Booking::isActive).collect(Collectors.toList());
    }

    public synchronized void clearAllRecords() {
        customers.clear();
        bookings.clear();
        roomToCustomerMap.clear();
        rooms.forEach(r -> r.setAvailable(true));
        saveAll();
    }

    // ---- FILE I/O (Week 6): Serialization / simple text persistence ----

    private void saveRooms() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ROOMS_FILE))) {
            for (Room r : rooms) {
                String typeCode = r instanceof LuxuryRoom ? "LUXURY" : r instanceof DeluxeRoom ? "DELUXE" : "STANDARD";
                String extra = "";
                if (r instanceof StandardRoom) extra = String.valueOf(((StandardRoom) r).isHasAC());
                if (r instanceof LuxuryRoom) extra = String.valueOf(((LuxuryRoom) r).isHasPrivatePool());
                pw.println(typeCode + "," + r.getRoomNumber() + "," + r.isAvailable() + "," + extra);
            }
        } catch (IOException ignored) {}
    }

    private void saveCustomers() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer c : customers) {
                pw.println(c.getCustomerId() + "," + c.getName() + "," + c.getContactNumber()
                        + "," + (c.getAllocatedRoomNumber() != null ? c.getAllocatedRoomNumber() : "null")
                        + "," + c.getDaysStayed());
            }
        } catch (IOException ignored) {}
    }

    private void saveBookings() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking b : bookings) {
                pw.println(b.getBookingId() + "," + b.getCustomerId() + "," + b.getCustomerName()
                        + "," + b.getRoomNumber() + "," + b.getRoomType().name()
                        + "," + b.getDays() + "," + b.getTotalAmount() + "," + b.isActive());
            }
        } catch (IOException ignored) {}
    }

    private void saveAll() { saveRooms(); saveCustomers(); saveBookings(); }

    private void loadData() {
        loadRooms();
        loadCustomers();
        loadBookings();
    }

    private void loadRooms() {
        File f = new File(ROOMS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) continue;
                String type = parts[0];
                int num = Integer.parseInt(parts[1]);
                boolean avail = Boolean.parseBoolean(parts[2]);
                Room room = null;
                if (type.equals("STANDARD")) {
                    boolean ac = parts.length > 3 && Boolean.parseBoolean(parts[3]);
                    room = new StandardRoom(num, ac);
                } else if (type.equals("DELUXE")) {
                    room = new DeluxeRoom(num);
                } else if (type.equals("LUXURY")) {
                    boolean pool = parts.length > 3 && Boolean.parseBoolean(parts[3]);
                    room = new LuxuryRoom(num, pool);
                }
                if (room != null) { room.setAvailable(avail); rooms.add(room); }
            }
        } catch (IOException ignored) {}
    }

    private void loadCustomers() {
        File f = new File(CUSTOMERS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 4) continue;
                Customer c = new Customer(p[1], p[2]);
                // override generated ID – not ideal but workable for file-based persistence
                if (!p[3].equals("null")) c.setAllocatedRoomNumber(Integer.parseInt(p[3]));
                if (p.length > 4) c.setDaysStayed(Integer.parseInt(p[4]));
                customers.add(c);
            }
        } catch (IOException ignored) {}
    }

    private void loadBookings() {
        File f = new File(BOOKINGS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 8) continue;
                RoomType rt = RoomType.valueOf(p[4]);
                Booking b = new Booking(p[1], p[2], Integer.parseInt(p[3]), rt, Integer.parseInt(p[5]), Double.parseDouble(p[6]));
                b.setActive(Boolean.parseBoolean(p[7]));
                bookings.add(b);
                if (b.isActive()) roomToCustomerMap.put(b.getRoomNumber(), b.getCustomerId());
            }
        } catch (IOException ignored) {}
    }

    private void seedDefaultRooms() {
        rooms.add(new StandardRoom(101, true));
        rooms.add(new StandardRoom(102, false));
        rooms.add(new StandardRoom(103, true));
        rooms.add(new DeluxeRoom(201));
        rooms.add(new DeluxeRoom(202));
        rooms.add(new LuxuryRoom(301, false));
        rooms.add(new LuxuryRoom(302, true));
        saveRooms();
    }

    // Inner result classes
    public static class BookingResult {
        public final boolean success;
        public final String message;
        BookingResult(boolean s, String m) { success = s; message = m; }
    }

    public static class CheckoutResult {
        public final boolean success;
        public final String message;
        public final Bill bill;
        CheckoutResult(boolean s, String m, Bill b) { success = s; message = m; bill = b; }
    }

    public static class Bill {
        public final String customerName;
        public final int roomNumber;
        public final RoomType roomType;
        public final int days;
        public final double roomTariff;
        public final double serviceCharge;
        public final double totalAmount;

        Bill(String cn, int rn, RoomType rt, int d, double tar, double svc, double total) {
            customerName = cn; roomNumber = rn; roomType = rt; days = d;
            roomTariff = tar; serviceCharge = svc; totalAmount = total;
        }
    }
}
