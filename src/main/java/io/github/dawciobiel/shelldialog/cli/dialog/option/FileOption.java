package io.github.dawciobiel.shelldialog.cli.dialog.option;

import java.nio.file.Path;

/**
 * A dialog option representing a file or directory.
 */
public class FileOption implements DialogOption {

    private final Path path;
    private final String label;
    private final boolean isDirectory;
    private final boolean isParentLink;

    public FileOption(Path path, boolean isDirectory) {
        this(path, path.getFileName() != null ? path.getFileName().toString() : path.toString(), isDirectory, false);
    }

    public FileOption(Path path, String label, boolean isDirectory, boolean isParentLink) {
        this.path = path;
        this.label = label;
        this.isDirectory = isDirectory;
        this.isParentLink = isParentLink;
    }

    @Override
    public int getCode() {
        return path.hashCode();
    }

    @Override
    public String getLabel() {
        if (isParentLink) {
            return "..";
        }
        return (isDirectory ? "/" : "") + label;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Path getPath() {
        return path;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public boolean isParentLink() {
        return isParentLink;
    }
}
