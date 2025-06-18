package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Product;
import com.recursiveMind.WareHouseRecordManagement.model.Order;
import com.recursiveMind.WareHouseRecordManagement.model.OrderItem;
import com.recursiveMind.WareHouseRecordManagement.model.OrderStatus;
import com.recursiveMind.WareHouseRecordManagement.WarehouseUserApp;
import com.recursiveMind.WareHouseRecordManagement.service.ProductService;
import com.recursiveMind.WareHouseRecordManagement.service.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Controller
public class AdminWarehouseProductsController {

    @FXML
    private ListView<Product> productList;
    @FXML
    private Label overallTotalLabel;

    @Autowired
    private ProductService productService;

    @Autowired
    @Qualifier("userOrderService")
    private OrderService userService;

    @Autowired
    @Qualifier("adminOrderService")
    private com.recursiveMind.WareHouseRecordManagement.service.OrderService adminService;

    private BigDecimal overallTotal = BigDecimal.ZERO;
    private Map<Product, BigDecimal> itemTotals = new HashMap<>();
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    @FXML
    public void initialize() {
        productList.setCellFactory(lv -> new ListCell<Product>() {
            private ProductListItemController controller;
            private javafx.scene.Node graphic;

            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setGraphic(null);
                } else {
                    if (controller == null) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/views/product-list-item.fxml"));
                        try {
                            graphic = loader.load();
                            controller = loader.getController();
                            controller.setOnTotalChange(this::updateOverallTotal);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    controller.setProduct(product);
                    setGraphic(graphic);
                }
            }

            private void updateOverallTotal(BigDecimal itemNewTotal) {
                BigDecimal oldItemTotal = itemTotals.getOrDefault(controller.getProduct(), BigDecimal.ZERO);
                itemTotals.put(controller.getProduct(), itemNewTotal);
                overallTotal = overallTotal.subtract(oldItemTotal).add(itemNewTotal);
                overallTotalLabel.setText(currencyFormat.format(overallTotal));
            }
        });

        loadProducts();
    }

    private void loadProducts() {
        try {
            ObservableList<Product> products = FXCollections.observableArrayList(productService.getAllProducts());
            productList.setItems(products);
            overallTotal = BigDecimal.ZERO;
            overallTotalLabel.setText(currencyFormat.format(overallTotal));
            itemTotals.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePlaceOrder() {
        if (overallTotal.compareTo(BigDecimal.ZERO) <= 0) {
            showAlert("Order Error", "Please select at least one item to place an order.", AlertType.WARNING);
            return;
        }

        List<OrderItem> selectedOrderItems = new ArrayList<>();
        for (Product product : productList.getItems()) {
            // We need to retrieve the actual controller instance for each item to get its current quantity.
            // This is a common challenge with ListView and custom cells. 
            // For now, we'll assume the quantity is directly accessible or that itemTotals correctly reflects it.
            // A more robust solution might involve passing a map of product to quantity back from the cell factory.
            BigDecimal itemTotal = itemTotals.getOrDefault(product, BigDecimal.ZERO);
            if (itemTotal.compareTo(BigDecimal.ZERO) > 0) {
                // Calculate quantity from itemTotal and product price, as currentQuantity isn't directly exposed here
                int quantity = itemTotal.divide(BigDecimal.valueOf(product.getPrice()), BigDecimal.ROUND_HALF_UP).intValue();
                if (quantity > 0) {
                    OrderItem orderItem = OrderItem.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .quantity(quantity)
                            .unitPrice(product.getPrice())
                            .totalPrice(BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantity)).doubleValue())
                            .build();
                    selectedOrderItems.add(orderItem);
                }
            }
        }

        if (selectedOrderItems.isEmpty()) {
            showAlert("Order Error", "No items selected for order.", AlertType.WARNING);
            return;
        }

        try {
            // Create order for user database
            Order userOrder = Order.builder()
                    .user(WarehouseUserApp.getCurrentUser())
                    .orderDate(LocalDateTime.now())
                    .status(OrderStatus.PENDING)
                    .totalAmount(overallTotal.doubleValue()) // Convert BigDecimal to Double
                    .orderItems(selectedOrderItems)
                    .build();
            // Set order for each order item
            selectedOrderItems.forEach(item -> item.setOrder(userOrder));
            userService.createOrder(userOrder);
            // Create order for admin database (can be a simplified version or same model)
            List<OrderItem> adminOrderItems = new ArrayList<>();
            for (OrderItem originalItem : userOrder.getOrderItems()) {
                OrderItem adminOrderItem = OrderItem.builder()
                        .productId(originalItem.getProductId())
                        .productName(originalItem.getProductName())
                        .quantity(originalItem.getQuantity())
                        .unitPrice(originalItem.getUnitPrice())
                        .totalPrice(originalItem.getTotalPrice())
                        .notes(originalItem.getNotes())
                        .build();
                adminOrderItems.add(adminOrderItem);
            }
            Order adminOrder = Order.builder()
                    .orderId(userOrder.getOrderId()) // Reuse the same order ID
                    .user(null) // Set user to null for admin order to bypass foreign key constraint
                    .orderDate(userOrder.getOrderDate())
                    .status(userOrder.getStatus())
                    .totalAmount(userOrder.getTotalAmount())
                    .orderItems(adminOrderItems)
                    .build();
            adminOrder.getOrderItems().forEach(item -> item.setOrder(adminOrder));
            adminService.createOrder(adminOrder);
            showAlert("Order Placed", "Your order has been placed successfully!", AlertType.INFORMATION);
            Stage stage = (Stage) productList.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            showAlert("Order Error", "Failed to place order: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) productList.getScene().getWindow();
        stage.close();
    }
} 