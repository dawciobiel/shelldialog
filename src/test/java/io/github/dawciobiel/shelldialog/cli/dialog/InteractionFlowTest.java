package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.virtual.DefaultVirtualTerminal;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.InputArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InteractionFlowTest {

    @TempDir
    Path tempDir;

    @Test
    void yesNoDialogShouldReturnTrueOnEnterByDefault() throws Exception {
        DefaultVirtualTerminal terminal = new DefaultVirtualTerminal();
        terminal.addInput(new KeyStroke(KeyType.Enter));
        
        YesNoDialog dialog = new YesNoDialog.Builder(
                titleArea(),
                contentArea(),
                contentArea(),
                selectedContentArea(),
                navigationArea()
        )
                .withTerminal(terminal)
                .build();

        Optional<Boolean> result = dialog.show();
        assertTrue(result.isPresent(), "Dialog should return a result");
        assertTrue(result.get(), "Default selection should be 'Yes' (true)");
    }

    @Test
    void yesNoDialogShouldReturnFalseWhenMovingRightAndPressingEnter() throws Exception {
        DefaultVirtualTerminal terminal = new DefaultVirtualTerminal();
        terminal.addInput(new KeyStroke(KeyType.ArrowRight));
        terminal.addInput(new KeyStroke(KeyType.Enter));
        
        YesNoDialog dialog = new YesNoDialog.Builder(
                titleArea(),
                contentArea(),
                contentArea(),
                selectedContentArea(),
                navigationArea()
        )
                .withTerminal(terminal)
                .build();

        Optional<Boolean> result = dialog.show();
        assertTrue(result.isPresent(), "Dialog should return a result");
        assertFalse(result.get(), "Selection should be 'No' (false) after moving right");
    }

    @Test
    void formDialogShouldReturnValuesAfterNavigatingBetweenFields() throws Exception {
        DefaultVirtualTerminal terminal = new DefaultVirtualTerminal();
        terminal.addInput(new KeyStroke('j', false, false));
        terminal.addInput(new KeyStroke('o', false, false));
        terminal.addInput(new KeyStroke('e', false, false));
        terminal.addInput(new KeyStroke(KeyType.Tab));
        terminal.addInput(new KeyStroke('s', false, false));
        terminal.addInput(new KeyStroke('e', false, false));
        terminal.addInput(new KeyStroke('c', false, false));
        terminal.addInput(new KeyStroke('r', false, false));
        terminal.addInput(new KeyStroke('e', false, false));
        terminal.addInput(new KeyStroke('t', false, false));
        terminal.addInput(new KeyStroke(KeyType.Enter));

        FormDialog<LoginData> dialog = new FormDialog.Builder<LoginData>(
                titleArea(),
                contentArea(),
                contentArea(),
                inputArea(),
                focusedInputArea(),
                List.of(
                        FormField.text("username", "Username")
                                .withValidator(value -> value.isBlank() ? Optional.of("Required") : Optional.empty())
                                .build(),
                        FormField.password("password", "Password")
                                .withValidator(chars -> chars.length < 6 ? Optional.of("Too short") : Optional.empty())
                                .build()
                ),
                navigationArea()
        )
                .withTerminal(terminal)
                .withResultMapper(values -> new LoginData(
                        values.getString("username"),
                        values.getPassword("password")
                ))
                .build();

        Optional<LoginData> result = dialog.show();
        assertTrue(result.isPresent(), "Dialog should return form values");
        assertEquals("joe", result.get().username());
        assertArrayEquals("secret".toCharArray(), result.get().password());
    }

    @Test
    void formDialogShouldKeepFocusOnFirstInvalidFieldUntilDataIsCorrected() throws Exception {
        DefaultVirtualTerminal terminal = new DefaultVirtualTerminal();
        terminal.addInput(new KeyStroke(KeyType.Enter));
        terminal.addInput(new KeyStroke('j', false, false));
        terminal.addInput(new KeyStroke('o', false, false));
        terminal.addInput(new KeyStroke('e', false, false));
        terminal.addInput(new KeyStroke(KeyType.Tab));
        terminal.addInput(new KeyStroke('s', false, false));
        terminal.addInput(new KeyStroke('e', false, false));
        terminal.addInput(new KeyStroke('c', false, false));
        terminal.addInput(new KeyStroke('r', false, false));
        terminal.addInput(new KeyStroke('e', false, false));
        terminal.addInput(new KeyStroke('t', false, false));
        terminal.addInput(new KeyStroke(KeyType.Enter));

        FormDialog<LoginData> dialog = new FormDialog.Builder<LoginData>(
                titleArea(),
                contentArea(),
                contentArea(),
                inputArea(),
                focusedInputArea(),
                List.of(
                        FormField.text("username", "Username")
                                .withValidator(value -> value.isBlank() ? Optional.of("Required") : Optional.empty())
                                .build(),
                        FormField.password("password", "Password")
                                .withValidator(chars -> chars.length < 6 ? Optional.of("Too short") : Optional.empty())
                                .build()
                ),
                navigationArea()
        )
                .withTerminal(terminal)
                .withResultMapper(values -> new LoginData(
                        values.getString("username"),
                        values.getPassword("password")
                ))
                .build();

        Optional<LoginData> result = dialog.show();
        assertTrue(result.isPresent(), "Dialog should return form values after correction");
        assertEquals("joe", result.get().username());
        assertArrayEquals("secret".toCharArray(), result.get().password());
    }

    @Test
    void fileDialogShouldToggleHiddenFilesWithF2() throws Exception {
        Path hiddenOnlyDir = Files.createDirectory(tempDir.resolve("hidden-only"));
        Path hiddenFile = Files.createFile(hiddenOnlyDir.resolve(".secret.txt"));

        DefaultVirtualTerminal terminal = new DefaultVirtualTerminal();
        terminal.addInput(new KeyStroke(KeyType.F2));
        terminal.addInput(new KeyStroke(KeyType.ArrowDown));
        terminal.addInput(new KeyStroke(KeyType.Enter));

        FileDialog dialog = new FileDialog.Builder(
                titleArea(),
                contentArea(),
                selectedContentArea(),
                navigationArea()
        )
                .withTerminal(terminal)
                .withInitialDirectory(hiddenOnlyDir)
                .build();

        Optional<Path> result = dialog.show();
        assertTrue(result.isPresent(), "Dialog should return a file after toggling hidden files");
        assertEquals(hiddenFile, result.get());
    }

    @Test
    void fileDialogShouldAllowSelectingCurrentDirectoryInDirectoriesOnlyMode() throws Exception {
        Path nestedDir = Files.createDirectory(tempDir.resolve("nested"));

        DefaultVirtualTerminal terminal = new DefaultVirtualTerminal();
        terminal.addInput(new KeyStroke(KeyType.ArrowDown));
        terminal.addInput(new KeyStroke(KeyType.Enter));

        FileDialog dialog = new FileDialog.Builder(
                titleArea(),
                contentArea(),
                selectedContentArea(),
                navigationArea()
        )
                .withTerminal(terminal)
                .withInitialDirectory(nestedDir)
                .directoriesOnly(true)
                .build();

        Optional<Path> result = dialog.show();
        assertTrue(result.isPresent(), "Dialog should return current directory in directories-only mode");
        assertEquals(nestedDir, result.get());
    }

    @Test
    void fileDialogShouldRecoverFromReadErrorAfterShortcutNavigation() throws Exception {
        Path validDir = Files.createDirectory(tempDir.resolve("valid"));
        Path expectedFile = Files.createFile(validDir.resolve("file.txt"));
        Path missingDir = tempDir.resolve("missing");

        DefaultVirtualTerminal terminal = new DefaultVirtualTerminal();
        terminal.addInput(new KeyStroke(KeyType.F1));
        terminal.addInput(new KeyStroke(KeyType.ArrowDown));
        terminal.addInput(new KeyStroke(KeyType.Enter));

        FileDialog dialog = new FileDialog.Builder(
                titleArea(),
                contentArea(),
                selectedContentArea(),
                navigationArea()
        )
                .withTerminal(terminal)
                .withInitialDirectory(missingDir)
                .withShortcuts(Map.of(KeyType.F1, validDir))
                .build();

        Optional<Path> result = dialog.show();
        assertTrue(result.isPresent(), "Dialog should recover after switching to a readable directory");
        assertEquals(expectedFile, result.get());
    }

    @Test
    void fileDialogShouldCreateDirectoryWithF7() throws Exception {
        Path emptyDir = Files.createDirectory(tempDir.resolve("empty"));

        DefaultVirtualTerminal terminal = new DefaultVirtualTerminal();
        terminal.addInput(new KeyStroke(KeyType.F7));
        terminal.addInput(new KeyStroke('n', false, false));
        terminal.addInput(new KeyStroke('e', false, false));
        terminal.addInput(new KeyStroke('w', false, false));
        terminal.addInput(new KeyStroke(KeyType.Enter));
        terminal.addInput(new KeyStroke(KeyType.ArrowDown));
        terminal.addInput(new KeyStroke(KeyType.ArrowDown));
        terminal.addInput(new KeyStroke(KeyType.Enter));
        terminal.addInput(new KeyStroke(KeyType.ArrowDown));
        terminal.addInput(new KeyStroke(KeyType.Enter));

        FileDialog dialog = new FileDialog.Builder(
                titleArea(),
                contentArea(),
                selectedContentArea(),
                navigationArea()
        )
                .withTerminal(terminal)
                .withInitialDirectory(emptyDir)
                .directoriesOnly(true)
                .build();

        Optional<Path> result = dialog.show();
        assertTrue(result.isPresent(), "Dialog should return the newly created directory");
        assertEquals(emptyDir.resolve("new"), result.get());
        assertTrue(Files.isDirectory(emptyDir.resolve("new")));
    }

    @Test
    void wizardDialogShouldFinishAfterMultipleSteps() throws Exception {
        DefaultVirtualTerminal terminal = new DefaultVirtualTerminal();
        terminal.addInput(new KeyStroke('j', false, false));
        terminal.addInput(new KeyStroke('o', false, false));
        terminal.addInput(new KeyStroke('e', false, false));
        terminal.addInput(new KeyStroke(KeyType.Enter));
        terminal.addInput(new KeyStroke('o', false, false));
        terminal.addInput(new KeyStroke('u', false, false));
        terminal.addInput(new KeyStroke('t', false, false));
        terminal.addInput(new KeyStroke(KeyType.Enter));
        terminal.addInput(new KeyStroke(KeyType.Enter));

        WizardDialog<WizardData> dialog = new WizardDialog.Builder<WizardData>(
                "Setup Wizard",
                List.of(
                        WizardTextStep.builder("Account", "Enter username", "username")
                                .withValidator(value -> value.isBlank() ? Optional.of("Required") : Optional.empty())
                                .build(),
                        WizardTextStep.builder("Location", "Enter directory", "directory")
                                .withValidator(value -> value.isBlank() ? Optional.of("Required") : Optional.empty())
                                .build(),
                        WizardSummaryStep.of("Summary", context -> List.of(
                                "User: " + context.getString("username"),
                                "Dir: " + context.getString("directory")
                        ))
                )
        )
                .withTerminal(terminal)
                .withResultMapper(context -> new WizardData(
                        context.getString("username"),
                        context.getString("directory")
                ))
                .build();

        Optional<WizardData> result = dialog.show();
        assertTrue(result.isPresent(), "Wizard should return a result");
        assertEquals("joe", result.get().username());
        assertEquals("out", result.get().directory());
    }

    @Test
    void wizardDialogShouldStayOnStepUntilValidationPasses() throws Exception {
        DefaultVirtualTerminal terminal = new DefaultVirtualTerminal();
        terminal.addInput(new KeyStroke(KeyType.Enter));
        terminal.addInput(new KeyStroke('j', false, false));
        terminal.addInput(new KeyStroke('o', false, false));
        terminal.addInput(new KeyStroke('e', false, false));
        terminal.addInput(new KeyStroke(KeyType.Enter));
        terminal.addInput(new KeyStroke(KeyType.Enter));
        terminal.addInput(new KeyStroke('o', false, false));
        terminal.addInput(new KeyStroke('u', false, false));
        terminal.addInput(new KeyStroke('t', false, false));
        terminal.addInput(new KeyStroke(KeyType.Enter));
        terminal.addInput(new KeyStroke(KeyType.Enter));

        WizardDialog<WizardData> dialog = new WizardDialog.Builder<WizardData>(
                "Setup Wizard",
                List.of(
                        WizardTextStep.builder("Account", "Enter username", "username")
                                .withValidator(value -> value.isBlank() ? Optional.of("Required") : Optional.empty())
                                .build(),
                        WizardTextStep.builder("Location", "Enter directory", "directory")
                                .withValidator(value -> value.isBlank() ? Optional.of("Required") : Optional.empty())
                                .build(),
                        WizardSummaryStep.of("Summary", context -> List.of("Ready"))
                )
        )
                .withTerminal(terminal)
                .withResultMapper(context -> new WizardData(
                        context.getString("username"),
                        context.getString("directory")
                ))
                .build();

        Optional<WizardData> result = dialog.show();
        assertTrue(result.isPresent(), "Wizard should finish after validation succeeds");
        assertEquals("joe", result.get().username());
        assertEquals("out", result.get().directory());
    }

    @Test
    void wizardDialogShouldSupportPasswordStep() throws Exception {
        DefaultVirtualTerminal terminal = new DefaultVirtualTerminal();
        terminal.addInput(new KeyStroke('j', false, false));
        terminal.addInput(new KeyStroke('o', false, false));
        terminal.addInput(new KeyStroke('e', false, false));
        terminal.addInput(new KeyStroke(KeyType.Enter));
        terminal.addInput(new KeyStroke('s', false, false));
        terminal.addInput(new KeyStroke('e', false, false));
        terminal.addInput(new KeyStroke('c', false, false));
        terminal.addInput(new KeyStroke('r', false, false));
        terminal.addInput(new KeyStroke('e', false, false));
        terminal.addInput(new KeyStroke('t', false, false));
        terminal.addInput(new KeyStroke(KeyType.Enter));
        terminal.addInput(new KeyStroke('o', false, false));
        terminal.addInput(new KeyStroke('u', false, false));
        terminal.addInput(new KeyStroke('t', false, false));
        terminal.addInput(new KeyStroke(KeyType.Enter));
        terminal.addInput(new KeyStroke(KeyType.Enter));

        WizardDialog<WizardPasswordData> dialog = new WizardDialog.Builder<WizardPasswordData>(
                "Setup Wizard",
                List.of(
                        WizardTextStep.builder("Account", "Enter username", "username")
                                .withValidator(value -> value.isBlank() ? Optional.of("Required") : Optional.empty())
                                .build(),
                        WizardPasswordStep.builder("Security", "Enter password", "password")
                                .withValidator(chars -> chars.length < 6 ? Optional.of("Too short") : Optional.empty())
                                .build(),
                        WizardTextStep.builder("Location", "Enter directory", "directory")
                                .withValidator(value -> value.isBlank() ? Optional.of("Required") : Optional.empty())
                                .build(),
                        WizardSummaryStep.of("Summary", context -> List.of("Ready"))
                )
        )
                .withTerminal(terminal)
                .withResultMapper(context -> new WizardPasswordData(
                        context.getString("username"),
                        context.getPassword("password"),
                        context.getString("directory")
                ))
                .build();

        Optional<WizardPasswordData> result = dialog.show();
        assertTrue(result.isPresent(), "Wizard should finish with password step");
        assertEquals("joe", result.get().username());
        assertArrayEquals("secret".toCharArray(), result.get().password());
        assertEquals("out", result.get().directory());
    }

    private record LoginData(String username, char[] password) {
    }

    private record WizardData(String username, String directory) {
    }

    private record WizardPasswordData(String username, char[] password, String directory) {
    }

    private TitleArea titleArea() {
        return new TitleArea.Builder().withTitle("Title").build();
    }

    private ContentArea contentArea() {
        return new ContentArea.Builder().withContent("Content").build();
    }

    private ContentArea selectedContentArea() {
        return new ContentArea.Builder()
                .withContent("Selected")
                .withForegroundColor(TextColor.ANSI.BLACK)
                .withBackgroundColor(TextColor.ANSI.WHITE)
                .build();
    }

    private NavigationArea navigationArea() {
        return new NavigationArea.Builder()
                .withToolbar(NavigationToolbar.builder().withHorizontalArrowsNavigation().withEnterAccept().build())
                .withTheme(DialogTheme.darkTheme())
                .build();
    }

    private InputArea inputArea() {
        return new InputArea.Builder().build();
    }

    private InputArea focusedInputArea() {
        return new InputArea.Builder()
                .withForegroundColor(TextColor.ANSI.BLACK)
                .withBackgroundColor(TextColor.ANSI.WHITE)
                .build();
    }
}
