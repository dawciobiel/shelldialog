package org.dawciobiel.shelldialog.cli.ui;

import com.googlecode.lanterna.graphics.TextGraphics;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbarRenderer;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;

import java.io.IOException;

public class NavigationArea implements Renderable {

    private final NavigationToolbar toolbar;
    private final NavigationToolbarRenderer renderer;

    private NavigationArea(Builder builder) {
        this.toolbar = builder.toolbar;
        this.renderer = builder.renderer;
    }

    @Override
    public void render(TextGraphics tg, int startRow) throws IOException {
        renderer.render(tg, toolbar, startRow);
    }

    public static class Builder {
        private NavigationToolbar toolbar;
        private NavigationToolbarRenderer renderer;

        public Builder withToolbar(NavigationToolbar toolbar) {
            this.toolbar = toolbar;
            return this;
        }

        public Builder withTheme(DialogTheme theme) {
            this.renderer = new NavigationToolbarRenderer(
                    theme.navigationStyle().foreground(),
                    theme.navigationStyle().foreground(),
                    theme.navigationStyle().background()
            );
            return this;
        }
        
        public Builder withRenderer(NavigationToolbarRenderer renderer) {
            this.renderer = renderer;
            return this;
        }

        public NavigationArea build() {
            return new NavigationArea(this);
        }
    }
}
