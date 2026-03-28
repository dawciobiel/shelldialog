package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.style.TextStyle;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.DialogFrame;
import io.github.dawciobiel.shelldialog.cli.ui.InputArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * A CLI dialog for editing multiple input fields on a single screen.
 */
public class FormDialog<T> extends AbstractDialog<T> {

    private final TitleArea titleArea;
    private final ContentArea contentArea;
    private final ContentArea labelArea;
    private final InputArea inputArea;
    private final InputArea focusedInputArea;
    private final NavigationArea navigationArea;
    private final boolean borderVisible;
    private final DialogFrame dialogFrame;
    private final TextStyle validationMessageStyle;
    private final List<FormField> fields;

    private final Function<FormValues, T> resultMapper;

    private FormDialog(Builder<T> builder) {
        super(builder.inputStream, builder.outputStream, builder.inputStreamPath, builder.outputStreamPath, builder.terminal);
        this.titleArea = builder.titleArea;
        this.contentArea = builder.contentArea;
        this.labelArea = builder.labelArea;
        this.inputArea = builder.inputArea;
        this.focusedInputArea = builder.focusedInputArea;
        this.navigationArea = builder.navigationArea;
        this.borderVisible = builder.borderVisible;
        this.dialogFrame = new DialogFrame(borderVisible, builder.borderStyle);
        this.validationMessageStyle = builder.validationMessageStyle;
        this.fields = List.copyOf(builder.fields);
        this.resultMapper = builder.resultMapper;
    }

    @Override
    protected Optional<T> runDialog(Screen screen) throws IOException {
        screen.setCursorPosition(null);
        TextGraphics tg = screen.newTextGraphics();

        Map<String, StringBuilder> buffers = new LinkedHashMap<>();
        Map<String, String> validationMessages = new LinkedHashMap<>();
        for (FormField field : fields) {
            buffers.put(field.name(), new StringBuilder(field.initialValue()));
        }

        int focusedFieldIndex = 0;
        while (true) {
            render(screen, tg, buffers, validationMessages, focusedFieldIndex);

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            switch (type) {
                case ArrowUp, ReverseTab -> {
                    validateField(fields.get(focusedFieldIndex), buffers, validationMessages);
                    focusedFieldIndex = previousFieldIndex(focusedFieldIndex);
                }
                case ArrowDown, Tab -> {
                    validateField(fields.get(focusedFieldIndex), buffers, validationMessages);
                    focusedFieldIndex = nextFieldIndex(focusedFieldIndex);
                }
                case Enter -> {
                    Optional<Integer> firstInvalidIndex = validateAllFields(buffers, validationMessages);
                    if (firstInvalidIndex.isPresent()) {
                        focusedFieldIndex = firstInvalidIndex.get();
                        break;
                    }
                    return Optional.of(resultMapper.apply(new FormValues(acceptedValues(buffers))));
                }
                case Escape -> {
                    return Optional.empty();
                }
                case Backspace -> {
                    StringBuilder buffer = currentBuffer(buffers, focusedFieldIndex);
                    if (!buffer.isEmpty()) {
                        buffer.setLength(buffer.length() - 1);
                        validationMessages.remove(fields.get(focusedFieldIndex).name());
                    }
                }
                case Character -> {
                    StringBuilder buffer = currentBuffer(buffers, focusedFieldIndex);
                    FormField field = fields.get(focusedFieldIndex);
                    if (buffer.length() < field.maxLength()) {
                        buffer.append(key.getCharacter());
                        validationMessages.remove(field.name());
                    }
                }
                default -> {
                }
            }
        }
    }

    private void render(
            Screen screen,
            TextGraphics tg,
            Map<String, StringBuilder> buffers,
            Map<String, String> validationMessages,
            int focusedFieldIndex
    ) throws IOException {
        screen.clear();

        int labelWidth = fields.stream()
                .mapToInt(field -> (field.label() + ": ").length())
                .max()
                .orElse(0);

        int fieldRowWidth = fields.stream()
                .mapToInt(field -> (field.label() + ": ").length() + field.displayValue(buffers.get(field.name()).toString()).length())
                .max()
                .orElse(0);

        int validationWidth = validationMessages.values().stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);

        int contentWidth = Math.max(
                Math.max(titleArea.getWidth(), contentArea.getWidth()),
                Math.max(Math.max(fieldRowWidth, navigationArea.getWidth()), validationWidth)
        );

        int contentHeight = titleArea.getHeight()
                + 1
                + contentArea.getHeight()
                + 1
                + fieldHeight(validationMessages)
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

        int cursorColumn = column;
        int cursorRow = row;

        for (int i = 0; i < fields.size(); i++) {
            FormField field = fields.get(i);
            String label = field.label() + ": ";
            String rawInput = buffers.get(field.name()).toString();
            String displayValue = field.displayValue(rawInput);

            labelArea.withContent(label).render(tg, column, row);

            InputArea currentInputArea = (i == focusedFieldIndex ? focusedInputArea : inputArea)
                    .withContent(displayValue);
            currentInputArea.render(tg, column + labelWidth, row);

            if (i == focusedFieldIndex) {
                cursorColumn = column + labelWidth + rawInput.length();
                cursorRow = row;
            }

            row++;

            String validationMessage = validationMessages.get(field.name());
            if (validationMessage != null) {
                ContentArea validationArea = new ContentArea.Builder()
                        .withContent(validationMessage)
                        .withForegroundColor(validationMessageStyle.foreground())
                        .withBackgroundColor(validationMessageStyle.background())
                        .build();
                validationArea.render(tg, column, row);
                row++;
            }
        }

        row++;

        navigationArea.render(tg, column, row);

        screen.setCursorPosition(new TerminalPosition(cursorColumn, cursorRow));
        screen.refresh();
    }

    private int fieldHeight(Map<String, String> validationMessages) {
        return fields.size() + validationMessages.size();
    }

    private StringBuilder currentBuffer(Map<String, StringBuilder> buffers, int focusedFieldIndex) {
        return buffers.get(fields.get(focusedFieldIndex).name());
    }

    private int nextFieldIndex(int currentIndex) {
        return currentIndex + 1 < fields.size() ? currentIndex + 1 : 0;
    }

    private int previousFieldIndex(int currentIndex) {
        return currentIndex > 0 ? currentIndex - 1 : fields.size() - 1;
    }

    private void validateField(
            FormField field,
            Map<String, StringBuilder> buffers,
            Map<String, String> validationMessages
    ) {
        Optional<String> validationResult = field.validate(buffers.get(field.name()).toString());
        if (validationResult.isPresent()) {
            validationMessages.put(field.name(), validationResult.get());
        } else {
            validationMessages.remove(field.name());
        }
    }

    private Optional<Integer> validateAllFields(
            Map<String, StringBuilder> buffers,
            Map<String, String> validationMessages
    ) {
        Integer firstInvalidIndex = null;
        for (int i = 0; i < fields.size(); i++) {
            FormField field = fields.get(i);
            Optional<String> validationResult = field.validate(buffers.get(field.name()).toString());
            if (validationResult.isPresent()) {
                validationMessages.put(field.name(), validationResult.get());
                if (firstInvalidIndex == null) {
                    firstInvalidIndex = i;
                }
            } else {
                validationMessages.remove(field.name());
            }
        }
        return Optional.ofNullable(firstInvalidIndex);
    }

    private Map<String, Object> acceptedValues(Map<String, StringBuilder> buffers) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (FormField field : fields) {
            result.put(field.name(), field.acceptedValue(buffers.get(field.name()).toString()));
        }
        return result;
    }

    /**
     * Builder for {@link FormDialog} instances.
     */
    public static class Builder<T> extends AbstractFrameDialogBuilder<Builder<T>> {
        private final TitleArea titleArea;
        private final ContentArea contentArea;
        private final ContentArea labelArea;
        private final InputArea inputArea;
        private final InputArea focusedInputArea;
        private final NavigationArea navigationArea;
        private final List<FormField> fields;

        private TextStyle validationMessageStyle = TextStyle.of(com.googlecode.lanterna.TextColor.ANSI.RED_BRIGHT, com.googlecode.lanterna.TextColor.ANSI.DEFAULT);
        private Function<FormValues, T> resultMapper;

        /**
         * Creates a new builder with required UI components and field definitions.
         *
         * @param titleArea title area
         * @param contentArea body content area
         * @param labelArea style used for field labels
         * @param inputArea style used for non-focused inputs
         * @param focusedInputArea style used for the focused input
         * @param fields ordered form field definitions
         * @param navigationArea bottom toolbar area
         */
        public Builder(
                TitleArea titleArea,
                ContentArea contentArea,
                ContentArea labelArea,
                InputArea inputArea,
                InputArea focusedInputArea,
                List<FormField> fields,
                NavigationArea navigationArea
        ) {
            this.titleArea = Objects.requireNonNull(titleArea);
            this.contentArea = Objects.requireNonNull(contentArea);
            this.labelArea = Objects.requireNonNull(labelArea);
            this.inputArea = Objects.requireNonNull(inputArea);
            this.focusedInputArea = Objects.requireNonNull(focusedInputArea);
            this.fields = List.copyOf(Objects.requireNonNull(fields));
            this.navigationArea = Objects.requireNonNull(navigationArea);
        }

        @Override
        protected Builder<T> self() {
            return this;
        }

        @Override
        public Builder<T> withTheme(DialogTheme theme) {
            super.withTheme(theme);
            this.validationMessageStyle = theme.validationMessageStyle();
            return this;
        }

        /**
         * Sets the style used to render validation error messages.
         *
         * @param style validation message style
         * @return this builder
         */
        public Builder<T> withValidationMessageStyle(TextStyle style) {
            this.validationMessageStyle = Objects.requireNonNull(style);
            return this;
        }

        /**
         * Sets the function used to convert submitted form values into the final dialog result.
         *
         * @param resultMapper mapper producing the result type
         * @return this builder
         */
        public Builder<T> withResultMapper(Function<FormValues, T> resultMapper) {
            this.resultMapper = Objects.requireNonNull(resultMapper);
            return this;
        }

        /**
         * Builds the form dialog.
         *
         * @return a new {@link FormDialog}
         */
        public FormDialog<T> build() {
            if (fields.isEmpty()) {
                throw new IllegalArgumentException("fields must not be empty");
            }
            if (resultMapper == null) {
                throw new IllegalStateException("resultMapper must be provided");
            }
            ensureUniqueFieldNames(fields);
            return new FormDialog<>(this);
        }

        private void ensureUniqueFieldNames(List<FormField> fields) {
            Set<String> names = new java.util.HashSet<>();
            for (FormField field : fields) {
                if (!names.add(field.name())) {
                    throw new IllegalArgumentException("duplicate field name: " + field.name());
                }
            }
        }
    }
}
