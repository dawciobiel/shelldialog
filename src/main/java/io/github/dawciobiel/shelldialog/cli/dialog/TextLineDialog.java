package io.github.dawciobiel.shelldialog.cli.dialog;

import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.style.TextStyle;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.InputArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A CLI dialog for single-line text input with optional real-time validation.
 */
public class TextLineDialog extends AbstractInputDialog<String> {

    private final Function<String, Optional<String>> validator;

    private TextLineDialog(Builder builder) {
        super(
                builder.inputStream,
                builder.outputStream,
                builder.inputStreamPath,
                builder.outputStreamPath,
                builder.terminal,
                builder.titleArea,
                builder.contentArea,
                builder.inputArea,
                builder.navigationArea,
                builder.borderVisible,
                builder.validationMessageStyle,
                builder.maxLength,
                builder.initialValue,
                builder.borderStyle
        );
        this.validator = builder.validator;
    }

    @Override
    protected String inputDisplay(String rawInput) {
        return rawInput;
    }

    @Override
    protected Optional<String> validate(String rawInput) {
        return validator.apply(rawInput);
    }

    @Override
    protected String acceptedValue(String rawInput) {
        return rawInput;
    }

    /**
     * Builder for {@link TextLineDialog} instances.
     */
    public static class Builder extends AbstractFrameDialogBuilder<Builder> {
        private final TitleArea titleArea;
        private final ContentArea contentArea;
        private final InputArea inputArea;
        private final NavigationArea navigationArea;

        private Function<String, Optional<String>> validator = value -> Optional.empty();
        private TextStyle validationMessageStyle = TextStyle.of(com.googlecode.lanterna.TextColor.ANSI.RED_BRIGHT, com.googlecode.lanterna.TextColor.ANSI.DEFAULT);
        private int maxLength = Integer.MAX_VALUE;
        private String initialValue = "";

        /**
         * Creates a new builder with required UI components.
         *
         * @param titleArea      the title area
         * @param contentArea    the body content area
         * @param inputArea      the editable field area
         * @param navigationArea bottom toolbar area
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

        @Override
        public Builder withTheme(DialogTheme theme) {
            super.withTheme(theme);
            this.validationMessageStyle = theme.validationMessageStyle();
            return this;
        }

        /**
         * Sets a validator function that runs when user attempts to confirm the input.
         *
         * @param validator function returning an error message if invalid, or empty otherwise
         * @return this builder
         */
        public Builder withValidator(Function<String, Optional<String>> validator) {
            this.validator = Objects.requireNonNull(validator);
            return this;
        }

        /**
         * Sets the style for the validation error message rendered below the input field.
         *
         * @param style text style for error messages
         * @return this builder
         */
        public Builder withValidationMessageStyle(TextStyle style) {
            this.validationMessageStyle = Objects.requireNonNull(style);
            return this;
        }

        /**
         * Sets the maximum allowed number of characters.
         *
         * @param maxLength positive integer limit
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
         * Prefills the input field with an initial value.
         *
         * @param initialValue the text to show initially
         * @return this builder
         */
        public Builder withInitialValue(String initialValue) {
            this.initialValue = Objects.requireNonNull(initialValue);
            return this;
        }

        /**
         * Builds the {@link TextLineDialog} instance.
         *
         * @return a new dialog
         */
        public TextLineDialog build() {
            if (initialValue.length() > maxLength) {
                initialValue = initialValue.substring(0, maxLength);
            }
            return new TextLineDialog(this);
        }
    }
}
