package com.hcmut.irms.kds_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.rabbitmq.listener.simple.auto-startup=false",
		"spring.rabbitmq.listener.direct.auto-startup=false",
		"eureka.client.enabled=false",
		"spring.cloud.discovery.enabled=false"
})
class KdsServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
