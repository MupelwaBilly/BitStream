package org.example.views;

import java.net.URL;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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

            TextField idInput = new TextField();
            idInput.setPromptText("Auto ID");
            idInput.setDisable(true);

            CheckBox modifyIdCheckbox = new CheckBox("Modify ID");
            modifyIdCheckbox.setOnAction(e -> {
                boolean selected = modifyIdCheckbox.isSelected();
                idInput.setDisable(!selected);
                if (!selected) {
                    idInput.clear();
                    idInput.setPromptText("Auto ID");
                } else {
                    idInput.setPromptText("Custom ID");
                }
            });

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
                String idStr = idInput.getText().trim();

                if (name.isEmpty() || program.isEmpty() || yearStr.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
                    return;
                }

                int year;
                try {
                    year = Integer.parseInt(yearStr);
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Year must be a valid number.");
                    return;
                }

                boolean success;
                if (modifyIdCheckbox.isSelected()) {
                    if (idStr.isEmpty()) {
                        showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a custom ID or uncheck 'Modify ID'.");
                        return;
                    }
                    int customId;
                    try {
                        customId = Integer.parseInt(idStr);
                    } catch (NumberFormatException ex) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Input", "ID must be a valid number.");
                        return;
                    }
                    success = StudentHandler.addStudent(customId, name, year, program);
                } else {
                    success = StudentHandler.addStudent(name, year, program);
                }

                if (success) {
                    tableUI.refreshTable();
                    updateStudentCount();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Student added successfully!");
                    addStudentWindow.close();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add student (ID might already exist).");
                }
            });

            HBox idBox = new HBox(10, idInput, modifyIdCheckbox);
            idBox.setAlignment(Pos.CENTER);

            layout.getChildren().addAll(heading, idBox, nameInput, programInput, yearInput, submitBtn);
            addStudentWindow.setScene(new Scene(layout, 420, 350));
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

        // Updated window dimensions: 1080 width x 600 height
        Scene scene = new Scene(root, 1080, 600);
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