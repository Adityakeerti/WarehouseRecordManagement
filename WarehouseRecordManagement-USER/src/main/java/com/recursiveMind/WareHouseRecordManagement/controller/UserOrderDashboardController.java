package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Product;
import com.recursiveMind.WareHouseRecordManagement.model.Order;
import com.recursiveMind.WareHouseRecordManagement.model.OrderItem;
import com.recursiveMind.WareHouseRecordManagement.service.ProductService;
import com.recursiveMind.WareHouseRecordManagement.service.OrderService;
import com.recursiveMind.WareHouseRecordManagement.WarehouseUserApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

@Controller
public class UserOrderDashboardController {

    @FXML
    private Label selectedWarehouseLabel;
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, String> productCodeColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, Integer> quantityColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private TableColumn<Product, Void> actionColumn;
    @FXML
    private Label totalCostLabel;
    @FXML
    private Button orderButton;

    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;

    private ObservableList<Product> availableProducts;
    private Map<Long, Integer> cartItems = new HashMap<>(); // Product ID -> Quantity
    private double totalCost = 0.0;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadProducts();
        updateTotalCost();

        orderButton.setOnAction(event -> handlePlaceOrder());

        // Placeholder for selected warehouse name (will be dynamic later)
        selectedWarehouseLabel.setText("Main Warehouse");
    }

    private void setupTableColumns() {
        productCodeColumn.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button addButton = new Button("Add to Cart");

            {
                addButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    addToCart(product);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(addButton);
                }
            }
        });
    }

    private void loadProducts() {
        try {
            // For now, load all products from the admin's database. Will be filtered by warehouse later.
            availableProducts = FXCollections.observableArrayList(productService.getAllProducts());
            productTable.setItems(availableProducts);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load products: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void addToCart(Product product) {
        if (product.getQuantity() > 0) {
            cartItems.put(product.getId(), cartItems.getOrDefault(product.getId(), 0) + 1);
            product.setQuantity(product.getQuantity() - 1); // Decrement available quantity
            productTable.refresh();
            updateTotalCost();
        } else {
            showAlert("Out of Stock", product.getName() + " is out of stock.", Alert.AlertType.WARNING);
        }
    }

    private void updateTotalCost() {
        totalCost = 0.0;
        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Product product = availableProducts.stream()
                                .filter(p -> p.getId().equals(entry.getKey()))
                                .findFirst().orElse(null);
            if (product != null) {
                totalCost += product.getPrice() * entry.getValue();
            }
        }
        totalCostLabel.setText(String.format("$%.2f", totalCost));
    }

    private void handlePlaceOrder() {
        if (cartItems.isEmpty()) {
            showAlert("No Items", "Your cart is empty. Please add items before placing an order.", Alert.AlertType.WARNING);
            return;
        }

        // Create a new order
        Order newOrder = new Order();
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStatus(com.recursiveMind.WareHouseRecordManagement.model.OrderStatus.PENDING);
        newOrder.setTotalAmount(totalCost);
        newOrder.setUser(WarehouseUserApp.getCurrentUser());
        // TODO: Set warehouse for the order (will be dynamic later)

        // Add order items
        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Product product = availableProducts.stream()
                                .filter(p -> p.getId().equals(entry.getKey()))
                                .findFirst().orElse(null);
            if (product != null) {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(product.getId());
                orderItem.setProductName(product.getName());
                orderItem.setQuantity(entry.getValue());
                orderItem.setUnitPrice(product.getPrice());
                newOrder.addOrderItem(orderItem);
            }
        }

        try {
            orderService.createOrder(newOrder);
            showAlert("Order Placed!", "Your order has been placed successfully.", Alert.AlertType.INFORMATION);
            cartItems.clear();
            updateTotalCost();
            loadProducts(); // Reload products to reflect updated quantities
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to place order: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 