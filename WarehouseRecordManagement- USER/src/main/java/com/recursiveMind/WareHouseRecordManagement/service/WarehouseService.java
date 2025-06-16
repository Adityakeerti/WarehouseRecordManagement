package com.recursiveMind.WareHouseRecordManagement.service;

import com.recursiveMind.WareHouseRecordManagement.model.Warehouse;
import java.util.List;

public interface WarehouseService {
    List<Warehouse> getAllWarehouses();
    List<Warehouse> getActiveWarehouses();
    Warehouse getWarehouseById(Long id);
    boolean existsByName(String name);
} 