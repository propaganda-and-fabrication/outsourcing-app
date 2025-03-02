package com.outsourcing.domain.user.dto.response;

import java.util.List;

import com.outsourcing.domain.user.entity.Address;
import com.outsourcing.domain.user.enums.AddressStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetAllAddressResponse {

	private final List<AddressResponse> addresses;

	public static GetAllAddressResponse of(List<Address> addresses) {
		return new GetAllAddressResponse(
			addresses.stream()
				.map(AddressResponse::from)
				.toList()
		);
	}

	@Getter
	@RequiredArgsConstructor
	public static class AddressResponse {
		private final String address;
		private final AddressStatus status;

		private static AddressResponse from(Address address) {
			return new AddressResponse(address.getAddress(), address.getStatus());
		}
	}
}
