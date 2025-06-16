package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Warehouse;
import com.recursiveMind.WareHouseRecordManagement.service.WarehouseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.io.IOException;
import java.util.List;

@Controller
public class UserWarehousesController {
    @FXML private FlowPane warehousesContainer;
    
    @Autowired
    private WarehouseService warehouseService;
    
    @FXML
    public void initialize() {
        loadWarehouses();
    }
    
    private void loadWarehouses() {
        try {
            List<Warehouse> warehouses = warehouseService.getAllWarehouses();
            warehousesContainer.getChildren().clear();
            
            for (Warehouse warehouse : warehouses) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/views/warehouse-card.fxml"));
                Parent card = loader.load();
                
                WarehouseCardController controller = loader.getController();
                controller.setWarehouse(warehouse);
                
                warehousesContainer.getChildren().add(card);
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to load warehouse cards: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to load warehouses: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadWarehouses();
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 