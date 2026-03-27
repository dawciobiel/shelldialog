package io.github.dawciobiel.shelldialog.cli.validation;

import java.util.Optional;

/**
 * Functional interface for password (char array) validation.
 */
@FunctionalInterface
public interface PasswordValidator {
    /**
     * Validates the password character array.
     *
     * @param input the characters to validate
     * @return an empty Optional if valid, or an Optional containing an error message if invalid
     */
    Optional<String> validate(char[] input);

    /**
     * Combines this validator with another. The resulting validator returns the first error found.
     *
     * @param other the other validator to run
     * @return a combined validator
     */
    default PasswordValidator and(PasswordValidator other) {
        return input -> {
            Optional<String> result = this.validate(input);
            return result.isPresent() ? result : other.validate(input);
        };
    }
}
