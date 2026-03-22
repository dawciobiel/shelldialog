package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import io.github.dawciobiel.shelldialog.cli.dialog.option.DialogOption;
import io.github.dawciobiel.shelldialog.cli.i18n.UIProperties;
import io.github.dawciobiel.shelldialog.cli.style.MultiChoiceMarker;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.DialogFrame;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.io.IOException;
import java.util.*;

/**
 * A CLI dialog that allows selecting multiple options from a list.
 * It composes preconfigured UI areas inside a shared optional frame.
 * The dialog distinguishes four visual row states: regular, focused, selected, and selected+focused.
 */
public class MultiChoiceDialog extends AbstractDialog<List<DialogOption>> {

    private static final String MORE_ABOVE_LABEL = "\u2191 more";
    private static final String MORE_BELOW_LABEL = "\u2193 more";
    private static final String DISABLED_SUFFIX = UIProperties.getString("dialog.option.disabled_suffix");

    private final TitleArea titleArea;
    private final ContentArea menuItemArea;
    private final ContentArea focusedMenuItemArea;
    private final ContentArea selectedMenuItemArea;
    private final ContentArea selectedFocusedMenuItemArea;
    private final List<DialogOption> options;
    private final Set<Integer> initialSelectedIndices;
    private final NavigationArea navigationArea;
    private final boolean borderVisible;
    private final DialogFrame dialogFrame;
    private final int visibleItemCount;

    private MultiChoiceDialog(Builder builder) {
        super(builder.inputStreamPath, builder.outputStreamPath);
        this.titleArea = builder.titleArea;
        this.menuItemArea = builder.menuItemArea;
        this.focusedMenuItemArea = builder.focusedMenuItemArea;
        this.selectedMenuItemArea = builder.selectedMenuItemArea;
        this.selectedFocusedMenuItemArea = builder.selectedFocusedMenuItemArea;
        this.options = builder.options;
        this.initialSelectedIndices = builder.initialSelectedIndices;
        this.navigationArea = builder.navigationArea;
        this.borderVisible = builder.borderVisible;
        this.dialogFrame = new DialogFrame(borderVisible, builder.borderStyle);
        this.visibleItemCount = builder.visibleItemCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Optional<List<DialogOption>> runDialog(Screen screen) throws IOException {
        int focusedIndex = initialFocusedIndex();
        Set<Integer> selectedIndices = new LinkedHashSet<>(initialSelectedIndices);
        screen.setCursorPosition(null);
        TextGraphics tg = screen.newTextGraphics();

        while (true) {
            render(screen, tg, focusedIndex, selectedIndices);

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            switch (type) {
                case ArrowUp -> {
                    focusedIndex = previousEnabledIndex(focusedIndex);
                }
                case ArrowDown -> {
                    focusedIndex = nextEnabledIndex(focusedIndex);
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

        int firstVisibleIndex = firstVisibleIndex(focusedIndex);
        int lastVisibleIndex = lastVisibleIndex(firstVisibleIndex);
        List<DialogOption> visibleOptions = options.subList(firstVisibleIndex, lastVisibleIndex);
        boolean hasItemsAbove = firstVisibleIndex > 0;
        boolean hasItemsBelow = lastVisibleIndex < options.size();
        boolean hasViewport = hasViewport();
        String positionIndicator = hasViewport ? positionIndicatorLabel(focusedIndex) : "";

        int optionsWidth = Math.max(
                visibleOptions.stream()
                .mapToInt(option -> menuItemWidth(displayLabel(option)))
                .max()
                .orElse(0),
                moreIndicatorWidth(hasItemsAbove, hasItemsBelow)
        );
        if (hasViewport) {
            optionsWidth = Math.max(optionsWidth, positionIndicator.length());
        }
        int contentWidth = Math.max(
                Math.max(titleArea.getWidth(), optionsWidth),
                navigationArea.getWidth()
        );
        int contentHeight = titleArea.getHeight()
                + 1
                + (hasItemsAbove ? 1 : 0)
                + visibleOptions.size()
                + (hasItemsBelow ? 1 : 0)
                + (hasViewport ? 1 : 0)
                + 1
                + navigationArea.getHeight();
        DialogFrame.FrameLayout layout = dialogFrame.layoutFor(contentWidth, contentHeight);
        dialogFrame.render(tg, layout);

        int column = layout.contentColumn();
        int row = layout.contentRow();
        titleArea.render(tg, column, row);
        row += titleArea.getHeight();

        row++;

        if (hasItemsAbove) {
            menuItemArea.withContent(MORE_ABOVE_LABEL).render(tg, column, row++);
        }

        for (int i = firstVisibleIndex; i < lastVisibleIndex; i++) {
            DialogOption option = options.get(i);
            boolean focused = i == focusedIndex && option.isEnabled();
            renderMenuItem(tg, column, row++, option, focused, selectedIndices.contains(i));
        }

        if (hasItemsBelow) {
            menuItemArea.withContent(MORE_BELOW_LABEL).render(tg, column, row++);
        }

        if (hasViewport) {
            menuItemArea.withContent(positionIndicator).render(tg, column, row++);
        }

        row++;

        navigationArea.render(tg, column, row);
        screen.refresh();
    }

    private void renderMenuItem(TextGraphics tg, int column, int row, DialogOption option, boolean focused, boolean selected)
            throws IOException {
        String item = displayLabel(option);
        String marker = selected ? MultiChoiceMarker.SELECTED : MultiChoiceMarker.UNSELECTED;
        String text = marker + " " + item;
        ContentArea currentArea = resolveArea(focused, selected && option.isEnabled()).withContent(text);
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

    private String displayLabel(DialogOption option) {
        return option.getLabel() + (option.isEnabled() ? "" : DISABLED_SUFFIX);
    }

    private int firstVisibleIndex(int focusedIndex) {
        if (visibleItemCount <= 0 || visibleItemCount >= options.size()) {
            return 0;
        }
        int maxStartIndex = options.size() - visibleItemCount;
        return Math.min(Math.max(0, focusedIndex - visibleItemCount + 1), maxStartIndex);
    }

    private int lastVisibleIndex(int firstVisibleIndex) {
        if (visibleItemCount <= 0 || visibleItemCount >= options.size()) {
            return options.size();
        }
        return Math.min(options.size(), firstVisibleIndex + visibleItemCount);
    }

    private int moreIndicatorWidth(boolean hasItemsAbove, boolean hasItemsBelow) {
        int width = 0;
        if (hasItemsAbove) {
            width = MORE_ABOVE_LABEL.length();
        }
        if (hasItemsBelow) {
            width = Math.max(width, MORE_BELOW_LABEL.length());
        }
        return width;
    }

    private boolean hasViewport() {
        return visibleItemCount > 0 && visibleItemCount < options.size();
    }

    private String positionIndicatorLabel(int focusedIndex) {
        return (focusedIndex + 1) + "/" + options.size();
    }

    private void toggleSelection(Set<Integer> selectedIndices, int focusedIndex) {
        if (focusedIndex < 0 || focusedIndex >= options.size() || !options.get(focusedIndex).isEnabled()) {
            return;
        }
        if (!selectedIndices.add(focusedIndex)) {
            selectedIndices.remove(focusedIndex);
        }
    }

    private int initialFocusedIndex() {
        for (int index = 0; index < options.size(); index++) {
            if (options.get(index).isEnabled()) {
                return index;
            }
        }
        return 0;
    }

    private int nextEnabledIndex(int currentIndex) {
        for (int index = currentIndex + 1; index < options.size(); index++) {
            if (options.get(index).isEnabled()) {
                return index;
            }
        }
        return currentIndex;
    }

    private int previousEnabledIndex(int currentIndex) {
        for (int index = currentIndex - 1; index >= 0; index--) {
            if (options.get(index).isEnabled()) {
                return index;
            }
        }
        return currentIndex;
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
        private Set<Integer> initialSelectedIndices = Set.of();
        private int visibleItemCount = 0;
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
         * Sets the options that should be marked as selected when the dialog opens.
         * Options are matched by their numeric codes. Unknown codes are ignored.
         *
         * @param selectedOptions the options that should start selected
         * @return this builder
         */
        public Builder withInitiallySelectedOptions(List<DialogOption> selectedOptions) {
            Objects.requireNonNull(selectedOptions);
            Set<Integer> selectedCodes = new HashSet<>();
            for (DialogOption option : selectedOptions) {
                selectedCodes.add(Objects.requireNonNull(option).getCode());
            }

            LinkedHashSet<Integer> resolvedIndices = new LinkedHashSet<>();
            for (int index = 0; index < options.size(); index++) {
                if (options.get(index).isEnabled() && selectedCodes.contains(options.get(index).getCode())) {
                    resolvedIndices.add(index);
                }
            }

            this.initialSelectedIndices = Set.copyOf(resolvedIndices);
            return this;
        }

        /**
         * Limits the number of menu items visible at once.
         * When the focus moves outside the visible window, the dialog scrolls the list.
         *
         * @param visibleItemCount the maximum number of visible menu items, must be positive
         * @return this builder
         */
        public Builder withVisibleItemCount(int visibleItemCount) {
            if (visibleItemCount <= 0) {
                throw new IllegalArgumentException("visibleItemCount must be positive");
            }
            this.visibleItemCount = visibleItemCount;
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
