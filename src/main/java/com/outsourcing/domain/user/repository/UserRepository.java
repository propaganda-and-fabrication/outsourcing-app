package com.outsourcing.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.outsourcing.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query(value = "select u from User u where u.email = :email and u.deletedAt is null")
	Optional<User> findByEmailAndDeletedAt(String email);
}
