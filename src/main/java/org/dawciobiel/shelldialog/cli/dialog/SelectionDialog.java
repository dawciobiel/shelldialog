package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import org.dawciobiel.shelldialog.cli.dialog.result.ErrorValue;
import org.dawciobiel.shelldialog.cli.dialog.result.IntegerValue;
import org.dawciobiel.shelldialog.cli.dialog.result.TextValue;
import org.dawciobiel.shelldialog.cli.dialog.result.Value;
import org.dawciobiel.shelldialog.cli.navigation.Arrow;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbarRenderer;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;
import org.dawciobiel.shelldialog.cli.style.TextStyle;
import org.dawciobiel.shelldialog.cli.ui.Body;
import org.dawciobiel.shelldialog.cli.ui.Footer;
import org.dawciobiel.shelldialog.cli.ui.Header;

import java.io.IOException;
import java.util.Objects;

/**
 * A CLI selection menu that allows the user to choose an option from a list.
 * It supports keyboard navigation (up/down arrows), selection (Enter), and cancellation (Escape).
 * <p>
 * The menu is rendered using the Lanterna library.
 * </p>
 */
public class SelectionDialog extends AbstractDialog {

    private final String[] menuItems;
    private final DialogTheme theme;
    private final NavigationToolbar navigationToolbar;

    private SelectionDialog(Builder builder) {
        super(builder.inputStreamPath, builder.outputStreamPath);
        this.menuItems = builder.menuItems;
        this.theme = builder.theme;
        this.navigationToolbar = builder.navigationToolbar;
    }

    /**
     * Displays the selection menu to the user and waits for input.
     *
     * @return A {@link Value} representing the result of the interaction:
     * <ul>
     *     <li>{@link IntegerValue}: The index of the selected item (0-based, where 0 is the title, so selection starts at 1).</li>
     *     <li>{@link TextValue}: Containing {@link Showable#DIALOG_CANCELED_FLAG} if the user cancelled the dialog.</li>
     *     <li>{@link ErrorValue}: If an I/O error occurred.</li>
     * </ul>
     */
    @Override
    protected Value runDialog(Screen screen) throws IOException {

        int selectedIndex = 1;
        screen.setCursorPosition(null); // Hide cursor
        TextGraphics tg = screen.newTextGraphics();

        NavigationToolbarRenderer toolbarRenderer = new NavigationToolbarRenderer(theme.navigationStyle()
                                                                                       .foreground(), theme.navigationStyle()
                                                                                                           .foreground(), theme.navigationStyle()
                                                                                                                               .background());

        while (true) {
            render(screen, tg, selectedIndex, toolbarRenderer);

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            switch (type) {
                case ArrowUp -> {
                    if (selectedIndex > 1) selectedIndex--;
                }
                case ArrowDown -> {
                    if (selectedIndex < menuItems.length - 1) selectedIndex++;
                }
                case Enter -> {
                    return new IntegerValue(selectedIndex);
                }
                case Escape -> {
                    return new TextValue(Showable.DIALOG_CANCELED_FLAG);
                }
                default -> {
                }
            }
        }
    }

    private void render(Screen screen, TextGraphics tg, int selectedIndex, NavigationToolbarRenderer toolbarRenderer) throws IOException {
        screen.clear();
        int terminalWidth = screen.getTerminalSize()
                                  .getColumns();

        Header header = new Header(menuItems[0], terminalWidth, theme.borderStyle()
                                                                     .foreground(), theme.titleStyle()
                                                                                         .foreground());
        int row = 0;
        header.render(tg, row);
        row += 3;

        for (int i = 1; i < menuItems.length; i++) {
            renderMenuItem(tg, row++, menuItems[i], i == selectedIndex);
        }

        Footer footer = new Footer(navigationToolbar, toolbarRenderer);
        footer.render(tg, row + 1);

        screen.refresh();
    }

    private void renderMenuItem(TextGraphics tg, int row, String item, boolean selected) throws IOException {
        TextStyle style = selected ? theme.menuItemSelectedStyle() : theme.menuItemStyle();

        String text = (selected ? Arrow.MARKER_EFT : Arrow.MARKER_EMPTY) + item + (selected ? Arrow.MARKER_RIGHT : Arrow.MARKER_EMPTY);

        new Body(text, style.foreground(), style.background()).render(tg, row);
    }

    /**
     * Builder for creating instances of {@link SelectionDialog}.
     */
    public static class Builder {

        private final String[] menuItems;
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
            this.menuItems = Objects.requireNonNull(menuItems);
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
         * Builds the {@link SelectionDialog} instance.
         *
         * @return A new {@link SelectionDialog}.
         */
        public SelectionDialog build() {
            return new SelectionDialog(this);
        }
    }
}