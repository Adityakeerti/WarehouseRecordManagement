package com.recursiveMind.WareHouseRecordManagement.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.recursiveMind.WareHouseRecordManagement.service.UserService;
import com.recursiveMind.WareHouseRecordManagement.WarehouseUserApp;
import com.recursiveMind.WareHouseRecordManagement.model.User;

@Controller
public class UserLoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    @Autowired
    private UserService userService;
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }
        
        try {
            User user = userService.authenticate(username, password);
            if (user != null) {
                errorLabel.setText("");
                WarehouseUserApp.setCurrentUser(user);
                WarehouseUserApp.loadScene("/fxml/user-dashboard.fxml");
            } else {
                errorLabel.setText("Invalid username or password");
            }
        } catch (Exception e) {
            errorLabel.setText("Error during login: " + e.getMessage());
        }
    }
} 