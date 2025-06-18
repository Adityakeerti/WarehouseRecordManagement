package com.recursiveMind.WareHouseRecordManagement.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.springframework.stereotype.Controller;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class DashboardController {
    
    @FXML
    private StackPane contentArea;
    
    @FXML
    private Label userNameLabel;
    
    @FXML
    private Label userRoleLabel;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Label dateTimeLabel;
    
    private ApplicationContext springContext;
    
    private static final String DASHBOARD_VIEW = "/fxml/views/dashboard-view.fxml";
    private static final String PRODUCTS_VIEW = "/fxml/views/product.fxml";
    private static final String ORDERS_VIEW = "/fxml/views/orders.fxml";
    private static final String SUPPLIERS_VIEW = "/fxml/views/suppliers.fxml";
    private static final String REPORTS_VIEW = "/fxml/views/reports.fxml";

    @Autowired
    public DashboardController(ApplicationContext springContext) {
        this.springContext = springContext;
    }
    
    @FXML
    public void initialize() {
        // Set static admin user info
        userNameLabel.setText("Admin User");
        userRoleLabel.setText("Administrator");

        updateDateTime();
        showDashboard();
    }
    
    private void updateDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dateTimeLabel.setText(LocalDateTime.now().format(formatter));
    }
    
    private void loadView(String viewPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath));
            loader.setControllerFactory(springContext::getBean);
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error loading view: " + e.getMessage());
        }
    }
    
    @FXML
    private void showDashboard() {
        loadView(DASHBOARD_VIEW);
        statusLabel.setText("Dashboard loaded");
    }
    
    @FXML
    private void showProducts() {
        loadView(PRODUCTS_VIEW);
        statusLabel.setText("Products management loaded");
    }
    
    @FXML
    private void showOrders() {
        loadView(ORDERS_VIEW);
        statusLabel.setText("Orders management loaded");
    }
    
    @FXML
    private void showSuppliers() {
        loadView(SUPPLIERS_VIEW);
        statusLabel.setText("Suppliers management loaded");
    }
    
    @FXML
    private void showReports() {
        loadView(REPORTS_VIEW);
        statusLabel.setText("Reports loaded");
    }
    
    @FXML
    public void showProfile() {
        loadView("/fxml/views/profile-view.fxml");
    }
    
    @FXML
    private void handleLogout() {
        // Implement logout logic here
        System.exit(0);
    }
} 