package com.recursiveMind.WareHouseRecordManagement.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.recursiveMind.WareHouseRecordManagement.WarehouseUserApp;
import com.recursiveMind.WareHouseRecordManagement.model.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class UserDashboardController {
    @FXML
    private Label userNameLabel;
    
    @FXML
    private Label dateTimeLabel;
    
    @FXML
    private StackPane contentArea;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @FXML
    public void initialize() {
        User currentUser = WarehouseUserApp.getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getFullName());
        }
        updateDateTime();
    }
    
    private void updateDateTime() {
        dateTimeLabel.setText(LocalDateTime.now().format(formatter));
    }
    
    @FXML
    private void handleDashboard() {
        loadView("/fxml/views/user-dashboard-content.fxml");
    }
    
    @FXML
    private void handleWarehouses() {
        loadView("/fxml/views/user-warehouses.fxml");
    }
    
    @FXML
    private void handleOrders() {
        loadView("/fxml/views/user-orders.fxml");
    }
    
    @FXML
    private void handleLogout() {
        WarehouseUserApp.setCurrentUser(null);
        WarehouseUserApp.loadScene("/fxml/user-login.fxml");
    }
    
    private void loadView(String fxmlPath) {
        try {
            System.out.println("Attempting to load FXML: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(WarehouseUserApp.class.getResource(fxmlPath));
            loader.setControllerFactory(WarehouseUserApp.getSpringContext()::getBean);
            if (loader.getLocation() == null) {
                System.err.println("Error: Could not find FXML file at path: " + fxmlPath);
                return;
            }
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            System.out.println("Successfully loaded FXML: " + fxmlPath);
        } catch (Exception e) {
            System.err.println("Error loading FXML file: " + fxmlPath);
            e.printStackTrace();
        }
    }
} 