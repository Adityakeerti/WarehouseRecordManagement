package com.recursiveMind.WareHouseRecordManagement.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;
import com.recursiveMind.WareHouseRecordManagement.service.UserService;
import com.recursiveMind.WareHouseRecordManagement.model.User;
import org.springframework.context.ApplicationContext;

@Controller
public class LoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    private final UserService userService;
    private final ApplicationContext springContext;
    
    public LoginController(UserService userService, ApplicationContext springContext) {
        this.userService = userService;
        this.springContext = springContext;
    }
    
    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }
        
        try {
            User user = userService.authenticate(username, password);
            if (user != null) {
                loadDashboard(user);
            } else {
                errorLabel.setText("Invalid username or password");
            }
        } catch (Exception e) {
            errorLabel.setText("An error occurred during login");
        }
    }
    
    private void loadDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setCurrentUser(user);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
            
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error loading dashboard");
        }
    }
} 