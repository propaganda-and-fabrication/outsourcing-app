package com.outsourcing.domain.user.entity;

import static jakarta.persistence.CascadeType.*;
import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.outsourcing.domain.user.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@DiscriminatorValue(value = "customer")
@NoArgsConstructor(access = PROTECTED)
public class Customer extends User {

	@Column(unique = true)
	private String nickname;

	@OneToMany(mappedBy = "customer", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
	private List<Address> addresses = new ArrayList<>();

	public Customer(String email, String password, String name, String phoneNumber,
		UserRole role) {
		super(email, password, name, phoneNumber, role);
		this.nickname = email + "_" + UUID.randomUUID().toString().substring(0, 8);
	}

	public void changeNickname(String nickname) {
		this.nickname = nickname;
	}

	public static Customer toCustomer(User user) {
		return new Customer(
			user.getEmail(),
			user.getPassword(),
			user.getName(),
			user.getPhoneNumber(),
			user.getRole()
		);
	}
}
