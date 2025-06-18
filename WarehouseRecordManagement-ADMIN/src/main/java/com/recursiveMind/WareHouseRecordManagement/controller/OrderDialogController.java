package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Order;
import com.recursiveMind.WareHouseRecordManagement.model.OrderStatus;
import com.recursiveMind.WareHouseRecordManagement.service.OrderService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class OrderDialogController extends BaseController {
    
    @FXML private Label orderIdLabel;
    @FXML private DatePicker orderDatePicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<String> paymentStatusComboBox;
    @FXML private ComboBox<String> paymentMethodComboBox;
    @FXML private TextArea shippingAddressArea;
    @FXML private TextArea billingAddressArea;
    @FXML private TextArea notesArea;
    @FXML private TextField totalAmountField;
    @FXML private Label dialogTitle;
    
    @Autowired
    private OrderService orderService;
    
    private Order order;
    
    @FXML
    public void initialize() {
        setupComboBoxes();
        setupValidation();
    }
    
    private void setupComboBoxes() {
        statusComboBox.setItems(FXCollections.observableArrayList(
            "Pending", "Processing", "Completed", "Cancelled"
        ));
        
        paymentStatusComboBox.setItems(FXCollections.observableArrayList(
            "Pending", "Paid", "Failed", "Refunded"
        ));
        
        paymentMethodComboBox.setItems(FXCollections.observableArrayList(
            "Cash", "Credit Card", "Debit Card", "Bank Transfer", "PayPal"
        ));
    }
    
    private void setupValidation() {
        totalAmountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                totalAmountField.setText(newVal.replaceAll("[^\\d.]", ""));
            }
        });
    }
    
    public void setOrder(Order order) {
        this.order = order;
        if (order != null) {
            dialogTitle.setText("Edit Order");
            orderIdLabel.setText(order.getOrderId());
            orderDatePicker.setValue(order.getOrderDate().toLocalDate());
            statusComboBox.setValue(order.getStatus().name());
            paymentStatusComboBox.setValue(order.getPaymentStatus());
            paymentMethodComboBox.setValue(order.getPaymentMethod());
            shippingAddressArea.setText(order.getShippingAddress());
            billingAddressArea.setText(order.getBillingAddress());
            notesArea.setText(order.getNotes());
            totalAmountField.setText(String.valueOf(order.getTotalAmount()));
        } else {
            dialogTitle.setText("New Order");
            orderIdLabel.setText("New Order");
            orderDatePicker.setValue(LocalDate.now());
            statusComboBox.setValue("Pending");
            paymentStatusComboBox.setValue("Pending");
            paymentMethodComboBox.setValue(null);
            shippingAddressArea.clear();
            billingAddressArea.clear();
            notesArea.clear();
            totalAmountField.clear();
        }
    }
    
    @FXML
    private void handleAddItem() {
        showError("Adding order items directly is not supported in this admin view.");
    }
    
    protected void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public Order getOrder() {
        if (order == null) {
            order = new Order();
        }
        
        if (!"New Order".equals(orderIdLabel.getText())) {
            order.setOrderId(orderIdLabel.getText());
        }
        
        order.setOrderDate(orderDatePicker.getValue().atStartOfDay());
        order.setStatus(OrderStatus.valueOf(statusComboBox.getValue()));
        order.setPaymentStatus(paymentStatusComboBox.getValue());
        order.setPaymentMethod(paymentMethodComboBox.getValue());
        order.setShippingAddress(shippingAddressArea.getText());
        order.setBillingAddress(billingAddressArea.getText());
        order.setNotes(notesArea.getText());
        
        try {
            order.setTotalAmount(Double.parseDouble(totalAmountField.getText()));
        } catch (NumberFormatException e) {
            showError("Invalid Total Amount. Please enter a valid number.");
            return null;
        }
        
        return order;
    }
    
    @FXML
    private void handleStatusChange() {
        String selectedStatus = statusComboBox.getValue();
        if (selectedStatus != null && order != null) {
            order.setStatus(OrderStatus.valueOf(selectedStatus));
        }
    }
    
    private void populateStatusComboBox() {
        // No longer needed as setupComboBoxes directly sets items from an array
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) orderIdLabel.getScene().getWindow();
        stage.close();
    }
} 