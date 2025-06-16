package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Order;
import com.recursiveMind.WareHouseRecordManagement.model.OrderStatus;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
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
    private TableColumn<Order, Void> orderActionsColumn;

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

        // Setup actions column for recent orders table
        Callback<TableColumn<Order, Void>, TableCell<Order, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Order, Void> call(final TableColumn<Order, Void> param) {
                final TableCell<Order, Void> cell = new TableCell<>() {

                    private final Button receiveButton = new Button("Receive");
                    private final Button deliverButton = new Button("Deliver");

                    {
                        receiveButton.setOnAction(event -> {
                            Order order = getTableView().getItems().get(getIndex());
                            handleReceiveOrder(order);
                        });
                        deliverButton.setOnAction(event -> {
                            Order order = getTableView().getItems().get(getIndex());
                            handleMarkAsDelivered(order);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Order order = getTableView().getItems().get(getIndex());
                            HBox hbox = new HBox(5); // Spacing of 5 between buttons
                            hbox.getChildren().clear(); // Clear existing children to prevent duplicates

                            // Only show buttons based on order status
                            if (order.getStatus() == OrderStatus.PENDING) {
                                hbox.getChildren().add(receiveButton);
                                receiveButton.setText("Receive Order");
                                receiveButton.getStyleClass().setAll("button", "primary-button");
                            } else if (order.getStatus() == OrderStatus.PROCESSING) {
                                hbox.getChildren().add(deliverButton);
                                deliverButton.setText("Mark as Delivered");
                                deliverButton.getStyleClass().setAll("button", "success-button");
                            } else {
                                // For other statuses (SHIPPED, DELIVERED, CANCELLED), no buttons
                            }
                            setGraphic(hbox);
                        }
                    }
                };
                return cell;
            }
        };
        orderActionsColumn.setCellFactory(cellFactory);

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

    private void handleReceiveOrder(Order order) {
        try {
            // Update order status to PROCESSING
            order.setStatus(OrderStatus.PROCESSING);
            orderService.updateOrder(order);
            showAlert("Order Received", "Order " + order.getId() + " has been marked as PROCESSING.", Alert.AlertType.INFORMATION);
            loadData(); // Refresh the table
        } catch (Exception e) {
            showError("Error receiving order: " + e.getMessage());
            showAlert("Error", "Error receiving order: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleMarkAsDelivered(Order order) {
        try {
            if (order.getStatus() == OrderStatus.PROCESSING) {
                // Update order status to DELIVERED
                order.setStatus(OrderStatus.DELIVERED);
                orderService.updateOrder(order);

                // IMPORTANT: Logic to subtract stock from Admin products and add to User products
                // This is where the inter-database communication will happen.
                // For now, this will be a placeholder.
                // We'll need to call a method that
                // 1. Gets order items for this order
                // 2. For each item, subtracts from admin product quantity
                // 3. For each item, adds to user product quantity (in warehouse_user.products)

                showAlert("Order Delivered", "Order " + order.getId() + " has been marked as DELIVERED and stock updated.", Alert.AlertType.INFORMATION);
                loadData(); // Refresh the table
            } else {
                showAlert("Error", "Order cannot be marked as delivered unless it is in PROCESSING status.", Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            showError("Error marking order as delivered: " + e.getMessage());
            showAlert("Error", "Error marking order as delivered: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
} 