package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TextColor;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageDialogTest {

    @Test
    void shouldBuildMessageDialogCorrectly() throws Exception {
        TitleArea titleArea = new TitleArea.Builder().withTitle("Info").build();
        ContentArea contentArea = new ContentArea.Builder().withContent("The operation finished.").build();
        NavigationArea navigationArea = new NavigationArea.Builder()
                .withToolbar(NavigationToolbar.builder().withEnterOK().build())
                .withTheme(DialogTheme.darkTheme())
                .build();

        MessageDialog dialog = new MessageDialog.Builder(titleArea, contentArea, navigationArea)
                .withBorder(true)
                .withBorderColor(TextColor.ANSI.BLUE)
                .build();

        assertNotNull(dialog);
        assertEquals(titleArea, readField(dialog, "titleArea"));
        assertEquals(contentArea, readField(dialog, "contentArea"));
        assertEquals(navigationArea, readField(dialog, "navigationArea"));
        assertTrue((boolean) readField(dialog, "borderVisible"));
    }

    private Object readField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }
}
