package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TextColor;
import io.github.dawciobiel.shelldialog.cli.dialog.option.DialogOption;
import io.github.dawciobiel.shelldialog.cli.dialog.option.SimpleDialogOption;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.style.TextStyle;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.DialogFrame;
import io.github.dawciobiel.shelldialog.cli.ui.InputArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

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
    void textLineDialogBuilderShouldApplyMaxLength() throws Exception {
        TextLineDialog dialog = new TextLineDialog.Builder(
                titleArea(),
                contentArea(),
                inputArea(),
                navigationArea()
        )
                .withMaxLength(12)
                .build();

        assertEquals(12, readField(dialog, "maxLength"));
    }

    @Test
    void textLineDialogBuilderShouldApplyValidator() throws Exception {
        TextLineDialog dialog = new TextLineDialog.Builder(
                titleArea(),
                contentArea(),
                inputArea(),
                navigationArea()
        )
                .withValidator(value -> value.isBlank() ? Optional.of("Required") : Optional.empty())
                .build();

        @SuppressWarnings("unchecked")
        java.util.function.Function<String, Optional<String>> validator =
                (java.util.function.Function<String, Optional<String>>) readField(dialog, "validator");

        assertEquals(Optional.of("Required"), validator.apply(""));
        assertEquals(Optional.empty(), validator.apply("value"));
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

    @Test
    void yesNoDialogBuilderShouldAllowDisablingBorder() throws Exception {
        YesNoDialog dialog = new YesNoDialog.Builder(
                titleArea(),
                contentArea(),
                contentArea(),
                selectedContentArea(),
                navigationArea()
        )
                .withBorder(false)
                .build();

        assertFalse(readBooleanField(dialog, "borderVisible"));
        assertNotNull(readField(dialog, "dialogFrame"));
    }

    @Test
    void yesNoDialogBuilderShouldApplyCustomLabels() throws Exception {
        YesNoDialog dialog = new YesNoDialog.Builder(
                titleArea(),
                contentArea(),
                contentArea(),
                selectedContentArea(),
                navigationArea()
        )
                .withYesLabel("Proceed")
                .withNoLabel("Abort")
                .build();

        assertEquals("Proceed", readField(dialog, "yesLabel"));
        assertEquals("Abort", readField(dialog, "noLabel"));
    }

    @Test
    void yesNoDialogBuilderShouldApplyDefaultFocusedAnswer() throws Exception {
        YesNoDialog dialog = new YesNoDialog.Builder(
                titleArea(),
                contentArea(),
                contentArea(),
                selectedContentArea(),
                navigationArea()
        )
                .withDefaultYesSelected(false)
                .build();

        assertFalse(readBooleanField(dialog, "defaultYesSelected"));
    }

    @Test
    void passwordDialogBuilderShouldApplyMaskCharacter() throws Exception {
        PasswordDialog dialog = new PasswordDialog.Builder(
                titleArea(),
                contentArea(),
                inputArea(),
                navigationArea()
        )
                .withMaskCharacter('#')
                .build();

        assertEquals('#', readField(dialog, "maskCharacter"));
        assertTrue(readBooleanField(dialog, "borderVisible"));
    }

    @Test
    void passwordDialogBuilderShouldApplyMaxLength() throws Exception {
        PasswordDialog dialog = new PasswordDialog.Builder(
                titleArea(),
                contentArea(),
                inputArea(),
                navigationArea()
        )
                .withMaxLength(16)
                .build();

        assertEquals(16, readField(dialog, "maxLength"));
    }

    @Test
    void passwordDialogBuilderShouldApplyValidator() throws Exception {
        PasswordDialog dialog = new PasswordDialog.Builder(
                titleArea(),
                contentArea(),
                inputArea(),
                navigationArea()
        )
                .withValidator(value -> value.length < 6 ? Optional.of("Too short") : Optional.empty())
                .build();

        @SuppressWarnings("unchecked")
        java.util.function.Function<char[], Optional<String>> validator =
                (java.util.function.Function<char[], Optional<String>>) readField(dialog, "validator");

        assertEquals(Optional.of("Too short"), validator.apply("123".toCharArray()));
        assertEquals(Optional.empty(), validator.apply("123456".toCharArray()));
    }

    @Test
    void multiChoiceDialogBuilderShouldAllowDisablingBorder() throws Exception {
        MultiChoiceDialog dialog = new MultiChoiceDialog.Builder(
                titleArea(),
                contentArea(),
                focusedContentArea(),
                selectedContentArea(),
                selectedFocusedContentArea(),
                options(),
                navigationArea()
        )
                .withBorder(false)
                .build();

        assertFalse(readBooleanField(dialog, "borderVisible"));
        assertNotNull(readField(dialog, "dialogFrame"));
    }

    @Test
    void multiChoiceDialogBuilderShouldApplyBorderTheme() throws Exception {
        DialogTheme theme = DialogTheme.builder()
                .borderStyle(TextStyle.of(TextColor.ANSI.MAGENTA, TextColor.ANSI.BLACK))
                .build();

        MultiChoiceDialog dialog = new MultiChoiceDialog.Builder(
                titleArea(),
                contentArea(),
                focusedContentArea(),
                selectedContentArea(),
                selectedFocusedContentArea(),
                options(),
                navigationArea()
        )
                .withTheme(theme)
                .build();

        DialogFrame frame = (DialogFrame) readField(dialog, "dialogFrame");
        TextStyle borderStyle = (TextStyle) readField(frame, "borderStyle");

        assertEquals(TextColor.ANSI.MAGENTA, borderStyle.foreground());
        assertEquals(TextColor.ANSI.BLACK, borderStyle.background());
    }

    @Test
    void multiChoiceDialogBuilderShouldApplyInitiallySelectedOptions() throws Exception {
        List<DialogOption> availableOptions = options();
        MultiChoiceDialog dialog = new MultiChoiceDialog.Builder(
                titleArea(),
                contentArea(),
                focusedContentArea(),
                selectedContentArea(),
                selectedFocusedContentArea(),
                availableOptions,
                navigationArea()
        )
                .withInitiallySelectedOptions(List.of(
                        availableOptions.get(1),
                        new SimpleDialogOption(999, "Unknown")
                ))
                .build();

        @SuppressWarnings("unchecked")
        java.util.Set<Integer> initialSelectedIndices =
                (java.util.Set<Integer>) readField(dialog, "initialSelectedIndices");

        assertEquals(java.util.Set.of(1), initialSelectedIndices);
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

    private ContentArea focusedContentArea() {
        return new ContentArea.Builder()
                .withContent("Focused")
                .withForegroundColor(TextColor.ANSI.BLACK)
                .withBackgroundColor(TextColor.ANSI.YELLOW)
                .build();
    }

    private ContentArea selectedFocusedContentArea() {
        return new ContentArea.Builder()
                .withContent("SelectedFocused")
                .withForegroundColor(TextColor.ANSI.BLACK)
                .withBackgroundColor(TextColor.ANSI.GREEN)
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
        Class<?> currentClass = target.getClass();
        while (currentClass != null) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException ignored) {
                currentClass = currentClass.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    private boolean readBooleanField(Object target, String fieldName) throws Exception {
        return (boolean) readField(target, fieldName);
    }
}
