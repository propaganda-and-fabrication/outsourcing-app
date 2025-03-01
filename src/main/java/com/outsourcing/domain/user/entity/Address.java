package com.outsourcing.domain.user.entity;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import com.outsourcing.common.entity.BaseTime;
import com.outsourcing.domain.user.enums.AddressStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "addresses")
@NoArgsConstructor(access = PROTECTED)
public class Address extends BaseTime {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String address;

	@Enumerated(STRING)
	@Column(nullable = false)
	private AddressStatus status;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	public Address(String address) {
		this.address = address;
	}

	public void addAddress(String address) {
		this.address = address;
		this.customer.getAddresses().add(this);
	}
}
