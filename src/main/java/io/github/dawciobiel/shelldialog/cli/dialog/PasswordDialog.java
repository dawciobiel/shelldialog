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
 * A dialog for masked single-line text input.
 */
public class PasswordDialog extends AbstractInputDialog<char[]> {

    private final Function<char[], Optional<String>> validator;
    private final char maskCharacter;

    private PasswordDialog(Builder builder) {
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
        this.maskCharacter = builder.maskCharacter;
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
     * Builder for {@link PasswordDialog} instances.
     */
    public static class Builder extends AbstractFrameDialogBuilder<Builder> {
        private final TitleArea titleArea;
        private final ContentArea contentArea;
        private final InputArea inputArea;
        private final NavigationArea navigationArea;

        private Function<char[], Optional<String>> validator = value -> Optional.empty();
        private TextStyle validationMessageStyle = TextStyle.of(com.googlecode.lanterna.TextColor.ANSI.RED_BRIGHT, com.googlecode.lanterna.TextColor.ANSI.DEFAULT);
        private int maxLength = Integer.MAX_VALUE;
        private String initialValue = "";
        private char maskCharacter = '*';

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

        public Builder withValidator(Function<char[], Optional<String>> validator) {
            this.validator = Objects.requireNonNull(validator);
            return this;
        }

        public Builder withValidationMessageStyle(TextStyle style) {
            this.validationMessageStyle = Objects.requireNonNull(style);
            return this;
        }

        public Builder withMaxLength(int maxLength) {
            if (maxLength <= 0) {
                throw new IllegalArgumentException("maxLength must be positive");
            }
            this.maxLength = maxLength;
            return this;
        }

        public Builder withInitialValue(char[] initialValue) {
            this.initialValue = String.valueOf(Objects.requireNonNull(initialValue));
            return this;
        }

        public Builder withMaskCharacter(char maskCharacter) {
            this.maskCharacter = maskCharacter;
            return this;
        }

        public PasswordDialog build() {
            if (initialValue.length() > maxLength) {
                initialValue = initialValue.substring(0, maxLength);
            }
            return new PasswordDialog(this);
        }
    }
}
