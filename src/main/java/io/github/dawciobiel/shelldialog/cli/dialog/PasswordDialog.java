package io.github.dawciobiel.shelldialog.cli.dialog;

import io.github.dawciobiel.shelldialog.cli.ui.InputArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A CLI dialog that captures a password as a character array.
 * The typed value is rendered as masked content inside the shared optional frame.
 */
public class PasswordDialog extends AbstractInputDialog<char[]> {

    private final char maskCharacter;
    private final int maxLength;
    private final Function<char[], Optional<String>> validator;

    private PasswordDialog(Builder builder) {
        super(
                builder.inputStreamPath,
                builder.outputStreamPath,
                builder.titleArea,
                builder.contentArea,
                builder.inputArea,
                builder.navigationArea,
                builder.borderVisible,
                builder.maxLength,
                "",
                builder.borderStyle
        );
        this.maskCharacter = builder.maskCharacter;
        this.maxLength = builder.maxLength;
        this.validator = builder.validator;
    }

    @Override
    protected String inputDisplay(String rawInput) {
        return String.valueOf(maskCharacter).repeat(rawInput.length());
    }

    @Override
    protected Optional<String> validate(String rawInput) {
        return validator.apply(rawInput.toCharArray());
    }

    @Override
    protected char[] acceptedValue(String rawInput) {
        return rawInput.toCharArray();
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
        private int maxLength = Integer.MAX_VALUE;
        private Function<char[], Optional<String>> validator = value -> Optional.empty();
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
        public Builder withValidator(Function<char[], Optional<String>> validator) {
            this.validator = Objects.requireNonNull(validator);
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
