package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Product;
import com.recursiveMind.WareHouseRecordManagement.service.ProductService;
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
    
    // @Autowired
    // private ProductService productService; // No longer directly used to load products here
    
    @FXML
    public void initialize() {
        loadAdminWarehouseCard();
    }
    
    private void loadAdminWarehouseCard() {
        try {
            warehousesContainer.getChildren().clear();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/views/warehouse-card.fxml"));
            Parent card = loader.load();
            
            WarehouseCardController controller = loader.getController();
            // We are no longer passing a Product object directly here,
            // as this card represents the Admin Warehouse itself.
            // The controller will need to be updated to handle this.
            // For now, we might set a flag or specific text.
            controller.setWarehouseName("Admin Warehouse"); 
            
            warehousesContainer.getChildren().add(card);
        } catch (IOException e) {
            showAlert("Error", "Failed to load admin warehouse card: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to load admin warehouse card: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    // The handleRefresh method might not be needed anymore or could be adapted
    // @FXML
    // private void handleRefresh() {
    //     loadAdminWarehouseCard();
    // }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 