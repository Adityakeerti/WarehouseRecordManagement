package com.recursiveMind.WareHouseRecordManagement.controller;

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
// import javafx.scene.input.MouseEvent; // No longer needed
import org.springframework.stereotype.Controller;

@Controller
public class WarehouseCardController {
    @FXML private Label nameLabel;
    @FXML private Label locationLabel;
    @FXML private Label statusLabel;
    @FXML private Button viewProductsButton;
    @FXML private Label descriptionLabel;
    
    // private Product product; // No longer needed
    
    public void setWarehouseName(String name) {
        nameLabel.setText(name);
        locationLabel.setText("Click to view products");
        descriptionLabel.setText("Administered warehouse containing various products.");
        statusLabel.setText(""); // Clear any product-specific status
        viewProductsButton.setText("View Products");
    }
    
    // private void updateUI() { // No longer needed
    //     if (product != null) {
    //         String status = product.isActive() ? "Available" : "Unavailable";
    //         statusLabel.setText(status);
    //         
    //         // Set status color
    //         String statusStyle = product.isActive() ? 
    //             "-fx-text-fill: #2ecc71;" : "-fx-text-fill: #e74c3c;";
    //         statusLabel.setStyle(statusStyle);
    //     }
    // }
    
    @FXML
    private void handleViewProducts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/admin-warehouse-products-dialog.fxml"));
            loader.setControllerFactory(com.recursiveMind.WareHouseRecordManagement.WarehouseUserApp.getSpringContext()::getBean);
            Parent parent = loader.load();

            // You might need to pass the ProductService to the new controller
            // AdminWarehouseProductsController controller = loader.getController();
            // controller.setProductService(productService);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Admin Warehouse Products");
            stage.setScene(new Scene(parent));
            
            // Set the dialog dimensions here
            stage.setWidth(800);  // Change this value for width
            stage.setHeight(600); // Change this value for height
            
            // Optional: Set minimum dimensions
            stage.setMinWidth(600);
            stage.setMinHeight(400);
            
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Error", "Failed to open product dialog: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    // @FXML
    // private void handleCardClick(MouseEvent event) { // No longer needed
    //     // This method is no longer relevant for product display on user side.
    //     // If product details need to be shown on click, a new method should be implemented.
    //     // For now, it does nothing.
    // }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 