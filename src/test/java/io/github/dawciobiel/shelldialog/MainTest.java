package io.github.dawciobiel.shelldialog;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
    void shouldRecognizeSupportedArguments() {
        assertTrue(Main.isSupportedArgument("--version"));
        assertTrue(Main.isSupportedArgument("-v"));
        assertTrue(Main.isSupportedArgument("version"));
        assertTrue(Main.isSupportedArgument("singlechoice"));
        assertTrue(Main.isSupportedArgument("multichoice"));
        assertTrue(Main.isSupportedArgument("textline"));
        assertTrue(Main.isSupportedArgument("password"));
        assertTrue(Main.isSupportedArgument("yesno"));
        assertTrue(Main.isSupportedArgument("file"));
        assertTrue(Main.isSupportedArgument("progress"));
        assertTrue(Main.isSupportedArgument("spinner"));
        assertTrue(Main.isSupportedArgument("message"));
        assertTrue(Main.isSupportedArgument("form"));
        assertTrue(Main.isSupportedArgument("wizard"));
        assertFalse(Main.isSupportedArgument("unknown"));
    }

    @Test
    void shouldPrintVersionForVersionArgument() {
        String output = captureStdout(() -> Main.main(new String[]{"--version"}));

        assertEquals(Version.get() + System.lineSeparator(), output);
    }

    @Test
    void shouldPrintHelpForUnknownArgument() {
        String output = captureStdout(() -> Main.main(new String[]{"unknown"}));

        assertTrue(output.contains("Unknown dialog example: [unknown]"));
        assertTrue(output.contains("Possible dialog examples:"));
        assertTrue(output.contains("singlechoice, multichoice, textline, password, yesno, file, progress, spinner, message, form, wizard, version"));
    }

    private String captureStdout(Runnable action) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try (PrintStream capture = new PrintStream(outputStream, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return outputStream.toString(StandardCharsets.UTF_8);
    }
}
