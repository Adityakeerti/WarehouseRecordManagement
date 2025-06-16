package com.recursiveMind.WareHouseRecordManagement.service;

import com.recursiveMind.WareHouseRecordManagement.model.Supplier;
import java.util.List;

public interface SupplierService {
    Supplier createSupplier(Supplier supplier);
    Supplier updateSupplier(Supplier supplier);
    void deleteSupplier(Long id);
    Supplier getSupplierById(Long id);
    List<Supplier> getAllSuppliers();
    List<Supplier> getActiveSuppliers();
    long getTotalSuppliers();
    long getActiveSuppliersCount();
    long getPendingDeliveries();
    void updateSupplierStatus(Long supplierId, boolean active);
} 