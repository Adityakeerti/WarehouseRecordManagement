package com.recursiveMind.WareHouseRecordManagement.repository;

import com.recursiveMind.WareHouseRecordManagement.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    // Removed findByStatus and countByStatus as 'status' does not exist
} 