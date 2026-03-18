package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import org.dawciobiel.shelldialog.cli.dialog.result.TextValue;
import org.dawciobiel.shelldialog.cli.dialog.result.Value;
import org.dawciobiel.shelldialog.cli.header.border.BorderType;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbarRenderer;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;
import org.dawciobiel.shelldialog.cli.ui.Body;
import org.dawciobiel.shelldialog.cli.ui.Footer;
import org.dawciobiel.shelldialog.cli.ui.Header;

import java.io.IOException;
import java.util.Objects;

/**
 * A CLI dialog that prompts the user for a single line of text input.
 * It supports typing, backspace, confirmation (Enter), and cancellation (Escape).
 * <p>
 * The dialog is rendered using the Lanterna library.
 * </p>
 */
public class TextLineDialog extends AbstractDialog {

    private final String title;
    private final BorderType borderType;
    private final DialogTheme theme;
    private final NavigationToolbar navigationToolbar;

    private TextLineDialog(Builder builder) {
        super(builder.inputStreamPath, builder.outputStreamPath);
        this.title = builder.title;
        this.borderType = builder.borderType;
        this.theme = builder.theme;
        this.navigationToolbar = builder.navigationToolbar;
    }

    @Override
    protected Value runDialog(Screen screen) throws IOException {
        StringBuilder inputBuffer = new StringBuilder();
        TextGraphics tg = screen.newTextGraphics();

        while (true) {
            render(screen, tg, inputBuffer.toString());

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            switch (type) {
                case Enter:
                    return new TextValue(inputBuffer.toString());
                case Escape:
                    return new TextValue(DIALOG_CANCELED_FLAG);
                case Backspace:
                    if (!inputBuffer.isEmpty()) inputBuffer.setLength(inputBuffer.length() - 1);
                    break;
                case Character:
                    inputBuffer.append(key.getCharacter());
                    break;
                default:
                    break;
            }
        }
    }

    private void render(Screen screen, TextGraphics tg, String inputContent) throws IOException {
        screen.clear();
        int terminalWidth = screen.getTerminalSize().getColumns();

        Header header = new Header(title, terminalWidth, theme.borderStyle().foreground(), theme.titleStyle().foreground());
        Body body = new Body(inputContent, theme.inputStyle().foreground(), theme.inputStyle().background());
        NavigationToolbarRenderer toolbarRenderer = new NavigationToolbarRenderer(theme.navigationStyle().foreground(), theme.navigationStyle().foreground(), theme.navigationStyle().background());
        Footer footer = new Footer(navigationToolbar, toolbarRenderer);

        int row = 0;
        header.render(tg, row);
        row += 3;
        body.render(tg, row);
        row += 2;
        footer.render(tg, row);

        screen.setCursorPosition(new TerminalPosition(2 + inputContent.length(), row - 2));
        screen.refresh();
    }

    /**
     * Builder for creating instances of {@link TextLineDialog}.
     */
    public static class Builder {

        private final String title;

        private final BorderType borderType = BorderType.BORDER_ALL;
        private final String inputStreamPath = "/dev/tty";
        private final String outputStreamPath = "/dev/tty";
        private DialogTheme theme = DialogTheme.darkTheme();

        private NavigationToolbar navigationToolbar = NavigationToolbar.builder().withEnterAccept().withEscapeCancel().build();

        /**
         * Creates a new Builder with the specified question title.
         *
         * @param title The title of the question to be displayed.
         */
        public Builder(String title) {
            this.title = Objects.requireNonNull(title);
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
         * Builds the {@link TextLineDialog} instance.
         *
         * @return A new {@link TextLineDialog}.
         */
        public TextLineDialog build() {
            return new TextLineDialog(this);
        }
    }
}