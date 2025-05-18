package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Order;
import com.recursiveMind.WareHouseRecordManagement.model.OrderItem;
import com.recursiveMind.WareHouseRecordManagement.model.Product;
import com.recursiveMind.WareHouseRecordManagement.model.OrderStatus;
import com.recursiveMind.WareHouseRecordManagement.service.OrderService;
import com.recursiveMind.WareHouseRecordManagement.service.ProductService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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
    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField quantityField;
    @FXML private TableView<OrderItem> itemsTable;
    @FXML private TableColumn<OrderItem, String> productColumn;
    @FXML private TableColumn<OrderItem, Integer> quantityColumn;
    @FXML private TableColumn<OrderItem, Double> unitPriceColumn;
    @FXML private TableColumn<OrderItem, Double> totalPriceColumn;
    @FXML private TableColumn<OrderItem, Void> actionsColumn;
    @FXML private TextArea shippingAddressArea;
    @FXML private TextArea billingAddressArea;
    @FXML private TextArea notesArea;
    @FXML private Label totalAmountLabel;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ProductService productService;
    
    private Order order;
    private ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        setupComboBoxes();
        setupTable();
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
        
        productComboBox.setItems(FXCollections.observableArrayList(productService.getAllProducts()));
        productComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getSku() + ")");
                }
            }
        });
    }
    
    private void setupTable() {
        productColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getProduct().getName()));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        
        setupActionsColumn();
        
        itemsTable.setItems(orderItems);
    }
    
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(createActionButtonCellFactory());
    }
    
    private Callback<TableColumn<OrderItem, Void>, TableCell<OrderItem, Void>> createActionButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");
            
            {
                removeButton.setOnAction(event -> {
                    OrderItem item = getTableRow().getItem();
                    if (item != null) {
                        orderItems.remove(item);
                        updateTotalAmount();
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeButton);
            }
        };
    }
    
    private void setupValidation() {
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                quantityField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    public void setOrder(Order order) {
        this.order = order;
        if (order != null) {
            orderIdLabel.setText(order.getOrderId());
            orderDatePicker.setValue(order.getOrderDate().toLocalDate());
            statusComboBox.setValue(order.getStatus().name());
            paymentStatusComboBox.setValue(order.getPaymentStatus());
            paymentMethodComboBox.setValue(order.getPaymentMethod());
            shippingAddressArea.setText(order.getShippingAddress());
            billingAddressArea.setText(order.getBillingAddress());
            notesArea.setText(order.getNotes());
            
            orderItems.setAll(order.getOrderItems());
            updateTotalAmount();
        } else {
            orderIdLabel.setText("New Order");
            orderDatePicker.setValue(LocalDate.now());
            statusComboBox.setValue("Pending");
            paymentStatusComboBox.setValue("Pending");
            orderItems.clear();
            updateTotalAmount();
        }
    }
    
    @FXML
    private void handleAddItem() {
        Product product = productComboBox.getValue();
        String quantityText = quantityField.getText();
        
        if (product == null || quantityText.isEmpty()) {
            showError("Please select a product and enter quantity");
            return;
        }
        
        int quantity = Integer.parseInt(quantityText);
        if (quantity <= 0) {
            showError("Quantity must be greater than 0");
            return;
        }
        
        if (quantity > product.getQuantity()) {
            showError("Insufficient stock available");
            return;
        }
        
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(product.getPrice());
        item.calculateTotal();
        
        orderItems.add(item);
        updateTotalAmount();
        
        productComboBox.setValue(null);
        quantityField.clear();
    }
    
    private void updateTotalAmount() {
        double total = orderItems.stream()
            .mapToDouble(OrderItem::getTotalPrice)
            .sum();
        totalAmountLabel.setText(String.format("$%.2f", total));
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
        
        order.setOrderDate(orderDatePicker.getValue().atStartOfDay());
        order.setStatus(OrderStatus.valueOf(statusComboBox.getValue()));
        order.setPaymentStatus(paymentStatusComboBox.getValue());
        order.setPaymentMethod(paymentMethodComboBox.getValue());
        order.setShippingAddress(shippingAddressArea.getText());
        order.setBillingAddress(billingAddressArea.getText());
        order.setNotes(notesArea.getText());
        
        order.getOrderItems().clear();
        order.getOrderItems().addAll(orderItems);
        order.calculateTotal();
        
        return order;
    }
    
    @FXML
    private void handleStatusChange() {
        String selectedStatus = statusComboBox.getValue();
        if (selectedStatus != null) {
            order.setStatus(OrderStatus.valueOf(selectedStatus));
        }
    }
    
    private void populateStatusComboBox() {
        statusComboBox.getItems().addAll(
            OrderStatus.PENDING.name(),
            OrderStatus.PROCESSING.name(),
            OrderStatus.SHIPPED.name(),
            OrderStatus.DELIVERED.name(),
            OrderStatus.CANCELLED.name()
        );
    }
} 