package com.recursiveMind.WareHouseRecordManagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import com.recursiveMind.WareHouseRecordManagement.model.User;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
                       scanBasePackages = {"com.recursiveMind.WareHouseRecordManagement", "com.recursiveMind.WareHouseRecordManagement.service.Impl"})
public class WarehouseUserApp extends Application {
    private static ConfigurableApplicationContext springContext;
    private static Stage primaryStage;
    private static User currentUser;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        springContext = SpringApplication.run(WarehouseUserApp.class);
        
        // Load the login scene
        loadScene("/fxml/user-login.fxml");
        
        stage.setTitle("Warehouse Record Management - User");
        stage.show();
    }

    @Override
    public void stop() {
        springContext.close();
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void loadScene(String fxmlPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(WarehouseUserApp.class.getResource(fxmlPath));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(WarehouseUserApp.class.getResource("/styles/user-main.css").toExternalForm());
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConfigurableApplicationContext getSpringContext() {
        return springContext;
    }
} 