package com.cloudops.backend;

import com.cloudops.backend.repository.IncidentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class CloudopsBackendApplicationTests {

	@MockitoBean
	private IncidentRepository incidentRepository;

	@Test
	void contextLoads() {
	}

}
