package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Warehouse;
import com.recursiveMind.WareHouseRecordManagement.service.WarehouseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserWarehousesController {
    
    @FXML
    private VBox warehousesContainer;
    
    @Autowired
    private WarehouseService warehouseService;
    
    private ObservableList<Warehouse> warehouseList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        loadWarehouses();
    }
    
    private void loadWarehouses() {
        List<Warehouse> warehouses = warehouseService.getActiveWarehouses();
        warehouseList.setAll(warehouses);
        displayWarehouses();
    }
    
    private void displayWarehouses() {
        warehousesContainer.getChildren().clear();
        
        for (Warehouse warehouse : warehouseList) {
            GridPane card = createWarehouseCard(warehouse);
            warehousesContainer.getChildren().add(card);
        }
    }
    
    private GridPane createWarehouseCard(Warehouse warehouse) {
        GridPane card = new GridPane();
        card.getStyleClass().add("warehouse-card");
        card.setHgap(10);
        card.setVgap(10);
        card.setPadding(new javafx.geometry.Insets(15));
        
        Label nameLabel = new Label(warehouse.getName());
        nameLabel.getStyleClass().add("warehouse-name");
        
        Label locationLabel = new Label(warehouse.getLocation());
        locationLabel.getStyleClass().add("warehouse-location");
        
        Button placeOrderButton = new Button("Place Order");
        placeOrderButton.getStyleClass().add("place-order-button");
        placeOrderButton.setOnAction(e -> handlePlaceOrder(warehouse));
        
        card.add(nameLabel, 0, 0);
        card.add(locationLabel, 0, 1);
        card.add(placeOrderButton, 0, 2);
        
        return card;
    }
    
    private void handlePlaceOrder(Warehouse warehouse) {
        // TODO: Implement order placement logic
        // This will be implemented in the next step
    }
} 