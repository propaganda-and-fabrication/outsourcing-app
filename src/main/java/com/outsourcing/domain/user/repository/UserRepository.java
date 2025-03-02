package com.outsourcing.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.outsourcing.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	// 탈퇴 유저 포함 이메일 검색
	Optional<User> findByEmail(String email);

	// 탈퇴 유저 제외 이메일 검색
	@Query(value = "select u from User u where u.email = :email and u.deletedAt is null")
	Optional<User> findByEmailAndDeletedAt(String email);

	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(String phoneNumber);
}
