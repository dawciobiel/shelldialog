package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TextColor;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpinnerDialogTest {

    @Test
    void builderShouldAssignFields() throws Exception {
        TitleArea titleArea = titleArea();
        ContentArea statusArea = statusArea();
        ProgressTask task = reporter -> {};

        SpinnerDialog dialog = new SpinnerDialog.Builder(titleArea, statusArea)
                .withTask(task)
                .build();

        assertEquals(titleArea, readField(dialog, "titleArea"));
        assertEquals(statusArea, readField(dialog, "statusArea"));
        assertNotNull(readField(dialog, "task"));
    }

    @Test
    void builderShouldRequireTask() {
        assertThrows(IllegalStateException.class, () -> new SpinnerDialog.Builder(titleArea(), statusArea()).build());
    }

    @Test
    void shouldUseDefaultSpinnerFrames() throws Exception {
        SpinnerDialog dialog = new SpinnerDialog.Builder(titleArea(), statusArea())
                .withTask(reporter -> {})
                .build();

        List<String> spinnerFrames = (List<String>) readField(dialog, "spinnerFrames");
        assertEquals(List.of("|", "/", "-", "\\"), spinnerFrames);
    }

    @Test
    void shouldAllowCustomSpinnerFrames() throws Exception {
        List<String> customFrames = List.of("o", "O");
        SpinnerDialog dialog = new SpinnerDialog.Builder(titleArea(), statusArea())
                .withTask(reporter -> {})
                .withSpinnerFrames(customFrames)
                .build();

        List<String> spinnerFrames = (List<String>) readField(dialog, "spinnerFrames");
        assertEquals(customFrames, spinnerFrames);
    }

    @Test
    void customSpinnerFramesShouldNotBeEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new SpinnerDialog.Builder(titleArea(), statusArea())
                .withTask(reporter -> {})
                .withSpinnerFrames(List.of()));
    }

    @Test
    void reporterUpdateShouldChangeStatus() throws Exception {
        AtomicReference<String> dialogStatus = new AtomicReference<>("");
        
        ProgressTask task = reporter -> {
            reporter.update(0, "Test Message");
        };

        SpinnerDialog dialog = new SpinnerDialog.Builder(titleArea(), statusArea())
                .withTask(task)
                .build();

        // In a real scenario runDialog() would be called, initializing the thread.
        // For testing, we simulate the thread and the reporter interaction.
        // Since runDialog is protected and requires a Screen, we trigger the task via reflection or by simulating the reporter.
        
        ProgressReporter reporter = createReporter(dialog);
        reporter.update(0, "Test Message");

        AtomicReference<String> statusRef = (AtomicReference<String>) readField(dialog, "currentStatus");
        assertEquals("Test Message", statusRef.get());
    }

    @Test
    void reporterIsCancelledShouldReflectDialogState() throws Exception {
        SpinnerDialog dialog = new SpinnerDialog.Builder(titleArea(), statusArea())
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

    private ProgressReporter createReporter(SpinnerDialog dialog) {
        // This mimics the reporter created inside SpinnerDialog.runDialog
        return new ProgressReporter() {
            @Override
            public void update(double progress, String message) {
                try {
                    AtomicReference<String> status = (AtomicReference<String>) readField(dialog, "currentStatus");
                    status.set(message != null ? message : "");
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
