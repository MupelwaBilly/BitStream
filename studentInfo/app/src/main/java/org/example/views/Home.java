package org.example.views;

import java.net.URL;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.controllers.StudentHandler;

public class Home {

    private Label studentNumberLabel;

    public void homePage(Stage stage) {
        StudentTable tableUI = new StudentTable();

        Label label = new Label("Total Students");
        label.getStyleClass().addAll("sub-heading", "text");
        studentNumberLabel = new Label();
        studentNumberLabel.getStyleClass().addAll("heading", "text");
        updateStudentCount();

        VBox studentInfoCard = new VBox(label, studentNumberLabel);
        studentInfoCard.getStyleClass().addAll("displayCards", "addbtn");
        studentInfoCard.setAlignment(Pos.CENTER);
        HBox.setHgrow(studentInfoCard, Priority.ALWAYS);

        ImageView addico = new ImageView();
        URL imgUrl = getClass().getResource("images/add.png");
        if (imgUrl != null) {
            addico.setImage(new Image(imgUrl.toExternalForm()));
            addico.setFitWidth(40);
            addico.setFitHeight(40);
            addico.setPreserveRatio(true);
        }

        VBox addBtn = new VBox(addico);
        addBtn.getStyleClass().add("displayCards");
        addBtn.setAlignment(Pos.CENTER);
        HBox.setHgrow(addBtn, Priority.ALWAYS);

        addBtn.setOnMouseClicked(event -> {
            Stage addStudentWindow = new Stage();
            addStudentWindow.initModality(Modality.APPLICATION_MODAL);
            addStudentWindow.setTitle("Add New Student");

            VBox layout = new VBox(15);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(20));

            Label heading = new Label("Enter Student Details");
            TextField nameInput = new TextField();
            nameInput.setPromptText("Student Name");
            TextField programInput = new TextField();
            programInput.setPromptText("Student Program");
            TextField yearInput = new TextField();
            yearInput.setPromptText("Student Year");

            Button submitBtn = new Button("Create");
            submitBtn.setOnAction(e -> {
                String name = nameInput.getText().trim();
                String program = programInput.getText().trim();
                String yearStr = yearInput.getText().trim();

                if (name.isEmpty() || program.isEmpty() || yearStr.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
                    return;
                }

                int year;
                try {
                    year = Integer.parseInt(yearStr);
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Year must be a valid number.");
                    return;
                }

                if (StudentHandler.addStudent(name, year, program)) {
                    tableUI.refreshTable();
                    updateStudentCount();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Student added successfully!");
                    addStudentWindow.close();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add student. Please try again.");
                }
            });

            layout.getChildren().addAll(heading, nameInput, programInput, yearInput, submitBtn);
            addStudentWindow.setScene(new Scene(layout, 400, 300));
            addStudentWindow.show();
        });

        VBox editBtn = new VBox();
        editBtn.getStyleClass().add("displayCards");
        editBtn.setAlignment(Pos.CENTER);
        HBox.setHgrow(editBtn, Priority.ALWAYS);

        Label editLabel = new Label("Edit Student");
        editLabel.getStyleClass().addAll("sub-heading", "text");
        editBtn.getChildren().add(editLabel);

        editBtn.setOnMouseClicked(event -> {
            new ModifyStudent().openModifyWindow(tableUI, this::updateStudentCount);
        });

        HBox toolbarContainer = new HBox(15);
        toolbarContainer.getStyleClass().add("toolbarContainer");
        toolbarContainer.setPadding(new Insets(10));
        toolbarContainer.getChildren().addAll(studentInfoCard, addBtn, editBtn);

        BorderPane root = new BorderPane();
        root.setTop(toolbarContainer);
        root.setCenter(tableUI.createTable());

        Scene scene = new Scene(root, 640, 480);
        URL cssUrl = getClass().getResource("css/Home.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle("BitStream");
        stage.setScene(scene);
        stage.show();
    }

    private void updateStudentCount() {
        int count = StudentHandler.getStudentCount();
        studentNumberLabel.setText(String.valueOf(count));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}