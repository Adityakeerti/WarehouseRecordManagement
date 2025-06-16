package com.recursiveMind.WareHouseRecordManagement.service.Impl;

import com.recursiveMind.WareHouseRecordManagement.model.StockMovement;
import com.recursiveMind.WareHouseRecordManagement.repository.StockMovementRepository;
import com.recursiveMind.WareHouseRecordManagement.service.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockMovementServiceImpl implements StockMovementService {
    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Override
    public StockMovement logMovement(StockMovement movement) {
        return stockMovementRepository.save(movement);
    }

    @Override
    public List<StockMovement> getAllMovements() {
        return stockMovementRepository.findAll();
    }

    @Override
    public List<StockMovement> getMovementsByProduct(Long productId) {
        return stockMovementRepository.findByProductId(productId);
    }

    @Override
    public List<StockMovement> getMovementsByType(StockMovement.MovementType type) {
        return stockMovementRepository.findByType(type);
    }

    @Override
    public List<StockMovement> getMovementsByUser(Long userId) {
        return stockMovementRepository.findByPerformedById(userId);
    }
} 