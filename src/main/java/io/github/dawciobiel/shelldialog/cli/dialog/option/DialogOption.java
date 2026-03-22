package io.github.dawciobiel.shelldialog.cli.dialog.option;

/**
 * Describes a selectable option displayed by choice-based dialogs.
 */
public interface DialogOption {

    /**
     * Returns the machine-readable code associated with the option.
     *
     * @return the option code
     */
    int getCode();

    /**
     * Returns the human-readable label shown in the dialog.
     *
     * @return the option label
     */
    String getLabel();

    /**
     * Returns whether the option can currently be focused and selected.
     *
     * @return {@code true} when the option is enabled, otherwise {@code false}
     */
    default boolean isEnabled() {
        return true;
    }
}
