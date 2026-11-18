package com.cloudops.backend.monitoring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@ConfigurationProperties(prefix = "cloudops.monitoring")
public record MonitoringProperties(
        @DefaultValue("false") boolean enabled,
        @DefaultValue("60000") long intervalMs,
        @DefaultValue List<String> functions
) {
}