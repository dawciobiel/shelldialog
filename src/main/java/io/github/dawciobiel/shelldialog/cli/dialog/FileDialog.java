package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import io.github.dawciobiel.shelldialog.cli.dialog.option.DialogOption;
import io.github.dawciobiel.shelldialog.cli.dialog.option.FileOption;
import io.github.dawciobiel.shelldialog.cli.i18n.UIProperties;
import io.github.dawciobiel.shelldialog.cli.style.Arrow;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.DialogFrame;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A dialog for selecting files or directories.
 */
public class FileDialog extends AbstractListDialog<Path> {

    private static final String MORE_ABOVE_LABEL = "\u2191 more";
    private static final String MORE_BELOW_LABEL = "\u2193 more";

    private final TitleArea titleArea;
    private final ContentArea menuItemArea;
    private final ContentArea selectedMenuItemArea;
    private final NavigationArea navigationArea;
    private final boolean borderVisible;
    private final DialogFrame dialogFrame;

    private Path currentDirectory;
    private final boolean directoriesOnly;
    private final Predicate<Path> filter;

    private FileDialog(Builder builder) {
        super(builder.inputStreamPath, builder.outputStreamPath, new ArrayList<>(), builder.visibleItemCount);
        this.titleArea = builder.titleArea;
        this.menuItemArea = builder.menuItemArea;
        this.selectedMenuItemArea = builder.selectedMenuItemArea;
        this.navigationArea = builder.navigationArea;
        this.borderVisible = builder.borderVisible;
        this.dialogFrame = new DialogFrame(borderVisible, builder.borderStyle);
        this.directoriesOnly = builder.directoriesOnly;
        this.currentDirectory = builder.initialDirectory != null ? builder.initialDirectory : Paths.get(".").toAbsolutePath().normalize();
        this.filter = builder.filter;

        refreshOptions();
    }

    private void refreshOptions() {
        List<DialogOption> newOptions = new ArrayList<>();

        if (currentDirectory.getParent() != null) {
            newOptions.add(new FileOption(currentDirectory.getParent(), "..", true, true));
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentDirectory)) {
            List<Path> entries = new ArrayList<>();
            for (Path entry : stream) {
                entries.add(entry);
            }
            entries.sort(Comparator.comparing(Path::getFileName));

            for (Path entry : entries) {
                boolean isDirectory = Files.isDirectory(entry);
                if (isDirectory) {
                    newOptions.add(new FileOption(entry, isDirectory));
                } else if (!directoriesOnly && filter.test(entry)) {
                    newOptions.add(new FileOption(entry, isDirectory));
                }
            }
        } catch (IOException e) {
            // Handle error, maybe show empty list or error message
        }

        this.options = newOptions;
    }

    @Override
    protected Optional<Path> runDialog(Screen screen) throws IOException {
        int selectedIndex = 0;
        screen.setCursorPosition(null);
        TextGraphics tg = screen.newTextGraphics();

        while (true) {
            render(screen, tg, selectedIndex);

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            if (type == KeyType.ArrowUp) {
                selectedIndex = previousEnabledIndex(selectedIndex);
            } else if (type == KeyType.ArrowDown) {
                selectedIndex = nextEnabledIndex(selectedIndex);
            } else if (type == KeyType.Enter) {
                if (selectedIndex >= 0 && selectedIndex < options.size()) {
                    FileOption selectedOption = (FileOption) options.get(selectedIndex);
                    if (selectedOption.isParentLink()) {
                         currentDirectory = selectedOption.getPath();
                         refreshOptions();
                         selectedIndex = 0;
                    } else if (selectedOption.isDirectory()) {
                        if (directoriesOnly) {
                             currentDirectory = selectedOption.getPath();
                             refreshOptions();
                             selectedIndex = 0;
                        } else {
                            currentDirectory = selectedOption.getPath();
                            refreshOptions();
                            selectedIndex = 0;
                        }
                    } else {
                        // It's a file
                        return Optional.of(selectedOption.getPath());
                    }
                }
            } else if (type == KeyType.Escape) {
                return Optional.empty();
            }
        }
    }

    private void render(Screen screen, TextGraphics tg, int selectedIndex) throws IOException {
        screen.clear();

        int firstVisibleIndex = firstVisibleIndex(selectedIndex);
        int lastVisibleIndex = lastVisibleIndex(firstVisibleIndex);
        List<DialogOption> visibleOptions = options.subList(firstVisibleIndex, lastVisibleIndex);
        boolean hasItemsAbove = firstVisibleIndex > 0;
        boolean hasItemsBelow = lastVisibleIndex < options.size();
        boolean hasViewport = hasViewport();
        String positionIndicator = hasViewport ? positionIndicatorLabel(selectedIndex) : "";

        int optionsWidth = Math.max(
                visibleOptions.stream()
                        .mapToInt(option -> menuItemWidth(option.getLabel()))
                        .max()
                        .orElse(0),
                moreIndicatorWidth(hasItemsAbove, hasItemsBelow)
        );
        if (hasViewport) {
            optionsWidth = Math.max(optionsWidth, positionIndicator.length());
        }
        
        String pathString = currentDirectory.toString();
        int pathWidth = pathString.length();
        int contentWidth = Math.max(
                Math.max(Math.max(titleArea.getWidth(), optionsWidth), pathWidth),
                navigationArea.getWidth()
        );
        
        int contentHeight = titleArea.getHeight()
                + 1 // Path line
                + 1 // Spacer
                + (hasItemsAbove ? 1 : 0)
                + visibleOptions.size()
                + (hasItemsBelow ? 1 : 0)
                + (hasViewport ? 1 : 0)
                + 1
                + navigationArea.getHeight();

        DialogFrame.FrameLayout layout = dialogFrame.layoutFor(contentWidth, contentHeight);
        dialogFrame.render(tg, layout);

        int column = layout.contentColumn();
        int row = layout.contentRow();
        titleArea.render(tg, column, row);
        row += titleArea.getHeight();

        // Render current path
        menuItemArea.withContent(pathString).render(tg, column, row++);
        row++; // Spacer

        if (hasItemsAbove) {
            menuItemArea.withContent(MORE_ABOVE_LABEL).render(tg, column, row++);
        }

        for (int i = firstVisibleIndex; i < lastVisibleIndex; i++) {
            DialogOption option = options.get(i);
            renderMenuItem(tg, column, row++, option, i == selectedIndex);
        }

        if (hasItemsBelow) {
            menuItemArea.withContent(MORE_BELOW_LABEL).render(tg, column, row++);
        }

        if (hasViewport) {
            menuItemArea.withContent(positionIndicator).render(tg, column, row++);
        }

        row++;

        navigationArea.render(tg, column, row);

        screen.refresh();
    }

    private void renderMenuItem(TextGraphics tg, int column, int row, DialogOption option, boolean selected) throws IOException {
        String item = option.getLabel();
        String leftMarker = selected ? Arrow.MARKER_EFT : Arrow.MARKER_EMPTY;
        String rightMarker = selected ? Arrow.MARKER_RIGHT : Arrow.MARKER_EMPTY;
        String text = leftMarker + item + rightMarker;
        ContentArea currentArea = (selected ? selectedMenuItemArea : menuItemArea).withContent(text);
        currentArea.render(tg, column, row);
    }

    private int menuItemWidth(String item) {
        return (Arrow.MARKER_EMPTY + item + Arrow.MARKER_EMPTY).length();
    }

    private int moreIndicatorWidth(boolean hasItemsAbove, boolean hasItemsBelow) {
        int width = 0;
        if (hasItemsAbove) {
            width = MORE_ABOVE_LABEL.length();
        }
        if (hasItemsBelow) {
            width = Math.max(width, MORE_BELOW_LABEL.length());
        }
        return width;
    }

    public static class Builder extends AbstractFrameDialogBuilder<Builder> {
        private final TitleArea titleArea;
        private final ContentArea menuItemArea;
        private final ContentArea selectedMenuItemArea;
        private final NavigationArea navigationArea;
        private int visibleItemCount = 0;
        private Path initialDirectory;
        private boolean directoriesOnly = false;
        private Predicate<Path> filter = path -> true;
        private String inputStreamPath = "/dev/tty";
        private String outputStreamPath = "/dev/tty";

        public Builder(TitleArea titleArea, ContentArea menuItemArea, ContentArea selectedMenuItemArea, NavigationArea navigationArea) {
            this.titleArea = Objects.requireNonNull(titleArea);
            this.menuItemArea = Objects.requireNonNull(menuItemArea);
            this.selectedMenuItemArea = Objects.requireNonNull(selectedMenuItemArea);
            this.navigationArea = Objects.requireNonNull(navigationArea);
        }

        @Override
        protected Builder self() {
            return this;
        }

        public Builder inputStream(String path) {
            this.inputStreamPath = Objects.requireNonNull(path);
            return this;
        }

        public Builder outputStream(String path) {
            this.outputStreamPath = Objects.requireNonNull(path);
            return this;
        }

        public Builder withVisibleItemCount(int visibleItemCount) {
            if (visibleItemCount <= 0) {
                throw new IllegalArgumentException("visibleItemCount must be positive");
            }
            this.visibleItemCount = visibleItemCount;
            return this;
        }

        public Builder withInitialDirectory(Path initialDirectory) {
            this.initialDirectory = initialDirectory;
            return this;
        }

        public Builder directoriesOnly(boolean directoriesOnly) {
            this.directoriesOnly = directoriesOnly;
            return this;
        }

        /**
         * Sets a filter to include only specific files.
         * Directories are always shown regardless of the filter to allow navigation.
         *
         * @param filter the predicate to test file paths against
         * @return this builder
         */
        public Builder withFileFilter(Predicate<Path> filter) {
            this.filter = Objects.requireNonNull(filter);
            return this;
        }

        /**
         * Sets a filter to include only files with specific extensions.
         * Extensions are case-insensitive and can be provided with or without a leading dot.
         *
         * @param extensions the list of allowed extensions (e.g. "java", ".md")
         * @return this builder
         */
        public Builder withExtensions(List<String> extensions) {
            Objects.requireNonNull(extensions);
            List<String> normalized = extensions.stream()
                    .map(ext -> ext.startsWith(".") ? ext.toLowerCase() : "." + ext.toLowerCase())
                    .toList();
            return withFileFilter(path -> {
                String fileName = path.getFileName().toString().toLowerCase();
                return normalized.stream().anyMatch(fileName::endsWith);
            });
        }

        public FileDialog build() {
            return new FileDialog(this);
        }
    }
}
