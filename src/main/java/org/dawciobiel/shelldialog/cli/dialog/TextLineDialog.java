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
import java.util.function.Function;

/**
 * A CLI dialog that prompts the user for a single line of text input.
 * It composes preconfigured UI areas inside a shared optional frame.
 * It supports typing, backspace, confirmation (Enter), and cancellation (Escape).
 * <p>
 * The dialog is rendered using the Lanterna library.
 * </p>
 */
public class TextLineDialog extends AbstractDialog<String> {

    private final TitleArea titleArea;
    private final ContentArea contentArea;
    private final InputArea inputArea;
    private final boolean borderVisible;
    private final NavigationArea navigationArea;
    private final DialogFrame dialogFrame;
    private final int maxLength;
    private final Function<String, Optional<String>> validator;

    private TextLineDialog(Builder builder) {
        super(builder.inputStreamPath, builder.outputStreamPath);
        this.titleArea = builder.titleArea;
        this.contentArea = builder.contentArea;
        this.inputArea = builder.inputArea;
        this.borderVisible = builder.borderVisible;
        this.navigationArea = builder.navigationArea;
        this.dialogFrame = new DialogFrame(borderVisible, builder.borderStyle);
        this.maxLength = builder.maxLength;
        this.validator = builder.validator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Optional<String> runDialog(Screen screen) throws IOException {
        StringBuilder inputBuffer = new StringBuilder();
        TextGraphics tg = screen.newTextGraphics();
        String validationMessage = null;

        while (true) {
            render(screen, tg, inputBuffer.toString(), validationMessage);

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            switch (type) {
                case Enter:
                    Optional<String> validationResult = validator.apply(inputBuffer.toString());
                    if (validationResult.isPresent()) {
                        validationMessage = validationResult.get();
                        break;
                    }
                    return Optional.of(inputBuffer.toString());
                case Escape:
                    return Optional.empty();
                case Backspace:
                    if (!inputBuffer.isEmpty()) {
                        inputBuffer.setLength(inputBuffer.length() - 1);
                        validationMessage = null;
                    }
                    break;
                case Character:
                    if (inputBuffer.length() < maxLength) {
                        inputBuffer.append(key.getCharacter());
                        validationMessage = null;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void render(Screen screen, TextGraphics tg, String inputContent, String validationMessage) throws IOException {
        screen.clear();

        InputArea currentInputArea = inputArea.withContent(inputContent);
        ContentArea validationArea = validationMessage == null ? null : contentArea.withContent(validationMessage);
        int contentWidth = Math.max(
                Math.max(titleArea.getWidth(), contentArea.getWidth()),
                Math.max(currentInputArea.getWidth(), navigationArea.getWidth())
        );
        if (validationArea != null) {
            contentWidth = Math.max(contentWidth, validationArea.getWidth());
        }
        int contentHeight = titleArea.getHeight()
                + 1
                + contentArea.getHeight()
                + 1
                + currentInputArea.getHeight();
        if (validationArea != null) {
            contentHeight += 1 + validationArea.getHeight();
        }
        contentHeight += 1
                + navigationArea.getHeight();
        DialogFrame.FrameLayout layout = dialogFrame.layoutFor(contentWidth, contentHeight);
        dialogFrame.render(tg, layout);

        int column = layout.contentColumn();
        int row = layout.contentRow();
        titleArea.render(tg, column, row);
        row += titleArea.getHeight();

        row++; // Add a blank line for spacing

        contentArea.render(tg, column, row);
        row += contentArea.getHeight();

        row++; // Add a blank line for spacing

        int inputRow = row;
        currentInputArea.render(tg, column, row++);

        if (validationArea != null) {
            row++;
            validationArea.render(tg, column, row++);
        }

        row++; // Add a blank line for spacing

        navigationArea.render(tg, column, row);

        screen.setCursorPosition(new TerminalPosition(column + inputContent.length(), inputRow));
        screen.refresh();
    }

    /**
     * Builder for creating instances of {@link TextLineDialog}.
     */
    public static class Builder extends AbstractFrameDialogBuilder<Builder> {

        private final TitleArea titleArea;
        private final ContentArea contentArea;
        private final InputArea inputArea;
        private final NavigationArea navigationArea;

        private int maxLength = Integer.MAX_VALUE;
        private Function<String, Optional<String>> validator = value -> Optional.empty();
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

        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Sets the maximum number of characters accepted by the dialog.
         *
         * @param maxLength the maximum allowed input length, must be positive
         * @return this builder
         */
        public Builder withMaxLength(int maxLength) {
            if (maxLength <= 0) {
                throw new IllegalArgumentException("maxLength must be positive");
            }
            this.maxLength = maxLength;
            return this;
        }

        /**
         * Sets the validator used when the user confirms the dialog.
         * The returned optional should be empty for valid input or contain an error message otherwise.
         *
         * @param validator the validation function
         * @return this builder
         */
        public Builder withValidator(Function<String, Optional<String>> validator) {
            this.validator = Objects.requireNonNull(validator);
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
