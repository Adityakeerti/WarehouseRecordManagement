package com.recursiveMind.WareHouseRecordManagement;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class WarehouseManagementApp extends Application {
    
    private ConfigurableApplicationContext springContext;
    private Parent rootNode;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(WarehouseManagementApp.class);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        rootNode = fxmlLoader.load();
    }
    
    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Warehouse Management System");
            Scene scene = new Scene(rootNode);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }
    
    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
        Platform.exit();
    }
} 