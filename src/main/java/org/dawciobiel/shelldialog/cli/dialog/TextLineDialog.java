package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import org.dawciobiel.shelldialog.cli.style.BorderType;
import org.dawciobiel.shelldialog.cli.ui.ContentArea;
import org.dawciobiel.shelldialog.cli.ui.InputArea;
import org.dawciobiel.shelldialog.cli.ui.NavigationArea;
import org.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * A CLI dialog that prompts the user for a single line of text input.
 * It supports typing, backspace, confirmation (Enter), and cancellation (Escape).
 * <p>
 * The dialog is rendered using the Lanterna library.
 * </p>
 */
public class TextLineDialog extends AbstractDialog<String> {

    private final TitleArea titleArea;
    private final ContentArea contentArea;
    private final InputArea inputArea;
    private final BorderType borderType;
    private final NavigationArea navigationArea;

    private TextLineDialog(Builder builder) {
        super(builder.inputStreamPath, builder.outputStreamPath);
        this.titleArea = builder.titleArea;
        this.contentArea = builder.contentArea;
        this.inputArea = builder.inputArea;
        this.borderType = builder.borderType;
        this.navigationArea = builder.navigationArea;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Optional<String> runDialog(Screen screen) throws IOException {
        StringBuilder inputBuffer = new StringBuilder();
        TextGraphics tg = screen.newTextGraphics();

        while (true) {
            render(screen, tg, inputBuffer.toString());

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            switch (type) {
                case Enter:
                    return Optional.of(inputBuffer.toString());
                case Escape:
                    return Optional.empty();
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

        InputArea currentInputArea = inputArea.withContent(inputContent);

        int row = 0;
        titleArea.render(tg, row);
        row += titleArea.getHeight();

        row++; // Add a blank line for spacing

        contentArea.render(tg, row);
        row += contentArea.getHeight();

        row++; // Add a blank line for spacing

        int inputRow = row;
        currentInputArea.render(tg, row++);
        row++; // Add a blank line for spacing

        navigationArea.render(tg, row);

        screen.setCursorPosition(new TerminalPosition(2 + inputContent.length(), inputRow));
        screen.refresh();
    }

    /**
     * Builder for creating instances of {@link TextLineDialog}.
     */
    public static class Builder {

        private final TitleArea titleArea;
        private final ContentArea contentArea;
        private final InputArea inputArea;
        private final NavigationArea navigationArea;

        private final BorderType borderType = BorderType.BORDER_ALL;
        private final String inputStreamPath = "/dev/tty";
        private final String outputStreamPath = "/dev/tty";

        /**
         * Creates a new Builder with the specified title, content, input and navigation areas.
         *
         * @param titleArea The preconfigured {@link TitleArea} to render.
         * @param contentArea The preconfigured {@link ContentArea} to render.
         * @param inputArea The preconfigured {@link InputArea} to render.
         * @param navigationArea The preconfigured {@link NavigationArea} to render.
         */
        public Builder(TitleArea titleArea, ContentArea contentArea, InputArea inputArea, NavigationArea navigationArea) {
            this.titleArea = Objects.requireNonNull(titleArea);
            this.contentArea = Objects.requireNonNull(contentArea);
            this.inputArea = Objects.requireNonNull(inputArea);
            this.navigationArea = Objects.requireNonNull(navigationArea);
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
