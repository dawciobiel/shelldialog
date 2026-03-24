package io.github.dawciobiel.shelldialog.cli.dialog;

import io.github.dawciobiel.shelldialog.cli.dialog.option.DialogOption;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileDialogTest {

    @TempDir
    Path tempDir;

    private Path file1;
    private Path dir1;

    @BeforeEach
    void setUp() throws IOException {
        file1 = Files.createFile(tempDir.resolve("file1.txt"));
        dir1 = Files.createDirectory(tempDir.resolve("dir1"));
    }

    @Test
    void shouldListFilesAndDirectories() throws Exception {
        TitleArea titleArea = new TitleArea.Builder().withTitle("Title").build();
        ContentArea contentArea = new ContentArea.Builder().withContent("Item").build();
        ContentArea selectedContentArea = new ContentArea.Builder().withContent("Selected").build();
        NavigationArea navigationArea = new NavigationArea.Builder()
                .withToolbar(NavigationToolbar.builder().build())
                .withTheme(DialogTheme.darkTheme())
                .build();

        FileDialog dialog = new FileDialog.Builder(
                titleArea,
                contentArea,
                selectedContentArea,
                navigationArea
        )
                .withInitialDirectory(tempDir)
                .build();

        List<DialogOption> options = getOptions(dialog);

        // Expect: ".." (if parent exists), "dir1", "file1.txt"
        // TempDir usually has a parent in system temp.
        boolean hasParent = tempDir.getParent() != null;
        int expectedSize = 2 + (hasParent ? 1 : 0);

        assertEquals(expectedSize, options.size());
        
        // Check content
        boolean foundFile = options.stream().anyMatch(o -> o.getLabel().endsWith("file1.txt"));
        boolean foundDir = options.stream().anyMatch(o -> o.getLabel().endsWith("dir1"));

        assertTrue(foundFile, "File should be listed");
        assertTrue(foundDir, "Directory should be listed");
    }

    @Test
    void shouldListOnlyDirectoriesWhenConfigured() throws Exception {
        TitleArea titleArea = new TitleArea.Builder().withTitle("Title").build();
        ContentArea contentArea = new ContentArea.Builder().withContent("Item").build();
        ContentArea selectedContentArea = new ContentArea.Builder().withContent("Selected").build();
        NavigationArea navigationArea = new NavigationArea.Builder()
                .withToolbar(NavigationToolbar.builder().build())
                .withTheme(DialogTheme.darkTheme())
                .build();

        FileDialog dialog = new FileDialog.Builder(
                titleArea,
                contentArea,
                selectedContentArea,
                navigationArea
        )
                .withInitialDirectory(tempDir)
                .directoriesOnly(true)
                .build();

        List<DialogOption> options = getOptions(dialog);

        boolean hasParent = tempDir.getParent() != null;
        // Expect: ".." and "dir1", but NOT "file1.txt"
        int expectedSize = 1 + (hasParent ? 1 : 0);
        
        assertEquals(expectedSize, options.size());
        
        boolean foundFile = options.stream().anyMatch(o -> o.getLabel().endsWith("file1.txt"));
        boolean foundDir = options.stream().anyMatch(o -> o.getLabel().endsWith("dir1"));

        assertTrue(!foundFile, "File should NOT be listed");
        assertTrue(foundDir, "Directory should be listed");
    }

    @SuppressWarnings("unchecked")
    private List<DialogOption> getOptions(FileDialog dialog) throws Exception {
        // AbstractListDialog stores options in 'options' field
        Field field = AbstractListDialog.class.getDeclaredField("options");
        field.setAccessible(true);
        return (List<DialogOption>) field.get(dialog);
    }
}
