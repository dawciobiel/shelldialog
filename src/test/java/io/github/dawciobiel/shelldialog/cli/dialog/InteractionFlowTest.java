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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InteractionFlowTest {

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

    private record LoginData(String username, char[] password) {
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
