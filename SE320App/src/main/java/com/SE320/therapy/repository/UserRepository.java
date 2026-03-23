package com.SE320.therapy.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SE320.therapy.objects.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("select u.passwordHash from User u where u.email = :email")
    Optional<String> findPasswordByEmail(@Param("email") String email);

    Optional<User> findByEmail(String email);
}
