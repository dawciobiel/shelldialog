package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;
import org.dawciobiel.shelldialog.cli.style.TextStyle;
import org.dawciobiel.shelldialog.cli.ui.ContentArea;
import org.dawciobiel.shelldialog.cli.ui.DialogFrame;
import org.dawciobiel.shelldialog.cli.ui.InputArea;
import org.dawciobiel.shelldialog.cli.ui.NavigationArea;
import org.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * A CLI dialog that captures a password as a character array.
 * The typed value is rendered as masked content inside the shared optional frame.
 */
public class PasswordDialog extends AbstractDialog<char[]> {

    private final TitleArea titleArea;
    private final ContentArea contentArea;
    private final InputArea inputArea;
    private final boolean borderVisible;
    private final NavigationArea navigationArea;
    private final DialogFrame dialogFrame;
    private final char maskCharacter;

    private PasswordDialog(Builder builder) {
        super(builder.inputStreamPath, builder.outputStreamPath);
        this.titleArea = builder.titleArea;
        this.contentArea = builder.contentArea;
        this.inputArea = builder.inputArea;
        this.borderVisible = builder.borderVisible;
        this.navigationArea = builder.navigationArea;
        this.dialogFrame = new DialogFrame(borderVisible, builder.borderStyle);
        this.maskCharacter = builder.maskCharacter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Optional<char[]> runDialog(Screen screen) throws IOException {
        StringBuilder inputBuffer = new StringBuilder();
        TextGraphics tg = screen.newTextGraphics();

        while (true) {
            render(screen, tg, inputBuffer.length());

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            switch (type) {
                case Enter -> {
                    return Optional.of(inputBuffer.toString().toCharArray());
                }
                case Escape -> {
                    return Optional.empty();
                }
                case Backspace -> {
                    if (!inputBuffer.isEmpty()) {
                        inputBuffer.setLength(inputBuffer.length() - 1);
                    }
                }
                case Character -> inputBuffer.append(key.getCharacter());
                default -> {
                }
            }
        }
    }

    private void render(Screen screen, TextGraphics tg, int inputLength) throws IOException {
        screen.clear();

        String maskedValue = String.valueOf(maskCharacter).repeat(inputLength);
        InputArea currentInputArea = inputArea.withContent(maskedValue);
        int contentWidth = Math.max(
                Math.max(titleArea.getWidth(), contentArea.getWidth()),
                Math.max(currentInputArea.getWidth(), navigationArea.getWidth())
        );
        int contentHeight = titleArea.getHeight()
                + 1
                + contentArea.getHeight()
                + 1
                + currentInputArea.getHeight()
                + 1
                + navigationArea.getHeight();
        DialogFrame.FrameLayout layout = dialogFrame.layoutFor(contentWidth, contentHeight);
        dialogFrame.render(tg, layout);

        int column = layout.contentColumn();
        int row = layout.contentRow();
        titleArea.render(tg, column, row);
        row += titleArea.getHeight();

        row++;

        contentArea.render(tg, column, row);
        row += contentArea.getHeight();

        row++;

        int inputRow = row;
        currentInputArea.render(tg, column, row++);
        row++;

        navigationArea.render(tg, column, row);

        screen.setCursorPosition(new TerminalPosition(column + inputLength, inputRow));
        screen.refresh();
    }

    /**
     * Builder for creating instances of {@link PasswordDialog}.
     */
    public static class Builder extends AbstractFrameDialogBuilder<Builder> {

        private final TitleArea titleArea;
        private final ContentArea contentArea;
        private final InputArea inputArea;
        private final NavigationArea navigationArea;

        private char maskCharacter = '*';
        private final String inputStreamPath = "/dev/tty";
        private final String outputStreamPath = "/dev/tty";

        /**
         * Creates a new Builder with the specified title, content, input and navigation areas.
         *
         * @param titleArea the preconfigured {@link TitleArea} to render
         * @param contentArea the preconfigured {@link ContentArea} to render
         * @param inputArea the preconfigured {@link InputArea} to render
         * @param navigationArea the preconfigured {@link NavigationArea} to render
         */
        public Builder(TitleArea titleArea, ContentArea contentArea, InputArea inputArea, NavigationArea navigationArea) {
            this.titleArea = Objects.requireNonNull(titleArea);
            this.contentArea = Objects.requireNonNull(contentArea);
            this.inputArea = Objects.requireNonNull(inputArea);
            this.navigationArea = Objects.requireNonNull(navigationArea);
        }

        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Sets the character used to mask the typed password.
         *
         * @param maskCharacter the masking character
         * @return this builder
         */
        public Builder withMaskCharacter(char maskCharacter) {
            this.maskCharacter = maskCharacter;
            return this;
        }

        /**
         * Builds the {@link PasswordDialog} instance.
         *
         * @return a new {@link PasswordDialog}
         */
        public PasswordDialog build() {
            return new PasswordDialog(this);
        }
    }
}
