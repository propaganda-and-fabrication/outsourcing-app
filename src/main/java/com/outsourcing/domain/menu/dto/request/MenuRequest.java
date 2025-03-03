package com.outsourcing.domain.menu.dto.request;

import com.outsourcing.domain.menu.enums.MenuStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuRequest {

	@NotNull(message = "가게 선택은 필수입니다.")
	private Long storeId;

	@NotBlank(message = "메뉴 이름은 필수입니다.")
	private String name;

	@NotNull(message = "가격은 필수입니다.")
	@Min(value = 1, message = "가격은 1원 이상이어야 합니다.")
	private int price;

	private String description;

	private String imageUrl;

	@NotNull(message = "메뉴 상태는 필수입니다.")
	private MenuStatus status;
}
