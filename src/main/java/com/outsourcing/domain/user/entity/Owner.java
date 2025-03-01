package com.outsourcing.domain.user.entity;

import static lombok.AccessLevel.*;

import com.outsourcing.domain.user.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "owners")
@NoArgsConstructor(access = PROTECTED)
public class Owner extends User {

	private int storeCount = 0;

	@Column(nullable = false, updatable = false)
	private String constantNickname;

	public Owner(String email, String password, String name, String phoneNumber,
		UserRole role) {
		super(email, password, name, phoneNumber, role);
		this.constantNickname = "사장님";
	}

	public void increaseStoreCount() {
		this.storeCount += 1;
	}

	public void decreaseStoreCount() {
		this.storeCount -= 1;
	}
}
