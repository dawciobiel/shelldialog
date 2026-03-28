package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import io.github.dawciobiel.shelldialog.cli.i18n.Messages;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.InputArea;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Single-line directory path input step for {@link WizardDialog}.
 */
public final class WizardDirectoryStep implements WizardStep {

    private static final String REQUIRED_LABEL = Messages.getString("dialog.wizard.directory_required");
    private static final String NOT_DIRECTORY_LABEL = Messages.getString("dialog.wizard.directory_not_directory");

    private final String title;
    private final String prompt;
    private final String contextKey;
    private final Function<Path, Optional<String>> validator;
    private final boolean mustExist;
    private final int maxLength;
    private final StringBuilder buffer;

    private WizardDirectoryStep(Builder builder) {
        this.title = builder.title;
        this.prompt = builder.prompt;
        this.contextKey = builder.contextKey;
        this.validator = builder.validator;
        this.mustExist = builder.mustExist;
        this.maxLength = builder.maxLength;
        this.buffer = new StringBuilder(builder.initialValue);
    }

    /**
     * Creates a builder for a directory wizard step.
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
        String value = buffer.toString().trim();
        if (value.isEmpty()) {
            return Optional.of(REQUIRED_LABEL);
        }

        Path path = Path.of(value);
        if (mustExist && !Files.isDirectory(path)) {
            return Optional.of(NOT_DIRECTORY_LABEL);
        }

        return validator.apply(path);
    }

    @Override
    public void commit(WizardContext context) {
        context.put(contextKey, Path.of(buffer.toString().trim()));
    }

    @Override
    public Optional<TerminalPosition> cursorPosition(int column, int row, WizardContext context) {
        return Optional.of(new TerminalPosition(column + buffer.length(), row + 1));
    }

    /**
     * Builder for {@link WizardDirectoryStep}.
     */
    public static final class Builder {
        private final String title;
        private final String prompt;
        private final String contextKey;
        private Function<Path, Optional<String>> validator = value -> Optional.empty();
        private boolean mustExist;
        private int maxLength = Integer.MAX_VALUE;
        private String initialValue = "";

        private Builder(String title, String prompt, String contextKey) {
            this.title = normalize(title, "title");
            this.prompt = normalize(prompt, "prompt");
            this.contextKey = normalize(contextKey, "contextKey");
        }

        /**
         * Requires the entered path to point to an existing directory.
         *
         * @param mustExist whether the directory must exist
         * @return this builder
         */
        public Builder mustExist(boolean mustExist) {
            this.mustExist = mustExist;
            return this;
        }

        /**
         * Sets the validator used after built-in directory checks.
         *
         * @param validator step validator
         * @return this builder
         */
        public Builder withValidator(Function<Path, Optional<String>> validator) {
            this.validator = Objects.requireNonNull(validator);
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
         * Sets the initial directory path shown when the step opens.
         *
         * @param initialValue initial path
         * @return this builder
         */
        public Builder withInitialValue(Path initialValue) {
            this.initialValue = Objects.requireNonNull(initialValue).toString();
            return this;
        }

        /**
         * Builds the step.
         *
         * @return a new {@link WizardDirectoryStep}
         */
        public WizardDirectoryStep build() {
            if (initialValue.length() > maxLength) {
                initialValue = initialValue.substring(0, maxLength);
            }
            return new WizardDirectoryStep(this);
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
