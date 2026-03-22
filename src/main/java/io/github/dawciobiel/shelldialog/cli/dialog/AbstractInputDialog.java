package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import io.github.dawciobiel.shelldialog.cli.style.TextStyle;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.DialogFrame;
import io.github.dawciobiel.shelldialog.cli.ui.InputArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.io.IOException;
import java.util.Optional;

/**
 * Base class for single-line input dialogs with optional validation feedback.
 *
 * @param <T> the accepted result type
 */
abstract class AbstractInputDialog<T> extends AbstractDialog<T> {

    private final TitleArea titleArea;
    private final ContentArea contentArea;
    private final InputArea inputArea;
    private final NavigationArea navigationArea;
    private final boolean borderVisible;
    private final DialogFrame dialogFrame;
    private final TextStyle validationMessageStyle;
    private final int maxLength;
    private final String initialValue;

    protected AbstractInputDialog(
            String inputStreamPath,
            String outputStreamPath,
            TitleArea titleArea,
            ContentArea contentArea,
            InputArea inputArea,
            NavigationArea navigationArea,
            boolean borderVisible,
            TextStyle validationMessageStyle,
            int maxLength,
            String initialValue,
            io.github.dawciobiel.shelldialog.cli.style.TextStyle borderStyle
    ) {
        super(inputStreamPath, outputStreamPath);
        this.titleArea = titleArea;
        this.contentArea = contentArea;
        this.inputArea = inputArea;
        this.navigationArea = navigationArea;
        this.borderVisible = borderVisible;
        this.dialogFrame = new DialogFrame(borderVisible, borderStyle);
        this.validationMessageStyle = validationMessageStyle;
        this.maxLength = maxLength;
        this.initialValue = initialValue;
    }

    @Override
    protected Optional<T> runDialog(Screen screen) throws IOException {
        StringBuilder inputBuffer = new StringBuilder(initialValue);
        TextGraphics tg = screen.newTextGraphics();
        String validationMessage = null;

        while (true) {
            render(screen, tg, inputBuffer.toString(), validationMessage);

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            switch (type) {
                case Enter -> {
                    Optional<String> validationResult = validate(inputBuffer.toString());
                    if (validationResult.isPresent()) {
                        validationMessage = validationResult.get();
                        break;
                    }
                    return Optional.of(acceptedValue(inputBuffer.toString()));
                }
                case Escape -> {
                    return Optional.empty();
                }
                case Backspace -> {
                    if (!inputBuffer.isEmpty()) {
                        inputBuffer.setLength(inputBuffer.length() - 1);
                        validationMessage = null;
                    }
                }
                case Character -> {
                    if (inputBuffer.length() < maxLength) {
                        inputBuffer.append(key.getCharacter());
                        validationMessage = null;
                    }
                }
                default -> {
                }
            }
        }
    }

    /**
     * Converts the raw input buffer into the text that should be rendered in the input field.
     *
     * @param rawInput the current raw input value
     * @return the text to display to the user
     */
    protected abstract String inputDisplay(String rawInput);

    /**
     * Validates the current raw input when the user confirms the dialog.
     *
     * @param rawInput the current raw input value
     * @return an empty optional when the input is valid, or an error message otherwise
     */
    protected abstract Optional<String> validate(String rawInput);

    /**
     * Converts the raw input buffer into the accepted dialog result type.
     *
     * @param rawInput the current raw input value
     * @return the accepted dialog result
     */
    protected abstract T acceptedValue(String rawInput);

    private void render(Screen screen, TextGraphics tg, String rawInput, String validationMessage) throws IOException {
        screen.clear();

        InputArea currentInputArea = inputArea.withContent(inputDisplay(rawInput));
        ContentArea validationArea = validationMessage == null ? null : new ContentArea.Builder()
                .withContent(validationMessage)
                .withForegroundColor(validationMessageStyle.foreground())
                .withBackgroundColor(validationMessageStyle.background())
                .build();
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
        contentHeight += 1 + navigationArea.getHeight();
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
        if (validationArea != null) {
            row++;
            validationArea.render(tg, column, row++);
        }
        row++;

        navigationArea.render(tg, column, row);

        screen.setCursorPosition(new TerminalPosition(column + rawInput.length(), inputRow));
        screen.refresh();
    }
}
