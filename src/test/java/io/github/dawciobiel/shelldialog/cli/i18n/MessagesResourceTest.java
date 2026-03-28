package io.github.dawciobiel.shelldialog.cli.i18n;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessagesResourceTest {

    @Test
    void terminalStreamMessageShouldUseVersionNeutralJarExampleInEnglish() {
        String message = ResourceBundle.getBundle("messages", Locale.ENGLISH).getString("error.terminal.stream");

        assertTrue(message.contains("shelldialog-<version>-all.jar"));
        assertFalse(message.contains("1.0.0-SNAPSHOT"));
    }

    @Test
    void terminalStreamMessageShouldUseVersionNeutralJarExampleInPolish() {
        String message = ResourceBundle.getBundle("messages", Locale.of("pl", "PL")).getString("error.terminal.stream");

        assertTrue(message.contains("shelldialog-<version>-all.jar"));
        assertFalse(message.contains("1.0.0-SNAPSHOT"));
    }
}
