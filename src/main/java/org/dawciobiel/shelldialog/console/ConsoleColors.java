package org.dawciobiel.shelldialog.console;

@SuppressWarnings("unused")
public class ConsoleColors {
    // Reset
    public static final String RESET  = "\u001B[0m";

    // Text colors (foreground)
    public static final String BLACK   = "\u001B[30m";
    public static final String RED     = "\u001B[31m";
    public static final String GREEN   = "\u001B[32m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String BLUE    = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN    = "\u001B[36m";
    public static final String WHITE   = "\u001B[37m";
    public static final String GREY    = "\u001B[90m";

    // Bright variants
    public static final String BRIGHT_RED    = "\u001B[91m";
    public static final String BRIGHT_GREEN  = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE   = "\u001B[94m";

    // Background colors
    public static final String BG_RED    = "\u001B[41m";
    public static final String BG_GREEN  = "\u001B[42m";
    public static final String BG_YELLOW = "\u001B[43m";
    public static final String BG_BLUE   = "\u001B[44m";

    // Styles
    public static final String BOLD      = "\u001B[1m";
    public static final String UNDERLINE = "\u001B[4m";
}
