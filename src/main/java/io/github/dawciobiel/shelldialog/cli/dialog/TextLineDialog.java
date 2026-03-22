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
 * A CLI dialog that prompts the user for a single line of text input.
 * It composes preconfigured UI areas inside a shared optional frame.
 * It supports typing, backspace, confirmation (Enter), and cancellation (Escape).
 * <p>
 * The dialog is rendered using the Lanterna library.
 * </p>
 */
public class TextLineDialog extends AbstractInputDialog<String> {

    private final int maxLength;
    private final Function<String, Optional<String>> validator;

    private TextLineDialog(Builder builder) {
        super(
                builder.inputStreamPath,
                builder.outputStreamPath,
                builder.titleArea,
                builder.contentArea,
                builder.inputArea,
                builder.navigationArea,
                builder.borderVisible,
                builder.validationMessageStyle,
                builder.maxLength,
                builder.normalizedInitialValue(),
                builder.borderStyle
        );
        this.maxLength = builder.maxLength;
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
     * Builder for creating instances of {@link TextLineDialog}.
     */
    public static class Builder extends AbstractFrameDialogBuilder<Builder> {

        private final TitleArea titleArea;
        private final ContentArea contentArea;
        private final InputArea inputArea;
        private final NavigationArea navigationArea;

        private int maxLength = Integer.MAX_VALUE;
        private String initialValue = "";
        private TextStyle validationMessageStyle = TextStyle.ofAnsi(com.googlecode.lanterna.TextColor.ANSI.RED_BRIGHT, com.googlecode.lanterna.TextColor.ANSI.DEFAULT);
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
         * Applies frame and validation message styles from the provided theme.
         *
         * @param theme the theme supplying dialog styles
         * @return this builder
         */
        @Override
        public Builder withTheme(DialogTheme theme) {
            super.withTheme(theme);
            this.validationMessageStyle = Objects.requireNonNull(theme).validationMessageStyle();
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
         * Sets the style used for validation messages rendered below the input field.
         *
         * @param validationMessageStyle the validation message style
         * @return this builder
         */
        public Builder withValidationMessageStyle(TextStyle validationMessageStyle) {
            this.validationMessageStyle = Objects.requireNonNull(validationMessageStyle);
            return this;
        }

        /**
         * Sets the initial value shown in the input field when the dialog opens.
         * If the value is longer than {@code maxLength}, it is truncated during build.
         *
         * @param initialValue the initial text value
         * @return this builder
         */
        public Builder withInitialValue(String initialValue) {
            this.initialValue = Objects.requireNonNull(initialValue);
            return this;
        }

        private String normalizedInitialValue() {
            return initialValue.length() <= maxLength
                    ? initialValue
                    : initialValue.substring(0, maxLength);
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
