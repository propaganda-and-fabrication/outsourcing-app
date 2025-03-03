package com.outsourcing.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.outsourcing.domain.user.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	// 탈퇴 유저 포함 이메일 검색
	Optional<Customer> findByEmail(String email);

	// 탈퇴 유저 제외 이메일 검색
	@Query(value = "select c from Customer c where c.email = :email and c.deletedAt is null")
	Optional<Customer> findByEmailAndDeletedAt(String email);

	@Modifying
	@Query(value = "update Customer c set c.deletedAt = current_timestamp()")
	void softDeleteCustomer();

	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(String phoneNumber);

	boolean existsByNickname(String nickname);
}
