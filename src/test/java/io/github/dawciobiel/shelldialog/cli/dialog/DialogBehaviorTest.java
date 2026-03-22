package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TextColor;
import io.github.dawciobiel.shelldialog.cli.dialog.option.DialogOption;
import io.github.dawciobiel.shelldialog.cli.dialog.option.SimpleDialogOption;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.style.MultiChoiceMarker;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.InputArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DialogBehaviorTest {

    @Test
    void textLineDialogBuilderShouldRejectNonPositiveMaxLength() {
        TextLineDialog.Builder builder = new TextLineDialog.Builder(
                titleArea(),
                contentArea(),
                inputArea(),
                navigationArea()
        );

        assertThrows(IllegalArgumentException.class, () -> builder.withMaxLength(0));
        assertThrows(IllegalArgumentException.class, () -> builder.withMaxLength(-1));
    }

    @Test
    void passwordDialogBuilderShouldRejectNonPositiveMaxLength() {
        PasswordDialog.Builder builder = new PasswordDialog.Builder(
                titleArea(),
                contentArea(),
                inputArea(),
                navigationArea()
        );

        assertThrows(IllegalArgumentException.class, () -> builder.withMaxLength(0));
        assertThrows(IllegalArgumentException.class, () -> builder.withMaxLength(-1));
    }

    @Test
    void yesNoDialogShouldDecorateChoicesWithBrackets() throws Exception {
        YesNoDialog dialog = new YesNoDialog.Builder(
                titleArea(),
                contentArea(),
                contentArea(),
                selectedContentArea(),
                navigationArea()
        )
                .withYesLabel("Proceed")
                .build();

        String decoratedChoice = (String) invokeMethod(dialog, "decorateChoice", new Class<?>[]{String.class}, "Proceed");

        assertEquals("[Proceed]", decoratedChoice);
    }

    @Test
    void multiChoiceDialogShouldToggleSelectionOnFocusedIndex() throws Exception {
        MultiChoiceDialog dialog = multiChoiceDialog();
        Set<Integer> selectedIndices = new LinkedHashSet<>();

        invokeMethod(dialog, "toggleSelection", new Class<?>[]{Set.class, int.class}, selectedIndices, 1);
        assertTrue(selectedIndices.contains(1));

        invokeMethod(dialog, "toggleSelection", new Class<?>[]{Set.class, int.class}, selectedIndices, 1);
        assertFalse(selectedIndices.contains(1));
    }

    @Test
    void multiChoiceDialogShouldIgnoreSelectionToggleForDisabledOption() throws Exception {
        MultiChoiceDialog dialog = multiChoiceDialog(disabledOptions(), 0);
        Set<Integer> selectedIndices = new LinkedHashSet<>();

        invokeMethod(dialog, "toggleSelection", new Class<?>[]{Set.class, int.class}, selectedIndices, 1);

        assertTrue(selectedIndices.isEmpty());
    }

    @Test
    void multiChoiceDialogShouldReturnSelectedOptionsInOriginalOrder() throws Exception {
        MultiChoiceDialog dialog = multiChoiceDialog();
        Set<Integer> selectedIndices = new LinkedHashSet<>();
        selectedIndices.add(2);
        selectedIndices.add(0);

        @SuppressWarnings("unchecked")
        List<DialogOption> selectedOptions = (List<DialogOption>) invokeMethod(
                dialog,
                "selectedOptions",
                new Class<?>[]{Set.class},
                selectedIndices
        );

        assertEquals(List.of("One", "Three"), selectedOptions.stream().map(DialogOption::getLabel).toList());
    }

    @Test
    void multiChoiceDialogShouldSkipDisabledItemsWhenMovingFocus() throws Exception {
        MultiChoiceDialog dialog = multiChoiceDialog(disabledOptions(), 0);

        int nextEnabledIndex = (int) invokeMethod(dialog, "nextEnabledIndex", new Class<?>[]{int.class}, 0);
        int previousEnabledIndex = (int) invokeMethod(dialog, "previousEnabledIndex", new Class<?>[]{int.class}, 2);

        assertEquals(2, nextEnabledIndex);
        assertEquals(0, previousEnabledIndex);
    }

    @Test
    void multiChoiceDialogShouldResolveDedicatedAreaForEveryVisualState() throws Exception {
        MultiChoiceDialog dialog = multiChoiceDialog();

        Object regular = invokeMethod(dialog, "resolveArea", new Class<?>[]{boolean.class, boolean.class}, false, false);
        Object focused = invokeMethod(dialog, "resolveArea", new Class<?>[]{boolean.class, boolean.class}, true, false);
        Object selected = invokeMethod(dialog, "resolveArea", new Class<?>[]{boolean.class, boolean.class}, false, true);
        Object selectedFocused = invokeMethod(dialog, "resolveArea", new Class<?>[]{boolean.class, boolean.class}, true, true);

        assertEquals(readField(dialog, "menuItemArea"), regular);
        assertEquals(readField(dialog, "focusedMenuItemArea"), focused);
        assertEquals(readField(dialog, "selectedMenuItemArea"), selected);
        assertEquals(readField(dialog, "selectedFocusedMenuItemArea"), selectedFocused);
    }

    @Test
    void multiChoiceDialogShouldAccountForMarkerAndSpacingInMenuItemWidth() throws Exception {
        MultiChoiceDialog dialog = multiChoiceDialog();

        int width = (int) invokeMethod(dialog, "menuItemWidth", new Class<?>[]{String.class}, "Alpha");

        assertEquals(MultiChoiceMarker.UNSELECTED.length() + 1 + "Alpha".length(), width);
    }

    @Test
    void multiChoiceDialogShouldStartViewportAtZeroWhenLimitIsNotReached() throws Exception {
        MultiChoiceDialog dialog = multiChoiceDialog(3);

        int firstVisibleIndex = (int) invokeMethod(dialog, "firstVisibleIndex", new Class<?>[]{int.class}, 1);

        assertEquals(0, firstVisibleIndex);
    }

    @Test
    void multiChoiceDialogShouldMoveViewportWhenFocusLeavesVisibleWindow() throws Exception {
        MultiChoiceDialog dialog = multiChoiceDialog(3);

        int firstVisibleIndex = (int) invokeMethod(dialog, "firstVisibleIndex", new Class<?>[]{int.class}, 4);
        int lastVisibleIndex = (int) invokeMethod(dialog, "lastVisibleIndex", new Class<?>[]{int.class}, firstVisibleIndex);

        assertEquals(2, firstVisibleIndex);
        assertEquals(5, lastVisibleIndex);
    }

    @Test
    void multiChoiceDialogShouldShowAllItemsWhenNoViewportLimitIsConfigured() throws Exception {
        MultiChoiceDialog dialog = multiChoiceDialog(0);

        int firstVisibleIndex = (int) invokeMethod(dialog, "firstVisibleIndex", new Class<?>[]{int.class}, 4);
        int lastVisibleIndex = (int) invokeMethod(dialog, "lastVisibleIndex", new Class<?>[]{int.class}, firstVisibleIndex);

        assertEquals(0, firstVisibleIndex);
        assertEquals(options().size(), lastVisibleIndex);
    }

    @Test
    void multiChoiceDialogShouldReserveWidthForMoreIndicatorsWhenViewportIsClipped() throws Exception {
        MultiChoiceDialog dialog = multiChoiceDialog(3);

        int width = (int) invokeMethod(dialog, "moreIndicatorWidth", new Class<?>[]{boolean.class, boolean.class}, true, true);

        assertEquals("\u2191 more".length(), width);
    }

    @Test
    void multiChoiceDialogShouldNotReserveMoreIndicatorWidthWhenEverythingIsVisible() throws Exception {
        MultiChoiceDialog dialog = multiChoiceDialog(0);

        int width = (int) invokeMethod(dialog, "moreIndicatorWidth", new Class<?>[]{boolean.class, boolean.class}, false, false);

        assertEquals(0, width);
    }

    @Test
    void multiChoiceDialogShouldFormatPositionIndicatorFromFocusedIndex() throws Exception {
        MultiChoiceDialog dialog = multiChoiceDialog(3);

        String label = (String) invokeMethod(dialog, "positionIndicatorLabel", new Class<?>[]{int.class}, 2);

        assertEquals("3/5", label);
    }

    @Test
    void singleChoiceDialogShouldStartViewportAtZeroWhenLimitIsNotReached() throws Exception {
        SingleChoiceDialog dialog = singleChoiceDialog(3);

        int firstVisibleIndex = (int) invokeMethod(dialog, "firstVisibleIndex", new Class<?>[]{int.class}, 1);

        assertEquals(0, firstVisibleIndex);
    }

    @Test
    void singleChoiceDialogShouldMoveViewportWhenSelectionLeavesVisibleWindow() throws Exception {
        SingleChoiceDialog dialog = singleChoiceDialog(3);

        int firstVisibleIndex = (int) invokeMethod(dialog, "firstVisibleIndex", new Class<?>[]{int.class}, 4);
        int lastVisibleIndex = (int) invokeMethod(dialog, "lastVisibleIndex", new Class<?>[]{int.class}, firstVisibleIndex);

        assertEquals(2, firstVisibleIndex);
        assertEquals(5, lastVisibleIndex);
    }

    @Test
    void singleChoiceDialogShouldShowAllItemsWhenNoViewportLimitIsConfigured() throws Exception {
        SingleChoiceDialog dialog = singleChoiceDialog(0);

        int firstVisibleIndex = (int) invokeMethod(dialog, "firstVisibleIndex", new Class<?>[]{int.class}, 4);
        int lastVisibleIndex = (int) invokeMethod(dialog, "lastVisibleIndex", new Class<?>[]{int.class}, firstVisibleIndex);

        assertEquals(0, firstVisibleIndex);
        assertEquals(options().size(), lastVisibleIndex);
    }

    @Test
    void singleChoiceDialogShouldReserveWidthForMoreIndicatorsWhenViewportIsClipped() throws Exception {
        SingleChoiceDialog dialog = singleChoiceDialog(3);

        int width = (int) invokeMethod(dialog, "moreIndicatorWidth", new Class<?>[]{boolean.class, boolean.class}, true, true);

        assertEquals("\u2191 more".length(), width);
    }

    @Test
    void singleChoiceDialogShouldNotReserveMoreIndicatorWidthWhenEverythingIsVisible() throws Exception {
        SingleChoiceDialog dialog = singleChoiceDialog(0);

        int width = (int) invokeMethod(dialog, "moreIndicatorWidth", new Class<?>[]{boolean.class, boolean.class}, false, false);

        assertEquals(0, width);
    }

    @Test
    void singleChoiceDialogShouldFormatPositionIndicatorFromSelectedIndex() throws Exception {
        SingleChoiceDialog dialog = singleChoiceDialog(3);

        String label = (String) invokeMethod(dialog, "positionIndicatorLabel", new Class<?>[]{int.class}, 2);

        assertEquals("3/5", label);
    }

    @Test
    void singleChoiceDialogShouldSkipDisabledItemsWhenMovingSelection() throws Exception {
        SingleChoiceDialog dialog = singleChoiceDialog(disabledOptions(), 0);

        int nextEnabledIndex = (int) invokeMethod(dialog, "nextEnabledIndex", new Class<?>[]{int.class}, 0);
        int previousEnabledIndex = (int) invokeMethod(dialog, "previousEnabledIndex", new Class<?>[]{int.class}, 2);

        assertEquals(2, nextEnabledIndex);
        assertEquals(0, previousEnabledIndex);
    }

    @Test
    void singleChoiceDialogShouldDecorateDisabledLabels() throws Exception {
        SingleChoiceDialog dialog = singleChoiceDialog(disabledOptions(), 0);

        String label = (String) invokeMethod(
                dialog,
                "displayLabel",
                new Class<?>[]{DialogOption.class},
                disabledOptions().get(1)
        );

        assertEquals("Two (disabled)", label);
    }

    private MultiChoiceDialog multiChoiceDialog() {
        return multiChoiceDialog(options(), 0);
    }

    private MultiChoiceDialog multiChoiceDialog(int visibleItemCount) {
        return multiChoiceDialog(options(), visibleItemCount);
    }

    private MultiChoiceDialog multiChoiceDialog(List<DialogOption> options, int visibleItemCount) {
        MultiChoiceDialog.Builder builder = new MultiChoiceDialog.Builder(
                titleArea(),
                contentArea(),
                focusedContentArea(),
                selectedContentArea(),
                selectedFocusedContentArea(),
                options,
                navigationArea()
        );
        if (visibleItemCount > 0) {
            builder.withVisibleItemCount(visibleItemCount);
        }
        return builder.build();
    }

    private SingleChoiceDialog singleChoiceDialog(int visibleItemCount) {
        return singleChoiceDialog(options(), visibleItemCount);
    }

    private SingleChoiceDialog singleChoiceDialog(List<DialogOption> options, int visibleItemCount) {
        SingleChoiceDialog.Builder builder = new SingleChoiceDialog.Builder(
                titleArea(),
                contentArea(),
                selectedContentArea(),
                options,
                navigationArea()
        );
        if (visibleItemCount > 0) {
            builder.withVisibleItemCount(visibleItemCount);
        }
        return builder.build();
    }

    private Object invokeMethod(Object target, String methodName, Class<?>[] parameterTypes, Object... args) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    private Object readField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private TitleArea titleArea() {
        return new TitleArea.Builder()
                .withTitle("Title")
                .build();
    }

    private ContentArea contentArea() {
        return new ContentArea.Builder()
                .withContent("Item")
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
                new SimpleDialogOption(2, "Two"),
                new SimpleDialogOption(3, "Three"),
                new SimpleDialogOption(4, "Four"),
                new SimpleDialogOption(5, "Five")
        );
    }

    private List<DialogOption> disabledOptions() {
        return List.of(
                new SimpleDialogOption(1, "One"),
                new SimpleDialogOption(2, "Two", false),
                new SimpleDialogOption(3, "Three"),
                new SimpleDialogOption(4, "Four", false),
                new SimpleDialogOption(5, "Five")
        );
    }
}
