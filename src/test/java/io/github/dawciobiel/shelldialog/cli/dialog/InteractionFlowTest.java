package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.virtual.DefaultVirtualTerminal;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
