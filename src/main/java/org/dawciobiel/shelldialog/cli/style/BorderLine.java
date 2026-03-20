package org.dawciobiel.shelldialog.cli.style;

import org.dawciobiel.shelldialog.cli.i18n.UIProperties;

public final class BorderLine {

    // @formatter:off
    public static final String NO = UIProperties.getString("border.no");

    public static final String DOUBLE_HORIZONTAL = UIProperties.getString("border.double.horizontal");
    public static final String DOUBLE_VERTICAL = UIProperties.getString("border.double.vertical");
    public static final String DOUBLE_TOP_LEFT = UIProperties.getString("border.double.top.left");
    public static final String DOUBLE_TOP_RIGHT = UIProperties.getString("border.double.top.right");
    public static final String DOUBLE_BOTTOM_LEFT = UIProperties.getString("border.double.bottom.left");
    public static final String DOUBLE_BOTTOM_RIGHT = UIProperties.getString("border.double.bottom.right");

    public static final String SINGLE_HORIZONTAL = UIProperties.getString("border.single.horizontal");
    public static final String SINGLE_VERTICAL = UIProperties.getString("border.single.vertical");
    public static final String SINGLE_TOP_LEFT = UIProperties.getString("border.single.top.left");
    public static final String SINGLE_TOP_RIGHT = UIProperties.getString("border.single.top.right");
    public static final String SINGLE_BOTTOM_LEFT = UIProperties.getString("border.single.bottom.left");
    public static final String SINGLE_BOTTOM_RIGHT = UIProperties.getString("border.single.bottom.right");
    // @formatter:on

    private BorderLine() {
        throw new UnsupportedOperationException("BorderLine is a utility class and cannot be instantiated");
    }
}
