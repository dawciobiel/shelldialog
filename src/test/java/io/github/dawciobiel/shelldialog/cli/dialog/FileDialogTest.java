package io.github.dawciobiel.shelldialog.cli.dialog;

import io.github.dawciobiel.shelldialog.cli.dialog.option.DialogOption;
import io.github.dawciobiel.shelldialog.cli.dialog.option.FileOption;
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

import static org.junit.jupiter.api.Assertions.*;

class FileDialogTest {

    @TempDir
    Path tempDir;

    private Path file1;
    private Path file2;
    private Path dir1;

    @BeforeEach
    void setUp() throws IOException {
        file1 = Files.createFile(tempDir.resolve("file1.txt"));
        file2 = Files.createFile(tempDir.resolve("file2.java"));
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

        // Expect: ".." (if parent exists), "/dir1", "file1.txt", "file2.java"
        boolean hasParent = tempDir.getParent() != null;
        int expectedSize = 3 + (hasParent ? 1 : 0);

        assertEquals(expectedSize, options.size());
        
        boolean foundFile1 = options.stream().anyMatch(o -> o.getLabel().endsWith("file1.txt"));
        boolean foundFile2 = options.stream().anyMatch(o -> o.getLabel().endsWith("file2.java"));
        boolean foundDir = options.stream().anyMatch(o -> o.getLabel().endsWith("dir1"));

        assertTrue(foundFile1, "File1 should be listed");
        assertTrue(foundFile2, "File2 should be listed");
        assertTrue(foundDir, "Directory should be listed");
    }

    @Test
    void shouldSortDirectoriesBeforeFiles() throws Exception {
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
        
        // Skip parent directory if present
        int startIndex = 0;
        if (((FileOption)options.get(0)).isParentLink()) {
            startIndex = 1;
        }

        // First item (after ..) should be directory dir1
        FileOption firstOption = (FileOption) options.get(startIndex);
        assertTrue(firstOption.isDirectory(), "First item should be a directory");
        assertEquals("/dir1", firstOption.getLabel());

        // Subsequent items should be files
        assertFalse(((FileOption)options.get(startIndex + 1)).isDirectory(), "Second item should be a file");
        assertFalse(((FileOption)options.get(startIndex + 2)).isDirectory(), "Third item should be a file");
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
        // Expect: ".." and "/dir1"
        int expectedSize = 1 + (hasParent ? 1 : 0);
        
        assertEquals(expectedSize, options.size());
        
        boolean foundFile = options.stream().anyMatch(o -> o.getLabel().endsWith("file1.txt"));
        boolean foundDir = options.stream().anyMatch(o -> o.getLabel().endsWith("dir1"));

        assertFalse(foundFile, "File should NOT be listed");
        assertTrue(foundDir, "Directory should be listed");
    }

    @Test
    void shouldFilterFiles() throws Exception {
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
                .withFileFilter(p -> p.toString().endsWith(".java"))
                .build();

        List<DialogOption> options = getOptions(dialog);

        boolean hasParent = tempDir.getParent() != null;
        // Expect: ".." (if parent), "/dir1" (dirs always shown), "file2.java"
        // "file1.txt" should be filtered out
        int expectedSize = 2 + (hasParent ? 1 : 0);
        
        assertEquals(expectedSize, options.size());
        
        boolean foundTxt = options.stream().anyMatch(o -> o.getLabel().endsWith("file1.txt"));
        boolean foundJava = options.stream().anyMatch(o -> o.getLabel().endsWith("file2.java"));
        boolean foundDir = options.stream().anyMatch(o -> o.getLabel().endsWith("dir1"));

        assertFalse(foundTxt, "TXT file should be filtered out");
        assertTrue(foundJava, "Java file should be listed");
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
