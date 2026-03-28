package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.input.KeyType;
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
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileDialogTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createFile(tempDir.resolve("file2.java"));
        Files.createFile(tempDir.resolve("file3.md"));
        Files.createFile(tempDir.resolve(".hidden.txt"));
        Files.createDirectory(tempDir.resolve("dir1"));
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

        boolean hasParent = tempDir.getParent() != null;
        int expectedSize = 4 + (hasParent ? 1 : 0);

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
        
        int startIndex = 0;
        if (((FileOption)options.getFirst()).isParentLink()) {
            startIndex = 1;
        }

        FileOption firstOption = (FileOption) options.get(startIndex);
        assertTrue(firstOption.isDirectory(), "First item should be a directory");
        assertEquals("/dir1", firstOption.getLabel());

        assertFalse(((FileOption)options.get(startIndex + 1)).isDirectory(), "Second item should be a file");
        assertFalse(((FileOption)options.get(startIndex + 2)).isDirectory(), "Third item should be a file");
    }

    @Test
    void shouldSupportCustomShortcuts() throws Exception {
        TitleArea titleArea = new TitleArea.Builder().withTitle("Title").build();
        ContentArea contentArea = new ContentArea.Builder().withContent("Item").build();
        ContentArea selectedContentArea = new ContentArea.Builder().withContent("Selected").build();
        NavigationArea navigationArea = new NavigationArea.Builder()
                .withToolbar(NavigationToolbar.builder().build())
                .withTheme(DialogTheme.darkTheme())
                .build();

        Path customPath = Paths.get("/tmp");
        Map<KeyType, Path> shortcuts = Map.of(KeyType.F1, customPath);

        FileDialog dialog = new FileDialog.Builder(
                titleArea,
                contentArea,
                selectedContentArea,
                navigationArea
        )
                .withShortcuts(shortcuts)
                .build();

        @SuppressWarnings("unchecked")
        Map<KeyType, Path> storedShortcuts = (Map<KeyType, Path>) readField(dialog, "shortcuts");
        assertEquals(1, storedShortcuts.size());
        assertEquals(customPath, storedShortcuts.get(KeyType.F1));
    }

    @Test
    void shouldHideHiddenFilesByDefault() throws Exception {
        FileDialog dialog = createDialogBuilder()
                .withInitialDirectory(tempDir)
                .build();

        List<DialogOption> options = getOptions(dialog);

        boolean foundHidden = options.stream().anyMatch(o -> o.getLabel().endsWith(".hidden.txt"));
        assertFalse(foundHidden, "Hidden files should not be listed by default");
        assertFalse((boolean) readField(dialog, "showHiddenFiles"));
    }

    @Test
    void shouldShowHiddenFilesWhenConfigured() throws Exception {
        FileDialog dialog = createDialogBuilder()
                .withInitialDirectory(tempDir)
                .withShowHiddenFiles(true)
                .build();

        List<DialogOption> options = getOptions(dialog);

        boolean foundHidden = options.stream().anyMatch(o -> o.getLabel().endsWith(".hidden.txt"));
        assertTrue(foundHidden, "Hidden files should be listed when enabled");
        assertTrue((boolean) readField(dialog, "showHiddenFiles"));
    }

    @Test
    void shouldStoreMetadataPreviewFlagWhenConfigured() throws Exception {
        FileDialog dialog = createDialogBuilder()
                .withInitialDirectory(tempDir)
                .withMetadataPreview(true)
                .build();

        assertTrue((boolean) readField(dialog, "metadataPreviewVisible"));
    }

    @Test
    void shouldBuildPreviewLinesForSelectedFile() throws Exception {
        FileDialog dialog = createDialogBuilder()
                .withInitialDirectory(tempDir)
                .withMetadataPreview(true)
                .build();

        List<String> previewLines = invokePreviewLines(dialog, 2);

        assertEquals(6, previewLines.size());
        assertTrue(previewLines.get(0).startsWith("Selected: "));
        assertTrue(previewLines.get(1).contains("Type: File"));
        assertTrue(previewLines.get(2).contains("Path: "));
        assertTrue(previewLines.get(3).contains("Size: "));
        assertTrue(previewLines.get(4).contains("Modified: "));
        assertTrue(previewLines.get(5).contains("Permissions: "));
    }

    @Test
    void shouldFormatPreviewSizeAsHumanReadableValue() throws Exception {
        FileDialog dialog = createDialogBuilder()
                .withInitialDirectory(tempDir)
                .build();

        assertEquals("999 B", invokeHumanReadableSize(dialog, 999));
        assertEquals("1.0 KB", invokeHumanReadableSize(dialog, 1024));
        assertEquals("1.5 KB", invokeHumanReadableSize(dialog, 1536));
        assertEquals("1.0 MB", invokeHumanReadableSize(dialog, 1024 * 1024));
    }

    @Test
    void shouldStoreErrorMessageWhenDirectoryCannotBeRead() throws Exception {
        Path missingDirectory = tempDir.resolve("missing");

        FileDialog dialog = createDialogBuilder()
                .withInitialDirectory(missingDirectory)
                .build();

        assertEquals("Unable to read directory: " + missingDirectory, readField(dialog, "errorMessage"));
        assertTrue(getOptions(dialog).isEmpty(), "Options should be empty when directory cannot be read");
    }

    @Test
    void shouldClearErrorMessageAfterSuccessfulRefresh() throws Exception {
        Path missingDirectory = tempDir.resolve("missing");

        FileDialog dialog = createDialogBuilder()
                .withInitialDirectory(missingDirectory)
                .build();

        writeField(dialog, "currentDirectory", tempDir);
        invokeRefresh(dialog);

        assertNull(readField(dialog, "errorMessage"));
        assertFalse(getOptions(dialog).isEmpty(), "Options should be restored after successful refresh");
    }

    @Test
    void shouldCreateDirectoryAndRefreshOptions() throws Exception {
        FileDialog dialog = createDialogBuilder()
                .withInitialDirectory(tempDir)
                .directoriesOnly(true)
                .build();

        boolean created = invokeCreateDirectory(dialog, "created-dir");

        assertTrue(created);
        assertTrue(Files.isDirectory(tempDir.resolve("created-dir")));
        assertNull(readField(dialog, "errorMessage"));
        assertTrue(getOptions(dialog).stream().anyMatch(option -> option.getLabel().endsWith("created-dir")));
    }

    @Test
    void shouldRejectBlankDirectoryName() throws Exception {
        FileDialog dialog = createDialogBuilder()
                .withInitialDirectory(tempDir)
                .build();

        boolean created = invokeCreateDirectory(dialog, "   ");

        assertFalse(created);
        assertEquals("Folder name cannot be empty", readField(dialog, "errorMessage"));
    }

    @Test
    void shouldStoreErrorWhenDirectoryCreationFails() throws Exception {
        Files.createDirectory(tempDir.resolve("existing"));

        FileDialog dialog = createDialogBuilder()
                .withInitialDirectory(tempDir)
                .build();

        boolean created = invokeCreateDirectory(dialog, "existing");

        assertFalse(created);
        assertEquals("Unable to create directory: existing", readField(dialog, "errorMessage"));
    }

    @Test
    void shouldListOnlyDirectoriesWhenConfigured() throws Exception {
        FileDialog dialog = createDialogBuilder()
                .withInitialDirectory(tempDir)
                .directoriesOnly(true)
                .build();

        List<DialogOption> options = getOptions(dialog);

        boolean hasParent = tempDir.getParent() != null;
        int expectedSize = 2 + (hasParent ? 1 : 0);
        
        assertEquals(expectedSize, options.size());
        
        boolean foundFile = options.stream().anyMatch(o -> o.getLabel().endsWith("file1.txt"));
        boolean foundDir = options.stream().anyMatch(o -> o.getLabel().endsWith("dir1"));
        boolean foundCurrentDir = options.stream().anyMatch(o -> o.getLabel().contains("(current directory)"));

        assertFalse(foundFile, "File should NOT be listed");
        assertTrue(foundDir, "Directory should be listed");
        assertTrue(foundCurrentDir, "Current directory option should be listed");
    }

    @Test
    void shouldExposeCurrentDirectorySelectionOptionInDirectoriesOnlyMode() throws Exception {
        FileDialog dialog = createDialogBuilder()
                .withInitialDirectory(tempDir)
                .directoriesOnly(true)
                .build();

        List<DialogOption> options = getOptions(dialog);

        FileOption currentDirectoryOption = options.stream()
                .map(option -> (FileOption) option)
                .filter(FileOption::isCurrentDirectoryLink)
                .findFirst()
                .orElseThrow();

        assertEquals(tempDir, currentDirectoryOption.getPath());
        assertEquals("./ (current directory)", currentDirectoryOption.getLabel());
    }

    @Test
    void shouldFilterFiles() throws Exception {
        FileDialog dialog = createDialogBuilder()
                .withInitialDirectory(tempDir)
                .withFileFilter(p -> p.toString().endsWith(".java"))
                .build();

        List<DialogOption> options = getOptions(dialog);

        boolean hasParent = tempDir.getParent() != null;
        int expectedSize = 2 + (hasParent ? 1 : 0);
        
        assertEquals(expectedSize, options.size());
        
        boolean foundTxt = options.stream().anyMatch(o -> o.getLabel().endsWith("file1.txt"));
        boolean foundJava = options.stream().anyMatch(o -> o.getLabel().endsWith("file2.java"));
        boolean foundDir = options.stream().anyMatch(o -> o.getLabel().endsWith("dir1"));

        assertFalse(foundTxt, "TXT file should be filtered out");
        assertTrue(foundJava, "Java file should be listed");
        assertTrue(foundDir, "Directory should be listed");
    }

    @Test
    void shouldFilterFilesByExtensionPreset() throws Exception {
        FileDialog dialog = createDialogBuilder()
                .withInitialDirectory(tempDir)
                .withExtensionPreset(FileDialog.ExtensionPreset.SOURCE_FILES)
                .build();

        List<DialogOption> options = getOptions(dialog);

        boolean foundTxt = options.stream().anyMatch(o -> o.getLabel().endsWith("file1.txt"));
        boolean foundJava = options.stream().anyMatch(o -> o.getLabel().endsWith("file2.java"));
        boolean foundDir = options.stream().anyMatch(o -> o.getLabel().endsWith("dir1"));

        assertFalse(foundTxt, "TXT file should be filtered out by source-files preset");
        assertTrue(foundJava, "Java file should be listed by source-files preset");
        assertTrue(foundDir, "Directory should still be listed");
        assertEquals("SOURCE_FILES", readField(dialog, "filterLabel"));
    }

    @Test
    void shouldCycleSelectableExtensionPresets() throws Exception {
        FileDialog dialog = createDialogBuilder()
                .withInitialDirectory(tempDir)
                .withSelectableExtensionPresets(
                        FileDialog.ExtensionPreset.SOURCE_FILES,
                        FileDialog.ExtensionPreset.DOCUMENTATION_FILES
                )
                .build();

        List<DialogOption> sourceOptions = getOptions(dialog);
        assertTrue(sourceOptions.stream().anyMatch(o -> o.getLabel().endsWith("file2.java")));
        assertFalse(sourceOptions.stream().anyMatch(o -> o.getLabel().endsWith("file3.md")));
        assertEquals("SOURCE_FILES (1/2)", readField(dialog, "filterLabel"));

        invokeCycleExtensionPreset(dialog);

        List<DialogOption> documentationOptions = getOptions(dialog);
        assertFalse(documentationOptions.stream().anyMatch(o -> o.getLabel().endsWith("file2.java")));
        assertTrue(documentationOptions.stream().anyMatch(o -> o.getLabel().endsWith("file1.txt")));
        assertTrue(documentationOptions.stream().anyMatch(o -> o.getLabel().endsWith("file3.md")));
        assertEquals("DOCUMENTATION_FILES (2/2)", readField(dialog, "filterLabel"));
    }

    private FileDialog.Builder createDialogBuilder() {
        TitleArea titleArea = new TitleArea.Builder().withTitle("Title").build();
        ContentArea contentArea = new ContentArea.Builder().withContent("Item").build();
        ContentArea selectedContentArea = new ContentArea.Builder().withContent("Selected").build();
        NavigationArea navigationArea = new NavigationArea.Builder()
                .withToolbar(NavigationToolbar.builder().build())
                .withTheme(DialogTheme.darkTheme())
                .build();

        return new FileDialog.Builder(
                titleArea,
                contentArea,
                selectedContentArea,
                navigationArea
        );
    }

    @SuppressWarnings("unchecked")
    private List<DialogOption> getOptions(FileDialog dialog) throws Exception {
        Field field = AbstractListDialog.class.getDeclaredField("options");
        field.setAccessible(true);
        return (List<DialogOption>) field.get(dialog);
    }

    private Object readField(Object target, @SuppressWarnings("SameParameterValue") String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private void writeField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private void invokeRefresh(FileDialog dialog) throws Exception {
        Method method = FileDialog.class.getDeclaredMethod("refreshDirectoryContent");
        method.setAccessible(true);
        method.invoke(dialog);
    }

    private boolean invokeCreateDirectory(FileDialog dialog, String directoryName) throws Exception {
        Method method = FileDialog.class.getDeclaredMethod("createDirectory", String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(dialog, directoryName);
    }

    private boolean invokeCycleExtensionPreset(FileDialog dialog) throws Exception {
        Method method = FileDialog.class.getDeclaredMethod("cycleExtensionPreset");
        method.setAccessible(true);
        boolean cycled = (boolean) method.invoke(dialog);
        if (cycled) {
            invokeRefresh(dialog);
        }
        return cycled;
    }

    @SuppressWarnings("unchecked")
    private List<String> invokePreviewLines(FileDialog dialog, int selectedIndex) throws Exception {
        Method method = FileDialog.class.getDeclaredMethod("previewLines", int.class);
        method.setAccessible(true);
        return (List<String>) method.invoke(dialog, selectedIndex);
    }

    private String invokeHumanReadableSize(FileDialog dialog, long sizeInBytes) throws Exception {
        Method method = FileDialog.class.getDeclaredMethod("humanReadableSize", long.class);
        method.setAccessible(true);
        return (String) method.invoke(dialog, sizeInBytes);
    }
}
