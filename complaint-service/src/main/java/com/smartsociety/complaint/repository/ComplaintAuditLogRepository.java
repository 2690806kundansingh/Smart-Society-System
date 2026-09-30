package com.smartsociety.complaint.repository;

import com.smartsociety.complaint.entity.ComplaintAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintAuditLogRepository extends JpaRepository<ComplaintAuditLog, Long> {
    List<ComplaintAuditLog> findByComplaintIdOrderByCreatedAtDesc(Long complaintId);
}
