package hotel.ui;

import hotel.model.Customer;
import hotel.service.HotelDataManager;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CustomersTab {
    private final HotelDataManager dm = HotelDataManager.getInstance();
    private TableView<CustRow> table;

    public VBox getContent() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #ffffff;");

        Label title = UIHelper.sectionTitle("Customer Management");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);
        form.setPadding(new Insets(12));
        form.setStyle("-fx-background-color: #fff7f7; -fx-border-color: #fecaca; -fx-border-radius: 8; -fx-background-radius: 8;");

        TextField tfName    = UIHelper.styledField("Full Name");
        TextField tfContact = UIHelper.styledField("Contact Number");
        Button btnAdd    = UIHelper.primaryButton("➕ Add Customer");
        Button btnRemove = UIHelper.dangerButton("🗑 Remove Selected");
        Label lblMsg     = UIHelper.messageLabel();

        form.addRow(0, UIHelper.formLabel("Name:"), tfName, UIHelper.formLabel("Contact:"), tfContact);
        form.addRow(1, btnAdd, btnRemove, lblMsg);

        btnAdd.setOnAction(e -> {
            String name = tfName.getText().trim();
            String contact = tfContact.getText().trim();
            if (name.isEmpty() || contact.isEmpty()) { UIHelper.setError(lblMsg, "Fill all fields."); return; }
            if (!contact.matches("\\d{10}")) { UIHelper.setError(lblMsg, "Contact must be 10 digits."); return; }
            Customer c = new Customer(name, contact);
            dm.addCustomer(c);
            refreshTable();
            UIHelper.setSuccess(lblMsg, "Customer added! ID: " + c.getCustomerId());
            tfName.clear(); tfContact.clear();
        });

        btnRemove.setOnAction(e -> {
            CustRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { UIHelper.setError(lblMsg, "Select a customer."); return; }
            if (!selected.getRoom().equals("—")) { UIHelper.setError(lblMsg, "Cannot remove — customer has active booking."); return; }
            boolean ok = dm.removeCustomer(selected.getId());
            if (ok) { refreshTable(); UIHelper.setSuccess(lblMsg, "Customer removed."); }
            else UIHelper.setError(lblMsg, "Failed to remove customer.");
        });

        table = buildTable();
        refreshTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        root.getChildren().addAll(title, form, table);
        return root;
    }

    private TableView<CustRow> buildTable() {
        TableView<CustRow> tv = new TableView<>();
        tv.setStyle("-fx-background-color: #ffffff;");
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.getColumns().addAll(
            UIHelper.col("Customer ID", "id", 100),
            UIHelper.col("Name", "name", 160),
            UIHelper.col("Contact", "contact", 130),
            UIHelper.col("Allocated Room", "room", 120),
            UIHelper.col("Days Stayed", "days", 100)
        );
        return tv;
    }

    void refreshTable() {
        ObservableList<CustRow> data = FXCollections.observableArrayList();
        dm.getAllCustomers().forEach(c -> data.add(new CustRow(
                c.getCustomerId(), c.getName(), c.getContactNumber(),
                c.getAllocatedRoomNumber() != null ? String.valueOf(c.getAllocatedRoomNumber()) : "—",
                String.valueOf(c.getDaysStayed()))));
        table.setItems(data);
    }

    public static class CustRow {
        private final SimpleStringProperty id, name, contact, room, days;
        CustRow(String id, String name, String contact, String room, String days) {
            this.id = new SimpleStringProperty(id);
            this.name = new SimpleStringProperty(name);
            this.contact = new SimpleStringProperty(contact);
            this.room = new SimpleStringProperty(room);
            this.days = new SimpleStringProperty(days);
        }
        public String getId() { return id.get(); }
        public String getName() { return name.get(); }
        public String getContact() { return contact.get(); }
        public String getRoom() { return room.get(); }
        public String getDays() { return days.get(); }
        public SimpleStringProperty idProperty() { return id; }
        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty contactProperty() { return contact; }
        public SimpleStringProperty roomProperty() { return room; }
        public SimpleStringProperty daysProperty() { return days; }
    }
}
