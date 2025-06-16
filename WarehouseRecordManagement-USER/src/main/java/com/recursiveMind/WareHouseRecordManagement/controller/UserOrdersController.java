package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Order;
import com.recursiveMind.WareHouseRecordManagement.model.OrderStatus;
import com.recursiveMind.WareHouseRecordManagement.service.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class UserOrdersController {
    @FXML
    private TableView<Order> ordersTable;
    
    @FXML
    private TableColumn<Order, String> orderIdColumn;
    
    @FXML
    private TableColumn<Order, String> orderDateColumn;
    
    @FXML
    private TableColumn<Order, String> warehouseColumn;
    
    @FXML
    private TableColumn<Order, String> statusColumn;
    
    @FXML
    private TableColumn<Order, Double> totalColumn;
    
    @FXML
    private TableColumn<Order, Void> detailsColumn;
    
    @FXML
    private ComboBox<OrderStatus> statusFilterComboBox;
    
    @FXML
    private Button refreshButton;
    
    @Autowired
    private OrderService orderService;
    
    private FilteredList<Order> filteredOrders;
    
    @FXML
    public void initialize() {
        setupTableColumns();
        setupStatusFilter();
        loadOrders();
    }
    
    private void setupTableColumns() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getOrderDate();
            return new javafx.beans.property.SimpleStringProperty(
                date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
        });
        warehouseColumn.setCellValueFactory(new PropertyValueFactory<>("warehouseName"));
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().toString())
        );
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        
        // Add view details button to each row
        detailsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewButton = new Button("View");
            
            {
                viewButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    handleViewDetails(order);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewButton);
            }
        });
    }
    
    private void setupStatusFilter() {
        statusFilterComboBox.getItems().addAll(OrderStatus.values());
        statusFilterComboBox.getItems().add(0, null); // Add null for "All" option
        statusFilterComboBox.setValue(null);
    }
    
    private void loadOrders() {
        try {
            Long userId = com.recursiveMind.WareHouseRecordManagement.WarehouseUserApp.getCurrentUser().getId();
            List<Order> orders = orderService.getOrdersByUserId(userId);
            filteredOrders = new FilteredList<>(FXCollections.observableArrayList(orders));
            ordersTable.setItems(filteredOrders);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleStatusFilter() {
        OrderStatus selectedStatus = statusFilterComboBox.getValue();
        filteredOrders.setPredicate(order -> 
            selectedStatus == null || order.getStatus() == selectedStatus
        );
    }
    
    @FXML
    private void handleRefresh() {
        loadOrders();
    }
    
    private void handleViewDetails(Order order) {
        // TODO: Show order details dialog
        showAlert("Order details will be shown here", Alert.AlertType.INFORMATION);
    }
    
    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Order Details");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 