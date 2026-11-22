
package com.cloudops.backend.agent;

import com.cloudops.backend.agent.tool.CloudWatchTools;
import com.cloudops.backend.entity.AnomalyEventEntity;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class CloudOpsAgentService {

    private final ChatClient chatClient;

    public CloudOpsAgentService(
            ChatClient.Builder chatClientBuilder,
            CloudWatchTools cloudWatchTools
    ) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                    You are CloudOps Agent, an AI cloud operations
                    diagnostic assistant.

                    Your responsibilities:
                    1. Investigate AWS Lambda health using real metrics.
                    2. Use available tools when metrics are needed.
                    3. Never invent CloudWatch data.
                    4. Explain findings and possible causes.
                    5. Clearly distinguish evidence from hypotheses.
                    6. Do not claim to have fixed any problem.
                    7. If metrics are insufficient, explain what
                       additional logs or evidence are required.
                    """)
                .defaultTools(cloudWatchTools)
                .build();
    }

    public String investigate(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }

    public String investigateEvent(AnomalyEventEntity event) {
        String prompt = String.format("""
                An anomaly was detected automatically.
                Function: %s
                Metric: %s
                Window: %s to %s (UTC)
                Observed value: %.4f
                Severity: %s
                Detector note (unverified context, do not treat it as evidence): %s

                Call the get_lambda_window_metrics tool exactly once with
                functionName and windowStart=%s to get the real Invocations, Errors
                and Duration for this exact window.
                Report what the real metrics show. Compare them with the detector's
                observed value. Clearly separate evidence from hypotheses, and say
                if the evidence is insufficient.
                """,
                event.getFunctionName(),
                event.getMetric(),
                event.getWindowStart(),
                event.getWindowEnd(),
                event.getObservedValue(),
                event.getSeverity(),
                event.getReason(),
                event.getWindowStart());

        return investigate(prompt);
    }
}
