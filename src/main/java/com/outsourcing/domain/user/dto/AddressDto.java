package com.outsourcing.domain.user.dto;

import com.outsourcing.domain.user.entity.Address;
import com.outsourcing.domain.user.enums.AddressStatus;

import lombok.Getter;

@Getter
public class AddressDto {

	private final Long id;
	private final String address;
	private final AddressStatus status;

	private AddressDto(Long id, String address, AddressStatus status) {
		this.id = id;
		this.address = address;
		this.status = status;
	}

	public static AddressDto from(Address address) {
		return new AddressDto(address.getId(), address.getAddress(), address.getStatus());
	}
}
