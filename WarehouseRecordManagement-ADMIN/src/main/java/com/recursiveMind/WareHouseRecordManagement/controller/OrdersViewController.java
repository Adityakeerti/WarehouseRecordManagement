package com.recursiveMind.WareHouseRecordManagement.controller;

import com.recursiveMind.WareHouseRecordManagement.model.Order;
import com.recursiveMind.WareHouseRecordManagement.service.OrderService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class OrdersViewController extends BaseController {
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, String> orderIdColumn;
    @FXML private TableColumn<Order, LocalDateTime> dateColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, Double> totalAmountColumn;
    @FXML private TableColumn<Order, String> paymentStatusColumn;
    @FXML private TableColumn<Order, Void> actionsColumn;
    @FXML private Label totalOrdersLabel;
    @FXML private Label totalRevenueLabel;
    
    @Autowired
    private OrderService orderService;
    
    private ObservableList<Order> orders = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        setupTable();
        setupFilters();
        loadOrders();
    }
    
    private void setupTable() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        
        setupActionsColumn();
        
        ordersTable.setItems(orders);
    }
    
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(createActionButtonCellFactory());
    }
    
    private Callback<TableColumn<Order, Void>, TableCell<Order, Void>> createActionButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button viewButton = new Button("View");
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttons = new HBox(5, viewButton, editButton, deleteButton);
            
            {
                viewButton.setOnAction(event -> handleViewOrder(getTableRow().getItem()));
                editButton.setOnAction(event -> handleEditOrder(getTableRow().getItem()));
                deleteButton.setOnAction(event -> handleDeleteOrder(getTableRow().getItem()));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        };
    }
    
    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList(
            "All", "Pending", "Processing", "Completed", "Cancelled"
        ));
        statusFilter.getSelectionModel().selectFirst();
    }
    
    private void loadOrders() {
        List<Order> orderList = orderService.getAllOrders();
        orders.setAll(orderList);
        updateSummary();
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        String status = statusFilter.getValue();
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();
        
        List<Order> filteredOrders = orderService.getAllOrders().stream()
            .filter(order -> matchesSearch(order, searchText))
            .filter(order -> matchesStatus(order, status))
            .filter(order -> matchesDateRange(order, start, end))
            .toList();
        
        orders.setAll(filteredOrders);
        updateSummary();
    }
    
    private boolean matchesSearch(Order order, String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return true;
        }
        return order.getOrderId().toLowerCase().contains(searchText.toLowerCase());
    }
    
    private boolean matchesStatus(Order order, String status) {
        if (status == null || status.equals("All")) {
            return true;
        }
        return order.getStatus().toString().equalsIgnoreCase(status);
    }
    
    private boolean matchesDateRange(Order order, LocalDate start, LocalDate end) {
        if (start == null && end == null) {
            return true;
        }
        
        LocalDateTime orderDate = order.getOrderDate();
        LocalDate orderLocalDate = orderDate.toLocalDate();
        
        if (start != null && end != null) {
            return !orderLocalDate.isBefore(start) && !orderLocalDate.isAfter(end);
        } else if (start != null) {
            return !orderLocalDate.isBefore(start);
        } else {
            return !orderLocalDate.isAfter(end);
        }
    }
    
    @FXML
    private void handleClearFilters() {
        searchField.clear();
        statusFilter.getSelectionModel().selectFirst();
        startDate.setValue(null);
        endDate.setValue(null);
        loadOrders();
    }
    
    @FXML
    private void handleNewOrder() {
        showOrderDialog(null);
    }
    
    private void handleViewOrder(Order order) {
        showOrderDialog(order);
    }
    
    private void handleEditOrder(Order order) {
        showOrderDialog(order);
    }
    
    private void handleDeleteOrder(Order order) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Order");
        alert.setContentText("Are you sure you want to delete order " + order.getOrderId() + "?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                orderService.deleteOrder(order.getId());
                loadOrders();
            }
        });
    }
    
    private void showOrderDialog(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/order-dialog.fxml"));
            DialogPane dialogPane = loader.load();
            
            OrderDialogController controller = loader.getController();
            controller.setOrder(order);
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(order == null ? "New Order" : "Edit Order");
            
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Order updatedOrder = controller.getOrder();
                    orderService.createOrder(updatedOrder);
                    loadOrders();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading order dialog: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleExportToExcel() {
        // TODO: Implement Excel export
    }
    
    @FXML
    private void handlePrintReport() {
        // TODO: Implement report printing
    }
    
    @FXML
    private void handleSaveOrder() {
        try {
            Order order = getOrderFromForm();
            orderService.createOrder(order);
            showInfo("Order saved successfully");
            loadOrders();
        } catch (Exception e) {
            showError("Failed to save order: " + e.getMessage());
        }
    }
    
    private void updateSummary() {
        totalOrdersLabel.setText(String.valueOf(orders.size()));
        double totalRevenue = orders.stream()
            .mapToDouble(Order::getTotalAmount)
            .sum();
        totalRevenueLabel.setText(String.format("$%.2f", totalRevenue));
    }
    
    private Order getOrderFromForm() {
        // TODO: Implement form data collection
        return new Order();
    }
} 