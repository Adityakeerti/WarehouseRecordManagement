package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Warehouse;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.io.IOException;

public class WarehouseCardController {
    @FXML private Label nameLabel;
    @FXML private Label locationLabel;
    @FXML private Label statusLabel;
    @FXML private Button viewProductsButton;
    
    private Warehouse warehouse;
    
    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
        updateUI();
    }
    
    private void updateUI() {
        if (warehouse != null) {
            nameLabel.setText(warehouse.getName());
            locationLabel.setText(warehouse.getLocation());
            String status = warehouse.isActive() ? "Available" : "Unavailable";
            statusLabel.setText(status);
            
            // Set status color
            String statusStyle = warehouse.isActive() ? 
                "-fx-text-fill: #2ecc71;" : "-fx-text-fill: #e74c3c;";
            statusLabel.setStyle(statusStyle);
        }
    }
    
    @FXML
    private void handleViewProducts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/views/warehouse-products.fxml"));
            Parent root = loader.load();
            
            WarehouseProductsController controller = loader.getController();
            controller.setWarehouse(warehouse);
            
            Stage stage = new Stage();
            stage.setTitle("Warehouse Products - " + warehouse.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Could not load products view: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 