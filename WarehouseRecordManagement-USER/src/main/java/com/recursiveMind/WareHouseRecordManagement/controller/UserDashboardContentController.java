package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Order;
import com.recursiveMind.WareHouseRecordManagement.model.OrderStatus;
import com.recursiveMind.WareHouseRecordManagement.service.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UserDashboardContentController {
    @FXML
    private Label totalOrdersLabel;
    
    @FXML
    private Label pendingOrdersLabel;
    
    @FXML
    private Label deliveredOrdersLabel;
    
    @FXML
    private Label totalSpentLabel;
    
    @FXML
    private TableView<Order> recentOrdersTable;
    
    @FXML
    private TableColumn<Order, String> orderIdColumn;
    
    @FXML
    private TableColumn<Order, String> orderDateColumn;
    
    @FXML
    private TableColumn<Order, String> orderStatusColumn;
    
    @FXML
    private TableColumn<Order, Double> orderTotalColumn;
    
    @FXML
    private PieChart orderStatusChart;
    
    @FXML
    private LineChart<String, Number> monthlyOrdersChart;
    
    @Autowired
    private OrderService orderService;
    
    @FXML
    public void initialize() {
        setupTableColumns();
        loadDashboardData();
    }
    
    private void setupTableColumns() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getOrderDate();
            return new javafx.beans.property.SimpleStringProperty(
                date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
        });
        orderStatusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().toString())
        );
        orderTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
    }
    
    private void loadDashboardData() {
        try {
            // Get current user's orders
            Long userId = com.recursiveMind.WareHouseRecordManagement.WarehouseUserApp.getCurrentUser().getId();
            List<Order> userOrders = orderService.getOrdersByUserId(userId);
            
            // Update metrics
            totalOrdersLabel.setText(String.valueOf(userOrders.size()));
            
            long pendingCount = userOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.PENDING)
                .count();
            pendingOrdersLabel.setText(String.valueOf(pendingCount));
            
            long deliveredCount = userOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .count();
            deliveredOrdersLabel.setText(String.valueOf(deliveredCount));
            
            double totalSpent = userOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
            totalSpentLabel.setText(String.format("$%.2f", totalSpent));
            
            // Update recent orders table
            List<Order> recentOrders = userOrders.stream()
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .limit(5)
                .collect(Collectors.toList());
            recentOrdersTable.setItems(FXCollections.observableArrayList(recentOrders));
            
            // Update order status chart
            Map<OrderStatus, Long> statusCounts = userOrders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
            
            orderStatusChart.getData().clear();
            statusCounts.forEach((status, count) -> 
                orderStatusChart.getData().add(new PieChart.Data(status.toString(), count))
            );
            
            // Update monthly orders chart
            Map<String, Long> monthlyOrders = userOrders.stream()
                .collect(Collectors.groupingBy(
                    order -> order.getOrderDate().format(DateTimeFormatter.ofPattern("MMM yyyy")),
                    Collectors.counting()
                ));
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Monthly Orders");
            
            monthlyOrders.forEach((month, count) -> 
                series.getData().add(new XYChart.Data<>(month, count))
            );
            
            monthlyOrdersChart.getData().clear();
            monthlyOrdersChart.getData().add(series);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 