package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Product;
import com.recursiveMind.WareHouseRecordManagement.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductInventoryController {
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Long> idColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, String> categoryColumn;
    @FXML private TableColumn<Product, Integer> quantityColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, String> lowStockColumn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    @FXML private Label infoLabel;

    @Autowired
    private ProductService productService;

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        lowStockColumn.setCellValueFactory(cellData -> {
            int quantity = cellData.getValue().getQuantity();
            int minStock = cellData.getValue().getMinStockLevel();
            return new ReadOnlyStringWrapper(quantity <= minStock ? "Yes" : "No");
        });
        refreshProductTable();
    }

    @FXML
    private void refreshProductTable() {
        List<Product> products = productService.getAllProducts();
        productList.setAll(products);
        productTable.setItems(productList);
        infoLabel.setText("Loaded " + products.size() + " products.");
    }

    @FXML
    private void handleAddProduct() {
        // TODO: Show dialog to add product
        infoLabel.setText("Add Product clicked (implement dialog)");
    }

    @FXML
    private void handleEditProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            infoLabel.setText("Select a product to edit.");
            return;
        }
        // TODO: Show dialog to edit product
        infoLabel.setText("Edit Product clicked (implement dialog)");
    }

    @FXML
    private void handleDeleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            infoLabel.setText("Select a product to delete.");
            return;
        }
        productService.deleteProduct(selected.getId());
        refreshProductTable();
        infoLabel.setText("Product deleted.");
    }

    @FXML
    private void handleRefresh() {
        refreshProductTable();
    }

    private void checkLowStock(Product product) {
        if (product.getQuantity() <= product.getMinStockLevel()) {
            showAlert("Low Stock Alert", 
                "Product " + product.getName() + " is running low on stock. Current quantity: " + product.getQuantity(),
                Alert.AlertType.WARNING);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 