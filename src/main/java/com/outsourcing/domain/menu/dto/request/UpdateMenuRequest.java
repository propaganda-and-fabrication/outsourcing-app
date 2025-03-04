package com.outsourcing.domain.menu.dto.request;

import com.outsourcing.domain.menu.enums.MenuStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMenuRequest {

	@NotBlank(message = "메뉴 이름은 필수입니다.")
	private String name;

	@NotNull(message = "가격은 필수입니다.")
	private Integer price;

	private String description;

	@NotBlank(message = "이미지 URL은 필수입니다.")
	private String imageUrl;

	private MenuStatus status;
}
