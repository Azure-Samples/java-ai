package com.microsoft.azure.samples.aishop.blob_storage_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BlobStorageServiceApplicationTests {

	static {
		System.setProperty("STORAGE_ACCOUNT_NAME", "test");
		System.setProperty("STORAGE_ACCOUNT_ENDPOINT", "https://test");
		System.setProperty("STORAGE_ACCOUNT_CONTAINER_NAME", "test");
	}

	@Test
	void contextLoads() {
	}

}
