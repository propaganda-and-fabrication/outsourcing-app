package com.outsourcing.domain.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.outsourcing.domain.user.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

	List<Address> findAllByCustomerId(Long customerId);

}
