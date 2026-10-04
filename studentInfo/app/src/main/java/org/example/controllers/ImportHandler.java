package org.example.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ImportHandler {

    public static int importFile(File file) throws Exception {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".csv")) {
            return importCSV(file);
        } else if (name.endsWith(".json")) {
            return importJSON(file);
        } else if (name.endsWith(".xlsx") || name.endsWith(".xls")) {
            return importExcel(file);
        } else if (name.endsWith(".accdb") || name.endsWith(".mdb")) {
            return importAccess(file);
        } else {
            throw new IllegalArgumentException("Unsupported file format.");
        }
    }

    private static int importCSV(File file) throws Exception {
        int count = 0;
        try (Reader in = new FileReader(file)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .build()
                    .parse(in);

            for (CSVRecord record : records) {
                String name = record.get("name");
                int year = Integer.parseInt(record.get("year"));
                String program = record.get("program");

                if (record.isMapped("id") && !record.get("id").isEmpty()) {
                    int id = Integer.parseInt(record.get("id"));
                    if (StudentHandler.addStudent(id, name, year, program)) count++;
                } else {
                    if (StudentHandler.addStudent(name, year, program)) count++;
                }
            }
        }
        return count;
    }

    private static int importJSON(File file) throws Exception {
        int count = 0;
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> students = mapper.readValue(file, new TypeReference<>() {});

        for (Map<String, Object> s : students) {
            String name = (String) s.get("name");
            String program = (String) s.get("program");
            int year = Integer.parseInt(s.get("year").toString());

            if (s.containsKey("id") && s.get("id") != null) {
                int id = Integer.parseInt(s.get("id").toString());
                if (StudentHandler.addStudent(id, name, year, program)) count++;
            } else {
                if (StudentHandler.addStudent(name, year, program)) count++;
            }
        }
        return count;
    }

    private static int importExcel(File file) throws Exception {
        int count = 0;
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            int idCol = -1, nameCol = -1, yearCol = -1, programCol = -1;
            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().trim().toLowerCase();
                if (header.equals("id")) idCol = cell.getColumnIndex();
                else if (header.equals("name") || header.equals("student name")) nameCol = cell.getColumnIndex();
                else if (header.equals("year")) yearCol = cell.getColumnIndex();
                else if (header.equals("program")) programCol = cell.getColumnIndex();
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String name = row.getCell(nameCol).getStringCellValue();
                String program = row.getCell(programCol).getStringCellValue();
                int year = (int) row.getCell(yearCol).getNumericCellValue();

                if (idCol != -1 && row.getCell(idCol) != null) {
                    int id = (int) row.getCell(idCol).getNumericCellValue();
                    if (StudentHandler.addStudent(id, name, year, program)) count++;
                } else {
                    if (StudentHandler.addStudent(name, year, program)) count++;
                }
            }
        }
        return count;
    }

    private static int importAccess(File file) throws Exception {
        int count = 0;
        String url = "jdbc:ucanaccess://" + file.getAbsolutePath();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {

            while (rs.next()) {
                String name = rs.getString("name");
                int year = rs.getInt("year");
                String program = rs.getString("program");
                
                try {
                    int id = rs.getInt("id");
                    if (StudentHandler.addStudent(id, name, year, program)) count++;
                } catch (Exception e) {
                    if (StudentHandler.addStudent(name, year, program)) count++;
                }
            }
        }
        return count;
    }
}