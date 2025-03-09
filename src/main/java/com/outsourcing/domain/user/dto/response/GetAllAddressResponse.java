package com.outsourcing.domain.user.dto.response;

import java.util.List;

import com.outsourcing.domain.user.dto.AddressDto;

import lombok.Getter;

@Getter
public class GetAllAddressResponse {

	private List<AddressDto> addressResponses;

	public GetAllAddressResponse(List<AddressDto> addressResponses) {
		this.addressResponses = addressResponses;
	}

	public static GetAllAddressResponse of(List<AddressDto> addressResponses) {
		return new GetAllAddressResponse(addressResponses);
	}
}
