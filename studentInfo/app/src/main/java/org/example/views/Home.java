package org.example.views;

import java.io.File;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.controllers.ImportHandler;
import org.example.controllers.StudentHandler;

public class Home {

    private Label studentNumberLabel;

    public void homePage(Stage stage) {
        StudentTable tableUI = new StudentTable();

        Label label = new Label("Total Students: ");
        label.getStyleClass().add("sub-heading");
        studentNumberLabel = new Label();
        studentNumberLabel.getStyleClass().add("heading");
        updateStudentCount();

        HBox studentInfoCard = new HBox(8, label, studentNumberLabel);
        studentInfoCard.getStyleClass().add("count-badge");
        studentInfoCard.setAlignment(Pos.CENTER_LEFT);

        // Import Button
        Button importBtn = new Button("Import Data");
        importBtn.getStyleClass().add("action-btn");
        importBtn.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import Student File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Supported Files (*.csv, *.json, *.xlsx, *.xls, *.accdb, *.mdb)",
                            "*.csv", "*.json", "*.xlsx", "*.xls", "*.accdb", "*.mdb"),
                    new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"),
                    new FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json"),
                    new FileChooser.ExtensionFilter("Excel Files (*.xlsx, *.xls)", "*.xlsx", "*.xls"),
                    new FileChooser.ExtensionFilter("MS Access Databases (*.accdb, *.mdb)", "*.accdb", "*.mdb")
            );

            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                try {
                    int importedCount = ImportHandler.importFile(selectedFile);
                    tableUI.refreshTable();
                    updateStudentCount();
                    showAlert(Alert.AlertType.INFORMATION, "Import Successful", "Successfully imported " + importedCount + " students.");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Import Failed", "Error processing file: " + e.getMessage());
                }
            }
        });

        // Green Add Button
        ImageView addico = new ImageView();
        URL imgUrl = getClass().getResource("images/add.png");
        if (imgUrl != null) {
            addico.setImage(new Image(imgUrl.toExternalForm()));
            addico.setFitWidth(16);
            addico.setFitHeight(16);
            addico.setPreserveRatio(true);
        }

        Button addBtn = new Button("Add Student", addico);
        addBtn.getStyleClass().addAll("action-btn", "btn-create");

        addBtn.setOnAction(event -> {
            Stage addStudentWindow = new Stage();
            addStudentWindow.initModality(Modality.APPLICATION_MODAL);
            addStudentWindow.setTitle("Add New Student");

            VBox layout = new VBox(15);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(20));

            Label heading = new Label("Enter Student Details");
            heading.getStyleClass().add("sub-heading");

            TextField idInput = new TextField();
            idInput.setPromptText("Auto ID");
            idInput.setDisable(true);

            CheckBox modifyIdCheckbox = new CheckBox("Modify ID");
            modifyIdCheckbox.setOnAction(e -> {
                boolean selected = modifyIdCheckbox.isSelected();
                idInput.setDisable(!selected);
                idInput.setPromptText(selected ? "Custom ID" : "Auto ID");
                if (!selected) idInput.clear();
            });

            TextField nameInput = new TextField();
            nameInput.setPromptText("Student Name (letters only)");
            TextField programInput = new TextField();
            programInput.setPromptText("Student Program (letters only)");
            TextField yearInput = new TextField();
            yearInput.setPromptText("Student Year");

            Button submitBtn = new Button("Create");
            submitBtn.getStyleClass().add("button-primary");
            submitBtn.setOnAction(e -> {
                String name = nameInput.getText().trim();
                String program = programInput.getText().trim();
                String yearStr = yearInput.getText().trim();
                String idStr = idInput.getText().trim();

                if (name.isEmpty() || program.isEmpty() || yearStr.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
                    return;
                }

                if (!name.matches("[\\p{L} ]+") || !program.matches("[\\p{L} ]+")) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error", "Name and program must contain letters only. Spaces are allowed.");
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
                    try {
                        int customId = Integer.parseInt(idStr);
                        success = StudentHandler.addStudent(customId, name, year, program);
                    } catch (NumberFormatException ex) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Input", "ID must be a valid number.");
                        return;
                    }
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
            Scene addScene = new Scene(layout, 420, 350);
            applyStylesheet(addScene);
            addStudentWindow.setScene(addScene);
            addStudentWindow.show();
        });

        // Red-Orange Edit Button
        Button editBtn = new Button("Edit Student");
        editBtn.getStyleClass().addAll("action-btn", "btn-edit");

        editBtn.setOnAction(event -> {
            new ModifyStudent().openModifyWindow(tableUI, this::updateStudentCount, this::applyStylesheet);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbarContainer = new HBox(12);
        toolbarContainer.setAlignment(Pos.CENTER_LEFT);
        toolbarContainer.setPadding(new Insets(15));
        toolbarContainer.getChildren().addAll(studentInfoCard, spacer, importBtn, addBtn, editBtn);

        BorderPane root = new BorderPane();
        root.setTop(toolbarContainer);
        root.setCenter(tableUI.createTable());
        BorderPane.setMargin(root.getCenter(), new Insets(0, 15, 15, 15));

        Scene scene = new Scene(root, 1080, 600);
        applyStylesheet(scene);

        stage.setTitle("BitStream");
        stage.setScene(scene);
        stage.show();
    }

    private void updateStudentCount() {
        int count = StudentHandler.getStudentCount();
        studentNumberLabel.setText(String.valueOf(count));
    }

    public void applyStylesheet(Scene scene) {
        URL cssUrl = getClass().getResource("css/Home.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}