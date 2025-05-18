package com.recursiveMind.WareHouseRecordManagement.service.Impl;

import com.recursiveMind.WareHouseRecordManagement.model.Supplier;
import com.recursiveMind.WareHouseRecordManagement.repository.SupplierRepository;
import com.recursiveMind.WareHouseRecordManagement.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {
    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier updateSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Override
    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }

    @Override
    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id).orElse(null);
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public List<Supplier> getActiveSuppliers() {
        return supplierRepository.findAll().stream().filter(Supplier::isActive).toList();
    }

    @Override
    public long getTotalSuppliers() {
        return supplierRepository.count();
    }

    @Override
    public long getActiveSuppliersCount() {
        return getActiveSuppliers().size();
    }

    @Override
    public long getPendingDeliveries() {
        // TODO: Implement logic for pending deliveries
        return 0;
    }

    @Override
    public void updateSupplierStatus(Long supplierId, boolean active) {
        Supplier supplier = getSupplierById(supplierId);
        if (supplier != null) {
            supplier.setActive(active);
            supplierRepository.save(supplier);
        }
    }
} 