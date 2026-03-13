package org.dawciobiel.shelldialog.console.header.border;

public final class BorderLine {

    public static final String NO = " ";

    public static final String HORIZONTAL = "═";
    public static final String VERTICAL = "║";
    public static final String TOP_LEFT = "╔";
    public static final String TOP_RIGHT = "╗";
    public static final String BOTTOM_LEFT = "╚";
    public static final String BOTTOM_RIGHT = "╝";

    public static final String SINGLE_HORIZONTAL = "─";
    public static final String SINGLE_VERTICAL = "│";
    public static final String SINGLE_TOP_LEFT = "┌";
    public static final String SINGLE_TOP_RIGHT = "┐";
    public static final String SINGLE_BOTTOM_LEFT = "└";
    public static final String SINGLE_BOTTOM_RIGHT = "┘";

    /* Borders examples
        ┌───────┐
        │       │
        └───────┘

        ╔───────╗
        │       │
        ╚───────╝

        ╔═══════╗
        ║       ║
        ╚═══════╝
     */

    private BorderLine() {
        throw new UnsupportedOperationException("BorderLine is a utility class and cannot be instantiated");
    }
}