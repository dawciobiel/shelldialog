package io.github.dawciobiel.shelldialog.cli.dialog.option;

/**
 * Immutable {@link DialogOption} implementation backed by a numeric code and label.
 */
public class SimpleDialogOption implements DialogOption {
    private final int code;
    private final String label;
    private final boolean enabled;

    /**
     * Creates a new dialog option.
     *
     * @param code the machine-readable option code
     * @param label the text displayed to the user
     */
    public SimpleDialogOption(int code, String label) {
        this(code, label, true);
    }

    /**
     * Creates a new dialog option.
     *
     * @param code the machine-readable option code
     * @param label the text displayed to the user
     * @param enabled whether the option is enabled
     */
    public SimpleDialogOption(int code, String label, boolean enabled) {
        this.code = code;
        this.label = label;
        this.enabled = enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabel() {
        return label;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
