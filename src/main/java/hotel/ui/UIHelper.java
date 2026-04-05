package hotel.ui;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class UIHelper {
    public static final String COMBO_STYLE =
        "-fx-background-color: #ffffff; -fx-text-fill: #111827; " +
        "-fx-prompt-text-fill: #6b7280; -fx-min-width: 180; " +
        "-fx-border-color: #fca5a5; -fx-border-radius: 5; -fx-background-radius: 5;";

    private static final String FIELD_STYLE =
        "-fx-background-color: #ffffff; -fx-text-fill: #111827; " +
        "-fx-prompt-text-fill: #6b7280; -fx-border-color: #fca5a5; " +
        "-fx-border-radius: 5; -fx-background-radius: 5; -fx-min-width: 160; -fx-padding: 6 10;";

    public static Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 15));
        l.setTextFill(Color.web("#b91c1c"));
        return l;
    }

    public static Label formLabel(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.web("#7f1d1d"));
        l.setFont(Font.font(13));
        return l;
    }

    public static TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(FIELD_STYLE);
        return tf;
    }

    public static Button primaryButton(String text) {
        Button b = new Button(text);
        String base  = "-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 7; -fx-padding: 7 16; -fx-cursor: hand;";
        String hover = "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 7; -fx-padding: 7 16; -fx-cursor: hand;";
        b.setStyle(base);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e -> b.setStyle(base));
        return b;
    }

    public static Button dangerButton(String text) {
        Button b = new Button(text);
        String base  = "-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-font-weight: bold; -fx-background-radius: 7; -fx-padding: 7 16; -fx-border-color: #ef4444; -fx-border-radius: 7; -fx-cursor: hand;";
        String hover = "-fx-background-color: #fecaca; -fx-text-fill: #7f1d1d; -fx-font-weight: bold; -fx-background-radius: 7; -fx-padding: 7 16; -fx-border-color: #dc2626; -fx-border-radius: 7; -fx-cursor: hand;";
        b.setStyle(base);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e -> b.setStyle(base));
        return b;
    }

    public static Button secondaryButton(String text) {
        Button b = new Button(text);
        String base  = "-fx-background-color: #ffffff; -fx-text-fill: #b91c1c; -fx-font-weight: bold; -fx-background-radius: 7; -fx-padding: 7 16; -fx-border-color: #fca5a5; -fx-border-radius: 7; -fx-cursor: hand;";
        String hover = "-fx-background-color: #fff5f5; -fx-text-fill: #991b1b; -fx-font-weight: bold; -fx-background-radius: 7; -fx-padding: 7 16; -fx-border-color: #ef4444; -fx-border-radius: 7; -fx-cursor: hand;";
        b.setStyle(base);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e -> b.setStyle(base));
        return b;
    }

    public static Label messageLabel() {
        Label l = new Label();
        l.setWrapText(true);
        l.setMaxWidth(320);
        return l;
    }

    public static void setSuccess(Label lbl, String msg) {
        lbl.setText("✅ " + msg);
        lbl.setTextFill(Color.web("#ffffff"));
    }

    public static void setError(Label lbl, String msg) {
        lbl.setText("❌ " + msg);
        lbl.setTextFill(Color.web("#fca5a5"));
    }

    @SuppressWarnings("unchecked")
    public static <T> TableColumn<T, String> col(String header, String property, double width) {
        TableColumn<T, String> col = new TableColumn<>(header);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setMinWidth(width);
        return col;
    }
}
