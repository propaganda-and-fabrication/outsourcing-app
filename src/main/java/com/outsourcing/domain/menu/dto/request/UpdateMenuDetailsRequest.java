package com.outsourcing.domain.menu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMenuDetailsRequest {

	private String name;

	private Integer price;

	private String description;
}
