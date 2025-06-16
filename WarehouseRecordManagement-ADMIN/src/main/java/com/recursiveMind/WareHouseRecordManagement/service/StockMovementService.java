package com.recursiveMind.WareHouseRecordManagement.service;

import com.recursiveMind.WareHouseRecordManagement.model.StockMovement;
import java.util.List;

public interface StockMovementService {
    StockMovement logMovement(StockMovement movement);
    List<StockMovement> getAllMovements();
    List<StockMovement> getMovementsByProduct(Long productId);
    List<StockMovement> getMovementsByType(StockMovement.MovementType type);
    List<StockMovement> getMovementsByUser(Long userId);
} 