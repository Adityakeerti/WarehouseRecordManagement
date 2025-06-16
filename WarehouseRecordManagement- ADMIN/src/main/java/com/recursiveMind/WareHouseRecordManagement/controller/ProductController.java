package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Product;
import com.recursiveMind.WareHouseRecordManagement.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @FXML
    private TableView<Product> productTable;
    
    @FXML
    private TableColumn<Product, String> productCodeColumn;
    
    @FXML
    private TableColumn<Product, String> nameColumn;
    
    @FXML
    private TableColumn<Product, String> categoryColumn;
    
    @FXML
    private TableColumn<Product, Integer> quantityColumn;
    
    @FXML
    private TableColumn<Product, Double> priceColumn;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private TextField productCodeField;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField categoryField;
    
    @FXML
    private TextField quantityField;
    
    @FXML
    private TextField priceField;
    
    @FXML
    private TextArea descriptionField;
    
    @FXML
    private TextField minStockField;
    
    @FXML
    private TextField maxStockField;
    
    @FXML
    private TextField skuField;
    
    @FXML
    private TextField locationField;

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadProducts();
        setupSearchListener();
    }

    private void setupTableColumns() {
        productCodeColumn.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void loadProducts() {
        productList.clear();
        productList.addAll(productService.getAllProducts());
        productTable.setItems(productList);
    }

    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                loadProducts();
            } else {
                List<Product> searchResults = productService.searchProducts(newValue);
                productList.clear();
                productList.addAll(searchResults);
            }
        });
    }

    @FXML
    private void handleAddProduct() {
        try {
            Product product = new Product();
            product.setProductCode(productCodeField.getText());
            product.setName(nameField.getText());
            product.setCategory(categoryField.getText());
            product.setQuantity(Integer.parseInt(quantityField.getText()));
            product.setPrice(Double.parseDouble(priceField.getText()));
            product.setDescription(descriptionField.getText());
            product.setMinStockLevel(Integer.parseInt(minStockField.getText()));
            product.setMaxStockLevel(Integer.parseInt(maxStockField.getText()));
            product.setSku(skuField.getText());
            product.setLocation(locationField.getText());

            productService.saveProduct(product);
            clearFields();
            loadProducts();
            showAlert("Success", "Product added successfully!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Error", "Failed to add product: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdateProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                // Fetch the product from the DB to ensure it's managed
                Product productToUpdate = productService.getProductById(selectedProduct.getId()).orElseThrow(() ->
                    new RuntimeException("Product not found with id: " + selectedProduct.getId()));

                productToUpdate.setName(nameField.getText());
                productToUpdate.setCategory(categoryField.getText());
                productToUpdate.setQuantity(Integer.parseInt(quantityField.getText()));
                productToUpdate.setPrice(Double.parseDouble(priceField.getText()));
                productToUpdate.setDescription(descriptionField.getText());
                productToUpdate.setMinStockLevel(Integer.parseInt(minStockField.getText()));
                productToUpdate.setMaxStockLevel(Integer.parseInt(maxStockField.getText()));
                productToUpdate.setLocation(locationField.getText());

                productService.saveProduct(productToUpdate);
                clearFields();
                loadProducts();
                showAlert("Success", "Product updated successfully!", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Error", "Failed to update product: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Warning", "Please select a product to update", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleDeleteProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Delete Product");
            alert.setContentText("Are you sure you want to delete this product?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                productService.deleteProduct(selectedProduct.getId());
                loadProducts();
                clearFields();
                showAlert("Success", "Product deleted successfully!", Alert.AlertType.INFORMATION);
            }
        } else {
            showAlert("Warning", "Please select a product to delete", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleTableSelection() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            productCodeField.setText(selectedProduct.getProductCode());
            nameField.setText(selectedProduct.getName());
            categoryField.setText(selectedProduct.getCategory());
            quantityField.setText(String.valueOf(selectedProduct.getQuantity()));
            priceField.setText(String.valueOf(selectedProduct.getPrice()));
            descriptionField.setText(selectedProduct.getDescription());
            minStockField.setText(String.valueOf(selectedProduct.getMinStockLevel()));
            maxStockField.setText(String.valueOf(selectedProduct.getMaxStockLevel()));
            skuField.setText(selectedProduct.getSku());
            locationField.setText(selectedProduct.getLocation());
        }
    }

    private void clearFields() {
        productCodeField.clear();
        nameField.clear();
        categoryField.clear();
        quantityField.clear();
        priceField.clear();
        descriptionField.clear();
        minStockField.clear();
        maxStockField.clear();
        skuField.clear();
        locationField.clear();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
