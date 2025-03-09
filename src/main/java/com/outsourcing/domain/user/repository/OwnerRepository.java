package com.outsourcing.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.outsourcing.domain.user.entity.Owner;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

	// 삭제된 유저까지 전체 조회
	Optional<Owner> findByEmail(String email);

	// 탈퇴 유저 제외 이메일 검색
	@Query(value = "select o from Owner o where o.email = :email and o.deletedAt is null")
	Optional<Owner> findByEmailAndDeletedAt(String email);

	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(String phoneNumber);
}
