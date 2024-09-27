package com.microsoft.azure.samples.aishop.ai_image_processing_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiImageProcessingServiceApplicationTests {

	static {
		System.setProperty("AZURE_OPENAI_ENDPOINT", "https://test");
	}

	@Test
	void contextLoads() {
		AiImageProcessingServiceApplication.main(new String[] {});
	}

}
