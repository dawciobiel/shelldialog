package org.dawciobiel.shelldialog.cli.header.border;

@SuppressWarnings("unused")
public final class BorderLine {

    // @formatter:off
    /* Borders examples:
            ┌───────┐    ╔───────╗   ╔═══════╗
            │       │    │       │   ║       ║
            └───────┘    ╚───────╝   ╚═══════╝
     */

    public static final String NO = " ";

    public static final String DOUBLE_HORIZONTAL = "═";
    public static final String DOUBLE_VERTICAL = "║";
    public static final String DOUBLE_TOP_LEFT = "╔";
    public static final String DOUBLE_TOP_RIGHT = "╗";
    public static final String DOUBLE_BOTTOM_LEFT = "╚";
    public static final String DOUBLE_BOTTOM_RIGHT = "╝";

    public static final String SINGLE_HORIZONTAL = "─";
    public static final String SINGLE_VERTICAL = "│";
    public static final String SINGLE_TOP_LEFT = "┌";
    public static final String SINGLE_TOP_RIGHT = "┐";
    public static final String SINGLE_BOTTOM_LEFT = "└";
    public static final String SINGLE_BOTTOM_RIGHT = "┘";
    // @formatter:on

    private BorderLine() {
        throw new UnsupportedOperationException("BorderLine is a utility class and cannot be instantiated");
    }
}
