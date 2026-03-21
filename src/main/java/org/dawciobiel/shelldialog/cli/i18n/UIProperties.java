package org.dawciobiel.shelldialog.cli.i18n;

import java.util.ResourceBundle;

/**
 * Accesses localized UI symbols and layout strings from the {@code ui} resource bundle.
 */
public class UIProperties {

    private static final String BUNDLE_NAME = "ui";
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);

    private UIProperties() {
    }

    /**
     * Returns the localized value associated with the supplied key.
     *
     * @param key the resource-bundle key
     * @return the localized value, or {@code !key!} when the key cannot be resolved
     */
    public static String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            return '!' + key + '!';
        }
    }
}
