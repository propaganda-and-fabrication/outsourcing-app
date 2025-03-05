package com.outsourcing.common.storage.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MultipleUploadResponse {

	private final List<String> uploadUrls;

	public static MultipleUploadResponse of(List<String> uploadUrls) {
		return new MultipleUploadResponse(uploadUrls);
	}
}
