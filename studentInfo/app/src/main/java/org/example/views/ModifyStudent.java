package org.example.views;

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

    public void openModifyWindow(StudentTable tableUI, Runnable onUpdateCallback) {
        Stage modifyWindow = new Stage();
        modifyWindow.initModality(Modality.APPLICATION_MODAL);
        modifyWindow.setTitle("Modify Student Details");

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Label heading = new Label("Enter Student ID to Edit");
        TextField idInput = new TextField();
        idInput.setPromptText("Student ID");
        Button searchBtn = new Button("Search");

        CheckBox modifyIdCheckbox = new CheckBox("Modify ID");
        modifyIdCheckbox.setDisable(true);
        modifyIdCheckbox.setOnAction(e -> idInput.setEditable(modifyIdCheckbox.isSelected()));

        TextField nameInput = new TextField();
        nameInput.setPromptText("Student Name");
        TextField programInput = new TextField();
        programInput.setPromptText("Student Program");
        TextField yearInput = new TextField();
        yearInput.setPromptText("Student Year");

        Button saveBtn = new Button("Save Changes");
        saveBtn.setDisable(true);

        Button deleteBtn = new Button("Delete Student");
        deleteBtn.setDisable(true);
        deleteBtn.setStyle("-fx-background-color: #ff4c4c; -fx-text-fill: white;");

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

            Student student = StudentHandler.getStudentById(id);

            if (student == null) {
                showAlert(Alert.AlertType.INFORMATION, "Not Found", "No student found with ID: " + id);
                saveBtn.setDisable(true);
                deleteBtn.setDisable(true);
                modifyIdCheckbox.setDisable(true);
            } else {
                heading.setText("Editing: " + student.getName());
                nameInput.setText(student.getName());
                programInput.setText(student.getProgram());
                yearInput.setText(String.valueOf(student.getYear()));
                currentId[0] = id;
                
                // Lock ID input until "Modify ID" is checked
                idInput.setEditable(false);
                modifyIdCheckbox.setSelected(false);
                modifyIdCheckbox.setDisable(false);
                saveBtn.setDisable(false);
                deleteBtn.setDisable(false);
            }
        });

        saveBtn.setOnAction(e -> {
            String name = nameInput.getText().trim();
            String program = programInput.getText().trim();
            String yearStr = yearInput.getText().trim();
            String idStr = idInput.getText().trim();

            if (name.isEmpty() || program.isEmpty() || yearStr.isEmpty() || idStr.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
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
                if (onUpdateCallback != null) {
                    onUpdateCallback.run();
                }
                showAlert(Alert.AlertType.INFORMATION, "Success", "Student updated successfully!");
                modifyWindow.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Update failed. Target ID might already belong to another student.");
            }
        });

        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete this student? This cannot be undone.");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (StudentHandler.deleteStudent(currentId[0])) {
                        tableUI.refreshTable();
                        if (onUpdateCallback != null) {
                            onUpdateCallback.run();
                        }
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Student deleted successfully!");
                        modifyWindow.close();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete student.");
                    }
                }
            });
        });

        HBox idBox = new HBox(10, idInput, searchBtn, modifyIdCheckbox);
        idBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(heading, idBox, nameInput, programInput, yearInput, buttonBox);
        modifyWindow.setScene(new Scene(layout, 480, 420));
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