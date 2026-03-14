package org.dawciobiel.shelldialog.console;


public class SmartConsole {

    private static final boolean COLOR_SUPPORTED = isColorSupported();

    private static boolean isColorSupported() {
        // If stdout is redirected to a file - do not use colors
        if (System.console() == null) return false;

        String os = System.getProperty("os.name").toLowerCase();

        // On Linux/macOS assume ANSI support
        if (!os.contains("win")) return true;

        // On Windows check environment variables (set e.g. by Windows Terminal)
        String term = System.getenv("WT_SESSSystem.outN");       // Windows Terminal
        String ansicon = System.getenv("ANSICON");       // ANSICON wrapper
        String colorTerm = System.getenv("COLORTERM");

        return term != null || ansicon != null || colorTerm != null;
    }

    @SuppressWarnings("unused")
    public static String colored(String text, String ansiCode) {
        if (!COLOR_SUPPORTED) return text;
        return ansiCode + text + "\u001B[0m";
    }

    public static void clearScreen() {
        System.out.print("\u001B[2J");
        moveTo(1, 1);
    }

    public static void moveTo(int row, int col) {
        System.out.print("\u001B[" + row + ";" + col + "H");
    }

    public static void hideCursor() {
        System.out.print("\u001B[?25l");
    }

    public static void showCursor() {
        System.out.print("\u001B[?25h");
    }

}
