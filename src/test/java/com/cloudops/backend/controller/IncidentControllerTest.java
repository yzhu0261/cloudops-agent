
package com.cloudops.backend.controller;

import com.cloudops.backend.service.IncidentService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cloudops.backend.dto.CreateIncidentRequest;
import com.cloudops.backend.dto.IncidentResponse;
import org.springframework.http.MediaType;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class IncidentControllerTest {

    @Test
    void shouldReturnAllIncidents() throws Exception {

        // 1. 创建假的 Service
        IncidentService incidentService = mock(IncidentService.class);

        // 2. 规定假的 Service 返回空列表
        when(incidentService.findAll()).thenReturn(List.of());

        // 3. 创建真实 Controller
        IncidentController controller =
                new IncidentController(incidentService);

        // 4. 创建 MockMvc
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        // 5. 模拟 HTTP GET 请求
        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldReturn404WhenIncidentNotFound() throws Exception {

        // 1. 创建模拟 Service
        IncidentService incidentService = mock(IncidentService.class);

        // 2. 创建一个不存在的 Incident ID
        UUID id = UUID.randomUUID();

        // 3. 模拟 Service 抛出 404 异常
        when(incidentService.findById(id))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Incident not found"
                ));

        // 4. 创建真实 Controller
        IncidentController controller =
                new IncidentController(incidentService);

        // 5. 创建 MockMvc
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        // 6. 模拟 GET 请求并检查 HTTP 状态码
        mockMvc.perform(get("/api/incidents/{id}", id))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldCreateIncident() throws Exception {

        // 1. 创建模拟 Service
        IncidentService incidentService = mock(IncidentService.class);

        // 2. 准备模拟返回的数据
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();

        IncidentResponse response = new IncidentResponse(
                id,
                "AWS Lambda Timeout",
                "Lambda execution exceeded timeout",
                "HIGH",
                "OPEN",
                createdAt
        );

        // 3. 模拟 Service 的返回结果
        when(incidentService.create(any(CreateIncidentRequest.class)))
                .thenReturn(response);

        // 4. 创建 Controller 和 MockMvc
        IncidentController controller =
                new IncidentController(incidentService);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        // 5. 准备 HTTP 请求的 JSON
        String requestJson = """
            {
                "title": "AWS Lambda Timeout",
                "description": "Lambda execution exceeded timeout",
                "severity": "HIGH"
            }
            """;

        // 6. 模拟 POST 请求
        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title")
                        .value("AWS Lambda Timeout"))
                .andExpect(jsonPath("$.status")
                        .value("OPEN"));

        // 7. 验证 Service 被调用
        verify(incidentService)
                .create(any(CreateIncidentRequest.class));
    }


    @Test
    void shouldRejectIncidentWithBlankTitle() throws Exception {

        // 1. 创建模拟 Service
        IncidentService incidentService = mock(IncidentService.class);

        // 2. 创建真实 Controller
        IncidentController controller =
                new IncidentController(incidentService);

        // 3. 创建 MockMvc
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        // 4. 准备非法 JSON
        String requestJson = """
            {
                "title": "",
                "description": "Database connection failed",
                "severity": "HIGH"
            }
            """;

        // 5. 模拟 POST 请求，期望返回 400
        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        // 6. 验证 Service 从未被调用
        verify(incidentService, never())
                .create(any(CreateIncidentRequest.class));
    }
}