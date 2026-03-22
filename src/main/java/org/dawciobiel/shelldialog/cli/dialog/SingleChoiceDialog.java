package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import org.dawciobiel.shelldialog.cli.dialog.option.DialogOption;
import org.dawciobiel.shelldialog.cli.style.Arrow;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;
import org.dawciobiel.shelldialog.cli.style.TextStyle;
import org.dawciobiel.shelldialog.cli.ui.ContentArea;
import org.dawciobiel.shelldialog.cli.ui.DialogFrame;
import org.dawciobiel.shelldialog.cli.ui.NavigationArea;
import org.dawciobiel.shelldialog.cli.ui.TitleArea;

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
public class SingleChoiceDialog extends AbstractDialog<DialogOption> {

    private final TitleArea titleArea;
    private final ContentArea menuItemArea;
    private final ContentArea selectedMenuItemArea;
    private final List<DialogOption> options;
    private final NavigationArea navigationArea;
    private final boolean borderVisible;
    private final DialogFrame dialogFrame;

    private SingleChoiceDialog(Builder builder) {
        super(builder.inputStreamPath, builder.outputStreamPath);
        this.titleArea = builder.titleArea;
        this.menuItemArea = builder.menuItemArea;
        this.selectedMenuItemArea = builder.selectedMenuItemArea;
        this.options = builder.options;
        this.navigationArea = builder.navigationArea;
        this.borderVisible = builder.borderVisible;
        this.dialogFrame = new DialogFrame(borderVisible, builder.borderStyle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Optional<DialogOption> runDialog(Screen screen) throws IOException {

        int selectedIndex = 0;
        screen.setCursorPosition(null); // Hide cursor
        TextGraphics tg = screen.newTextGraphics();

        while (true) {
            render(screen, tg, selectedIndex);

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            switch (type) {
                case ArrowUp -> {
                    if (selectedIndex > 0) selectedIndex--;
                }
                case ArrowDown -> {
                    if (selectedIndex < options.size() - 1) selectedIndex++;
                }
                case Enter -> {
                    if (selectedIndex >= 0 && selectedIndex < options.size()) {
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

        row++; // Add blank line

        for (int i = 0; i < options.size(); i++) {
            renderMenuItem(tg, column, row++, options.get(i).getLabel(), i == selectedIndex);
        }
        row++; // Add blank line

        navigationArea.render(tg, column, row);

        screen.refresh();
    }

    private void renderMenuItem(TextGraphics tg, int column, int row, String item, boolean selected) throws IOException {
        String text = (selected ? Arrow.MARKER_EFT : Arrow.MARKER_EMPTY) + item + (selected ? Arrow.MARKER_RIGHT : Arrow.MARKER_EMPTY);
        ContentArea currentArea = (selected ? selectedMenuItemArea : menuItemArea).withContent(text);
        currentArea.render(tg, column, row);
    }

    private int menuItemWidth(String item) {
        return (Arrow.MARKER_EMPTY + item + Arrow.MARKER_EMPTY).length();
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
         * Builds the {@link SingleChoiceDialog} instance.
         *
         * @return A new {@link SingleChoiceDialog}.
         */
        public SingleChoiceDialog build() {
            return new SingleChoiceDialog(this);
        }
    }
}
