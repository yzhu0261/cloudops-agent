
package com.cloudops.backend;

import com.cloudops.backend.enums.IncidentStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IncidentStatusTest {

    @Test
    void shouldHaveOpenStatus() {

        IncidentStatus status = IncidentStatus.OPEN;

        assertEquals(IncidentStatus.OPEN, status);
    }
}
