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
 * Supports live filtering by typing.
 */
public class MultiChoiceDialog extends AbstractListDialog<List<DialogOption>> {

    private static final String MORE_ABOVE_LABEL = "\u2191 more";
    private static final String MORE_BELOW_LABEL = "\u2193 more";
    private static final String DISABLED_SUFFIX = UIProperties.getString("dialog.option.disabled_suffix");

    private final TitleArea titleArea;
    private final ContentArea menuItemArea;
    private final ContentArea focusedMenuItemArea;
    private final ContentArea selectedMenuItemArea;
    private final ContentArea selectedFocusedMenuItemArea;
    private final Set<DialogOption> selectedOptions;
    private final NavigationArea navigationArea;
    private final boolean borderVisible;
    private final DialogFrame dialogFrame;

    private MultiChoiceDialog(Builder builder) {
        super(builder.inputStreamPath, builder.outputStreamPath, builder.options, builder.visibleItemCount);
        this.titleArea = builder.titleArea;
        this.menuItemArea = builder.menuItemArea;
        this.focusedMenuItemArea = builder.focusedMenuItemArea;
        this.selectedMenuItemArea = builder.selectedMenuItemArea;
        this.selectedFocusedMenuItemArea = builder.selectedFocusedMenuItemArea;
        this.selectedOptions = new HashSet<>();
        
        // Initialize selection from builder
        for (Integer index : builder.initialSelectedIndices) {
            if (index >= 0 && index < allOptions.size()) {
                selectedOptions.add(allOptions.get(index));
            }
        }
        
        this.navigationArea = builder.navigationArea;
        this.borderVisible = builder.borderVisible;
        this.dialogFrame = new DialogFrame(borderVisible, builder.borderStyle);
    }

    @Override
    protected Optional<List<DialogOption>> runDialog(Screen screen) throws IOException {
        int focusedIndex = initialFocusedIndex();
        screen.setCursorPosition(null);
        TextGraphics tg = screen.newTextGraphics();

        while (true) {
            render(screen, tg, focusedIndex);

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            switch (type) {
                case ArrowUp -> focusedIndex = previousEnabledIndex(focusedIndex);
                case ArrowDown -> focusedIndex = nextEnabledIndex(focusedIndex);
                case Character -> {
                    Character c = key.getCharacter();
                    if (c != null && c == ' ') {
                        toggleSelection(focusedIndex);
                    } else {
                        updateFilter(filterText + c);
                        focusedIndex = 0;
                    }
                }
                case Backspace -> {
                    if (!filterText.isEmpty()) {
                        updateFilter(filterText.substring(0, filterText.length() - 1));
                        focusedIndex = 0;
                    }
                }
                case Enter -> {
                    return Optional.of(buildResult());
                }
                case Escape -> {
                    if (!filterText.isEmpty()) {
                        clearFilter();
                        focusedIndex = 0;
                    } else {
                        return Optional.empty();
                    }
                }
                default -> {
                }
            }
        }
    }

    private void render(Screen screen, TextGraphics tg, int focusedIndex) throws IOException {
        screen.clear();

        int firstVisibleIndex = firstVisibleIndex(focusedIndex);
        int lastVisibleIndex = lastVisibleIndex(firstVisibleIndex);
        List<DialogOption> visibleOptions = options.subList(firstVisibleIndex, lastVisibleIndex);
        boolean hasItemsAbove = firstVisibleIndex > 0;
        boolean hasItemsBelow = lastVisibleIndex < options.size();
        boolean hasViewport = hasViewport();
        String positionIndicator = hasViewport ? positionIndicatorLabel(focusedIndex) : "";
        String searchLine = filterText.isEmpty() ? "" : "Search: " + filterText + "_";

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
                Math.max(Math.max(titleArea.getWidth(), optionsWidth), searchLine.length()),
                navigationArea.getWidth()
        );
        int contentHeight = titleArea.getHeight()
                + (filterText.isEmpty() ? 0 : 2)
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

        if (!filterText.isEmpty()) {
            row++;
            menuItemArea.withContent(searchLine).render(tg, column, row++);
        }

        row++;

        if (hasItemsAbove) {
            menuItemArea.withContent(MORE_ABOVE_LABEL).render(tg, column, row++);
        }

        if (options.isEmpty()) {
            menuItemArea.withContent("(no results)").render(tg, column, row++);
        } else {
            for (int i = firstVisibleIndex; i < lastVisibleIndex; i++) {
                DialogOption option = options.get(i);
                boolean focused = i == focusedIndex && option.isEnabled();
                boolean selected = selectedOptions.contains(option);
                renderMenuItem(tg, column, row++, option, focused, selected);
            }
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
        if (focused && selected) return selectedFocusedMenuItemArea;
        if (focused) return focusedMenuItemArea;
        if (selected) return selectedMenuItemArea;
        return menuItemArea;
    }

    private int menuItemWidth(String item) {
        return MultiChoiceMarker.UNSELECTED.length() + 1 + item.length();
    }

    private String displayLabel(DialogOption option) {
        return option.getLabel() + (option.isEnabled() ? "" : DISABLED_SUFFIX);
    }

    private int moreIndicatorWidth(boolean hasItemsAbove, boolean hasItemsBelow) {
        int width = 0;
        if (hasItemsAbove) width = MORE_ABOVE_LABEL.length();
        if (hasItemsBelow) width = Math.max(width, MORE_BELOW_LABEL.length());
        return width;
    }

    private void toggleSelection(int focusedIndex) {
        if (focusedIndex < 0 || focusedIndex >= options.size() || !options.get(focusedIndex).isEnabled()) {
            return;
        }
        DialogOption option = options.get(focusedIndex);
        if (!selectedOptions.add(option)) {
            selectedOptions.remove(option);
        }
    }

    private List<DialogOption> buildResult() {
        // Return options in original order
        return allOptions.stream()
                .filter(selectedOptions::contains)
                .toList();
    }

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

        public Builder(TitleArea titleArea, ContentArea menuItemArea, ContentArea focusedMenuItemArea,
                       ContentArea selectedMenuItemArea, ContentArea selectedFocusedMenuItemArea,
                       List<DialogOption> options, NavigationArea navigationArea) {
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

        public Builder inputStream(String path) {
            this.inputStreamPath = Objects.requireNonNull(path);
            return this;
        }

        public Builder outputStream(String path) {
            this.outputStreamPath = Objects.requireNonNull(path);
            return this;
        }

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

        public Builder withVisibleItemCount(int visibleItemCount) {
            if (visibleItemCount <= 0) {
                throw new IllegalArgumentException("visibleItemCount must be positive");
            }
            this.visibleItemCount = visibleItemCount;
            return this;
        }

        public MultiChoiceDialog build() {
            return new MultiChoiceDialog(this);
        }
    }
}
