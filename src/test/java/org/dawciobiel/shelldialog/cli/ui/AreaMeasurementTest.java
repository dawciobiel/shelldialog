package org.dawciobiel.shelldialog.cli.ui;

import com.googlecode.lanterna.TextColor;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbarRenderer;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AreaMeasurementTest {

    @Test
    void titleAreaShouldMeasureLongestLineAndLineCount() {
        TitleArea area = new TitleArea.Builder()
                .withTitle(List.of("Short", "Much longer title"))
                .build();

        assertEquals(17, area.getWidth());
        assertEquals(2, area.getHeight());
    }

    @Test
    void contentAreaShouldMeasureSingleLineContent() {
        ContentArea area = new ContentArea.Builder()
                .withContent("Static content")
                .build();

        assertEquals(14, area.getWidth());
        assertEquals(1, area.getHeight());
    }

    @Test
    void inputAreaShouldMeasureCurrentContent() {
        InputArea area = new InputArea.Builder()
                .withContent("typed")
                .build();

        assertEquals(5, area.getWidth());
        assertEquals(1, area.getHeight());
    }

    @Test
    void navigationAreaShouldMeasureRenderedToolbarWidth() {
        NavigationToolbar toolbar = NavigationToolbar.builder()
                .withArrowsNavigation()
                .withEnterAccept()
                .withEscapeCancel()
                .build();
        NavigationToolbarRenderer renderer = new NavigationToolbarRenderer(
                TextColor.ANSI.CYAN,
                TextColor.ANSI.WHITE,
                TextColor.ANSI.DEFAULT
        );
        NavigationArea area = new NavigationArea.Builder()
                .withToolbar(toolbar)
                .withRenderer(renderer)
                .build();

        int expectedWidth = renderer.measureWidth(toolbar);

        assertEquals(expectedWidth, area.getWidth());
        assertEquals(1, area.getHeight());
    }

    @Test
    void titleAreaShouldApplyTitleColorFromThemeWithoutDependingOnBorderConfiguration() {
        DialogTheme theme = DialogTheme.darkTheme();
        TitleArea area = new TitleArea.Builder()
                .withTitle("Hello")
                .withTheme(theme)
                .build();

        assertEquals(5, area.getWidth());
        assertEquals(1, area.getHeight());
    }
}
