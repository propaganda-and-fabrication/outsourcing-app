package com.outsourcing.common.util;

import org.springframework.util.DigestUtils;

public class FileUtils {
	private static final String FILE_EXTENSION_SEPARATOR = ".";

	public static String buildFilename(String originalFilename, String email) {
		int fileExtensionIndex = originalFilename.lastIndexOf(FILE_EXTENSION_SEPARATOR);
		String fileExtension = originalFilename.substring(fileExtensionIndex).toLowerCase();
		String filename = originalFilename.substring(0, fileExtensionIndex);
		String now = String.valueOf(System.currentTimeMillis());

		String convertedEmail = DigestUtils.md5DigestAsHex(email.getBytes());

		return convertedEmail + "/" + now + "_" + filename + fileExtension;
	}
}
