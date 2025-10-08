package org.geo7.model.repository;

import org.geo7.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByActiveTrue();

    List<User> findByRole(String role);

    @Query("SELECT u FROM User u WHERE u.active = true AND u.role = :role")
    List<User> findActiveUsersByRole(@Param("role") String role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
    long countActiveUsers();
}
