package com.outsourcing.domain.store.service;

import static com.outsourcing.common.exception.ErrorCode.*;
import static com.outsourcing.domain.menu.enums.MenuStatus.*;
import static com.outsourcing.domain.store.enums.StoreStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.exception.ErrorCode;
import com.outsourcing.domain.menu.entity.Menu;
import com.outsourcing.domain.menu.enums.MenuStatus;
import com.outsourcing.domain.menu.repository.MenuRepository;
import com.outsourcing.domain.store.dto.response.StoreCustomerResponse;
import com.outsourcing.domain.store.dto.response.StoreResponse;
import com.outsourcing.domain.store.entity.Store;
import com.outsourcing.domain.store.enums.StoreStatus;
import com.outsourcing.domain.store.repository.StoreRepository;

@ExtendWith(MockitoExtension.class)
class StoreCustomerServiceTest {

	@Mock
	StoreRepository storeRepository;

	@Mock
	MenuRepository menuRepository;

	@InjectMocks
	StoreCustomerService storeCustomerService;

	@Test
	public void 운영_중인_가게_전체_조회() {
		// given
		Store store = new Store("name", "url", new BigDecimal(100));
		Page<Store> stores = new PageImpl<>(List.of(store), PageRequest.of(0, 10), 1);

		given(storeRepository.findByStoreStatus(any(StoreStatus.class), any(Pageable.class))).willReturn(stores);

		// when
		Page<StoreCustomerResponse> response = storeCustomerService.getAllPage(1, 10);

		// then
		assertNotNull(response);
		assertEquals(stores.getSize(), response.getSize());
	}
	
	@Test
	public void 가게_단건_조회() {
	    // given
		Store store = new Store("name", "url", new BigDecimal(100));
		ReflectionTestUtils.setField(store, "id", 1L);
		ReflectionTestUtils.setField(store, "storeStatus", OPERATIONAL);

		Menu menu = new Menu(store, "name", 100, "des", "demo", AVAILABLE);
		List<Menu> menus = List.of(menu);

		given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));
		given(menuRepository.findAllByStoreId(anyLong())).willReturn(menus);
	
	    // when
		StoreResponse response = storeCustomerService.getStore(store.getId());

		// then
		assertNotNull(response);
		assertEquals(store.getId(), response.getId());
	}

	@Test
	public void 단건_조회_중_운영_중인_가게를_찾지_못해서_실패() {
	    // given
		Store store = new Store("name", "url", new BigDecimal(100));
		ReflectionTestUtils.setField(store, "id", 1L);
		ReflectionTestUtils.setField(store, "storeStatus", SHUTDOWN);

		given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));

	    // when
		BaseException exception = assertThrows(BaseException.class, () -> storeCustomerService.getStore(store.getId()));

		// then
		assertEquals(STORE_NOT_FOUND, exception.getErrorCode());
	}


}