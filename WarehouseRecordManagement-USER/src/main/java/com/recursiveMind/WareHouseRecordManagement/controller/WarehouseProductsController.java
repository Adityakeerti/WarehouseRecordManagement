package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Product;
import com.recursiveMind.WareHouseRecordManagement.service.ProductService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import com.recursiveMind.WareHouseRecordManagement.WarehouseUserApp;

@Controller
public class WarehouseProductsController {
    @FXML private Label warehouseNameLabel;
    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, Integer> quantityColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, Void> actionColumn;
    @FXML private Label totalAmountLabel;
    
    @Autowired
    private ProductService productService;
    
    private Map<Product, Integer> selectedProducts = new HashMap<>();
    
    private void setupTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button addButton = new Button("Add to Order");
            {
                addButton.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    handleAddToOrder(product);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : addButton);
            }
        });
    }
    
    private void loadProducts() {
        try {
            List<Product> products = productService.getAvailableProducts();
            productsTable.setItems(FXCollections.observableArrayList(products));
            updateTotalAmount();
        } catch (Exception e) {
            showAlert("Error", "Failed to load products: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void handleAddToOrder(Product product) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Add to Order");
        dialog.setHeaderText("Enter quantity for " + product.getName());
        dialog.setContentText("Quantity:");
        
        dialog.showAndWait().ifPresent(quantity -> {
            try {
                int qty = Integer.parseInt(quantity);
                if (qty <= 0) {
                    showAlert("Invalid Quantity", "Please enter a positive number", Alert.AlertType.ERROR);
                    return;
                }
                if (qty > product.getQuantity()) {
                    showAlert("Invalid Quantity", "Quantity exceeds available stock", Alert.AlertType.ERROR);
                    return;
                }
                
                selectedProducts.put(product, qty);
                updateTotalAmount();
                showAlert("Success", "Product added to order", Alert.AlertType.INFORMATION);
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number", Alert.AlertType.ERROR);
            }
        });
    }
    
    private void updateTotalAmount() {
        double total = selectedProducts.entrySet().stream()
            .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
            .sum();
        totalAmountLabel.setText(String.format("$%.2f", total));
    }
    
    @FXML
    private void handleRefresh() {
        loadProducts();
    }
    
    @FXML
    private void handlePlaceOrder() {
        if (selectedProducts.isEmpty()) {
            showAlert("No Products Selected", "Please add products to your order", Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/dialogs/user-order-dialog.fxml"));
            fxmlLoader.setControllerFactory(WarehouseUserApp.getSpringContext()::getBean);
            Parent parent = fxmlLoader.load();
            UserOrderDialogController controller = fxmlLoader.getController();
            controller.initData(selectedProducts);
            Stage stage = new Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setTitle("Place New Order");
            stage.setScene(new javafx.scene.Scene(parent));
            stage.showAndWait();
            selectedProducts.clear();
            loadProducts();
        } catch (Exception e) {
            showAlert("Order Placement Error", "Failed to open order dialog: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
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