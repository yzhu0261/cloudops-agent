
package com.cloudops.backend.controller;

import com.cloudops.backend.agent.CloudOpsAgentService;
import com.cloudops.backend.entity.AnomalyEventEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agent")
public class CloudOpsAgentController {

    private final CloudOpsAgentService agentService;

    public CloudOpsAgentController(
            CloudOpsAgentService agentService
    ) {
        this.agentService = agentService;
    }

    @PostMapping("/investigate")
    public Map<String, String> investigate(
            @RequestBody Map<String, String> request
    ) {
        String message = request.get("message");

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                    "Message cannot be empty"
            );
        }

        String response = agentService.investigate(message);

        return Map.of("response", response);
    }
}
