package org.dawciobiel.shelldialog.cli.i18n;

import java.util.ResourceBundle;

public class UIProperties {

    private static final String BUNDLE_NAME = "ui";
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);

    private UIProperties() {
    }

    public static String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            return '!' + key + '!';
        }
    }
}
