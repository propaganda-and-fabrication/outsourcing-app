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
@DiscriminatorValue(value = "customers")
@NoArgsConstructor(access = PROTECTED)
public class Customer extends User {

	@Column(unique = true)
	private String nickname;

	@OneToMany(mappedBy = "customer", cascade = ALL, orphanRemoval = true)
	private final List<Address> addresses = new ArrayList<>();

	public Customer(String email, String password, String name, String phoneNumber,
		UserRole role) {
		super(email, password, name, phoneNumber, role);
		int idx = email.indexOf("@");
		this.nickname = email.substring(0, idx) + "_" + UUID.randomUUID().toString().substring(0, 8);
	}

	public void changeNickname(String nickname) {
		this.nickname = nickname;
	}

	public void addAddress(Address address) {
		addresses.add(address);
		address.addCustomer(this); // 양방향 관계 설정
	}

	public void removeAddress(Address address) {
		addresses.remove(address);
		address.addCustomer(null); // 양방향 관계 해제
	}
}
