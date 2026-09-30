package com.smartsociety.complaint.repository;

import com.smartsociety.complaint.entity.Complaint;
import com.smartsociety.complaint.entity.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findBySocietyIdOrderByCreatedAtDesc(Long societyId);

    List<Complaint> findBySocietyIdAndStatus(Long societyId, ComplaintStatus status);

    List<Complaint> findByResidentIdOrderByCreatedAtDesc(Long residentId);

    List<Complaint> findByAssignedStaffIdOrderByCreatedAtDesc(Long staffId);

    List<Complaint> findByAssignedStaffIdAndStatus(Long staffId, ComplaintStatus status);

    List<Complaint> findBySocietyIdAndEscalatedTrueOrderByCreatedAtDesc(Long societyId);

    List<Complaint> findBySlaDeadlineBeforeAndSlaBreachedFalseAndStatusNotIn(
            Instant now, Collection<ComplaintStatus> excludedStatuses
    );

    long countBySocietyId(Long societyId);

    long countBySocietyIdAndStatus(Long societyId, ComplaintStatus status);

    long countBySocietyIdAndSlaBreachedTrue(Long societyId);

    long countByAssignedStaffIdAndStatusIn(Long staffId, Collection<ComplaintStatus> statuses);

    @Query("SELECT c.category.name, COUNT(c) FROM Complaint c WHERE c.societyId = :societyId GROUP BY c.category.name")
    List<Object[]> countComplaintsByCategory(@Param("societyId") Long societyId);

    @Query("SELECT c.status, COUNT(c) FROM Complaint c WHERE c.societyId = :societyId GROUP BY c.status")
    List<Object[]> countComplaintsByStatus(@Param("societyId") Long societyId);
}
