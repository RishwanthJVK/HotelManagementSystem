package hotel.ui;

import hotel.model.Room;
import hotel.service.HotelDataManager;
import hotel.service.ServiceTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ServicesTab {
    private final HotelDataManager dm = HotelDataManager.getInstance();
    private final ObservableList<String> logItems = FXCollections.observableArrayList();
    private ComboBox<String> roomCombo;

    public VBox getContent() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #ffffff;");

        Label title = UIHelper.sectionTitle("Room Service Management (Multithreading Demo)");

        Label desc = new Label("Dispatch concurrent service tasks to occupied rooms. Each task runs in its own Thread.");
        desc.setTextFill(Color.web("#4b5563"));
        desc.setWrapText(true);

        // Controls
        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);
        form.setPadding(new Insets(12));
        form.setStyle("-fx-background-color: #fff7f7; -fx-border-color: #fecaca; -fx-border-radius: 8; -fx-background-radius: 8;");

        roomCombo = new ComboBox<>();
        roomCombo.setStyle(UIHelper.COMBO_STYLE);
        roomCombo.setOnShowing(e -> refreshRoomList());
        refreshRoomList();

        ComboBox<String> cbService = new ComboBox<>(FXCollections.observableArrayList(
            "Room Cleaning", "Food Delivery", "Maintenance", "Laundry", "Turndown Service"
        ));
        cbService.setStyle(UIHelper.COMBO_STYLE);
        cbService.setPromptText("Select Service");

        Button btnDispatch  = UIHelper.primaryButton("📤 Dispatch Service");
        Button btnDispatchAll = UIHelper.secondaryButton("Dispatch all services");
        Button btnClearLog  = UIHelper.dangerButton("Clear Log");
        Label lblMsg = UIHelper.messageLabel();

        form.addRow(0, UIHelper.formLabel("Room:"), roomCombo, UIHelper.formLabel("Service:"), cbService);
        form.addRow(1, btnDispatch, btnDispatchAll, btnClearLog, lblMsg);

        btnDispatch.setOnAction(e -> {
            refreshRoomList();
            String roomStr = roomCombo.getValue();
            String service = cbService.getValue();
            if (roomStr == null || service == null) { UIHelper.setError(lblMsg, "Select room and service."); return; }
            int rNum = Integer.parseInt(roomStr.split(" ")[1]);
            dispatchTask(service, rNum);
            UIHelper.setSuccess(lblMsg, "Service dispatched on a background thread.");
        });

        btnDispatchAll.setOnAction(e -> {
            refreshRoomList();
            String[] services = {"Room Cleaning", "Food Delivery", "Maintenance", "Laundry", "Turndown Service"};
            java.util.List<Room> occupied = dm.getAllRooms().stream()
                .filter(r -> !r.isAvailable()).toList();
            if (occupied.isEmpty()) {
                logItems.add(0, "⚠️  No occupied rooms. Book some rooms first.");
                UIHelper.setError(lblMsg, "No occupied rooms available for dispatch.");
                return;
            }

            int totalTasks = occupied.size() * services.length;
            logItems.add(0, "──── Dispatching " + totalTasks + " concurrent service task(s) across " + occupied.size() + " room(s) ────");
            for (Room room : occupied) {
                for (String service : services) {
                    dispatchTask(service, room.getRoomNumber());
                }
            }
            UIHelper.setSuccess(lblMsg, "Dispatched every service to every occupied room.");
        });

        btnClearLog.setOnAction(e -> logItems.clear());

        // Service task refresh button
        Button btnRefreshRooms = UIHelper.secondaryButton("🔄 Refresh Rooms");
        btnRefreshRooms.setOnAction(e -> refreshRoomList());
        form.addRow(2, btnRefreshRooms);

        // Log list
        Label logTitle = UIHelper.sectionTitle("Service Activity Log");
        ListView<String> logView = new ListView<>(logItems);
        logView.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #991b1b; -fx-control-inner-background: #fff7f7;");
        logView.setPrefHeight(280);
        VBox.setVgrow(logView, Priority.ALWAYS);

        root.getChildren().addAll(title, desc, form, logTitle, logView);
        return root;
    }

    private void dispatchTask(String service, int roomNumber) {
        int duration = switch (service) {
            case "Room Cleaning"    -> 3000;
            case "Food Delivery"    -> 2000;
            case "Maintenance"      -> 4000;
            case "Laundry"          -> 3500;
            case "Turndown Service" -> 1500;
            default -> 2000;
        };
        ServiceTask task = new ServiceTask(service, roomNumber, duration, logItems);
        Thread t = new Thread(task, service + "-Room" + roomNumber);
        t.setDaemon(true);
        t.start();
    }

    public void refreshRoomList() {
        refreshRoomCombo(roomCombo);
    }

    private void refreshRoomCombo(ComboBox<String> cb) {
        if (cb == null) return;

        String currentSelection = cb.getValue();
        cb.getItems().clear();
        dm.getAllRooms().stream()
            .filter(r -> !r.isAvailable())
            .forEach(r -> cb.getItems().add("Room " + r.getRoomNumber() + " (" + r.getRoomType().getDisplayName() + ")"));

        if (currentSelection != null && cb.getItems().contains(currentSelection)) {
            cb.setValue(currentSelection);
        } else {
            cb.getSelectionModel().clearSelection();
            cb.setValue(null);
        }

        boolean hasOccupiedRooms = !cb.getItems().isEmpty();
        cb.setDisable(!hasOccupiedRooms);
        cb.setPromptText(hasOccupiedRooms ? "Select Occupied Room" : "No occupied rooms");
    }
}
