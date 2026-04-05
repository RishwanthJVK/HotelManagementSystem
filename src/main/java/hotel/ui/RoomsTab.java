package hotel.ui;

import hotel.model.*;
import hotel.service.HotelDataManager;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class RoomsTab {
    private final HotelDataManager dm = HotelDataManager.getInstance();
    private TableView<RoomRow> table;
    private ObservableList<RoomRow> data;

    public VBox getContent() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #ffffff;");

        Label title = UIHelper.sectionTitle("Room Management");

        // Form
        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);
        form.setPadding(new Insets(12));
        form.setStyle("-fx-background-color: #fff7f7; -fx-border-color: #fecaca; -fx-border-radius: 8; -fx-background-radius: 8;");

        TextField tfNumber = UIHelper.styledField("Room Number");
        ComboBox<String> cbType = new ComboBox<>(FXCollections.observableArrayList("Standard", "Deluxe", "Luxury"));
        cbType.setPromptText("Room Type");
        cbType.setStyle(UIHelper.COMBO_STYLE);
        CheckBox cbAC = new CheckBox("AC");
        cbAC.setTextFill(javafx.scene.paint.Color.web("#111827"));
        CheckBox cbPool = new CheckBox("Private Pool");
        cbPool.setTextFill(javafx.scene.paint.Color.web("#111827"));

        Runnable syncAmenityControls = () -> {
            String t = cbType.getValue();
            if ("Standard".equals(t)) {
                cbAC.setDisable(false);
                cbAC.setSelected(false);
                cbPool.setDisable(true);
                cbPool.setSelected(false);
            } else if ("Deluxe".equals(t)) {
                cbAC.setSelected(true);
                cbAC.setDisable(true);
                cbPool.setDisable(true);
                cbPool.setSelected(false);
            } else if ("Luxury".equals(t)) {
                cbAC.setSelected(true);
                cbAC.setDisable(true);
                cbPool.setDisable(false);
            } else {
                cbAC.setSelected(false);
                cbAC.setDisable(true);
                cbPool.setSelected(false);
                cbPool.setDisable(true);
            }
        };

        cbType.setOnAction(e -> syncAmenityControls.run());
        syncAmenityControls.run();

        Button btnAdd = UIHelper.primaryButton("➕ Add Room");
        Button btnRemove = UIHelper.dangerButton("🗑 Remove Selected");
        Button btnSortPrice = UIHelper.secondaryButton("Sort by Price");
        Label lblMsg = UIHelper.messageLabel();

        form.addRow(0, UIHelper.formLabel("Room No:"), tfNumber,
                UIHelper.formLabel("Type:"), cbType, cbAC, cbPool);
        form.addRow(1, btnAdd, btnRemove, btnSortPrice, lblMsg);

        btnAdd.setOnAction(e -> {
            try {
                int num = Integer.parseInt(tfNumber.getText().trim());
                if (dm.roomNumberExists(num)) { UIHelper.setError(lblMsg, "Room number already exists!"); return; }
                String type = cbType.getValue();
                if (type == null) { UIHelper.setError(lblMsg, "Select room type."); return; }
                Room room = switch (type) {
                    case "Standard" -> new StandardRoom(num, cbAC.isSelected());
                    case "Deluxe"   -> new DeluxeRoom(num);
                    default         -> new LuxuryRoom(num, cbPool.isSelected());
                };
                dm.addRoom(room);
                refreshTable();
                UIHelper.setSuccess(lblMsg, "Room " + num + " added!");
                tfNumber.clear();
                cbType.setValue(null);
                syncAmenityControls.run();
            } catch (NumberFormatException ex) {
                UIHelper.setError(lblMsg, "Enter a valid room number.");
            }
        });

        btnRemove.setOnAction(e -> {
            RoomRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { UIHelper.setError(lblMsg, "Select a room first."); return; }
            if (!selected.getAvailable().equals("✅ Yes")) { UIHelper.setError(lblMsg, "Cannot remove occupied room."); return; }
            boolean ok = dm.removeRoom(selected.getRoomNumber());
            if (ok) { refreshTable(); UIHelper.setSuccess(lblMsg, "Room removed."); }
            else UIHelper.setError(lblMsg, "Failed to remove room.");
        });

        btnSortPrice.setOnAction(e -> {
            data.clear();
            dm.getRoomsSortedByPrice().forEach(r -> data.add(toRow(r)));
        });

        // Table
        table = buildTable();
        data = FXCollections.observableArrayList();
        table.setItems(data);
        refreshTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        root.getChildren().addAll(title, form, table);
        return root;
    }

    public void refreshContent() {
        if (table != null) refreshTable();
    }

    private TableView<RoomRow> buildTable() {
        TableView<RoomRow> tv = new TableView<>();
        tv.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #111827;");
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tv.getColumns().addAll(
            UIHelper.col("Room No", "roomNumber", 80),
            UIHelper.col("Type", "type", 100),
            UIHelper.col("Base Price/Day", "basePrice", 130),
            UIHelper.col("Tariff (1 day)", "tariff", 130),
            UIHelper.col("Amenities", "amenities", 200),
            UIHelper.col("Available", "available", 90)
        );
        return tv;
    }

    private void refreshTable() {
        data = FXCollections.observableArrayList();
        dm.getAllRooms().forEach(r -> data.add(toRow(r)));
        table.setItems(data);
    }

    private RoomRow toRow(Room r) {
        String amenities = (r instanceof Amenities a) ? a.getAmenitiesDescription() : "-";
        String tariff = "₹" + String.format("%.0f", r.calculateTariff(1));
        return new RoomRow(r.getRoomNumber(), r.getRoomType().getDisplayName(),
                "₹" + r.getBasePrice(), tariff, amenities, r.isAvailable() ? "✅ Yes" : "❌ No");
    }

    public static class RoomRow {
        private final SimpleIntegerProperty roomNumber;
        private final SimpleStringProperty type, basePrice, tariff, amenities, available;
        RoomRow(int n, String t, String bp, String tar, String am, String av) {
            roomNumber = new SimpleIntegerProperty(n);
            type = new SimpleStringProperty(t);
            basePrice = new SimpleStringProperty(bp);
            tariff = new SimpleStringProperty(tar);
            amenities = new SimpleStringProperty(am);
            available = new SimpleStringProperty(av);
        }
        public int getRoomNumber() { return roomNumber.get(); }
        public String getType() { return type.get(); }
        public String getBasePrice() { return basePrice.get(); }
        public String getTariff() { return tariff.get(); }
        public String getAmenities() { return amenities.get(); }
        public String getAvailable() { return available.get(); }
        public SimpleIntegerProperty roomNumberProperty() { return roomNumber; }
        public SimpleStringProperty typeProperty() { return type; }
        public SimpleStringProperty basePriceProperty() { return basePrice; }
        public SimpleStringProperty tariffProperty() { return tariff; }
        public SimpleStringProperty amenitiesProperty() { return amenities; }
        public SimpleStringProperty availableProperty() { return available; }
    }
}
