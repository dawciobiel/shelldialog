package io.github.dawciobiel.shelldialog.cli.validation;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Functional interface for string input validation.
 */
@FunctionalInterface
public interface InputValidator {
    /**
     * Validates the input string.
     *
     * @param input the string to validate
     * @return an empty Optional if valid, or an Optional containing an error message if invalid
     */
    Optional<String> validate(String input);

    /**
     * Combines this validator with another. The resulting validator returns the first error found.
     *
     * @param other the other validator to run
     * @return a combined validator
     */
    default InputValidator and(InputValidator other) {
        return input -> {
            Optional<String> result = this.validate(input);
            return result.isPresent() ? result : other.validate(input);
        };
    }

    /**
     * Adapts this validator to work with character arrays.
     * Note: This temporarily creates a String from the character array.
     *
     * @return a PasswordValidator that delegates to this string validator
     */
    @SuppressWarnings("unused")
    default PasswordValidator asPasswordValidator() {
        return chars -> this.validate(new String(chars));
    }

    /**
     * Factory for common validators.
     */
    @SuppressWarnings("unused")
    class BuiltIn {
        private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        public static InputValidator nonEmpty(String errorMessage) {
            return input -> input == null || input.isBlank() ? Optional.of(errorMessage) : Optional.empty();
        }

        public static InputValidator maxLength(int max, String errorMessage) {
            return input -> input != null && input.length() > max ? Optional.of(errorMessage) : Optional.empty();
        }

        public static InputValidator regex(String regex, String errorMessage) {
            Pattern pattern = Pattern.compile(regex);
            return input -> pattern.matcher(input).matches() ? Optional.empty() : Optional.of(errorMessage);
        }

        public static InputValidator email(String errorMessage) {
            return input -> EMAIL_PATTERN.matcher(input).matches() ? Optional.empty() : Optional.of(errorMessage);
        }

        public static InputValidator isInteger(String errorMessage) {
            return input -> {
                if (input == null || input.isBlank()) return Optional.of(errorMessage);
                try {
                    Integer.parseInt(input);
                    return Optional.empty();
                } catch (NumberFormatException e) {
                    return Optional.of(errorMessage);
                }
            };
        }
    }
}
