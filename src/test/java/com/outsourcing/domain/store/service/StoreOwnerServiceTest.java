package com.outsourcing.domain.store.service;

import static com.outsourcing.common.exception.ErrorCode.*;
import static com.outsourcing.domain.menu.enums.MenuStatus.*;
import static com.outsourcing.domain.store.enums.StoreStatus.*;
import static com.outsourcing.domain.user.enums.UserRole.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.domain.menu.entity.Menu;
import com.outsourcing.domain.menu.repository.MenuRepository;
import com.outsourcing.domain.store.dto.request.CreateStoreRequest;
import com.outsourcing.domain.store.dto.request.UpdateAddressRequest;
import com.outsourcing.domain.store.dto.request.UpdateImageRequest;
import com.outsourcing.domain.store.dto.request.UpdateMinPriceRequest;
import com.outsourcing.domain.store.dto.request.UpdateNameRequest;
import com.outsourcing.domain.store.dto.request.UpdateNumberRequest;
import com.outsourcing.domain.store.dto.request.UpdateStatusRequest;
import com.outsourcing.domain.store.dto.request.UpdateStoreHoursRequest;
import com.outsourcing.domain.store.dto.response.OwnerStoresResponse;
import com.outsourcing.domain.store.dto.response.StoreOwnerResponse;
import com.outsourcing.domain.store.dto.response.StoreResponse;
import com.outsourcing.domain.store.entity.Store;
import com.outsourcing.domain.store.repository.StoreRepository;
import com.outsourcing.domain.user.entity.Owner;
import com.outsourcing.domain.user.repository.OwnerRepository;

@ExtendWith(MockitoExtension.class)
class StoreOwnerServiceTest {

	@Mock
	StoreRepository storeRepository;

	@Mock
	OwnerRepository ownerRepository;

	@Mock
	MenuRepository menuRepository;

	@InjectMocks
	StoreOwnerService storeOwnerService;

	@Test
	public void 가게_생성_성공() {
		// given
		CreateStoreRequest request = new CreateStoreRequest();
		LocalTime now = LocalTime.now();
		ReflectionTestUtils.setField(request, "storeName", "storeName");
		ReflectionTestUtils.setField(request, "storeProfileUrl", "profileUrl");
		ReflectionTestUtils.setField(request, "storeAddress", "address");
		ReflectionTestUtils.setField(request, "storePhoneNumber", "000");
		ReflectionTestUtils.setField(request, "minPrice", new BigDecimal(1000));
		ReflectionTestUtils.setField(request, "openedAt", now);
		ReflectionTestUtils.setField(request, "closedAt", now);

		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);
		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));

		given(ownerRepository.findById(anyLong())).willReturn(Optional.of(mockOwner));
		given(storeRepository.save(any(Store.class))).willReturn(mockStore);

		// when
		StoreOwnerResponse response = storeOwnerService.createStore(mockOwner.getId(), request);

		// then
		assertNotNull(response);
		assertEquals(mockStore.getId(), response.getId());
	}

	@Test
	public void Owner를_찾지_못해_실패() {
		// given
		CreateStoreRequest request = new CreateStoreRequest();
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);

		given(ownerRepository.findById(anyLong())).willReturn(Optional.empty());

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> storeOwnerService.createStore(anyLong(), request));

		// then
		assertEquals(USER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	public void 개업_가능_상한을_넘어서_실패() {
		// given
		CreateStoreRequest request = new CreateStoreRequest();
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);
		ReflectionTestUtils.setField(mockOwner, "storeCount", 3);

		given(ownerRepository.findById(anyLong())).willReturn(Optional.of(mockOwner));

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> storeOwnerService.createStore(anyLong(), request));

		// then
		assertEquals(MAX_STORE_LIMIT_REACHED, exception.getErrorCode());
	}

	@Test
	public void Owner_가게_전체_조회_성공() {
		// given
		Long ownerId = 1L;
		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		List<Store> mockStores = List.of(mockStore);

		given(storeRepository.findByOwnerId(anyLong())).willReturn(mockStores);

		// when
		List<OwnerStoresResponse> responses = storeOwnerService.getAll(ownerId);

		// then
		assertEquals(1, responses.size());
		assertEquals(mockStore.getId(), responses.get(0).getId());
	}

	@Test
	public void Owner_가게_단건_조회_성공() {
		// given
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);

		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		ReflectionTestUtils.setField(mockStore, "owner", mockOwner);

		Menu menu = new Menu(mockStore, "name", 1200, "des", "image", AVAILABLE);

		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));
		given(menuRepository.findAllByStoreId(mockStore.getId())).willReturn(List.of(menu));

		// when
		StoreResponse response = storeOwnerService.getStore(mockOwner.getId(), mockStore.getId());

		// then
		assertNotNull(response);
		assertEquals(mockStore.getId(), response.getId());
		assertEquals(1, response.getMenus().size());
	}

	@Test
	public void store를_찾지_못해서_실패() {
		// given
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);

		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		ReflectionTestUtils.setField(mockStore, "owner", mockOwner);

		given(storeRepository.findById(anyLong())).willReturn(Optional.empty());

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> storeOwnerService.getStore(mockOwner.getId(), mockStore.getId()));

		// then
		assertEquals(STORE_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	public void 남의_가게_조회해서_실패() {
		// given
		Owner mockOwner1 = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner1, "id", 1L);

		Owner mockOwner2 = new Owner("b@b.com", "password", "kim", "111", OWNER);
		ReflectionTestUtils.setField(mockOwner2, "id", 2L);

		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		ReflectionTestUtils.setField(mockStore, "owner", mockOwner2);

		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> storeOwnerService.getStore(mockOwner1.getId(), mockStore.getId()));

		// then
		assertEquals(UNAUTHORIZED_STORE, exception.getErrorCode());
	}

	@Test
	public void 가게_이름_수정_성공() {
		// given
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);

		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		ReflectionTestUtils.setField(mockStore, "owner", mockOwner);

		UpdateNameRequest request = new UpdateNameRequest();
		ReflectionTestUtils.setField(request, "storeName", "store name");

		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));

		// when
		StoreOwnerResponse response = storeOwnerService.updateStoreName(
			mockStore.getId(), request, mockOwner.getId());

		// then
		assertNotNull(response);
		assertEquals(mockStore.getId(), response.getId());
		assertEquals(mockStore.getStoreName(), response.getStoreName());
	}

	@Test
	public void 가게_사진_변경() {
		// given
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);

		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		ReflectionTestUtils.setField(mockStore, "owner", mockOwner);

		UpdateImageRequest request = new UpdateImageRequest();
		ReflectionTestUtils.setField(request, "storeProfileUrl", "url");

		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));

		// when
		StoreOwnerResponse response = storeOwnerService.updateProfileUrl(mockStore.getId(), request, mockOwner.getId());

		// then
		assertNotNull(response);
		assertEquals(mockStore.getId(), response.getId());
		assertEquals(mockStore.getStoreProfileUrl(), response.getStoreProfileUrl());
	}

	@Test
	public void 가게_주소_수정_성공() {
		// given
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);

		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		ReflectionTestUtils.setField(mockStore, "owner", mockOwner);

		UpdateAddressRequest request = new UpdateAddressRequest();
		ReflectionTestUtils.setField(request, "storeAddress", "address");

		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));

		// when
		StoreOwnerResponse response = storeOwnerService.updateStoreAddress(
			mockStore.getId(), request, mockOwner.getId());

		// then
		assertNotNull(response);
		assertEquals(mockStore.getId(), response.getId());
		assertEquals(mockStore.getStoreAddress(), response.getStoreAddress());
	}

	@Test
	public void 가게_전화번호_수정_성공() {
		// given
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);

		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		ReflectionTestUtils.setField(mockStore, "owner", mockOwner);

		UpdateNumberRequest request = new UpdateNumberRequest();
		ReflectionTestUtils.setField(request, "storePhoneNumber", "111");

		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));

		// when
		StoreOwnerResponse response = storeOwnerService.updateStorePhoneNumber(
			mockStore.getId(), request, mockOwner.getId());

		// then
		assertNotNull(response);
		assertEquals(mockStore.getId(), response.getId());
		assertEquals(mockStore.getStorePhoneNumber(), response.getStorePhoneNumber());
	}

	@Test
	public void 가게_운영_시간_수정_성공() {
		// given
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);

		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		ReflectionTestUtils.setField(mockStore, "owner", mockOwner);

		LocalTime now = LocalTime.now();
		UpdateStoreHoursRequest request = new UpdateStoreHoursRequest();
		ReflectionTestUtils.setField(request, "openedAt", now);
		ReflectionTestUtils.setField(request, "closedAt", now);

		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));

		// when
		StoreOwnerResponse response = storeOwnerService.updateStoreHours(
			mockStore.getId(), request, mockOwner.getId());

		// then
		assertNotNull(response);
		assertEquals(mockStore.getId(), response.getId());
		assertEquals(mockStore.getOpenedAt(), response.getOpenedAt());
		assertEquals(mockStore.getClosedAt(), response.getClosedAt());
	}

	@Test
	public void 가게_운영_시간_중_시작_시간이_null이여서_실패() {
		// given
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);

		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		ReflectionTestUtils.setField(mockStore, "owner", mockOwner);

		LocalTime now = LocalTime.now();
		UpdateStoreHoursRequest request = new UpdateStoreHoursRequest();
		ReflectionTestUtils.setField(request, "openedAt", null);
		ReflectionTestUtils.setField(request, "closedAt", now);

		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> storeOwnerService.updateStoreHours(mockStore.getId(), request, mockOwner.getId()));

		// then
		assertEquals(MISSING_STORE_HOURS, exception.getErrorCode());
	}

	@Test
	public void 가게_운영_시간_중_닫는_시간이_null이여서_실패() {
		// given
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);

		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		ReflectionTestUtils.setField(mockStore, "owner", mockOwner);

		LocalTime now = LocalTime.now();
		UpdateStoreHoursRequest request = new UpdateStoreHoursRequest();
		ReflectionTestUtils.setField(request, "openedAt", now);
		ReflectionTestUtils.setField(request, "closedAt", null);

		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> storeOwnerService.updateStoreHours(mockStore.getId(), request, mockOwner.getId()));

		// then
		assertEquals(MISSING_STORE_HOURS, exception.getErrorCode());
	}

	@Test
	public void 가게_상태_수정_성공() {
		// given
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);

		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		ReflectionTestUtils.setField(mockStore, "owner", mockOwner);
		ReflectionTestUtils.setField(mockStore, "storeStatus", TEMPORARY_CLOSED);

		UpdateStatusRequest request = new UpdateStatusRequest();
		ReflectionTestUtils.setField(request, "storeStatus", OPERATIONAL);

		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));

		// when
		StoreOwnerResponse response = storeOwnerService.updateStoreStatus(
			mockStore.getId(), request, mockOwner.getId());

		// then
		assertNotNull(response);
		assertEquals(mockStore.getStoreStatus(), response.getStoreStatus());
	}

	@Test
	public void 가게_최소_주문_금액_수정_성공() {
		// given
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);

		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		ReflectionTestUtils.setField(mockStore, "owner", mockOwner);

		UpdateMinPriceRequest request = new UpdateMinPriceRequest();
		ReflectionTestUtils.setField(request, "minPrice", new BigDecimal(20000));

		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));

		// when
		StoreOwnerResponse response = storeOwnerService.updateMinPrice(
			mockStore.getId(), request, mockOwner.getId());

		// then
		assertNotNull(response);
		assertEquals(mockStore.getMinPrice(), response.getMinPrice());
	}

	@Test
	public void 가게_최소_주문_금액이_null이어서_실패() {
		// given
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);

		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		ReflectionTestUtils.setField(mockStore, "owner", mockOwner);

		UpdateMinPriceRequest request = new UpdateMinPriceRequest();
		ReflectionTestUtils.setField(request, "minPrice", null);

		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> storeOwnerService.updateMinPrice(mockStore.getId(), request, mockOwner.getId()));

		// then
		assertEquals(MISSING_MIN_PRICE, exception.getErrorCode());
	}

	@Test
	public void 가게_폐업_성공() {
		// given
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);
		ReflectionTestUtils.setField(mockOwner, "storeCount", 1);

		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		ReflectionTestUtils.setField(mockStore, "owner", mockOwner);
		ReflectionTestUtils.setField(mockStore, "storeStatus", OPERATIONAL);

		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));
		given(storeRepository.save(any(Store.class))).willReturn(mockStore);
		given(ownerRepository.findById(anyLong())).willReturn(Optional.of(mockOwner));

		// when
		storeOwnerService.deleteById(mockStore.getId(), mockOwner.getId());

		// then
		assertEquals(SHUTDOWN, mockStore.getStoreStatus());
	}

	@Test
	public void 가게를_못찾아서_폐업_실패() {
		// given
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);
		ReflectionTestUtils.setField(mockOwner, "storeCount", 1);

		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		ReflectionTestUtils.setField(mockStore, "owner", mockOwner);

		given(storeRepository.findById(anyLong())).willReturn(Optional.empty());

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> storeOwnerService.deleteById(mockStore.getId(), mockOwner.getId()));

		// then
		assertEquals(STORE_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	public void 가게에_등록된_유저를_찾지_못해서_폐업_실패() {
		// given
		Owner mockOwner = new Owner("a@a.com", "password", "kim", "000", OWNER);
		ReflectionTestUtils.setField(mockOwner, "id", 1L);
		ReflectionTestUtils.setField(mockOwner, "storeCount", 1);

		Store mockStore = new Store("name", "storeImage", new BigDecimal(17000));
		ReflectionTestUtils.setField(mockStore, "id", 1L);
		ReflectionTestUtils.setField(mockStore, "owner", mockOwner);

		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));
		given(storeRepository.save(any(Store.class))).willReturn(mockStore);
		given(ownerRepository.findById(anyLong())).willReturn(Optional.empty());

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> storeOwnerService.deleteById(mockStore.getId(), mockOwner.getId()));

		// then
		assertEquals(USER_NOT_FOUND, exception.getErrorCode());
	}

}