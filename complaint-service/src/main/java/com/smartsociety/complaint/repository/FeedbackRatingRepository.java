package com.smartsociety.complaint.repository;

import com.smartsociety.complaint.entity.FeedbackRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRatingRepository extends JpaRepository<FeedbackRating, Long> {
    Optional<FeedbackRating> findByComplaintId(Long complaintId);
    List<FeedbackRating> findByResidentId(Long residentId);
}
