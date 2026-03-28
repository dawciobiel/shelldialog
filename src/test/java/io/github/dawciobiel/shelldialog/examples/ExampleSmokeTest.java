package io.github.dawciobiel.shelldialog.examples;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExampleSmokeTest {

    @Test
    void examplesShouldBuildDialogsWithoutThrowing() {
        assertNotNull(SingleChoiceExample.buildDialog());
        assertNotNull(MultiChoiceExample.buildDialog());
        assertNotNull(TextLineExample.buildDialog());
        assertNotNull(PasswordExample.buildDialog());
        assertNotNull(YesNoExample.buildDialog());
        assertNotNull(FileExample.buildDialog());
        assertNotNull(MessageExample.buildDialog());
        assertNotNull(FormExample.buildDialog());
        assertNotNull(WizardExample.buildDialog());
        assertNotNull(ProgressExample.buildDialog());
        assertNotNull(SpinnerExample.buildDialog());
    }
}
