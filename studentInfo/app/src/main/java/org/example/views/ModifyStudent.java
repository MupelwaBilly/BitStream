package org.example.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

        TextField nameInput = new TextField();
        nameInput.setPromptText("Student Name");
        TextField programInput = new TextField();
        programInput.setPromptText("Student Program");
        TextField yearInput = new TextField();
        yearInput.setPromptText("Student Year");

        Button saveBtn = new Button("Save Changes");
        saveBtn.setDisable(true);

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
            } else {
                heading.setText("Editing: " + student.getName());
                nameInput.setText(student.getName());
                programInput.setText(student.getProgram());
                yearInput.setText(String.valueOf(student.getYear()));
                currentId[0] = id;
                saveBtn.setDisable(false);
            }
        });

        saveBtn.setOnAction(e -> {
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

            if (StudentHandler.updateStudent(currentId[0], name, year, program)) {
                tableUI.refreshTable();
                if (onUpdateCallback != null) {
                    onUpdateCallback.run();
                }
                showAlert(Alert.AlertType.INFORMATION, "Success", "Student updated successfully!");
                modifyWindow.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Update failed. Try again.");
            }
        });

        layout.getChildren().addAll(heading, idInput, searchBtn, nameInput, programInput, yearInput, saveBtn);
        modifyWindow.setScene(new Scene(layout, 400, 400));
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