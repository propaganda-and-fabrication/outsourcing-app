package com.outsourcing.common.storage.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.outsourcing.common.storage.dto.response.MultipleUploadResponse;
import com.outsourcing.common.storage.dto.response.UploadResponse;
import com.outsourcing.domain.auth.service.CustomUserDetails;

public interface StorageService {

	UploadResponse upload(MultipartFile image, String type, CustomUserDetails currentUser);

	MultipleUploadResponse multipleUpload(List<MultipartFile> images, String type, CustomUserDetails currentUser);
}
