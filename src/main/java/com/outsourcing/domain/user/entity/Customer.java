package com.outsourcing.domain.user.entity;

import static lombok.AccessLevel.*;

import java.util.UUID;

import com.outsourcing.domain.user.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "customers")
@DiscriminatorValue(value = "customers")
@NoArgsConstructor(access = PROTECTED)
public class Customer extends User {

	@Column(unique = true)
	private String nickname;

	public Customer(String email, String password, String name, String phoneNumber, UserRole role) {
		super(email, password, name, phoneNumber, role);
		int idx = email.indexOf("@");
		this.nickname = email.substring(0, idx) + "_" + UUID.randomUUID().toString().substring(0, 8);
	}

	public void changeNickname(String nickname) {
		this.nickname = nickname;
	}

}
