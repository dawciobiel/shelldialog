package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import io.github.dawciobiel.shelldialog.cli.dialog.option.DialogOption;
import io.github.dawciobiel.shelldialog.cli.i18n.UIProperties;
import io.github.dawciobiel.shelldialog.cli.style.Arrow;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.DialogFrame;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A CLI selection menu that allows the user to choose an option from a list.
 * It composes preconfigured UI areas inside a shared optional frame.
 * It supports keyboard navigation (up/down arrows), selection (Enter), and cancellation (Escape).
 * <p>
 * The menu is rendered using the Lanterna library.
 * </p>
 */
public class SingleChoiceDialog extends AbstractListDialog<DialogOption> {

    private static final String MORE_ABOVE_LABEL = "\u2191 more";
    private static final String MORE_BELOW_LABEL = "\u2193 more";
    private static final String DISABLED_SUFFIX = UIProperties.getString("dialog.option.disabled_suffix");

    private final TitleArea titleArea;
    private final ContentArea menuItemArea;
    private final ContentArea selectedMenuItemArea;
    private final NavigationArea navigationArea;
    private final boolean borderVisible;
    private final DialogFrame dialogFrame;

    private SingleChoiceDialog(Builder builder) {
        super(builder.inputStreamPath, builder.outputStreamPath, builder.options, builder.visibleItemCount);
        this.titleArea = builder.titleArea;
        this.menuItemArea = builder.menuItemArea;
        this.selectedMenuItemArea = builder.selectedMenuItemArea;
        this.navigationArea = builder.navigationArea;
        this.borderVisible = builder.borderVisible;
        this.dialogFrame = new DialogFrame(borderVisible, builder.borderStyle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Optional<DialogOption> runDialog(Screen screen) throws IOException {

        int selectedIndex = initialFocusedIndex();
        screen.setCursorPosition(null); // Hide cursor
        TextGraphics tg = screen.newTextGraphics();

        while (true) {
            render(screen, tg, selectedIndex);

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            switch (type) {
                case ArrowUp -> {
                    selectedIndex = previousEnabledIndex(selectedIndex);
                }
                case ArrowDown -> {
                    selectedIndex = nextEnabledIndex(selectedIndex);
                }
                case Enter -> {
                    if (selectedIndex >= 0 && selectedIndex < options.size() && options.get(selectedIndex).isEnabled()) {
                        return Optional.of(options.get(selectedIndex));
                    }
                }
                case Escape -> {
                    return Optional.empty();
                }
                default -> {
                }
            }
        }
    }

    private void render(Screen screen, TextGraphics tg, int selectedIndex) throws IOException {
        screen.clear();

        int firstVisibleIndex = firstVisibleIndex(selectedIndex);
        int lastVisibleIndex = lastVisibleIndex(firstVisibleIndex);
        List<DialogOption> visibleOptions = options.subList(firstVisibleIndex, lastVisibleIndex);
        boolean hasItemsAbove = firstVisibleIndex > 0;
        boolean hasItemsBelow = lastVisibleIndex < options.size();
        boolean hasViewport = hasViewport();
        String positionIndicator = hasViewport ? positionIndicatorLabel(selectedIndex) : "";

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

        row++; // Add blank line

        if (hasItemsAbove) {
            menuItemArea.withContent(MORE_ABOVE_LABEL).render(tg, column, row++);
        }

        for (int i = firstVisibleIndex; i < lastVisibleIndex; i++) {
            DialogOption option = options.get(i);
            renderMenuItem(tg, column, row++, option, i == selectedIndex && option.isEnabled());
        }

        if (hasItemsBelow) {
            menuItemArea.withContent(MORE_BELOW_LABEL).render(tg, column, row++);
        }

        if (hasViewport) {
            menuItemArea.withContent(positionIndicator).render(tg, column, row++);
        }

        row++; // Add blank line

        navigationArea.render(tg, column, row);

        screen.refresh();
    }

    private void renderMenuItem(TextGraphics tg, int column, int row, DialogOption option, boolean selected) throws IOException {
        String item = displayLabel(option);
        boolean enabled = option.isEnabled();
        String leftMarker = selected && enabled ? Arrow.MARKER_EFT : Arrow.MARKER_EMPTY;
        String rightMarker = selected && enabled ? Arrow.MARKER_RIGHT : Arrow.MARKER_EMPTY;
        String text = leftMarker + item + rightMarker;
        ContentArea currentArea = (selected && enabled ? selectedMenuItemArea : menuItemArea).withContent(text);
        currentArea.render(tg, column, row);
    }

    private int menuItemWidth(String item) {
        return (Arrow.MARKER_EMPTY + item + Arrow.MARKER_EMPTY).length();
    }

    private String displayLabel(DialogOption option) {
        return option.getLabel() + (option.isEnabled() ? "" : DISABLED_SUFFIX);
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

    /**
     * Builder for creating instances of {@link SingleChoiceDialog}.
     */
    public static class Builder extends AbstractFrameDialogBuilder<Builder> {

        private final TitleArea titleArea;
        private final ContentArea menuItemArea;
        private final ContentArea selectedMenuItemArea;
        private final List<DialogOption> options;
        private final NavigationArea navigationArea;
        private int visibleItemCount = 0;
        private String inputStreamPath = "/dev/tty";
        private String outputStreamPath = "/dev/tty";

        /**
         * Creates a new Builder with the specified UI areas and options.
         *
         * @param titleArea The preconfigured {@link TitleArea} to render.
         * @param menuItemArea The preconfigured {@link ContentArea} for unselected options.
         * @param selectedMenuItemArea The preconfigured {@link ContentArea} for the selected option.
         * @param options The options to display.
         * @param navigationArea The preconfigured {@link NavigationArea} to render.
         */
        public Builder(TitleArea titleArea, ContentArea menuItemArea, ContentArea selectedMenuItemArea,
                       List<DialogOption> options, NavigationArea navigationArea) {
            this.titleArea = Objects.requireNonNull(titleArea);
            this.menuItemArea = Objects.requireNonNull(menuItemArea);
            this.selectedMenuItemArea = Objects.requireNonNull(selectedMenuItemArea);
            this.options = List.copyOf(Objects.requireNonNull(options));
            this.navigationArea = Objects.requireNonNull(navigationArea);
        }

        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Sets the input stream path (e.g., "/dev/tty").
         *
         * @param path The path to the input stream.
         * @return This Builder instance.
         */
        public Builder inputStream(String path) {
            this.inputStreamPath = Objects.requireNonNull(path);
            return this;
        }

        /**
         * Sets the output stream path (e.g., "/dev/tty").
         *
         * @param path The path to the output stream.
         * @return This Builder instance.
         */
        public Builder outputStream(String path) {
            this.outputStreamPath = Objects.requireNonNull(path);
            return this;
        }

        /**
         * Limits the number of menu items visible at once.
         * When the selection moves outside the visible window, the dialog scrolls the list.
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
         * Builds the {@link SingleChoiceDialog} instance.
         *
         * @return A new {@link SingleChoiceDialog}.
         */
        public SingleChoiceDialog build() {
            return new SingleChoiceDialog(this);
        }
    }
}
