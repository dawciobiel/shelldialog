package org.dawciobiel.shelldialog.cli.ui;

import com.googlecode.lanterna.graphics.TextGraphics;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbarRenderer;

import java.io.IOException;

public class NavigationArea implements Renderable {

    private final NavigationToolbar toolbar;
    private final NavigationToolbarRenderer renderer;

    public NavigationArea(NavigationToolbar toolbar, NavigationToolbarRenderer renderer) {
        this.toolbar = toolbar;
        this.renderer = renderer;
    }

    @Override
    public void render(TextGraphics tg, int startRow) throws IOException {
        renderer.render(tg, toolbar, startRow);
    }
}
