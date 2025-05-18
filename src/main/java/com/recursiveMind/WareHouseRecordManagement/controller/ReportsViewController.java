package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.ActivityLog;
import com.recursiveMind.WareHouseRecordManagement.repository.ActivityLogRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ReportsViewController {
    @FXML private TableView<ActivityLog> activityLogTable;
    @FXML private TableColumn<ActivityLog, String> timestampColumn;
    @FXML private TableColumn<ActivityLog, String> actionColumn;
    @FXML private TableColumn<ActivityLog, String> productCodeColumn;
    @FXML private TableColumn<ActivityLog, String> productNameColumn;
    @FXML private TableColumn<ActivityLog, String> detailsColumn;
    @FXML private TableColumn<ActivityLog, String> userColumn;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    private ObservableList<ActivityLog> logList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        timestampColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getTimestamp() != null) {
                return new SimpleStringProperty(
                    cellData.getValue().getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                return new SimpleStringProperty("");
            }
        });
        actionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAction()));
        productCodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductCode()));
        productNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
        detailsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDetails()));
        userColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getUser() != null ? cellData.getValue().getUser().getFullName() : ""));

        loadLogs();
    }

    private void loadLogs() {
        List<ActivityLog> logs = activityLogRepository.findAll();
        logList.setAll(logs);
        activityLogTable.setItems(logList);
    }

    @FXML
    private void handleExportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Activity Logs");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(activityLogTable.getScene().getWindow());
        if (file != null) {
            exportLogsToExcel(file);
        }
    }

    private void exportLogsToExcel(File file) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Activity Logs");
            Row header = sheet.createRow(0);
            String[] columns = {"Timestamp", "Action", "Product Code", "Product Name", "Details", "User"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }
            int rowIdx = 1;
            for (ActivityLog log : logList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(log.getTimestamp() != null ? log.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
                row.createCell(1).setCellValue(log.getAction());
                row.createCell(2).setCellValue(log.getProductCode());
                row.createCell(3).setCellValue(log.getProductName());
                row.createCell(4).setCellValue(log.getDetails());
                row.createCell(5).setCellValue(log.getUser() != null ? log.getUser().getFullName() : "");
            }
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally show error dialog
        }
    }
} 