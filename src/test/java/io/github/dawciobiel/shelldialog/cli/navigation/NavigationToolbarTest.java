package io.github.dawciobiel.shelldialog.cli.navigation;

import com.googlecode.lanterna.input.KeyType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NavigationToolbarTest {

    @Test
    void builderShouldSupportNewActionMethods() {
        NavigationToolbar toolbar = NavigationToolbar.builder()
                .withEnterOK()
                .withTabNextField()
                .withF2ToggleHiddenFiles()
                .withF7NewFolder()
                .withF5Refresh()
                .withHomeHomeDir()
                .withEndCWD()
                .build();

        List<NavigationItem> items = toolbar.getItems();
        assertEquals(7, items.size());

        assertEquals(NavigationLabels.ACTION_OK, items.getFirst().label());
        assertEquals(NavigationLabels.ACTION_NEXT_FIELD, items.get(1).label());
        assertEquals(NavigationLabels.ACTION_HIDDEN_FILES, items.get(2).label());
        assertEquals(NavigationLabels.ACTION_NEW_FOLDER, items.get(3).label());
        assertEquals(NavigationLabels.ACTION_REFRESH, items.get(4).label());
        assertEquals(NavigationLabels.ACTION_HOME, items.get(5).label());
        assertEquals(NavigationLabels.ACTION_CWD, items.get(6).label());
    }

    @Test
    void builderShouldSupportCustomKeys() {
        NavigationToolbar toolbar = NavigationToolbar.builder()
                .withKey(KeyType.F1, "Custom Help")
                .build();

        List<NavigationItem> items = toolbar.getItems();
        assertEquals(1, items.size());
        assertEquals("F1", items.getFirst().hotkey());
        assertEquals("Custom Help", items.getFirst().label());
    }
}
