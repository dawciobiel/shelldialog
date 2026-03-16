package org.dawciobiel.shelldialog.cli.dialog.result;


public sealed interface Value permits IntegerValue, TextValue, ErrorValue {

    default Integer getIntegerValue() {
        return null;
    }

    default String getTextValue() {
        return null;
    }

    default String getErrorValue() {
        return null;
    }
}
