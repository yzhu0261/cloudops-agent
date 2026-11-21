package com.cloudops.backend;

import com.cloudops.backend.repository.IncidentRepository;
import com.cloudops.backend.repository.LambdaMetricSampleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.cloudops.backend.repository.AnomalyEventRepository;

@SpringBootTest
@ActiveProfiles("test")
class CloudopsBackendApplicationTests {

	@MockitoBean
	private IncidentRepository incidentRepository;

	@MockitoBean
	private LambdaMetricSampleRepository lambdaMetricSampleRepository;

	@MockitoBean
	private AnomalyEventRepository anomalyEventRepository;

	@Test
	void contextLoads() {
	}

}