package io.github.dawciobiel.shelldialog.cli.dialog;

import io.github.dawciobiel.shelldialog.cli.validation.InputValidator;
import io.github.dawciobiel.shelldialog.cli.validation.PasswordValidator;

import java.util.Objects;
import java.util.Optional;

/**
 * Defines a single field rendered by {@link FormDialog}.
 */
public final class FormField {

    enum Type {
        TEXT,
        PASSWORD
    }

    private final String name;
    private final String label;
    private final Type type;
    private final InputValidator validator;
    private final int maxLength;
    private final String initialValue;
    private final char maskCharacter;

    private FormField(
            String name,
            String label,
            Type type,
            InputValidator validator,
            int maxLength,
            String initialValue,
            char maskCharacter
    ) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.validator = validator;
        this.maxLength = maxLength;
        this.initialValue = initialValue;
        this.maskCharacter = maskCharacter;
    }

    /**
     * Creates a builder for a plain text field.
     *
     * @param name unique field key in the returned result
     * @param label field label rendered in the dialog
     * @return a text field builder
     */
    public static TextBuilder text(String name, String label) {
        return new TextBuilder(name, label);
    }

    /**
     * Creates a builder for a password field.
     *
     * @param name unique field key in the returned result
     * @param label field label rendered in the dialog
     * @return a password field builder
     */
    public static PasswordBuilder password(String name, String label) {
        return new PasswordBuilder(name, label);
    }

    String name() {
        return name;
    }

    String label() {
        return label;
    }

    int maxLength() {
        return maxLength;
    }

    String initialValue() {
        return initialValue;
    }

    Optional<String> validate(String rawInput) {
        return validator.validate(rawInput);
    }

    String displayValue(String rawInput) {
        if (type == Type.PASSWORD) {
            return String.valueOf(maskCharacter).repeat(rawInput.length());
        }
        return rawInput;
    }

    Object acceptedValue(String rawInput) {
        if (type == Type.PASSWORD) {
            return rawInput.toCharArray();
        }
        return rawInput;
    }

    /**
     * Builder for a text form field.
     */
    public static final class TextBuilder {
        private final String name;
        private final String label;
        private InputValidator validator = value -> Optional.empty();
        private int maxLength = Integer.MAX_VALUE;
        private String initialValue = "";

        private TextBuilder(String name, String label) {
            this.name = normalizeName(name);
            this.label = normalizeLabel(label);
        }

        /**
         * Sets the field validator.
         *
         * @param validator validator invoked on blur and submit
         * @return this builder
         */
        public TextBuilder withValidator(InputValidator validator) {
            this.validator = Objects.requireNonNull(validator);
            return this;
        }

        /**
         * Sets the maximum allowed character count.
         *
         * @param maxLength positive character limit
         * @return this builder
         */
        public TextBuilder withMaxLength(int maxLength) {
            if (maxLength <= 0) {
                throw new IllegalArgumentException("maxLength must be positive");
            }
            this.maxLength = maxLength;
            return this;
        }

        /**
         * Sets the initial text value.
         *
         * @param initialValue initial field content
         * @return this builder
         */
        public TextBuilder withInitialValue(String initialValue) {
            this.initialValue = Objects.requireNonNull(initialValue);
            return this;
        }

        /**
         * Builds the field definition.
         *
         * @return a new {@link FormField}
         */
        public FormField build() {
            String normalizedInitialValue = initialValue.length() > maxLength
                    ? initialValue.substring(0, maxLength)
                    : initialValue;
            return new FormField(name, label, Type.TEXT, validator, maxLength, normalizedInitialValue, '*');
        }
    }

    /**
     * Builder for a password form field.
     */
    public static final class PasswordBuilder {
        private final String name;
        private final String label;
        private PasswordValidator validator = value -> Optional.empty();
        private int maxLength = Integer.MAX_VALUE;
        private String initialValue = "";
        private char maskCharacter = '*';

        private PasswordBuilder(String name, String label) {
            this.name = normalizeName(name);
            this.label = normalizeLabel(label);
        }

        /**
         * Sets the field validator.
         *
         * @param validator validator invoked on blur and submit
         * @return this builder
         */
        public PasswordBuilder withValidator(PasswordValidator validator) {
            this.validator = Objects.requireNonNull(validator);
            return this;
        }

        /**
         * Sets the maximum allowed character count.
         *
         * @param maxLength positive character limit
         * @return this builder
         */
        public PasswordBuilder withMaxLength(int maxLength) {
            if (maxLength <= 0) {
                throw new IllegalArgumentException("maxLength must be positive");
            }
            this.maxLength = maxLength;
            return this;
        }

        /**
         * Sets the initial password value.
         *
         * @param initialValue initial password characters
         * @return this builder
         */
        public PasswordBuilder withInitialValue(char[] initialValue) {
            this.initialValue = String.valueOf(Objects.requireNonNull(initialValue));
            return this;
        }

        /**
         * Sets the masking character used on screen.
         *
         * @param maskCharacter masking character
         * @return this builder
         */
        public PasswordBuilder withMaskCharacter(char maskCharacter) {
            this.maskCharacter = maskCharacter;
            return this;
        }

        /**
         * Builds the field definition.
         *
         * @return a new {@link FormField}
         */
        public FormField build() {
            String normalizedInitialValue = initialValue.length() > maxLength
                    ? initialValue.substring(0, maxLength)
                    : initialValue;
            InputValidator inputValidator = value -> validator.validate(value.toCharArray());
            return new FormField(name, label, Type.PASSWORD, inputValidator, maxLength, normalizedInitialValue, maskCharacter);
        }
    }

    private static String normalizeName(String name) {
        String normalizedName = Objects.requireNonNull(name).trim();
        if (normalizedName.isEmpty()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        return normalizedName;
    }

    private static String normalizeLabel(String label) {
        String normalizedLabel = Objects.requireNonNull(label).trim();
        if (normalizedLabel.isEmpty()) {
            throw new IllegalArgumentException("label must not be blank");
        }
        return normalizedLabel;
    }
}
