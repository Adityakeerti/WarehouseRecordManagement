package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.WarehouseUserApp;
import com.recursiveMind.WareHouseRecordManagement.model.Order;
import com.recursiveMind.WareHouseRecordManagement.model.OrderItem;
import com.recursiveMind.WareHouseRecordManagement.model.Product;
import com.recursiveMind.WareHouseRecordManagement.model.User;
import com.recursiveMind.WareHouseRecordManagement.service.OrderService;
import com.recursiveMind.WareHouseRecordManagement.service.ProductService;
import com.recursiveMind.WareHouseRecordManagement.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class UserOrderDialogController {

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
    private TableView<OrderItem> orderItemsTable;
    @FXML
    private TableColumn<OrderItem, String> itemNameColumn;
    @FXML
    private TableColumn<OrderItem, Integer> itemQuantityColumn;
    @FXML
    private TableColumn<OrderItem, Double> itemPriceColumn;
    @FXML
    private TableColumn<OrderItem, Double> itemTotalColumn;
    @FXML
    private TableColumn<OrderItem, Void> removeColumn;

    @FXML
    private Label totalLabel;

    @Autowired
    @Qualifier("userOrderService")
    private OrderService orderService;

    @Autowired
    @Qualifier("userProductService")
    private ProductService productService;

    @Autowired
    private UserService userService;

    private ObservableList<OrderItem> currentOrderItems;

    @FXML
    public void initialize() {
        currentOrderItems = FXCollections.observableArrayList();
        orderItemsTable.setItems(currentOrderItems);

        setupProductTable();
        setupOrderItemsTable();
        loadAvailableProducts();
        updateTotalLabel();
    }

    private void setupProductTable() {
        productCodeColumn.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button addButton = new Button("Add");

            {
                addButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    handleAddProduct(product);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : addButton);
            }
        });
    }

    private void setupOrderItemsTable() {
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        itemTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        removeColumn.setCellFactory(param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");

            {
                removeButton.setOnAction(event -> {
                    OrderItem item = getTableView().getItems().get(getIndex());
                    handleRemoveOrderItem(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeButton);
            }
        });
    }

    private void loadAvailableProducts() {
        try {
            // Assuming we display products from a default warehouse or all products
            // For now, let's fetch all products
            productTable.setItems(FXCollections.observableArrayList(productService.getAllProducts()));
        } catch (Exception e) {
            showAlert("Error loading products: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void handleAddProduct(Product product) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Add Product to Order");
        dialog.setHeaderText("Enter quantity for " + product.getName());
        dialog.setContentText("Quantity:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(qtyStr -> {
            try {
                int quantity = Integer.parseInt(qtyStr);
                if (quantity <= 0) {
                    showAlert("Quantity must be positive.", Alert.AlertType.WARNING);
                    return;
                }
                if (quantity > product.getQuantity()) {
                    showAlert("Not enough stock. Available: " + product.getQuantity(), Alert.AlertType.WARNING);
                    return;
                }

                // Check if product already in order items
                Optional<OrderItem> existingItem = currentOrderItems.stream()
                        .filter(item -> item.getProductId().equals(product.getId()))
                        .findFirst();

                if (existingItem.isPresent()) {
                    OrderItem item = existingItem.get();
                    int newQuantity = item.getQuantity() + quantity;
                    if (newQuantity > product.getQuantity()) {
                        showAlert("Adding " + quantity + " would exceed available stock. Total available: " + product.getQuantity(), Alert.AlertType.WARNING);
                        return;
                    }
                    item.setQuantity(newQuantity);
                    item.calculateTotal();
                } else {
                    OrderItem orderItem = OrderItem.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .quantity(quantity)
                            .unitPrice(product.getPrice())
                            .build();
                    orderItem.calculateTotal();
                    currentOrderItems.add(orderItem);
                }
                orderItemsTable.refresh();
                updateTotalLabel();
            } catch (NumberFormatException e) {
                showAlert("Invalid quantity. Please enter a number.", Alert.AlertType.ERROR);
            }
        });
    }

    private void handleRemoveOrderItem(OrderItem item) {
        currentOrderItems.remove(item);
        orderItemsTable.refresh();
        updateTotalLabel();
    }

    private void updateTotalLabel() {
        double total = currentOrderItems.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
        totalLabel.setText(String.format("Total: %.2f", total));
    }

    public void initData(Map<Product, Integer> productsInCart) {
        productsInCart.forEach((product, quantity) -> {
            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .build();
            orderItem.calculateTotal();
            currentOrderItems.add(orderItem);
        });
        orderItemsTable.refresh();
        updateTotalLabel();
    }

    @FXML
    private void handlePlaceOrder() {
        if (currentOrderItems.isEmpty()) {
            showAlert("Order cannot be empty. Please add products.", Alert.AlertType.WARNING);
            return;
        }

        User currentUser = WarehouseUserApp.getCurrentUser();
        if (currentUser == null) {
            showAlert("No user logged in. Please log in first.", Alert.AlertType.ERROR);
            return;
        }
        // Fetch the managed user entity from the DB
        User managedUser = userService.getUserById(currentUser.getId());
        if (managedUser == null) {
            showAlert("User not found in database.", Alert.AlertType.ERROR);
            return;
        }
        // --- BEGIN VALIDATION ---
        StringBuilder validationErrors = new StringBuilder();
        int idx = 1;
        for (OrderItem item : currentOrderItems) {
            if (item.getProductId() == null) validationErrors.append("Item ").append(idx).append(": Product ID is missing!\n");
            if (item.getProductName() == null) validationErrors.append("Item ").append(idx).append(": Product name is missing!\n");
            if (item.getUnitPrice() == null) validationErrors.append("Item ").append(idx).append(": Product price is missing!\n");
            if (item.getQuantity() == null) validationErrors.append("Item ").append(idx).append(": Product quantity is missing!\n");
            idx++;
        }
        if (validationErrors.length() > 0) {
            showAlert("Order validation failed:\n" + validationErrors, Alert.AlertType.ERROR);
            return;
        }
        // --- END VALIDATION ---
        Order newOrder = Order.builder()
                .user(managedUser)
                .orderId(java.util.UUID.randomUUID().toString()) // Generate a unique order ID
                .orderDate(java.time.LocalDateTime.now())      // Set the current date and time
                .status(com.recursiveMind.WareHouseRecordManagement.model.OrderStatus.PENDING) // Set initial status
                .totalAmount(currentOrderItems.stream().mapToDouble(OrderItem::getTotalPrice).sum())
                .build();

        // Add order items to the order
        currentOrderItems.forEach(item -> newOrder.addOrderItem(item));

        // --- DEBUG LOGGING ---
        System.out.println("Order: " + newOrder);
        for (OrderItem item : newOrder.getOrderItems()) {
            System.out.println("OrderItem: " + item);
        }
        // --- END DEBUG LOGGING ---

        try {
            Order placedOrder = orderService.createOrder(newOrder);
            showAlert("Order placed successfully! Order ID: " + placedOrder.getOrderId(), Alert.AlertType.INFORMATION);
            
            // Refresh the orders view after placing a new order
            // This assumes the UserOrdersController can be accessed or has a public refresh method
            // For now, we'll just close the dialog. The main view's refresh button can be used.
            // In a real application, you'd likely have an event bus or a way to update the parent controller.
            
            Stage stage = (Stage) productTable.getScene().getWindow();
            stage.close();
            
            // After successful order, consider refreshing the main orders view.
            // This would typically involve a callback or event.
            // For now, I'll recommend the user to click the refresh button on the main orders screen.

        } catch (Exception e) {
            showAlert("Failed to place order: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Order Placement");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 