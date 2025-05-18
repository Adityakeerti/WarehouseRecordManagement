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
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardViewController extends BaseController {

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

    private final ProductService productService;
    private final OrderService orderService;

    public DashboardViewController(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    @FXML
    public void initialize() {
        updateDashboardMetrics();
        setupCharts();
        setupTables();
        loadData();
    }

    private void updateDashboardMetrics() {
        try {
            // Update total products
            long totalProducts = productService.getTotalProducts();
            totalProductsLabel.setText(String.valueOf(totalProducts));
            
            // Update low stock items
            long lowStockItems = productService.getLowStockItemsCount();
            lowStockLabel.setText(String.valueOf(lowStockItems));
            
            // Update total orders
            long totalOrders = orderService.getTotalOrdersThisMonth();
            totalOrdersLabel.setText(String.valueOf(totalOrders));
            
            // Update total value
            double totalValue = productService.getTotalInventoryValue();
            totalValueLabel.setText(String.format("$%.2f", totalValue));
        } catch (Exception e) {
            showError("Error updating dashboard metrics: " + e.getMessage());
        }
    }

    private void setupCharts() {
        try {
            // Setup category distribution pie chart
            Map<String, Long> categoryDistribution = productService.getCategoryDistribution();
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                categoryDistribution.entrySet().stream()
                    .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList())
            );
            categoryChart.setData(pieChartData);
            
            // Setup stock level line chart
            // This will be populated in loadData()
        } catch (Exception e) {
            showError("Error setting up charts: " + e.getMessage());
        }
    }

    private void setupTables() {
        // Setup recent orders table
        orderIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        orderDateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        orderStatusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        orderTotalColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty().asObject());
        
        // Setup low stock table
        productCodeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
        productNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        currentStockColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        minStockColumn.setCellValueFactory(cellData -> cellData.getValue().minStockProperty().asObject());
    }

    private void loadData() {
        try {
            // Load recent orders
            List<Order> recentOrders = orderService.getRecentOrders(5);
            recentOrdersTable.setItems(FXCollections.observableArrayList(recentOrders));
            
            // Load low stock items
            List<Product> lowStockItems = productService.getLowStockItems();
            lowStockTable.setItems(FXCollections.observableArrayList(lowStockItems));
            
            // Update stock level chart
            Map<String, Integer> stockLevels = productService.getStockLevelsByCategory();
            stockLevelChart.getData().clear();
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Stock Levels");
            
            stockLevels.forEach((category, quantity) -> 
                series.getData().add(new XYChart.Data<>(category, quantity))
            );
            
            stockLevelChart.getData().add(series);
        } catch (Exception e) {
            showError("Error loading dashboard data: " + e.getMessage());
        }
    }
} 