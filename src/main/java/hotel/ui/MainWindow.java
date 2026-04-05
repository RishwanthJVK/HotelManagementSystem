package hotel.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainWindow {
    private final Stage stage;
    public static TabPane tabPane;

    public MainWindow(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        stage.setTitle("Red Rooms - Management System");
        stage.setMinWidth(960);
        stage.setMinHeight(680);

        VBox root = new VBox();
        root.setStyle("-fx-background-color: #ffffff;");

        HBox header = buildHeader();
        root.getChildren().add(header);

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: #ffffff; -fx-tab-min-height: 42px;");

        HomeTab homeView = new HomeTab(tabPane);
        RoomsTab roomsView = new RoomsTab();
        CustomersTab customersView = new CustomersTab();
        BookingTab bookingView = new BookingTab();
        ServicesTab servicesView = new ServicesTab();
        Tab homeTab     = new Tab("Home",      homeView.getContent());
        Tab roomTab     = new Tab("\uD83D\uDECF  Rooms",     roomsView.getContent());
        Tab customerTab = new Tab("\uD83D\uDC64  Customers", customersView.getContent());
        Tab bookingTab  = new Tab("\uD83D\uDCCB  Booking",   bookingView.getContent());
        Tab serviceTab  = new Tab("\uD83D\uDD14  Services",  servicesView.getContent());

        tabPane.getTabs().addAll(homeTab, roomTab, customerTab, bookingTab, serviceTab);
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == homeTab) {
                homeView.refreshContent();
            } else if (newTab == roomTab) {
                roomsView.refreshContent();
            } else if (newTab == customerTab) {
                customersView.refreshTable();
            } else if (newTab == bookingTab) {
                bookingView.refreshContent();
            } else if (newTab == serviceTab) {
                servicesView.refreshRoomList();
            }
        });
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        root.getChildren().add(tabPane);

        Label status = new Label("  Red Rooms Management System  |  Data saved to ~/HotelData/");
        status.setStyle("-fx-background-color: #fff5f5; -fx-text-fill: #991b1b; -fx-border-color: #fecaca; -fx-border-width: 1 0 0 0; -fx-padding: 6px; -fx-font-size: 11px;");
        status.setMaxWidth(Double.MAX_VALUE);
        root.getChildren().add(status);

        Scene scene = new Scene(root, 980, 720);
        scene.getStylesheets().add(getClass().getResource("/hotel/style.css") != null
                ? getClass().getResource("/hotel/style.css").toExternalForm() : "");
        stage.setScene(scene);
        stage.show();
    }

    private HBox buildHeader() {
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 24, 14, 24));
        header.setStyle("-fx-background-color: #ffffff; -fx-border-color: #fecaca; -fx-border-width: 0 0 1 0;");

        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/hotel/logo.png")));
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);

        VBox titleBox = new VBox(3);
        Label title = new Label("RED ROOMS");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#b91c1c"));
        Label subtitle = new Label("Hotel Management Application");
        subtitle.setFont(Font.font(11));
        subtitle.setTextFill(Color.web("#4b5563"));
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(logo, titleBox, spacer);
        return header;
    }
}

