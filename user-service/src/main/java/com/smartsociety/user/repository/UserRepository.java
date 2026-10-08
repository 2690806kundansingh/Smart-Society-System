package com.smartsociety.user.repository;

import com.smartsociety.user.entity.Department;
import com.smartsociety.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    List<User> findBySocietyId(Long societyId);

    List<User> findBySocietyIdAndDepartment(Long societyId, Department department);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.societyId = :societyId")
    List<User> findByRoleNameAndSocietyId(@Param("roleName") String roleName, @Param("societyId") Long societyId);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
}
