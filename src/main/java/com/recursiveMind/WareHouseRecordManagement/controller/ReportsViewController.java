package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.StockMovement;
import com.recursiveMind.WareHouseRecordManagement.service.StockMovementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;

@Controller
public class ReportsViewController {
    @FXML private TableView<StockMovement> stockMovementTable;
    @FXML private TableColumn<StockMovement, String> dateColumn;
    @FXML private TableColumn<StockMovement, String> productColumn;
    @FXML private TableColumn<StockMovement, String> typeColumn;
    @FXML private TableColumn<StockMovement, Integer> quantityColumn;
    @FXML private TableColumn<StockMovement, String> referenceColumn;
    @FXML private TableColumn<StockMovement, String> userColumn;
    @FXML private TableColumn<StockMovement, String> notesColumn;

    @Autowired
    private StockMovementService stockMovementService;

    private ObservableList<StockMovement> movementList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getMovementDate() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getMovementDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            } else {
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });
        productColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getProduct() != null ? cellData.getValue().getProduct().getName() : ""));
        typeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getType() != null ? cellData.getValue().getType().name() : ""));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        referenceColumn.setCellValueFactory(new PropertyValueFactory<>("reference"));
        userColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getPerformedBy() != null ? cellData.getValue().getPerformedBy().getFullName() : ""));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        loadMovements();
    }

    private void loadMovements() {
        movementList.setAll(stockMovementService.getAllMovements());
        stockMovementTable.setItems(movementList);
    }
} 