package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Order;
import com.recursiveMind.WareHouseRecordManagement.model.Product;
import com.recursiveMind.WareHouseRecordManagement.service.OrderService;
import com.recursiveMind.WareHouseRecordManagement.service.ProductService;
import javafx.beans.property.SimpleStringProperty;
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
public class DashboardViewController {

    @FXML
    private Label totalProductsLabel;

    @FXML
    private Label lowStockLabel;

    @FXML
    private Label totalOrdersLabel;

    @FXML
    private Label totalValueLabel;

    @FXML
    private LineChart<String, Number> stockLevelChart;

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
    private TableView<Product> lowStockTable;

    @FXML
    private TableColumn<Product, String> productCodeColumn;

    @FXML
    private TableColumn<Product, String> productNameColumn;

    @FXML
    private TableColumn<Product, Integer> currentStockColumn;

    @FXML
    private TableColumn<Product, Integer> minStockColumn;

    @FXML
    private PieChart categoryChart;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    private ObservableList<Order> recentOrders = FXCollections.observableArrayList();
    private ObservableList<Product> lowStockItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTables();
        loadDashboardData();
    }

    private void setupTables() {
        // Recent Orders Table
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getOrderDate();
            return new SimpleStringProperty(
                date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
        });
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        orderTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        recentOrdersTable.setItems(recentOrders);

        // Low Stock Table
        productCodeColumn.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        currentStockColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        minStockColumn.setCellValueFactory(new PropertyValueFactory<>("minStockLevel"));
        lowStockTable.setItems(lowStockItems);
    }

    private void loadDashboardData() {
        // Load summary metrics
        List<Product> allProducts = productService.getAllProducts();
        totalProductsLabel.setText(String.valueOf(allProducts.size()));
        
        List<Product> lowStockProducts = productService.getLowStockProducts();
        lowStockLabel.setText(String.valueOf(lowStockProducts.size()));
        
        double totalValue = allProducts.stream()
            .mapToDouble(p -> p.getPrice() * p.getQuantity())
            .sum();
        totalValueLabel.setText(String.format("$%.2f", totalValue));

        // Load recent orders
        loadRecentOrders();
        totalOrdersLabel.setText(String.valueOf(recentOrders.size()));

        // Load low stock items
        lowStockItems.setAll(lowStockProducts);

        // Update stock level chart
        updateStockLevelChart(allProducts);

        // Update category distribution chart
        updateCategoryChart(allProducts);
    }

    private void loadRecentOrders() {
        List<Order> orders = orderService.getAllOrders();
        // Sort by order date descending and take first 5
        orders.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));
        orders = orders.stream().limit(5).collect(Collectors.toList());
        recentOrdersTable.setItems(FXCollections.observableArrayList(orders));
    }

    private void updateStockLevelChart(List<Product> products) {
        stockLevelChart.getData().clear();
        
        Map<String, Double> categoryStock = products.stream()
            .collect(Collectors.groupingBy(
                Product::getCategory,
                Collectors.summingDouble(Product::getQuantity)
            ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Stock Levels");
        
        categoryStock.forEach((category, stock) -> 
            series.getData().add(new XYChart.Data<>(category, stock))
        );

        stockLevelChart.getData().add(series);
    }

    private void updateCategoryChart(List<Product> products) {
        categoryChart.getData().clear();
        
        Map<String, Long> categoryCount = products.stream()
            .collect(Collectors.groupingBy(
                Product::getCategory,
                Collectors.counting()
            ));

        categoryCount.forEach((category, count) -> 
            categoryChart.getData().add(new PieChart.Data(category, count))
        );
    }
} 