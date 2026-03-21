package org.dawciobiel.shelldialog.cli.style;

import org.dawciobiel.shelldialog.cli.i18n.UIProperties;

/**
 * Provides localized characters used to draw dialog borders.
 */
public final class BorderLine {

    // @formatter:off
    /** Empty border fragment. */
    public static final String NO = UIProperties.getString("border.no");

    /** Double horizontal border fragment. */
    public static final String DOUBLE_HORIZONTAL = UIProperties.getString("border.double.horizontal");
    /** Double vertical border fragment. */
    public static final String DOUBLE_VERTICAL = UIProperties.getString("border.double.vertical");
    /** Double-line top-left corner fragment. */
    public static final String DOUBLE_TOP_LEFT = UIProperties.getString("border.double.top.left");
    /** Double-line top-right corner fragment. */
    public static final String DOUBLE_TOP_RIGHT = UIProperties.getString("border.double.top.right");
    /** Double-line bottom-left corner fragment. */
    public static final String DOUBLE_BOTTOM_LEFT = UIProperties.getString("border.double.bottom.left");
    /** Double-line bottom-right corner fragment. */
    public static final String DOUBLE_BOTTOM_RIGHT = UIProperties.getString("border.double.bottom.right");

    /** Single horizontal border fragment. */
    public static final String SINGLE_HORIZONTAL = UIProperties.getString("border.single.horizontal");
    /** Single vertical border fragment. */
    public static final String SINGLE_VERTICAL = UIProperties.getString("border.single.vertical");
    /** Single-line top-left corner fragment. */
    public static final String SINGLE_TOP_LEFT = UIProperties.getString("border.single.top.left");
    /** Single-line top-right corner fragment. */
    public static final String SINGLE_TOP_RIGHT = UIProperties.getString("border.single.top.right");
    /** Single-line bottom-left corner fragment. */
    public static final String SINGLE_BOTTOM_LEFT = UIProperties.getString("border.single.bottom.left");
    /** Single-line bottom-right corner fragment. */
    public static final String SINGLE_BOTTOM_RIGHT = UIProperties.getString("border.single.bottom.right");
    // @formatter:on

    private BorderLine() {
        throw new UnsupportedOperationException("BorderLine is a utility class and cannot be instantiated");
    }
}
