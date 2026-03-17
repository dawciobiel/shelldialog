package org.dawciobiel.shelldialog.cli.header.border;

import org.dawciobiel.shelldialog.cli.i18n.Messages;

@SuppressWarnings("unused")
public final class BorderLine {

    //formatters:off
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
    //formatters:on

    private BorderLine() {
        throw new UnsupportedOperationException(Messages.getString("error.borderline.instantiation"));
    }
}
