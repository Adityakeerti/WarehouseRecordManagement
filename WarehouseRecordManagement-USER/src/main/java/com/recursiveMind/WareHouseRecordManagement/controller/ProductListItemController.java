package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;

public class ProductListItemController {

    @FXML private Label productNameLabel;
    @FXML private Label productCategoryLabel;
    @FXML private Label productDescriptionLabel;
    @FXML private Label productPriceLabel;
    @FXML private Label quantityLabel;
    @FXML private Label itemTotalLabel;
    @FXML private Button decreaseButton;
    @FXML private Button increaseButton;

    private Product product;
    private int currentQuantity = 0;
    private Consumer<BigDecimal> onTotalChange;

    public void setProduct(Product product) {
        this.product = product;
        productNameLabel.setText(product.getName());
        productCategoryLabel.setText("Category: " + product.getCategory());
        productDescriptionLabel.setText("Description: " + product.getDescription());
        productPriceLabel.setText(formatCurrency(BigDecimal.valueOf(product.getPrice())));
        updateQuantityDisplay();
    }

    public void setOnTotalChange(Consumer<BigDecimal> onTotalChange) {
        this.onTotalChange = onTotalChange;
    }

    @FXML
    private void handleIncreaseQuantity() {
        if (currentQuantity < product.getQuantity()) { // Assume product.getQuantity() is max available stock
            currentQuantity++;
            updateQuantityDisplay();
            notifyTotalChange();
        }
    }

    @FXML
    private void handleDecreaseQuantity() {
        if (currentQuantity > 0) {
            currentQuantity--;
            updateQuantityDisplay();
            notifyTotalChange();
        }
    }

    private void updateQuantityDisplay() {
        quantityLabel.setText(String.valueOf(currentQuantity));
        BigDecimal itemTotal = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(currentQuantity));
        itemTotalLabel.setText("Item Total: " + formatCurrency(itemTotal));
        
        // Disable decrease button if quantity is 0
        decreaseButton.setDisable(currentQuantity == 0);
        // Disable increase button if quantity reaches product's available quantity
        increaseButton.setDisable(currentQuantity >= product.getQuantity());
    }

    private void notifyTotalChange() {
        if (onTotalChange != null) {
            onTotalChange.accept(BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(currentQuantity)));
        }
    }

    private String formatCurrency(BigDecimal amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN")); // For Indian Rupee
        return currencyFormat.format(amount);
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public Product getProduct() {
        return product;
    }
} 