package hotel.ui;

import hotel.model.Booking;
import hotel.model.Room;
import hotel.service.HotelDataManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomeTab {
    private final HotelDataManager dm = HotelDataManager.getInstance();
    private final TabPane tabPane;
    private VBox root;

    public HomeTab(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public ScrollPane getContent() {
        root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #ffffff;");
        refreshContent();

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #ffffff; -fx-background: #ffffff;");
        return scroll;
    }

    public void refreshContent() {
        if (root == null) return;
        root.getChildren().setAll(
            buildWelcomeBanner(),
            buildStatsRow(),
            buildQuickActions(),
            buildRecentBookings(),
            buildRoomSummary()
        );
    }

    // ── Welcome Banner ──────────────────────────────────────────────────────
    private VBox buildWelcomeBanner() {
        VBox banner = new VBox(8);
        banner.setPadding(new Insets(28, 32, 28, 32));
        banner.setAlignment(Pos.CENTER);
        banner.setStyle("""
            -fx-background-color: #fff5f5;
            -fx-background-radius: 14;
            -fx-border-color: #fecaca;
            -fx-border-width: 1;
            -fx-border-radius: 14;
        """);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d yyyy"));

        Label greeting = new Label("Welcome back! 👋");
        greeting.setFont(Font.font("System", FontWeight.BOLD, 26));
        greeting.setTextFill(Color.web("#111827"));
        greeting.setTextAlignment(TextAlignment.CENTER);

        Label date = new Label(today);
        date.setFont(Font.font(13));
        date.setTextFill(Color.web("#b91c1c"));
        date.setTextAlignment(TextAlignment.CENTER);

        Label tagline = new Label("Manage your hotel rooms, customers, and bookings all in one place.");
        tagline.setFont(Font.font(13));
        tagline.setTextFill(Color.web("#4b5563"));
        tagline.setWrapText(true);
        tagline.setTextAlignment(TextAlignment.CENTER);

        // Availability pill
        long available = dm.getAvailableRooms().size();
        long total = dm.getAllRooms().size();
        Label pill = new Label("  " + available + " of " + total + " rooms available  ");
        pill.setStyle("-fx-background-color: #dc2626; -fx-text-fill: #ffffff; -fx-background-radius: 20; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5 14;");

        banner.getChildren().addAll(greeting, date, tagline, pill);
        return banner;
    }

    // ── Stats Cards ──────────────────────────────────────────────────────────
    private HBox buildStatsRow() {
        int totalRooms     = dm.getAllRooms().size();
        int availRooms     = dm.getAvailableRooms().size();
        int occupiedRooms  = totalRooms - availRooms;
        int totalCustomers = dm.getAllCustomers().size();
        int activeBookings = dm.getActiveBookings().size();
        double totalRevenue = dm.getAllBookings().stream()
                .filter(b -> !b.isActive())
                .mapToDouble(Booking::getTotalAmount).sum();

        HBox row = new HBox(14);
        row.getChildren().addAll(
            statCard("🛏", "Total Rooms",     String.valueOf(totalRooms),    "#b91c1c", "#ffffff"),
            statCard("✅", "Available",        String.valueOf(availRooms),    "#dc2626", "#fff7f7"),
            statCard("🔴", "Occupied",         String.valueOf(occupiedRooms), "#991b1b", "#fff1f2"),
            statCard("👤", "Customers",        String.valueOf(totalCustomers),"#b91c1c", "#ffffff"),
            statCard("📋", "Active Bookings",  String.valueOf(activeBookings),"#dc2626", "#fff7f7"),
            statCard("💰", "Revenue Earned",   "₹" + String.format("%.0f", totalRevenue), "#991b1b", "#fff1f2")
        );
        return row;
    }

    private VBox statCard(String icon, String label, String value, String accent, String bg) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16, 18, 16, 18));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 12; -fx-border-color: " + accent + "44; -fx-border-width: 1; -fx-border-radius: 12;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label ico = new Label(icon);
        ico.setFont(Font.font(20));

        Label val = new Label(value);
        val.setFont(Font.font("System", FontWeight.BOLD, 22));
        val.setTextFill(Color.web(accent));

        Label lbl = new Label(label);
        lbl.setFont(Font.font(11));
        lbl.setTextFill(Color.web("#4b5563"));

        card.getChildren().addAll(ico, val, lbl);
        return card;
    }

    // ── Quick Actions ────────────────────────────────────────────────────────
    private VBox buildQuickActions() {
        VBox section = new VBox(12);

        Label title = sectionLabel("⚡  Quick Actions");
        HBox row = new HBox(12);

        row.getChildren().addAll(
            quickBtn("➕  Add Room",      1, "#dc2626"),
            quickBtn("👤  Add Customer",  2, "#b91c1c"),
            quickBtn("📋  Book a Room",   3, "#ef4444"),
            quickBtn("🔔  Room Service",  4, "#fca5a5")
        );

        Button clearBtn = UIHelper.dangerButton("🗑 Clear All Records");
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setTooltip(new Tooltip("Clears customers, bookings, and room history."));
        clearBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Clear All Records");
            confirm.setHeaderText("Delete all saved hotel records?");
            confirm.setContentText("This clears customers, bookings, and room allocations. Room inventory will remain.");
            confirm.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                dm.clearAllRecords();
                refreshContent();
            }
        });

        section.getChildren().addAll(title, row, clearBtn);
        return section;
    }

    private Button quickBtn(String text, int tabIndex, String color) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btn, Priority.ALWAYS);
        btn.setStyle("-fx-background-color: " + color + "22; -fx-text-fill: " + color + "; " +
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; " +
                "-fx-border-color: " + color + "66; -fx-border-width: 1; -fx-border-radius: 10; " +
                "-fx-padding: 12 0; -fx-cursor: hand;");
        btn.setOnAction(e -> tabPane.getSelectionModel().select(tabIndex));
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + color + "44; -fx-text-fill: " + color + "; " +
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; " +
                "-fx-border-color: " + color + "; -fx-border-width: 1; -fx-border-radius: 10; " +
                "-fx-padding: 12 0; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + color + "22; -fx-text-fill: " + color + "; " +
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; " +
                "-fx-border-color: " + color + "66; -fx-border-width: 1; -fx-border-radius: 10; " +
                "-fx-padding: 12 0; -fx-cursor: hand;"));
        return btn;
    }

    // ── Recent Bookings ──────────────────────────────────────────────────────
    private VBox buildRecentBookings() {
        VBox section = new VBox(10);
        Label title = sectionLabel("📋  Recent Bookings");

        List<Booking> active = dm.getActiveBookings();
        if (active.isEmpty()) {
            Label empty = new Label("No active bookings yet. Book a room to get started!");
            empty.setTextFill(Color.web("#7f1d1d"));
            empty.setStyle("-fx-background-color: #fff7f7; -fx-border-color: #fecaca; -fx-border-radius: 10; -fx-padding: 16; -fx-background-radius: 10;");
            empty.setMaxWidth(Double.MAX_VALUE);
            section.getChildren().addAll(title, empty);
            return section;
        }

        VBox list = new VBox(8);
        int shown = Math.min(active.size(), 5);
        for (int i = 0; i < shown; i++) {
            Booking b = active.get(i);
            list.getChildren().add(bookingRow(b));
        }
        section.getChildren().addAll(title, list);
        return section;
    }

    private HBox bookingRow(Booking b) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-color: #fecaca; -fx-border-width: 1; -fx-border-radius: 10;");

        Label id = new Label(b.getBookingId());
        id.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-background-radius: 6; -fx-padding: 3 8; -fx-font-size: 11px; -fx-font-weight: bold;");

        Label customer = new Label("👤 " + b.getCustomerName());
        customer.setTextFill(Color.web("#111827"));
        customer.setFont(Font.font(13));

        Label room = new Label("🛏 Room " + b.getRoomNumber());
        room.setTextFill(Color.web("#b91c1c"));
        room.setFont(Font.font(13));

        Label days = new Label("📅 " + b.getDays() + " days");
        days.setTextFill(Color.web("#4b5563"));
        days.setFont(Font.font(12));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label amount = new Label("₹" + String.format("%.2f", b.getTotalAmount()));
        amount.setFont(Font.font("System", FontWeight.BOLD, 14));
        amount.setTextFill(Color.web("#111827"));

        Label status = new Label("Active");
        status.setStyle("-fx-background-color: #dc2626; -fx-text-fill: #ffffff; -fx-background-radius: 20; -fx-padding: 2 10; -fx-font-size: 11px;");

        row.getChildren().addAll(id, customer, room, days, spacer, amount, status);
        return row;
    }

    // ── Room Summary ─────────────────────────────────────────────────────────
    private VBox buildRoomSummary() {
        VBox section = new VBox(10);
        Label title = sectionLabel("🛏  Room Overview");

        HBox grid = new HBox(10);
        long std  = dm.getAllRooms().stream().filter(r -> r.getRoomType().name().equals("STANDARD")).count();
        long dlx  = dm.getAllRooms().stream().filter(r -> r.getRoomType().name().equals("DELUXE")).count();
        long lux  = dm.getAllRooms().stream().filter(r -> r.getRoomType().name().equals("SUITE")).count();
        long stdA = dm.getAvailableRooms().stream().filter(r -> r.getRoomType().name().equals("STANDARD")).count();
        long dlxA = dm.getAvailableRooms().stream().filter(r -> r.getRoomType().name().equals("DELUXE")).count();
        long luxA = dm.getAvailableRooms().stream().filter(r -> r.getRoomType().name().equals("SUITE")).count();

        grid.getChildren().addAll(
            roomTypeCard("Standard", std, stdA, "#dc2626"),
            roomTypeCard("Deluxe",   dlx, dlxA, "#b91c1c"),
            roomTypeCard("Suite",    lux, luxA,  "#ef4444")
        );
        section.getChildren().addAll(title, grid);
        return section;
    }

    private VBox roomTypeCard(String type, long total, long avail, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: #fff7f7; -fx-background-radius: 12; -fx-border-color: #fecaca; -fx-border-width: 1; -fx-border-radius: 12;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label lType = new Label(type);
        lType.setFont(Font.font("System", FontWeight.BOLD, 15));
        lType.setTextFill(Color.web(color));

        Label lTotal = new Label(total + " rooms");
        lTotal.setFont(Font.font(12));
        lTotal.setTextFill(Color.web("#4b5563"));

        // Visual bar
        double pct = total == 0 ? 0 : (double) avail / total;
        StackPane barBg = new StackPane();
        barBg.setStyle("-fx-background-color: #fee2e2; -fx-background-radius: 4;");
        barBg.setMinHeight(8); barBg.setMaxHeight(8); barBg.setMinWidth(140);

        HBox barFill = new HBox();
        barFill.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4;");
        barFill.setPrefWidth(140 * pct);
        barFill.setPrefHeight(8);
        StackPane.setAlignment(barFill, Pos.CENTER_LEFT);
        barBg.getChildren().add(barFill);

        Label lAvail = new Label(avail + " available");
        lAvail.setFont(Font.font(11));
        lAvail.setTextFill(Color.web("#991b1b"));

        card.getChildren().addAll(lType, lTotal, barBg, lAvail);
        return card;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 15));
        l.setTextFill(Color.web("#b91c1c"));
        return l;
    }
}
