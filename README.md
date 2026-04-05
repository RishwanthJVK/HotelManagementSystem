# 🏨 Hotel Management System — JavaFX Desktop Application
### OSDL Week 10 Final Project

---

## 📋 Overview

A fully-featured JavaFX Hotel Management System that demonstrates **all OOP and Java concepts** covered across the 10-week OSDL course:

| Week | Concept | Where Used |
|------|---------|------------|
| 1    | Classes & Objects | `Room`, `Customer`, `Booking` |
| 2    | Inheritance, Polymorphism, Abstract Class, Interface | `Room → StandardRoom / DeluxeRoom / LuxuryRoom`, `Amenities` interface |
| 3    | Exception Handling | Input validation throughout |
| 4    | Packages & Access Modifiers | `hotel.model`, `hotel.service`, `hotel.ui` |
| 5    | Wrapper Classes, Autoboxing/Unboxing, Enum | `Integer`/`Double` fields, `RoomType` enum |
| 6    | Multithreading (`Thread`, `Runnable`, `sleep()`) | `ServiceTask` — concurrent room service dispatching |
| 7    | Generics (`<T>`, `<T,U>`, Bounded Types) | `Pair<T,U>`, generic `<T extends Number>` tariff logic |
| 8    | File I/O (`FileReader`, `FileWriter`, `BufferedReader`) | `HotelDataManager` — persistent file storage |
| 9    | Collections (`ArrayList`, `HashMap`, `Iterator`, `Collections.sort`) | `HotelDataManager` — room, customer, booking storage |
| 10   | JavaFX GUI (Labels, Buttons, TableView, ComboBox, GridPane, VBox/HBox) | All UI tabs |

---

## 🚀 How to Run

### Prerequisites
- **Java 17+** (JDK)
- **Maven 3.6+**  OR  **JavaFX SDK 21** (for manual compile)

### Option A: Run with Maven (Recommended)
```bash
cd HotelManagementSystem
mvn javafx:run
```

### Option B: Run with JavaFX SDK (Manual)

1. Download JavaFX SDK 21 from https://gluonhq.com/products/javafx/
2. Extract to a folder, e.g. `/opt/javafx-sdk-21`
3. Compile and run:

```bash
# From HotelManagementSystem/
javac --module-path /opt/javafx-sdk-21/lib \
      --add-modules javafx.controls,javafx.fxml \
      -d out \
      $(find src/main/java -name "*.java")

java --module-path /opt/javafx-sdk-21/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp out \
     hotel.HotelApp
```

### Option C: Using an IDE (IntelliJ IDEA / Eclipse)

**IntelliJ IDEA:**
1. Open the `HotelManagementSystem` folder as a Maven project
2. IntelliJ will auto-download JavaFX via Maven
3. Run `HotelApp.java` → `main()`

**Eclipse:**
1. Install e(fx)clipse plugin
2. Import as Maven project
3. Run `HotelApp.java`

---

## 📁 Project Structure

```
HotelManagementSystem/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   ├── module-info.java
        │   └── hotel/
        │       ├── HotelApp.java            ← Entry point
        │       ├── model/
        │       │   ├── Amenities.java       ← Interface (Week 2)
        │       │   ├── RoomType.java        ← Enum (Week 5)
        │       │   ├── Room.java            ← Abstract class (Week 2)
        │       │   ├── StandardRoom.java    ← Inheritance + Interface
        │       │   ├── DeluxeRoom.java      ← Inheritance + Interface
        │       │   ├── LuxuryRoom.java      ← Inheritance + Interface
        │       │   ├── Customer.java        ← Wrapper class usage
        │       │   ├── Booking.java         ← Booking record
        │       │   └── Pair.java            ← Generic class (Week 7)
        │       ├── service/
        │       │   ├── HotelDataManager.java ← Collections + File I/O
        │       │   └── ServiceTask.java      ← Runnable (Week 6)
        │       └── ui/
        │           ├── MainWindow.java       ← Stage + TabPane
        │           ├── RoomsTab.java         ← Room CRUD + TableView
        │           ├── CustomersTab.java     ← Customer CRUD
        │           ├── BookingTab.java       ← Booking + Bill display
        │           ├── ServicesTab.java      ← Multithreading demo
        │           └── UIHelper.java         ← Shared UI utilities
        └── resources/
            └── hotel/
                └── style.css               ← Dark theme stylesheet
```

---

## 🖥️ Features

### 🛏 Room Management Tab
- Add rooms: Standard (with/without AC), Deluxe, Luxury (with/without Private Pool)
- View all rooms in a TableView with type, base price, amenities, availability
- Sort rooms by price using `Collections.sort()`
- Remove available (unoccupied) rooms

### 👤 Customer Management Tab
- Add customers with name and 10-digit contact validation
- Auto-generated Customer IDs (e.g. C1000, C1001...)
- View all customers and their allocated rooms
- Remove customers who don't have active bookings

### 📋 Booking & Checkout Tab
- Book a room: select customer + available room + number of days
- Live tariff preview (runtime polymorphism — calls correct `calculateTariff()`)
- Prevents double booking (occupied rooms hidden from selection)
- Checkout: releases room, generates itemised bill with service charges
- Full booking history table (active + checked-out)

### 🔔 Services Tab (Multithreading)
- Dispatch room service tasks (Cleaning, Food Delivery, Maintenance, etc.)
- Each task runs on its own `Thread` using the `Runnable` interface
- Tasks use `Thread.sleep()` to simulate work duration
- Live activity log updates via `Platform.runLater()` (thread-safe UI updates)
- "Dispatch All Rooms" button demonstrates truly concurrent execution

### 💾 Data Persistence
- All data saved to `~/HotelData/` as text files
- Rooms, customers, and bookings persist across app restarts
- Uses `FileWriter`, `BufferedReader`, `PrintWriter` (Week 6 File I/O)

---

## 📝 Marking Scheme Coverage

| Criterion | Implementation |
|-----------|---------------|
| Basic System (5M) | Room add/remove, Customer add/remove, Book room, Checkout, Bill display |
| GUI Design (5M) | Dark-themed JavaFX UI with TabPane, TableView, ComboBox, GridPane, VBox/HBox, event handling, validation messages |

---

## 🎨 UI Theme
Dark navy/purple theme inspired by modern developer tools.
Colors: `#1a1a2e` (background) · `#0f3460` (panels) · `#533483` (accent) · `#64ffda` (highlights)
