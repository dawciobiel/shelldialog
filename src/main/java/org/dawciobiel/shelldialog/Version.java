package org.dawciobiel.shelldialog;

/**
 * Reads the library version from the JAR manifest when available.
 */
public final class Version {

    private static final String UNKNOWN = "unknown";

    private Version() {
    }

    /**
     * Returns the version recorded in the package manifest.
     *
     * @return the implementation version or {@code "unknown"} when unavailable
     */
    public static String get() {
        Package shelldialogPackage = Version.class.getPackage();
        if (shelldialogPackage == null) {
            return UNKNOWN;
        }

        String implementationVersion = shelldialogPackage.getImplementationVersion();
        return implementationVersion != null ? implementationVersion : UNKNOWN;
    }
}
