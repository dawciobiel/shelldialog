package org.dawciobiel.shelldialog.console;

import java.util.Locale;
import java.util.ResourceBundle;

public class Messages {
    private static final String BUNDLE_NAME = "messages";
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());

    private Messages() {
    }

    /**
     * Sets the locale for the message resource bundle.
     * This allows changing the language used for retrieving localized strings.<br>
     * Usage example:<br>
     * <code>
     * Messages.setLocale(new Locale("en", "US"));
     * Messages.setLocale(new Locale("pl", "PL"));
     * </code>
     *
     * @param locale the new locale to be used
     */
    public static void setLocale(Locale locale) {
        resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
    }

    public static String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            return '!' + key + '!';
        }
    }
}
