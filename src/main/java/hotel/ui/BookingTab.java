package hotel.ui;

import hotel.model.Booking;
import hotel.model.Customer;
import hotel.model.Room;
import hotel.service.HotelDataManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class BookingTab {
    private final HotelDataManager dm = HotelDataManager.getInstance();
    private TableView<BookRow> bookingTable;
    private VBox billPane;
    private ComboBox<String> customerCombo;
    private ComboBox<String> availableRoomCombo;
    private ComboBox<String> checkoutRoomCombo;

    public VBox getContent() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #ffffff;");

        Label title = UIHelper.sectionTitle("Booking & Checkout");

        // Booking form
        TitledPane bookPane = buildBookingForm();
        TitledPane checkoutPane = buildCheckoutForm();

        HBox forms = new HBox(12, bookPane, checkoutPane);
        HBox.setHgrow(bookPane, Priority.ALWAYS);
        HBox.setHgrow(checkoutPane, Priority.ALWAYS);

        // Bill panel
        billPane = new VBox(6);
        billPane.setPadding(new Insets(12));
        billPane.setStyle("-fx-background-color: #fff7f7; -fx-border-color: #fecaca; -fx-border-radius: 8; -fx-background-radius: 8;");
        billPane.setVisible(false);
        billPane.setManaged(false);

        // Bookings history table
        Label histTitle = UIHelper.sectionTitle("Active Bookings");
        bookingTable = buildBookingTable();
        refreshBookingTable();
        VBox.setVgrow(bookingTable, Priority.ALWAYS);

        root.getChildren().addAll(title, forms, billPane, histTitle, bookingTable);
        return root;
    }

    public void refreshContent() {
        if (bookingTable != null) refreshBookingTable();
        refreshCustomerCombo(customerCombo);
        refreshRoomCombo(availableRoomCombo);
        refreshOccupiedRoomCombo(checkoutRoomCombo);
        if (billPane != null) {
            billPane.setVisible(false);
            billPane.setManaged(false);
        }
    }

    private TitledPane buildBookingForm() {
        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);
        form.setPadding(new Insets(12));
        form.setStyle("-fx-background-color: #fff7f7;");

        customerCombo = new ComboBox<>();
        customerCombo.setStyle(UIHelper.COMBO_STYLE);
        customerCombo.setPromptText("Select Customer");
        refreshCustomerCombo(customerCombo);

        availableRoomCombo = new ComboBox<>();
        availableRoomCombo.setStyle(UIHelper.COMBO_STYLE);
        availableRoomCombo.setPromptText("Select Available Room");
        refreshRoomCombo(availableRoomCombo);

        TextField tfDays = UIHelper.styledField("Days (e.g. 3)");
        Label lblPreview = new Label();
        lblPreview.setTextFill(Color.web("#fca5a5"));
        Button btnBook = UIHelper.primaryButton("📋 Book Room");
        Label lblMsg = UIHelper.messageLabel();

        // Live tariff preview
        Runnable updatePreview = () -> {
            try {
                int days = Integer.parseInt(tfDays.getText().trim());
                String roomStr = availableRoomCombo.getValue();
                if (roomStr != null && days > 0) {
                    int rNum = Integer.parseInt(roomStr.split(" ")[1]);
                    dm.findRoom(rNum).ifPresent(r -> {
                        double t = r.calculateTariff(days);
                        lblPreview.setText("Estimated: ₹" + String.format("%.2f", t));
                    });
                }
            } catch (Exception ignored) {}
        };
        tfDays.textProperty().addListener((o, ov, nv) -> updatePreview.run());
        availableRoomCombo.setOnAction(e -> updatePreview.run());

        btnBook.setOnAction(e -> {
            String custStr = customerCombo.getValue();
            String roomStr = availableRoomCombo.getValue();
            if (custStr == null || roomStr == null) { UIHelper.setError(lblMsg, "Select customer and room."); return; }
            try {
                String custId = custStr.split("\\(")[1].replace(")", "").trim();
                int roomNum = Integer.parseInt(roomStr.split(" ")[1]);
                int days = Integer.parseInt(tfDays.getText().trim());
                if (days <= 0) { UIHelper.setError(lblMsg, "Days must be > 0."); return; }

                HotelDataManager.BookingResult result = dm.bookRoom(custId, roomNum, days);
                if (result.success) {
                    UIHelper.setSuccess(lblMsg, result.message);
                    refreshBookingTable();
                    refreshCustomerCombo(customerCombo);
                    refreshRoomCombo(availableRoomCombo);
                    refreshOccupiedRoomCombo(checkoutRoomCombo);
                    tfDays.clear(); lblPreview.setText("");
                } else {
                    UIHelper.setError(lblMsg, result.message);
                }
            } catch (NumberFormatException ex) {
                UIHelper.setError(lblMsg, "Invalid input.");
            }
        });

        form.addRow(0, UIHelper.formLabel("Customer:"), customerCombo);
        form.addRow(1, UIHelper.formLabel("Room:"), availableRoomCombo);
        form.addRow(2, UIHelper.formLabel("Days:"), tfDays);
        form.addRow(3, btnBook, lblPreview);
        form.add(lblMsg, 0, 4, 2, 1);

        TitledPane tp = new TitledPane("Book Room", form);
        stylePane(tp);
        return tp;
    }

    private TitledPane buildCheckoutForm() {
        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);
        form.setPadding(new Insets(12));
        form.setStyle("-fx-background-color: #fff7f7;");

        checkoutRoomCombo = new ComboBox<>();
        checkoutRoomCombo.setStyle(UIHelper.COMBO_STYLE);
        refreshOccupiedRoomCombo(checkoutRoomCombo);

        Button btnCheckout = UIHelper.dangerButton("🚪 Checkout");
        Label lblMsg = UIHelper.messageLabel();

        btnCheckout.setOnAction(e -> {
            String roomStr = checkoutRoomCombo.getValue();
            if (roomStr == null) { UIHelper.setError(lblMsg, "Select a room."); return; }
            int rNum = Integer.parseInt(roomStr.split(" ")[1]);
            HotelDataManager.CheckoutResult result = dm.checkoutRoom(rNum);
            if (result.success) {
                UIHelper.setSuccess(lblMsg, result.message);
                refreshBookingTable();
                refreshOccupiedRoomCombo(checkoutRoomCombo);
                if (result.bill != null) showBill(result.bill);
            } else {
                UIHelper.setError(lblMsg, result.message);
            }
        });

        form.addRow(0, UIHelper.formLabel("Room:"), checkoutRoomCombo);
        form.addRow(1, btnCheckout);
        form.add(lblMsg, 0, 2, 2, 1);

        TitledPane tp = new TitledPane("Checkout", form);
        stylePane(tp);
        return tp;
    }

    private void showBill(HotelDataManager.Bill bill) {
        billPane.getChildren().clear();

        Label hdr = new Label("🧾  HOTEL BILL");
        hdr.setFont(Font.font("System", FontWeight.BOLD, 16));
        hdr.setTextFill(Color.web("#b91c1c"));

        String details = String.format(
            "Customer: %s%n" +
            "Room: %d (%s)%n" +
            "Days Stayed: %d%n" +
            "Room Tariff: ₹%.2f/day%n" +
            "Service Charge: ₹%.2f%n" +
            "─────────────────────%n" +
            "TOTAL AMOUNT: ₹%.2f",
            bill.customerName, bill.roomNumber, bill.roomType.getDisplayName(),
            bill.days, bill.roomTariff, bill.serviceCharge, bill.totalAmount
        );

        Label lblDetails = new Label(details);
        lblDetails.setTextFill(Color.web("#111827"));
        lblDetails.setFont(Font.font("Monospaced", 13));

        Button btnClose = UIHelper.secondaryButton("Close Bill");
        btnClose.setOnAction(e -> { billPane.setVisible(false); billPane.setManaged(false); });

        billPane.getChildren().addAll(hdr, new Separator(), lblDetails, btnClose);
        billPane.setVisible(true);
        billPane.setManaged(true);
    }

    private TableView<BookRow> buildBookingTable() {
        TableView<BookRow> tv = new TableView<>();
        tv.setStyle("-fx-background-color: #ffffff;");
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.getColumns().addAll(
            UIHelper.col("Booking ID", "bookingId", 90),
            UIHelper.col("Customer", "customerName", 140),
            UIHelper.col("Room", "roomNumber", 70),
            UIHelper.col("Type", "roomType", 90),
            UIHelper.col("Days", "days", 60),
            UIHelper.col("Check-In", "checkIn", 110),
            UIHelper.col("Total (₹)", "total", 100),
            UIHelper.col("Status", "status", 80)
        );
        return tv;
    }

    private void refreshBookingTable() {
        ObservableList<BookRow> data = FXCollections.observableArrayList();
        dm.getAllBookings().forEach(b -> data.add(new BookRow(
            b.getBookingId(), b.getCustomerName(), String.valueOf(b.getRoomNumber()),
            b.getRoomType().getDisplayName(), String.valueOf(b.getDays()),
            b.getCheckInDate().toString(), String.format("%.2f", b.getTotalAmount()),
            b.isActive() ? "Active" : "Checked Out"
        )));
        bookingTable.setItems(data);
    }

    private void refreshCustomerCombo(ComboBox<String> cb) {
        cb.getItems().clear();
        dm.getAllCustomers().stream()
            .filter(c -> c.getAllocatedRoomNumber() == null)
            .forEach(c -> cb.getItems().add(c.getName() + " (" + c.getCustomerId() + ")"));
    }

    private void refreshRoomCombo(ComboBox<String> cb) {
        cb.getItems().clear();
        dm.getAvailableRooms().forEach(r ->
            cb.getItems().add("Room " + r.getRoomNumber() + " — " + r.getRoomType().getDisplayName() + " ₹" + r.getBasePrice()));
    }

    private void refreshOccupiedRoomCombo(ComboBox<String> cb) {
        if (cb == null) return;

        String currentSelection = cb.getValue();
        cb.getItems().clear();
        dm.getAllRooms().stream().filter(r -> !r.isAvailable())
            .forEach(r -> cb.getItems().add("Room " + r.getRoomNumber() + " (" + r.getRoomType().getDisplayName() + ")"));

        if (currentSelection != null && cb.getItems().contains(currentSelection)) {
            cb.setValue(currentSelection);
        } else {
            cb.getSelectionModel().clearSelection();
            cb.setValue(null);
        }

        cb.setDisable(cb.getItems().isEmpty());
        cb.setPromptText(cb.getItems().isEmpty() ? "No occupied rooms available" : "Select Occupied Room");
    }

    private void stylePane(TitledPane tp) {
        tp.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #111827; -fx-border-color: #fecaca; -fx-border-radius: 8; -fx-background-radius: 8;");
        tp.setExpanded(true);
    }

    public static class BookRow {
        private final SimpleStringProperty bookingId, customerName, roomNumber, roomType, days, checkIn, total, status;
        BookRow(String bid, String cn, String rn, String rt, String d, String ci, String tot, String st) {
            bookingId=new SimpleStringProperty(bid); customerName=new SimpleStringProperty(cn);
            roomNumber=new SimpleStringProperty(rn); roomType=new SimpleStringProperty(rt);
            days=new SimpleStringProperty(d); checkIn=new SimpleStringProperty(ci);
            total=new SimpleStringProperty(tot); status=new SimpleStringProperty(st);
        }
        public String getBookingId() { return bookingId.get(); }
        public String getCustomerName() { return customerName.get(); }
        public String getRoomNumber() { return roomNumber.get(); }
        public String getRoomType() { return roomType.get(); }
        public String getDays() { return days.get(); }
        public String getCheckIn() { return checkIn.get(); }
        public String getTotal() { return total.get(); }
        public String getStatus() { return status.get(); }
        public SimpleStringProperty bookingIdProperty() { return bookingId; }
        public SimpleStringProperty customerNameProperty() { return customerName; }
        public SimpleStringProperty roomNumberProperty() { return roomNumber; }
        public SimpleStringProperty roomTypeProperty() { return roomType; }
        public SimpleStringProperty daysProperty() { return days; }
        public SimpleStringProperty checkInProperty() { return checkIn; }
        public SimpleStringProperty totalProperty() { return total; }
        public SimpleStringProperty statusProperty() { return status; }
    }
}
