package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import org.dawciobiel.shelldialog.cli.dialog.option.DialogOption;
import org.dawciobiel.shelldialog.cli.dialog.option.SimpleDialogOption;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import org.dawciobiel.shelldialog.cli.style.Arrow;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;
import org.dawciobiel.shelldialog.cli.style.TextStyle;
import org.dawciobiel.shelldialog.cli.ui.ContentArea;
import org.dawciobiel.shelldialog.cli.ui.NavigationArea;
import org.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A CLI selection menu that allows the user to choose an option from a list.
 * It supports keyboard navigation (up/down arrows), selection (Enter), and cancellation (Escape).
 * <p>
 * The menu is rendered using the Lanterna library.
 * </p>
 */
public class SingleChoiceDialog extends AbstractDialog<DialogOption> {

    private final String title;
    private final List<DialogOption> options;
    private final DialogTheme theme;
    private final NavigationToolbar navigationToolbar;

    private SingleChoiceDialog(Builder builder) {
        super(builder.inputStreamPath, builder.outputStreamPath);
        this.title = builder.title;
        this.options = builder.options;
        this.theme = builder.theme;
        this.navigationToolbar = builder.navigationToolbar;
    }

    /**
     * Displays the selection menu to the user and waits for input.
     *
     * @return An {@link Optional} containing the selected {@link DialogOption}
     *         or {@link Optional#empty()} if canceled.
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

        TitleArea titleArea = new TitleArea.Builder()
                .withTitle(title)
                .withTheme(theme)
                .build();
        
        int row = 0;
        titleArea.render(tg, row);
        row += titleArea.getHeight();

        row++; // Add blank line

        for (int i = 0; i < options.size(); i++) {
            renderMenuItem(tg, row++, options.get(i).getLabel(), i == selectedIndex);
        }
        row++; // Add blank line

        NavigationArea navigationArea = new NavigationArea.Builder()
                .withToolbar(navigationToolbar)
                .withTheme(theme)
                .build();
        
        navigationArea.render(tg, row);

        screen.refresh();
    }

    private void renderMenuItem(TextGraphics tg, int row, String item, boolean selected) throws IOException {
        TextStyle style = selected ? theme.menuItemSelectedStyle() : theme.menuItemStyle();

        String text = (selected ? Arrow.MARKER_EFT : Arrow.MARKER_EMPTY) + item + (selected ? Arrow.MARKER_RIGHT : Arrow.MARKER_EMPTY);

        new ContentArea.Builder()
                .withContent(text)
                .withForegroundColor(style.foreground())
                .withBackgroundColor(style.background())
                .build()
                .render(tg, row);
    }

    /**
     * Builder for creating instances of {@link SingleChoiceDialog}.
     */
    public static class Builder {

        private final String title;
        private final List<DialogOption> options = new ArrayList<>();
        private DialogTheme theme = DialogTheme.darkTheme();
        private NavigationToolbar navigationToolbar = NavigationToolbar.builder()
                                                                       .withArrowsNavigation()
                                                                       .withEnterAccept()
                                                                       .withEscapeCancel()
                                                                       .build();
        private String inputStreamPath = "/dev/tty";
        private String outputStreamPath = "/dev/tty";

        /**
         * Creates a new Builder with the specified menu items.
         * The first item in the array is considered the title of the menu.
         *
         * @param menuItems An array of strings representing the menu items.
         */
        public Builder(String[] menuItems) {
            Objects.requireNonNull(menuItems);
            if (menuItems.length < 1) {
                throw new IllegalArgumentException("Menu items must contain at least a title");
            }
            this.title = menuItems[0];
            for (int i = 1; i < menuItems.length; i++) {
                // Using i as the code (preserving 1-based index from the original array structure)
                this.options.add(new SimpleDialogOption(i, menuItems[i]));
            }
        }

        /**
         * Sets the theme for the dialog.
         *
         * @param theme The {@link DialogTheme} to use.
         * @return This Builder instance.
         */
        public Builder theme(DialogTheme theme) {
            this.theme = Objects.requireNonNull(theme);
            return this;
        }

        /**
         * Sets the navigation toolbar for the dialog.
         *
         * @param toolbar The {@link NavigationToolbar} to use.
         * @return This Builder instance.
         */
        public Builder navigationToolbar(NavigationToolbar toolbar) {
            this.navigationToolbar = Objects.requireNonNull(toolbar);
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