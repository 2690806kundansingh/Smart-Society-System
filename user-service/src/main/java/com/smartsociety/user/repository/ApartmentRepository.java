package com.smartsociety.user.repository;

import com.smartsociety.user.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    Optional<Apartment> findBySocietyIdAndBlockNameAndFlatNumber(Long societyId, String blockName, String flatNumber);
    List<Apartment> findBySocietyId(Long societyId);
}
