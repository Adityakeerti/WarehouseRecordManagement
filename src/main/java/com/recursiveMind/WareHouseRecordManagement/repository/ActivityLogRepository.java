package com.recursiveMind.WareHouseRecordManagement.repository;

import com.recursiveMind.WareHouseRecordManagement.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    // Additional query methods can be added here if needed
} 