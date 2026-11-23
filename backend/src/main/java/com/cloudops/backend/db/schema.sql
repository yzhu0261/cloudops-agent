CREATE TABLE lambda_metric_samples (
                                       id              UUID PRIMARY KEY,
                                       function_name   VARCHAR(128) NOT NULL,
                                       window_start    TIMESTAMPTZ  NOT NULL,
                                       window_end      TIMESTAMPTZ  NOT NULL,
                                       invocations     DOUBLE PRECISION,
                                       errors          DOUBLE PRECISION,
                                       avg_duration_ms DOUBLE PRECISION,
                                       status          VARCHAR(20)  NOT NULL,
                                       error_message   TEXT,
                                       collected_at    TIMESTAMPTZ  NOT NULL,
                                       CONSTRAINT uq_function_window UNIQUE (function_name, window_start)
);

CREATE TABLE anomaly_events (
                                id              UUID PRIMARY KEY,
                                function_name   VARCHAR(128) NOT NULL,
                                metric          VARCHAR(32)  NOT NULL,
                                window_start    TIMESTAMPTZ  NOT NULL,
                                window_end      TIMESTAMPTZ  NOT NULL,
                                observed_value  DOUBLE PRECISION NOT NULL,
                                baseline_mean   DOUBLE PRECISION NOT NULL,
                                baseline_std    DOUBLE PRECISION NOT NULL,
                                z_score         DOUBLE PRECISION NOT NULL,
                                severity        VARCHAR(16)  NOT NULL,
                                reason          TEXT         NOT NULL,
                                investigation_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                investigation_report TEXT,
                                investigation_error TEXT,
                                investigation_finished_at TIMESTAMPTZ,
                                detected_at     TIMESTAMPTZ  NOT NULL,
                                CONSTRAINT uq_anomaly_window UNIQUE (function_name, metric, window_start)
);