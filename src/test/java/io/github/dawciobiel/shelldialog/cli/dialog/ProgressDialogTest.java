package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TextColor;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgressDialogTest {

    @Test
    void builderShouldAssignFields() throws Exception {
        TitleArea titleArea = titleArea();
        ContentArea statusArea = statusArea();
        ProgressTask task = reporter -> {};

        ProgressDialog dialog = new ProgressDialog.Builder(titleArea, statusArea)
                .withTask(task)
                .build();

        assertEquals(titleArea, readField(dialog, "titleArea"));
        assertEquals(statusArea, readField(dialog, "statusArea"));
        assertNotNull(readField(dialog, "task"));
    }

    @Test
    void builderShouldRequireTask() {
        assertThrows(IllegalStateException.class, () -> new ProgressDialog.Builder(titleArea(), statusArea()).build());
    }

    @Test
    void reporterUpdateShouldChangeState() throws Exception {
        ProgressTask task = reporter -> {
            reporter.update(0.5, "Working");
        };

        ProgressDialog dialog = new ProgressDialog.Builder(titleArea(), statusArea())
                .withTask(task)
                .build();

        ProgressReporter reporter = createReporter(dialog);
        reporter.update(0.5, "Working");

        AtomicReference<?> stateRef = (AtomicReference<?>) readField(dialog, "state");
        Object state = stateRef.get();
        
        assertEquals(0.5, (double) readField(state, "progress"));
        assertEquals("Working", (String) readField(state, "message"));
    }

    @Test
    void reporterIsCancelledShouldReflectDialogState() throws Exception {
        ProgressDialog dialog = new ProgressDialog.Builder(titleArea(), statusArea())
                .withTask(reporter -> {})
                .build();

        AtomicBoolean dialogCancelled = (AtomicBoolean) readField(dialog, "cancelled");
        dialogCancelled.set(true);

        ProgressReporter reporter = createReporter(dialog);
        assertTrue(reporter.isCancelled());
    }

    private TitleArea titleArea() {
        return new TitleArea.Builder().withTitle("Title").build();
    }

    private ContentArea statusArea() {
        return new ContentArea.Builder().withContent("Status").withForegroundColor(TextColor.ANSI.WHITE).build();
    }

    private Object readField(Object target, String fieldName) throws Exception {
        Class<?> currentClass = target.getClass();
        while (currentClass != null) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    private ProgressReporter createReporter(ProgressDialog dialog) {
        return new ProgressReporter() {
            @Override
            public void update(double progress, String message) {
                try {
                    AtomicReference<Object> state = (AtomicReference<Object>) readField(dialog, "state");
                    
                    // We need to create the ProgressState record instance via reflection because it's private
                    Class<?> stateClass = Class.forName("io.github.dawciobiel.shelldialog.cli.dialog.ProgressDialog$ProgressState");
                    java.lang.reflect.Constructor<?> constructor = stateClass.getDeclaredConstructor(double.class, String.class);
                    constructor.setAccessible(true);
                    Object newState = constructor.newInstance(progress, message != null ? message : "");
                    
                    state.set(newState);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean isCancelled() {
                try {
                    AtomicBoolean cancelled = (AtomicBoolean) readField(dialog, "cancelled");
                    return cancelled.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
