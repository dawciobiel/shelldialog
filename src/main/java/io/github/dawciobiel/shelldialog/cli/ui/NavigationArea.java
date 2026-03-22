package io.github.dawciobiel.shelldialog.cli.ui;

import com.googlecode.lanterna.graphics.TextGraphics;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbarRenderer;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;

import java.io.IOException;

/**
 * Renders the navigation toolbar shown at the bottom of a dialog.
 */
public class NavigationArea implements Renderable {

    private final NavigationToolbar toolbar;
    private final NavigationToolbarRenderer renderer;

    private NavigationArea(Builder builder) {
        this.toolbar = builder.toolbar;
        this.renderer = builder.renderer;
    }

    @Override
    public void render(TextGraphics tg, int startColumn, int startRow) throws IOException {
        renderer.render(tg, toolbar, startColumn, startRow);
    }

    @Override
    public int getWidth() {
        return renderer.measureWidth(toolbar);
    }

    @Override
    public int getHeight() {
        return 1;
    }

    /**
     * Builder for {@link NavigationArea} instances.
     */
    public static class Builder {
        private NavigationToolbar toolbar;
        private NavigationToolbarRenderer renderer;

        /**
         * Creates an empty builder.
         */
        public Builder() {
        }

        /**
         * Sets the toolbar definition to render.
         *
         * @param toolbar the toolbar model
         * @return this builder
         */
        public Builder withToolbar(NavigationToolbar toolbar) {
            this.toolbar = toolbar;
            return this;
        }

        /**
         * Creates a renderer using the navigation colors from the supplied theme.
         *
         * @param theme the theme supplying navigation colors
         * @return this builder
         */
        public Builder withTheme(DialogTheme theme) {
            this.renderer = new NavigationToolbarRenderer(
                    theme.navigationStyle().foreground(),
                    theme.navigationStyle().foreground(),
                    theme.navigationStyle().background()
            );
            return this;
        }

        /**
         * Sets a custom renderer for the toolbar.
         *
         * @param renderer the renderer to use
         * @return this builder
         */
        public Builder withRenderer(NavigationToolbarRenderer renderer) {
            this.renderer = renderer;
            return this;
        }

        /**
         * Builds the navigation area.
         *
         * @return a new {@link NavigationArea}
         */
        public NavigationArea build() {
            return new NavigationArea(this);
        }
    }
}
