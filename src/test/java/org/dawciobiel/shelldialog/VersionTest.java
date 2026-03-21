package org.dawciobiel.shelldialog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VersionTest {

    @Test
    void shouldReturnUnknownWhenImplementationVersionIsUnavailable() {
        assertEquals("unknown", Version.get());
    }
}
