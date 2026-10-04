package org.example.views;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.controllers.StudentHandler;
import org.example.models.Student;

public class StudentTable {

    private final ObservableList<Student> studentData = FXCollections.observableArrayList();

    public TableView<Student> createTable() {
        TableView<Student> table = new TableView<>();
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Student, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setMaxWidth(1000);

        TableColumn<Student, String> nameColumn = new TableColumn<>("Student Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> programColumn = new TableColumn<>("Program");
        programColumn.setCellValueFactory(new PropertyValueFactory<>("program"));

        TableColumn<Student, Integer> yearColumn = new TableColumn<>("Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        // Swapped column order: ID -> Name -> Program -> Year
        table.getColumns().add(idColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(programColumn);
        table.getColumns().add(yearColumn);
        
        table.setItems(studentData);

        refreshTable();
        return table;
    }

    public void refreshTable() {
        studentData.clear();
        List<Student> dbStudents = StudentHandler.getAllStudents();
        studentData.addAll(dbStudents);
    }
}