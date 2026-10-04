package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.views.Home;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Database is automatically initialized via DatabaseManager static initializer
        new Home().homePage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}