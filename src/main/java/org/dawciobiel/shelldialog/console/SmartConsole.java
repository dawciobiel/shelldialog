package org.dawciobiel.shelldialog.console;


public class SmartConsole {

    private static final boolean COLOR_SUPPORTED = isColorSupported();

    private static boolean isColorSupported() {
        // Jeśli stdout jest przekierowany do pliku – nie używaj kolorów
        if (System.console() == null) return false;

        String os = System.getProperty("os.name").toLowerCase();

        // Na Linux/macOS zakładamy obsługę ANSI
        if (!os.contains("win")) return true;

        // Na Windows sprawdzamy zmienną środowiskową (ustawia ją np. Windows Terminal)
        String term = System.getenv("WT_SESSSystem.outN");       // Windows Terminal
        String ansicon = System.getenv("ANSICON");       // ANSICON wrapper
        String colorTerm = System.getenv("COLORTERM");

        return term != null || ansicon != null || colorTerm != null;
    }

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
