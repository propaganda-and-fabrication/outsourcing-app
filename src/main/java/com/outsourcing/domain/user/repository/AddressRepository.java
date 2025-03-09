package com.outsourcing.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.outsourcing.domain.user.dto.AddressDto;
import com.outsourcing.domain.user.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

	@Query("select a from Address a inner join fetch a.customer c where c.id = :customerId")
	List<Address> findAllByCustomerId(@Param("customerId") Long customerId);

	@Query("select new com.outsourcing.domain.user.dto.AddressDto(" +
		"a.id, a.address, a.status) " +
		"from Address a join a.customer c " +
		"where c.id = :customerId")
	List<AddressDto> findAddressResponseByCustomerId(@Param("customerId") Long customerId);

	@Query("select new com.outsourcing.domain.user.dto.AddressDto(" +
			"a.id, a.address, a.status) " +
			"from Address a join a.customer c " +
			"where c.id = :customerId and a.status='ACTIVE'")
	Optional<AddressDto> findAddressByCustomer_IdAndStatus(@Param("customerId") Long customerId);

}
