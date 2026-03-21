package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TextColor;
import org.dawciobiel.shelldialog.cli.dialog.option.DialogOption;
import org.dawciobiel.shelldialog.cli.dialog.option.SimpleDialogOption;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;
import org.dawciobiel.shelldialog.cli.style.TextStyle;
import org.dawciobiel.shelldialog.cli.ui.ContentArea;
import org.dawciobiel.shelldialog.cli.ui.DialogFrame;
import org.dawciobiel.shelldialog.cli.ui.InputArea;
import org.dawciobiel.shelldialog.cli.ui.NavigationArea;
import org.dawciobiel.shelldialog.cli.ui.TitleArea;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DialogBuilderTest {

    @Test
    void textLineDialogBuilderShouldAllowDisablingBorder() throws Exception {
        TextLineDialog dialog = new TextLineDialog.Builder(
                titleArea(),
                contentArea(),
                inputArea(),
                navigationArea()
        )
                .withBorder(false)
                .build();

        assertFalse(readBooleanField(dialog, "borderVisible"));
        assertNotNull(readField(dialog, "dialogFrame"));
    }

    @Test
    void textLineDialogBuilderShouldApplyBorderStyleFromTheme() throws Exception {
        DialogTheme theme = DialogTheme.builder()
                .borderStyle(TextStyle.of(TextColor.ANSI.GREEN, TextColor.ANSI.BLACK))
                .build();

        TextLineDialog dialog = new TextLineDialog.Builder(
                titleArea(),
                contentArea(),
                inputArea(),
                navigationArea()
        )
                .withTheme(theme)
                .build();

        DialogFrame frame = (DialogFrame) readField(dialog, "dialogFrame");
        TextStyle borderStyle = (TextStyle) readField(frame, "borderStyle");

        assertEquals(TextColor.ANSI.GREEN, borderStyle.foreground());
        assertEquals(TextColor.ANSI.BLACK, borderStyle.background());
    }

    @Test
    void singleChoiceDialogBuilderShouldAllowDisablingBorder() throws Exception {
        SingleChoiceDialog dialog = new SingleChoiceDialog.Builder(
                titleArea(),
                contentArea(),
                selectedContentArea(),
                options(),
                navigationArea()
        )
                .withBorder(false)
                .build();

        assertFalse(readBooleanField(dialog, "borderVisible"));
        assertNotNull(readField(dialog, "dialogFrame"));
    }

    @Test
    void singleChoiceDialogBuilderShouldApplyExplicitBorderColor() throws Exception {
        SingleChoiceDialog dialog = new SingleChoiceDialog.Builder(
                titleArea(),
                contentArea(),
                selectedContentArea(),
                options(),
                navigationArea()
        )
                .withBorderColor(TextColor.ANSI.BLUE)
                .build();

        DialogFrame frame = (DialogFrame) readField(dialog, "dialogFrame");
        TextStyle borderStyle = (TextStyle) readField(frame, "borderStyle");

        assertEquals(TextColor.ANSI.BLUE, borderStyle.foreground());
        assertEquals(TextColor.ANSI.DEFAULT, borderStyle.background());
        assertTrue(readBooleanField(dialog, "borderVisible"));
    }

    private TitleArea titleArea() {
        return new TitleArea.Builder()
                .withTitle("Title")
                .build();
    }

    private ContentArea contentArea() {
        return new ContentArea.Builder()
                .withContent("Content")
                .build();
    }

    private ContentArea selectedContentArea() {
        return new ContentArea.Builder()
                .withContent("Selected")
                .withForegroundColor(TextColor.ANSI.BLACK)
                .withBackgroundColor(TextColor.ANSI.WHITE)
                .build();
    }

    private InputArea inputArea() {
        return new InputArea.Builder()
                .withContent("Input")
                .build();
    }

    private NavigationArea navigationArea() {
        return new NavigationArea.Builder()
                .withToolbar(
                        NavigationToolbar.builder()
                                .withEnterAccept()
                                .withEscapeCancel()
                                .build()
                )
                .withTheme(DialogTheme.darkTheme())
                .build();
    }

    private List<DialogOption> options() {
        return List.of(
                new SimpleDialogOption(1, "One"),
                new SimpleDialogOption(2, "Two")
        );
    }

    private Object readField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private boolean readBooleanField(Object target, String fieldName) throws Exception {
        return (boolean) readField(target, fieldName);
    }
}
