package org.example.views;

import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.controllers.StudentHandler;
import org.example.models.Student;

public class ModifyStudent {

    public void openModifyWindow(StudentTable tableUI, Runnable onUpdateCallback, Consumer<Scene> styleConsumer) {
        Stage modifyWindow = new Stage();
        modifyWindow.initModality(Modality.APPLICATION_MODAL);
        modifyWindow.setTitle("Modify Student Details");

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Label heading = new Label("Enter Student ID to Edit");
        heading.getStyleClass().add("sub-heading");

        TextField idInput = new TextField();
        idInput.setPromptText("Student ID");
        Button searchBtn = new Button("Search");

        TextField editedIdInput = new TextField();
        editedIdInput.setPromptText("Student ID");
        editedIdInput.setEditable(false);
        editedIdInput.setDisable(true);

        CheckBox modifyIdCheckbox = new CheckBox("Modify ID");
        modifyIdCheckbox.setDisable(true);
        modifyIdCheckbox.setOnAction(e -> editedIdInput.setEditable(modifyIdCheckbox.isSelected()));

        TextField nameInput = new TextField();
        nameInput.setPromptText("Student Name (letters only)");
        TextField programInput = new TextField();
        programInput.setPromptText("Student Program (letters only)");
        TextField yearInput = new TextField();
        yearInput.setPromptText("Student Year");

        Button saveBtn = new Button("Save Changes");
        saveBtn.getStyleClass().add("button-primary");
        saveBtn.setDisable(true);

        Button deleteBtn = new Button("Delete Student");
        deleteBtn.getStyleClass().add("button-danger");
        deleteBtn.setDisable(true);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(saveBtn, deleteBtn);

        final int[] currentId = {-1};

        searchBtn.setOnAction(e -> {
            String idStr = idInput.getText().trim();
            if (idStr.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a Student ID.");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Student ID must be a valid number.");
                return;
            }

            saveBtn.setDisable(true);
            deleteBtn.setDisable(true);
            modifyIdCheckbox.setSelected(false);
            modifyIdCheckbox.setDisable(true);
            editedIdInput.clear();
            editedIdInput.setEditable(false);
            editedIdInput.setDisable(true);
            currentId[0] = -1;

            Student student = StudentHandler.getStudentById(id);

            if (student == null) {
                showAlert(Alert.AlertType.INFORMATION, "Not Found", "No student found with ID: " + id);
            } else {
                heading.setText("Editing: " + student.getName());
                editedIdInput.setText(String.valueOf(student.getId()));
                nameInput.setText(student.getName());
                programInput.setText(student.getProgram());
                yearInput.setText(String.valueOf(student.getYear()));
                currentId[0] = id;

                editedIdInput.setDisable(false);
                modifyIdCheckbox.setDisable(false);
                saveBtn.setDisable(false);
                deleteBtn.setDisable(false);
            }
        });

        saveBtn.setOnAction(e -> {
            String name = nameInput.getText().trim();
            String program = programInput.getText().trim();
            String yearStr = yearInput.getText().trim();
            String idStr = editedIdInput.getText().trim();

            if (name.isEmpty() || program.isEmpty() || yearStr.isEmpty() || idStr.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
                return;
            }

            if (!name.matches("[\\p{L} ]+") || !program.matches("[\\p{L} ]+")) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Name and program must contain letters only. Spaces are allowed.");
                return;
            }

            int year;
            int targetId;
            try {
                year = Integer.parseInt(yearStr);
                targetId = Integer.parseInt(idStr);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "ID and Year must be valid numbers.");
                return;
            }

            if (StudentHandler.updateStudent(currentId[0], targetId, name, year, program)) {
                tableUI.refreshTable();
                if (onUpdateCallback != null) onUpdateCallback.run();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Student updated successfully!");
                modifyWindow.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Update failed. Target ID might belong to another student.");
            }
        });

        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete this student?");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (StudentHandler.deleteStudent(currentId[0])) {
                        tableUI.refreshTable();
                        if (onUpdateCallback != null) onUpdateCallback.run();
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Student deleted successfully!");
                        modifyWindow.close();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete student.");
                    }
                }
            });
        });

        HBox searchBox = new HBox(10, idInput, searchBtn);
        searchBox.setAlignment(Pos.CENTER);
        HBox idBox = new HBox(10, new Label("Student ID:"), editedIdInput, modifyIdCheckbox);
        idBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(heading, searchBox, idBox, nameInput, programInput, yearInput, buttonBox);
        Scene scene = new Scene(layout, 480, 420);
        if (styleConsumer != null) {
            styleConsumer.accept(scene);
        }
        modifyWindow.setScene(scene);
        modifyWindow.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}