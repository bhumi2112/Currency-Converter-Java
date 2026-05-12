package com.currencyconverter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * MainApp — JavaFX application entry point.
 *
 * Launch order:
 *   1. JVM calls main() → Application.launch()
 *   2. JavaFX runtime calls start(Stage)
 *   3. FXML is loaded; MainController is instantiated and wired up
 */
public class MainApp extends Application {

    /** Window title shown in the OS title bar */
    private static final String APP_TITLE = "CurrencyFX — Real-Time Converter";

    /** Preferred window dimensions */
    private static final double WINDOW_WIDTH  = 860;
    private static final double WINDOW_HEIGHT = 680;

    // ---------------------------------------------------------------
    // JavaFX lifecycle
    // ---------------------------------------------------------------

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the FXML layout
        URL fxmlUrl = getClass().getResource("/com/currencyconverter/fxml/MainView.fxml");
        Objects.requireNonNull(fxmlUrl, "MainView.fxml not found on classpath");

        Parent root = FXMLLoader.load(fxmlUrl);

        // Build the scene and apply the global dark stylesheet
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        URL cssUrl = getClass().getResource("/com/currencyconverter/css/dark-theme.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        // Configure the primary stage
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(560);
        primaryStage.show();
    }

    // ---------------------------------------------------------------
    // Standard Java entry point
    // ---------------------------------------------------------------

    public static void main(String[] args) {
        launch(args);
    }
}
