package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import io.github.dawciobiel.shelldialog.cli.dialog.option.DialogOption;
import io.github.dawciobiel.shelldialog.cli.dialog.option.FileOption;
import io.github.dawciobiel.shelldialog.cli.i18n.Messages;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.style.Arrow;
import io.github.dawciobiel.shelldialog.cli.style.TextStyle;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.DialogFrame;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A CLI dialog for selecting files or directories from the local file system.
 * Supports navigation through directories, parent directory link (".."), and live filtering of files in the current view.
 */
public class FileDialog extends AbstractListDialog<Path> {

    /**
     * Named file-extension presets for common browsing scenarios.
     */
    public enum ExtensionPreset {
        SOURCE_FILES(List.of("java", "kt", "groovy", "scala", "c", "cpp", "h", "hpp", "js", "ts", "py", "go", "rs")),
        TEXT_FILES(List.of("txt", "md", "adoc", "rst")),
        CONFIG_FILES(List.of("properties", "yml", "yaml", "json", "toml", "xml", "ini", "conf")),
        DOCUMENTATION_FILES(List.of("md", "adoc", "rst", "txt"));

        private final List<String> extensions;

        ExtensionPreset(List<String> extensions) {
            this.extensions = List.copyOf(extensions);
        }

        List<String> extensions() {
            return extensions;
        }
    }

    private static final String MORE_ABOVE_LABEL = "\u2191 more";
    private static final String MORE_BELOW_LABEL = "\u2193 more";
    private static final String CURRENT_DIRECTORY_LABEL = Messages.getString("dialog.file.current_directory");
    private static final String READ_ERROR_LABEL = Messages.getString("dialog.file.read_error");
    private static final String NEW_DIRECTORY_LABEL = Messages.getString("dialog.file.new_directory");
    private static final String CREATE_ERROR_LABEL = Messages.getString("dialog.file.create_error");
    private static final String CREATE_ERROR_BLANK_LABEL = Messages.getString("dialog.file.create_error_blank");
    private static final String PREVIEW_SELECTED_LABEL = Messages.getString("dialog.file.preview_selected");
    private static final String PREVIEW_TYPE_LABEL = Messages.getString("dialog.file.preview_type");
    private static final String PREVIEW_PATH_LABEL = Messages.getString("dialog.file.preview_path");
    private static final String PREVIEW_SIZE_LABEL = Messages.getString("dialog.file.preview_size");
    private static final String PREVIEW_MODIFIED_LABEL = Messages.getString("dialog.file.preview_modified");
    private static final String PREVIEW_FILE_LABEL = Messages.getString("dialog.file.preview_file");
    private static final String PREVIEW_DIRECTORY_LABEL = Messages.getString("dialog.file.preview_directory");
    private static final String PREVIEW_PARENT_LABEL = Messages.getString("dialog.file.preview_parent");
    private static final String PREVIEW_CURRENT_LABEL = Messages.getString("dialog.file.preview_current");
    private static final String PREVIEW_UNKNOWN_LABEL = Messages.getString("dialog.file.preview_unknown");
    private static final String FILTER_LABEL = Messages.getString("dialog.file.filter");
    private static final Path CWD = Paths.get(".").toAbsolutePath().normalize();
    private static final DateTimeFormatter PREVIEW_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final TitleArea titleArea;
    private final ContentArea menuItemArea;
    private final ContentArea selectedMenuItemArea;
    private final NavigationArea navigationArea;
    private final boolean borderVisible;
    private final DialogFrame dialogFrame;
    private final TextStyle errorMessageStyle;
    private final boolean metadataPreviewVisible;

    private Path currentDirectory;
    private final boolean directoriesOnly;
    private final Predicate<Path> fileFilter;
    private final String filterLabel;
    private final Map<KeyType, Path> shortcuts;
    private boolean showHiddenFiles;
    private String errorMessage;
    private boolean creatingDirectory;
    private final StringBuilder newDirectoryName = new StringBuilder();

    private FileDialog(Builder builder) {
        super(builder.inputStream, builder.outputStream, builder.inputStreamPath, builder.outputStreamPath, builder.terminal,
              new ArrayList<>(), builder.visibleItemCount);
        this.titleArea = builder.titleArea;
        this.menuItemArea = builder.menuItemArea;
        this.selectedMenuItemArea = builder.selectedMenuItemArea;
        this.navigationArea = builder.navigationArea;
        this.borderVisible = builder.borderVisible;
        this.dialogFrame = new DialogFrame(borderVisible, builder.borderStyle);
        this.errorMessageStyle = builder.errorMessageStyle;
        this.metadataPreviewVisible = builder.metadataPreviewVisible;
        this.directoriesOnly = builder.directoriesOnly;
        this.fileFilter = builder.filter;
        this.filterLabel = builder.filterLabel;
        this.shortcuts = Map.copyOf(builder.shortcuts);
        this.showHiddenFiles = builder.showHiddenFiles;
        this.currentDirectory = builder.initialDirectory != null ? builder.initialDirectory : CWD;

        refreshDirectoryContent();
    }

    private void refreshDirectoryContent() {
        List<DialogOption> newOptions = new ArrayList<>();

        if (currentDirectory.getParent() != null) {
            newOptions.add(new FileOption(currentDirectory.getParent(), "..", true, true));
        }
        if (directoriesOnly) {
            newOptions.add(new FileOption(currentDirectory, CURRENT_DIRECTORY_LABEL, true, false, true));
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentDirectory)) {
            List<Path> entries = new ArrayList<>();
            for (Path entry : stream) {
                entries.add(entry);
            }
            
            // Modern sorting: Directories first, then files, both alphabetically
            entries.sort((p1, p2) -> {
                boolean isDir1 = Files.isDirectory(p1);
                boolean isDir2 = Files.isDirectory(p2);
                if (isDir1 != isDir2) {
                    return isDir1 ? -1 : 1;
                }
                return p1.getFileName().toString().compareToIgnoreCase(p2.getFileName().toString());
            });

            for (Path entry : entries) {
                boolean isDirectory = Files.isDirectory(entry);
                boolean hidden = isHiddenEntry(entry);
                if (hidden && !showHiddenFiles) {
                    continue;
                }

                if (isDirectory) {
                    newOptions.add(new FileOption(entry, true));
                } else if (!directoriesOnly && fileFilter.test(entry)) {
                    newOptions.add(new FileOption(entry, false));
                }
            }
            errorMessage = null;
        } catch (IOException e) {
            newOptions.clear();
            errorMessage = READ_ERROR_LABEL + ": " + currentDirectory;
        }

        this.allOptions.clear();
        this.allOptions.addAll(newOptions);
        updateFilter(filterText);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Optional<Path> runDialog(Screen screen) throws IOException {
        int selectedIndex = 0;
        TextGraphics tg = screen.newTextGraphics();

        while (true) {
            render(screen, tg, selectedIndex);

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            if (creatingDirectory) {
                switch (type) {
                    case Enter -> {
                        if (createDirectory(newDirectoryName.toString())) {
                            creatingDirectory = false;
                            newDirectoryName.setLength(0);
                            selectedIndex = 0;
                        }
                    }
                    case Escape -> {
                        creatingDirectory = false;
                        newDirectoryName.setLength(0);
                    }
                    case Backspace -> {
                        if (!newDirectoryName.isEmpty()) {
                            newDirectoryName.setLength(newDirectoryName.length() - 1);
                            errorMessage = null;
                        }
                    }
                    case Character -> {
                        newDirectoryName.append(key.getCharacter());
                        errorMessage = null;
                    }
                    default -> {
                    }
                }
                continue;
            }

            if (shortcuts.containsKey(type)) {
                currentDirectory = shortcuts.get(type);
                clearFilter();
                refreshDirectoryContent();
                selectedIndex = 0;
                continue;
            }

            switch (type) {
                case ArrowUp -> selectedIndex = previousEnabledIndex(selectedIndex);
                case ArrowDown -> selectedIndex = nextEnabledIndex(selectedIndex);
                case F5 -> {
                    refreshDirectoryContent();
                    selectedIndex = 0;
                }
                case F2 -> {
                    showHiddenFiles = !showHiddenFiles;
                    clearFilter();
                    refreshDirectoryContent();
                    selectedIndex = 0;
                }
                case F7 -> {
                    if (errorMessage == null) {
                        creatingDirectory = true;
                        newDirectoryName.setLength(0);
                    }
                }
                case Home -> {
                    currentDirectory = Paths.get(System.getProperty("user.home"));
                    clearFilter();
                    refreshDirectoryContent();
                    selectedIndex = 0;
                }
                case End -> {
                    currentDirectory = CWD;
                    clearFilter();
                    refreshDirectoryContent();
                    selectedIndex = 0;
                }
                case Character -> {
                    updateFilter(filterText + key.getCharacter());
                    selectedIndex = 0;
                }
                case Backspace -> {
                    if (!filterText.isEmpty()) {
                        updateFilter(filterText.substring(0, filterText.length() - 1));
                        selectedIndex = 0;
                    }
                }
                case Enter -> {
                    if (selectedIndex >= 0 && selectedIndex < options.size()) {
                        FileOption selectedOption = (FileOption) options.get(selectedIndex);
                        if (selectedOption.isCurrentDirectoryLink()) {
                            return Optional.of(currentDirectory);
                        } else if (selectedOption.isParentLink() || selectedOption.isDirectory()) {
                            currentDirectory = selectedOption.getPath();
                            clearFilter();
                            refreshDirectoryContent();
                            selectedIndex = 0;
                        } else {
                            return Optional.of(selectedOption.getPath());
                        }
                    }
                }
                case Escape -> {
                    if (!filterText.isEmpty()) {
                        clearFilter();
                        selectedIndex = 0;
                    } else {
                        return Optional.empty();
                    }
                }
                default -> {
                }
            }
        }
    }

    private void render(Screen screen, TextGraphics tg, int selectedIndex) throws IOException {
        screen.clear();
        screen.setCursorPosition(null);

        int firstVisibleIndex = firstVisibleIndex(selectedIndex);
        int lastVisibleIndex = lastVisibleIndex(firstVisibleIndex);
        List<DialogOption> visibleOptions = options.subList(firstVisibleIndex, lastVisibleIndex);
        boolean hasItemsAbove = firstVisibleIndex > 0;
        boolean hasItemsBelow = lastVisibleIndex < options.size();
        boolean hasViewport = hasViewport();
        String positionIndicator = hasViewport ? positionIndicatorLabel(selectedIndex) : "";
        String searchLine = filterText.isEmpty() ? "" : "Search: " + filterText + "_";
        String pathString = currentDirectory.toString();
        String filterLine = filterLabel == null ? "" : FILTER_LABEL + ": " + filterLabel;
        boolean hasError = errorMessage != null;
        String newDirectoryLine = creatingDirectory ? NEW_DIRECTORY_LABEL + ": " + newDirectoryName + "_" : "";
        List<String> previewLines = metadataPreviewVisible ? previewLines(selectedIndex) : List.of();

        int optionsWidth = Math.max(
                visibleOptions.stream()
                        .mapToInt(option -> menuItemWidth(option.getLabel()))
                        .max()
                        .orElse(0),
                moreIndicatorWidth(hasItemsAbove, hasItemsBelow)
        );
        
        int contentWidth = Math.max(
                Math.max(Math.max(Math.max(Math.max(titleArea.getWidth(), optionsWidth), pathString.length()), searchLine.length()), filterLine.length()),
                navigationArea.getWidth()
        );
        if (hasError) {
            contentWidth = Math.max(contentWidth, errorMessage.length());
        }
        if (creatingDirectory) {
            contentWidth = Math.max(contentWidth, newDirectoryLine.length());
        }
        if (!previewLines.isEmpty()) {
            contentWidth = Math.max(
                    contentWidth,
                    previewLines.stream().mapToInt(String::length).max().orElse(0)
            );
        }

        int contentHeight = titleArea.getHeight()
                + 1 // Path line
                + (filterLabel == null ? 0 : 1) // Filter line
                + (filterText.isEmpty() ? 0 : 2) // Search line + spacer
                + 1 // Spacer
                + (previewLines.isEmpty() ? 0 : previewLines.size() + 1)
                + (creatingDirectory ? 1 : 0)
                + (hasError ? 1 : 0)
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

        menuItemArea.withContent(pathString).render(tg, column, row++);
        if (filterLabel != null) {
            menuItemArea.withContent(filterLine).render(tg, column, row++);
        }
        
        if (!filterText.isEmpty()) {
            row++;
            menuItemArea.withContent(searchLine).render(tg, column, row++);
        }
        
        row++; // Spacer

        if (!previewLines.isEmpty()) {
            for (String previewLine : previewLines) {
                menuItemArea.withContent(previewLine).render(tg, column, row++);
            }
            row++;
        }

        int createDirectoryRow = row;
        if (creatingDirectory) {
            menuItemArea.withContent(newDirectoryLine).render(tg, column, row++);
        }

        if (hasError) {
            new ContentArea.Builder()
                    .withContent(errorMessage)
                    .withForegroundColor(errorMessageStyle.foreground())
                    .withBackgroundColor(errorMessageStyle.background())
                    .build()
                    .render(tg, column, row++);
        }

        if (hasItemsAbove) {
            menuItemArea.withContent(MORE_ABOVE_LABEL).render(tg, column, row++);
        }

        if (options.isEmpty()) {
            menuItemArea.withContent("(no results)").render(tg, column, row++);
        } else {
            for (int i = firstVisibleIndex; i < lastVisibleIndex; i++) {
                DialogOption option = options.get(i);
                renderMenuItem(tg, column, row++, option, i == selectedIndex);
            }
        }

        if (hasItemsBelow) {
            menuItemArea.withContent(MORE_BELOW_LABEL).render(tg, column, row++);
        }

        if (hasViewport) {
            menuItemArea.withContent(positionIndicator).render(tg, column, row++);
        }

        row++;

        navigationArea.render(tg, column, row);

        if (creatingDirectory) {
            screen.setCursorPosition(new TerminalPosition(column + NEW_DIRECTORY_LABEL.length() + 2 + newDirectoryName.length(), createDirectoryRow));
        }

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
        if (hasItemsAbove) width = MORE_ABOVE_LABEL.length();
        if (hasItemsBelow) width = Math.max(width, MORE_BELOW_LABEL.length());
        return width;
    }

    private boolean isHiddenEntry(Path entry) {
        try {
            return Files.isHidden(entry) || entry.getFileName().toString().startsWith(".");
        } catch (IOException e) {
            return entry.getFileName().toString().startsWith(".");
        }
    }

    private boolean createDirectory(String directoryName) {
        String normalizedName = directoryName.trim();
        if (normalizedName.isEmpty()) {
            errorMessage = CREATE_ERROR_BLANK_LABEL;
            return false;
        }

        try {
            Files.createDirectory(currentDirectory.resolve(normalizedName));
            errorMessage = null;
            clearFilter();
            refreshDirectoryContent();
            return true;
        } catch (IOException e) {
            errorMessage = CREATE_ERROR_LABEL + ": " + normalizedName;
            return false;
        }
    }

    private List<String> previewLines(int selectedIndex) {
        if (options.isEmpty() || selectedIndex < 0 || selectedIndex >= options.size()) {
            return List.of();
        }

        FileOption selectedOption = (FileOption) options.get(selectedIndex);
        Path selectedPath = selectedOption.getPath();
        String typeLabel = previewType(selectedOption);
        String sizeLabel = previewSize(selectedOption);
        String modifiedLabel = previewModified(selectedOption);

        return List.of(
                PREVIEW_SELECTED_LABEL + ": " + selectedOption.getLabel(),
                PREVIEW_TYPE_LABEL + ": " + typeLabel,
                PREVIEW_PATH_LABEL + ": " + selectedPath,
                PREVIEW_SIZE_LABEL + ": " + sizeLabel,
                PREVIEW_MODIFIED_LABEL + ": " + modifiedLabel
        );
    }

    private String previewType(FileOption option) {
        if (option.isCurrentDirectoryLink()) {
            return PREVIEW_CURRENT_LABEL;
        }
        if (option.isParentLink()) {
            return PREVIEW_PARENT_LABEL;
        }
        if (option.isDirectory()) {
            return PREVIEW_DIRECTORY_LABEL;
        }
        return PREVIEW_FILE_LABEL;
    }

    private String previewSize(FileOption option) {
        if (option.isParentLink() || option.isCurrentDirectoryLink() || option.isDirectory()) {
            return PREVIEW_UNKNOWN_LABEL;
        }
        try {
            return humanReadableSize(Files.size(option.getPath()));
        } catch (IOException e) {
            return PREVIEW_UNKNOWN_LABEL;
        }
    }

    private String humanReadableSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " B";
        }

        String[] units = {"KB", "MB", "GB", "TB"};
        double size = sizeInBytes;
        int unitIndex = -1;
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        return "%.1f %s".formatted(size, units[unitIndex]);
    }

    private String previewModified(FileOption option) {
        if (option.isParentLink() || option.isCurrentDirectoryLink()) {
            return PREVIEW_UNKNOWN_LABEL;
        }
        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(option.getPath());
            return PREVIEW_TIME_FORMAT.format(lastModifiedTime.toInstant().atZone(ZoneId.systemDefault()));
        } catch (IOException e) {
            return PREVIEW_UNKNOWN_LABEL;
        }
    }

    /**
     * Builder for {@link FileDialog} instances.
     */
    public static class Builder extends AbstractFrameDialogBuilder<Builder> {
        private final TitleArea titleArea;
        private final ContentArea menuItemArea;
        private final ContentArea selectedMenuItemArea;
        private final NavigationArea navigationArea;
        private int visibleItemCount = 0;
        private TextStyle errorMessageStyle = TextStyle.of(com.googlecode.lanterna.TextColor.ANSI.RED_BRIGHT, com.googlecode.lanterna.TextColor.ANSI.DEFAULT);
        private Path initialDirectory;
        private boolean directoriesOnly = false;
        private boolean showHiddenFiles = false;
        private boolean metadataPreviewVisible = false;
        private Predicate<Path> filter = path -> true;
        private String filterLabel;
        private Map<KeyType, Path> shortcuts = Collections.emptyMap();

        /**
         * Creates a new builder with required areas.
         *
         * @param titleArea            the title component
         * @param menuItemArea         base style for list items
         * @param selectedMenuItemArea style for the currently focused item
         * @param navigationArea       bottom toolbar area
         */
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

        @Override
        public Builder withTheme(DialogTheme theme) {
            super.withTheme(theme);
            this.errorMessageStyle = theme.validationMessageStyle();
            return this;
        }

        /**
         * Limits the number of visible items in the file list.
         *
         * @param visibleItemCount max visible rows
         * @return this builder
         */
        public Builder withVisibleItemCount(int visibleItemCount) {
            if (visibleItemCount <= 0) {
                throw new IllegalArgumentException("visibleItemCount must be positive");
            }
            this.visibleItemCount = visibleItemCount;
            return this;
        }

        /**
         * Sets the style used to render directory read error messages.
         *
         * @param style text style for error messages
         * @return this builder
         */
        public Builder withErrorMessageStyle(TextStyle style) {
            this.errorMessageStyle = Objects.requireNonNull(style);
            return this;
        }

        /**
         * Sets the starting directory for the file browser.
         *
         * @param initialDirectory starting path
         * @return this builder
         */
        public Builder withInitialDirectory(Path initialDirectory) {
            this.initialDirectory = initialDirectory;
            return this;
        }

        /**
         * Enables directory selection mode. If true, files will be hidden.
         *
         * @param directoriesOnly true to show only directories
         * @return this builder
         */
        public Builder directoriesOnly(boolean directoriesOnly) {
            this.directoriesOnly = directoriesOnly;
            return this;
        }

        /**
         * Controls whether hidden files and directories are visible.
         *
         * @param showHiddenFiles true to include hidden entries
         * @return this builder
         */
        public Builder withShowHiddenFiles(boolean showHiddenFiles) {
            this.showHiddenFiles = showHiddenFiles;
            return this;
        }

        /**
         * Controls whether a metadata preview panel is rendered for the selected entry.
         *
         * @param metadataPreviewVisible true to show file metadata below the current path
         * @return this builder
         */
        public Builder withMetadataPreview(boolean metadataPreviewVisible) {
            this.metadataPreviewVisible = metadataPreviewVisible;
            return this;
        }

        /**
         * Sets a custom filter for files. Directories are always shown to allow navigation.
         *
         * @param filter path predicate
         * @return this builder
         */
        public Builder withFileFilter(Predicate<Path> filter) {
            this.filter = Objects.requireNonNull(filter);
            this.filterLabel = null;
            return this;
        }

        /**
         * Configures custom keyboard shortcuts to specific directories.
         *
         * @param shortcuts map of keys to target paths
         * @return this builder
         */
        public Builder withShortcuts(Map<KeyType, Path> shortcuts) {
            this.shortcuts = Map.copyOf(Objects.requireNonNull(shortcuts));
            return this;
        }

        /**
         * Sets a filter based on allowed file extensions.
         *
         * @param extensions list of extensions (e.g. "java", ".md")
         * @return this builder
         */
        public Builder withExtensions(List<String> extensions) {
            List<String> normalized = normalizeExtensions(extensions);
            this.filter = path -> {
                String fileName = path.getFileName().toString().toLowerCase();
                return normalized.stream().anyMatch(fileName::endsWith);
            };
            this.filterLabel = normalized.stream()
                    .map(ext -> ext.startsWith(".") ? ext.substring(1) : ext)
                    .map(String::toUpperCase)
                    .reduce((left, right) -> left + ", " + right)
                    .orElse(null);
            return this;
        }

        /**
         * Sets a filter using one named extension preset.
         *
         * @param preset preset of file extensions
         * @return this builder
         */
        public Builder withExtensionPreset(ExtensionPreset preset) {
            return withExtensionPresets(Objects.requireNonNull(preset));
        }

        /**
         * Sets a filter using one or more named extension presets.
         *
         * @param presets presets of file extensions
         * @return this builder
         */
        public Builder withExtensionPresets(ExtensionPreset... presets) {
            Objects.requireNonNull(presets);
            if (presets.length == 0) {
                throw new IllegalArgumentException("presets must not be empty");
            }

            Set<String> extensions = new LinkedHashSet<>();
            List<String> labels = new ArrayList<>();
            for (ExtensionPreset preset : presets) {
                ExtensionPreset normalizedPreset = Objects.requireNonNull(preset);
                extensions.addAll(normalizedPreset.extensions());
                labels.add(normalizedPreset.name());
            }
            withExtensions(List.copyOf(extensions));
            this.filterLabel = String.join(", ", labels);
            return this;
        }

        private static List<String> normalizeExtensions(List<String> extensions) {
            Objects.requireNonNull(extensions);
            return extensions.stream()
                    .map(ext -> ext.startsWith(".") ? ext.toLowerCase() : "." + ext.toLowerCase())
                    .toList();
        }

        /**
         * Builds the {@link FileDialog} instance.
         *
         * @return a new dialog
         */
        public FileDialog build() {
            return new FileDialog(this);
        }
    }
}
