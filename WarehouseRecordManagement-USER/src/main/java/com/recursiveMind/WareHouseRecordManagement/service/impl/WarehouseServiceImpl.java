package com.recursiveMind.WareHouseRecordManagement.service.impl;

import com.recursiveMind.WareHouseRecordManagement.model.Warehouse;
import com.recursiveMind.WareHouseRecordManagement.repository.WarehouseRepository;
import com.recursiveMind.WareHouseRecordManagement.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {
    
    @Autowired
    private WarehouseRepository warehouseRepository;
    
    @Override
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }
    
    @Override
    public List<Warehouse> getActiveWarehouses() {
        return warehouseRepository.findByIsActiveTrue();
    }
    
    @Override
    public Warehouse getWarehouseById(Long id) {
        return warehouseRepository.findById(id).orElse(null);
    }
    
    @Override
    public boolean existsByName(String name) {
        return warehouseRepository.existsByName(name);
    }
} 