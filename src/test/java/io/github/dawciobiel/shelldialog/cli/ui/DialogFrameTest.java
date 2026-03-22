package io.github.dawciobiel.shelldialog.cli.ui;

import com.googlecode.lanterna.TextColor;
import io.github.dawciobiel.shelldialog.cli.style.TextStyle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DialogFrameTest {

    @Test
    void shouldIncludeBorderAndPaddingInLayoutWhenBorderIsVisible() {
        DialogFrame frame = new DialogFrame(
                true,
                TextStyle.of(TextColor.ANSI.BLUE, TextColor.ANSI.DEFAULT),
                2,
                1
        );

        DialogFrame.FrameLayout layout = frame.layoutFor(10, 4);

        assertEquals(16, layout.outerWidth());
        assertEquals(8, layout.outerHeight());
        assertEquals(3, layout.contentColumn());
        assertEquals(2, layout.contentRow());
        assertEquals(10, layout.contentWidth());
        assertEquals(4, layout.contentHeight());
    }

    @Test
    void shouldOnlyUsePaddingInLayoutWhenBorderIsHidden() {
        DialogFrame frame = new DialogFrame(
                false,
                TextStyle.of(TextColor.ANSI.BLUE, TextColor.ANSI.DEFAULT),
                2,
                1
        );

        DialogFrame.FrameLayout layout = frame.layoutFor(10, 4);

        assertEquals(14, layout.outerWidth());
        assertEquals(6, layout.outerHeight());
        assertEquals(2, layout.contentColumn());
        assertEquals(1, layout.contentRow());
        assertEquals(10, layout.contentWidth());
        assertEquals(4, layout.contentHeight());
    }
}
