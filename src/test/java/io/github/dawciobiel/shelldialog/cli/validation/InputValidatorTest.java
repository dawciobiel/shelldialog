package io.github.dawciobiel.shelldialog.cli.validation;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InputValidatorTest {

    @Test
    void nonEmptyValidatorShouldWork() {
        InputValidator v = InputValidator.BuiltIn.nonEmpty("Err");
        assertEquals(Optional.of("Err"), v.validate(""));
        assertEquals(Optional.of("Err"), v.validate("  "));
        assertEquals(Optional.of("Err"), v.validate(null));
        assertEquals(Optional.empty(), v.validate("content"));
    }

    @Test
    void maxLengthValidatorShouldWork() {
        InputValidator v = InputValidator.BuiltIn.maxLength(3, "Too long");
        assertEquals(Optional.empty(), v.validate("abc"));
        assertEquals(Optional.of("Too long"), v.validate("abcd"));
    }

    @Test
    void emailValidatorShouldWork() {
        InputValidator v = InputValidator.BuiltIn.email("Invalid");
        assertEquals(Optional.empty(), v.validate("test@example.com"));
        assertEquals(Optional.empty(), v.validate("user.name+tag@sub.domain.org"));
        assertEquals(Optional.of("Invalid"), v.validate("plainaddress"));
        assertEquals(Optional.of("Invalid"), v.validate("@missinguser.com"));
    }

    @Test
    void isIntegerValidatorShouldWork() {
        InputValidator v = InputValidator.BuiltIn.isInteger("Not a number");
        assertEquals(Optional.empty(), v.validate("123"));
        assertEquals(Optional.empty(), v.validate("-45"));
        assertEquals(Optional.of("Not a number"), v.validate("12.3"));
        assertEquals(Optional.of("Not a number"), v.validate("abc"));
    }

    @Test
    void regexValidatorShouldWork() {
        InputValidator v = InputValidator.BuiltIn.regex("^[0-9]+$", "Digits only");
        assertEquals(Optional.empty(), v.validate("12345"));
        assertEquals(Optional.of("Digits only"), v.validate("123a45"));
    }

    @Test
    void andOperatorShouldCombineValidators() {
        InputValidator v = InputValidator.BuiltIn.nonEmpty("Req")
                .and(InputValidator.BuiltIn.isInteger("Num"));

        assertEquals(Optional.of("Req"), v.validate(""));
        assertEquals(Optional.of("Num"), v.validate("abc"));
        assertEquals(Optional.empty(), v.validate("123"));
    }

    @Test
    void asPasswordValidatorShouldConvertTypes() {
        InputValidator v = InputValidator.BuiltIn.nonEmpty("Err");
        PasswordValidator pv = v.asPasswordValidator();
        
        assertEquals(Optional.of("Err"), pv.validate("".toCharArray()));
        assertEquals(Optional.empty(), pv.validate("secret".toCharArray()));
    }
}
