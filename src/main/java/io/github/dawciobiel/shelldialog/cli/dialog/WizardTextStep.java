package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.InputArea;
import io.github.dawciobiel.shelldialog.cli.validation.InputValidator;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Simple single-line text input step for {@link WizardDialog}.
 */
public final class WizardTextStep implements WizardStep {

    private final String title;
    private final String description;
    private final String prompt;
    private final String contextKey;
    private final InputValidator validator;
    private final int maxLength;
    private final StringBuilder buffer;

    private WizardTextStep(Builder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.prompt = builder.prompt;
        this.contextKey = builder.contextKey;
        this.validator = builder.validator;
        this.maxLength = builder.maxLength;
        this.buffer = new StringBuilder(builder.initialValue);
    }

    /**
     * Creates a builder for a text wizard step.
     *
     * @param title step title
     * @param prompt text shown above the input
     * @param contextKey key used when committing the value
     * @return a new builder
     */
    public static Builder builder(String title, String prompt, String contextKey) {
        return new Builder(title, prompt, contextKey);
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    @Override
    public int width(WizardContext context) {
        return Math.max(prompt.length(), buffer.length());
    }

    @Override
    public int height(WizardContext context) {
        return 2;
    }

    @Override
    public void render(TextGraphics tg, int column, int row, WizardContext context, ContentArea contentArea, InputArea inputArea) throws IOException {
        contentArea.withContent(prompt).render(tg, column, row);
        inputArea.withContent(buffer.toString()).render(tg, column, row + 1);
    }

    @Override
    public void handleInput(KeyStroke key) {
        KeyType type = key.getKeyType();
        switch (type) {
            case Backspace -> {
                if (!buffer.isEmpty()) {
                    buffer.setLength(buffer.length() - 1);
                }
            }
            case Character -> {
                if (buffer.length() < maxLength) {
                    buffer.append(key.getCharacter());
                }
            }
            default -> {
            }
        }
    }

    @Override
    public Optional<String> validate() {
        return validator.validate(buffer.toString());
    }

    @Override
    public void commit(WizardContext context) {
        context.put(contextKey, buffer.toString());
    }

    @Override
    public Optional<TerminalPosition> cursorPosition(int column, int row, WizardContext context) {
        return Optional.of(new TerminalPosition(column + buffer.length(), row + 1));
    }

    /**
     * Builder for {@link WizardTextStep}.
     */
    public static final class Builder {
        private final String title;
        private final String prompt;
        private final String contextKey;
        private String description;
        private InputValidator validator = value -> Optional.empty();
        private int maxLength = Integer.MAX_VALUE;
        private String initialValue = "";

        private Builder(String title, String prompt, String contextKey) {
            this.title = normalize(title, "title");
            this.prompt = normalize(prompt, "prompt");
            this.contextKey = normalize(contextKey, "contextKey");
        }

        /**
         * Sets the validator used before moving to the next step or finishing.
         *
         * @param validator step validator
         * @return this builder
         */
        public Builder withValidator(InputValidator validator) {
            this.validator = Objects.requireNonNull(validator);
            return this;
        }

        /**
         * Sets an optional single-line help text shown below the wizard header.
         *
         * @param description help text
         * @return this builder
         */
        public Builder withDescription(String description) {
            String normalized = Objects.requireNonNull(description).trim();
            this.description = normalized.isEmpty() ? null : normalized;
            return this;
        }

        /**
         * Sets the maximum allowed input length.
         *
         * @param maxLength positive character limit
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
         * Sets the initial value shown when the step opens.
         *
         * @param initialValue initial text value
         * @return this builder
         */
        public Builder withInitialValue(String initialValue) {
            this.initialValue = Objects.requireNonNull(initialValue);
            return this;
        }

        /**
         * Builds the step.
         *
         * @return a new {@link WizardTextStep}
         */
        public WizardTextStep build() {
            if (initialValue.length() > maxLength) {
                initialValue = initialValue.substring(0, maxLength);
            }
            return new WizardTextStep(this);
        }

        private static String normalize(String value, String name) {
            String normalized = Objects.requireNonNull(value).trim();
            if (normalized.isEmpty()) {
                throw new IllegalArgumentException(name + " must not be blank");
            }
            return normalized;
        }
    }
}
