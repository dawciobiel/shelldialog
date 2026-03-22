package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import org.dawciobiel.shelldialog.cli.dialog.option.DialogOption;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;
import org.dawciobiel.shelldialog.cli.style.MultiChoiceMarker;
import org.dawciobiel.shelldialog.cli.style.TextStyle;
import org.dawciobiel.shelldialog.cli.ui.ContentArea;
import org.dawciobiel.shelldialog.cli.ui.DialogFrame;
import org.dawciobiel.shelldialog.cli.ui.NavigationArea;
import org.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A CLI dialog that allows selecting multiple options from a list.
 * It composes preconfigured UI areas inside a shared optional frame.
 * The dialog distinguishes four visual row states: regular, focused, selected, and selected+focused.
 */
public class MultiChoiceDialog extends AbstractDialog<List<DialogOption>> {

    private final TitleArea titleArea;
    private final ContentArea menuItemArea;
    private final ContentArea focusedMenuItemArea;
    private final ContentArea selectedMenuItemArea;
    private final ContentArea selectedFocusedMenuItemArea;
    private final List<DialogOption> options;
    private final NavigationArea navigationArea;
    private final boolean borderVisible;
    private final DialogFrame dialogFrame;

    private MultiChoiceDialog(Builder builder) {
        super(builder.inputStreamPath, builder.outputStreamPath);
        this.titleArea = builder.titleArea;
        this.menuItemArea = builder.menuItemArea;
        this.focusedMenuItemArea = builder.focusedMenuItemArea;
        this.selectedMenuItemArea = builder.selectedMenuItemArea;
        this.selectedFocusedMenuItemArea = builder.selectedFocusedMenuItemArea;
        this.options = builder.options;
        this.navigationArea = builder.navigationArea;
        this.borderVisible = builder.borderVisible;
        this.dialogFrame = new DialogFrame(borderVisible, builder.borderStyle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Optional<List<DialogOption>> runDialog(Screen screen) throws IOException {
        int focusedIndex = 0;
        Set<Integer> selectedIndices = new LinkedHashSet<>();
        screen.setCursorPosition(null);
        TextGraphics tg = screen.newTextGraphics();

        while (true) {
            render(screen, tg, focusedIndex, selectedIndices);

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            switch (type) {
                case ArrowUp -> {
                    if (focusedIndex > 0) {
                        focusedIndex--;
                    }
                }
                case ArrowDown -> {
                    if (focusedIndex < options.size() - 1) {
                        focusedIndex++;
                    }
                }
                case Character -> {
                    Character character = key.getCharacter();
                    if (character != null && character == ' ') {
                        toggleSelection(selectedIndices, focusedIndex);
                    }
                }
                case Enter -> {
                    return Optional.of(selectedOptions(selectedIndices));
                }
                case Escape -> {
                    return Optional.empty();
                }
                default -> {
                }
            }
        }
    }

    private void render(Screen screen, TextGraphics tg, int focusedIndex, Set<Integer> selectedIndices) throws IOException {
        screen.clear();

        int optionsWidth = options.stream()
                .mapToInt(option -> menuItemWidth(option.getLabel()))
                .max()
                .orElse(0);
        int contentWidth = Math.max(
                Math.max(titleArea.getWidth(), optionsWidth),
                navigationArea.getWidth()
        );
        int contentHeight = titleArea.getHeight()
                + 1
                + options.size()
                + 1
                + navigationArea.getHeight();
        DialogFrame.FrameLayout layout = dialogFrame.layoutFor(contentWidth, contentHeight);
        dialogFrame.render(tg, layout);

        int column = layout.contentColumn();
        int row = layout.contentRow();
        titleArea.render(tg, column, row);
        row += titleArea.getHeight();

        row++;

        for (int i = 0; i < options.size(); i++) {
            renderMenuItem(tg, column, row++, options.get(i).getLabel(), i == focusedIndex, selectedIndices.contains(i));
        }
        row++;

        navigationArea.render(tg, column, row);
        screen.refresh();
    }

    private void renderMenuItem(TextGraphics tg, int column, int row, String item, boolean focused, boolean selected)
            throws IOException {
        String marker = selected ? MultiChoiceMarker.SELECTED : MultiChoiceMarker.UNSELECTED;
        String text = marker + " " + item;
        ContentArea currentArea = resolveArea(focused, selected).withContent(text);
        currentArea.render(tg, column, row);
    }

    private ContentArea resolveArea(boolean focused, boolean selected) {
        if (focused && selected) {
            return selectedFocusedMenuItemArea;
        }
        if (focused) {
            return focusedMenuItemArea;
        }
        if (selected) {
            return selectedMenuItemArea;
        }
        return menuItemArea;
    }

    private int menuItemWidth(String item) {
        return MultiChoiceMarker.UNSELECTED.length() + 1 + item.length();
    }

    private void toggleSelection(Set<Integer> selectedIndices, int focusedIndex) {
        if (!selectedIndices.add(focusedIndex)) {
            selectedIndices.remove(focusedIndex);
        }
    }

    private List<DialogOption> selectedOptions(Set<Integer> selectedIndices) {
        List<DialogOption> selected = new ArrayList<>();
        for (int index = 0; index < options.size(); index++) {
            if (selectedIndices.contains(index)) {
                selected.add(options.get(index));
            }
        }
        return List.copyOf(selected);
    }

    /**
     * Builder for creating instances of {@link MultiChoiceDialog}.
     * Separate content templates are required for every visual row state.
     */
    public static class Builder extends AbstractFrameDialogBuilder<Builder> {

        private final TitleArea titleArea;
        private final ContentArea menuItemArea;
        private final ContentArea focusedMenuItemArea;
        private final ContentArea selectedMenuItemArea;
        private final ContentArea selectedFocusedMenuItemArea;
        private final List<DialogOption> options;
        private final NavigationArea navigationArea;
        private String inputStreamPath = "/dev/tty";
        private String outputStreamPath = "/dev/tty";

        /**
         * Creates a new Builder with the specified UI areas and options.
         *
         * @param titleArea the preconfigured {@link TitleArea} to render
         * @param menuItemArea the area used for regular items
         * @param focusedMenuItemArea the area used for the focused item
         * @param selectedMenuItemArea the area used for selected items
         * @param selectedFocusedMenuItemArea the area used for selected and focused items
         * @param options the selectable options
         * @param navigationArea the preconfigured {@link NavigationArea} to render
         */
        public Builder(
                TitleArea titleArea,
                ContentArea menuItemArea,
                ContentArea focusedMenuItemArea,
                ContentArea selectedMenuItemArea,
                ContentArea selectedFocusedMenuItemArea,
                List<DialogOption> options,
                NavigationArea navigationArea
        ) {
            this.titleArea = Objects.requireNonNull(titleArea);
            this.menuItemArea = Objects.requireNonNull(menuItemArea);
            this.focusedMenuItemArea = Objects.requireNonNull(focusedMenuItemArea);
            this.selectedMenuItemArea = Objects.requireNonNull(selectedMenuItemArea);
            this.selectedFocusedMenuItemArea = Objects.requireNonNull(selectedFocusedMenuItemArea);
            this.options = List.copyOf(Objects.requireNonNull(options));
            this.navigationArea = Objects.requireNonNull(navigationArea);
        }

        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Sets the input stream path (e.g. {@code /dev/tty}).
         *
         * @param path the path to the input stream
         * @return this builder
         */
        public Builder inputStream(String path) {
            this.inputStreamPath = Objects.requireNonNull(path);
            return this;
        }

        /**
         * Sets the output stream path (e.g. {@code /dev/tty}).
         *
         * @param path the path to the output stream
         * @return this builder
         */
        public Builder outputStream(String path) {
            this.outputStreamPath = Objects.requireNonNull(path);
            return this;
        }

        /**
         * Builds the {@link MultiChoiceDialog} instance.
         *
         * @return a new {@link MultiChoiceDialog}
         */
        public MultiChoiceDialog build() {
            return new MultiChoiceDialog(this);
        }
    }
}
