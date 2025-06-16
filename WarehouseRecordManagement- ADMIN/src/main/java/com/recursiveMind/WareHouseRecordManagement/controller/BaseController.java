package com.recursiveMind.WareHouseRecordManagement.controller;

import javafx.scene.control.Alert;

public abstract class BaseController {
    
    protected void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    protected void showError(String message) {
        showAlert("Error", message, Alert.AlertType.ERROR);
    }
    
    protected void showInfo(String message) {
        showAlert("Information", message, Alert.AlertType.INFORMATION);
    }
    
    protected void showWarning(String message) {
        showAlert("Warning", message, Alert.AlertType.WARNING);
    }
} 