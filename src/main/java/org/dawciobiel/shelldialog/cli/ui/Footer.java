package org.dawciobiel.shelldialog.cli.ui;

import com.googlecode.lanterna.graphics.TextGraphics;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbarRenderer;

import java.io.IOException;

public class Footer implements Renderable {

    private final NavigationToolbar toolbar;
    private final NavigationToolbarRenderer renderer;

    public Footer(NavigationToolbar toolbar, NavigationToolbarRenderer renderer) {
        this.toolbar = toolbar;
        this.renderer = renderer;
    }

    @Override
    public void render(TextGraphics tg, int startRow) throws IOException {
        renderer.render(tg, toolbar, startRow);
    }
}