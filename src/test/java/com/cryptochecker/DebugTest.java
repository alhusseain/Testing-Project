package com.cryptochecker;

import org.junit.jupiter.api.*;
import java.io.*;
import java.awt.event.WindowEvent;
import javax.swing.SwingUtilities;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DebugTest {

    @BeforeAll
    public static void setupApp() throws Exception {
        // Initialize the app in the background if not already running
        if (Main.frame == null) {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    Main.main(new String[] {});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            // Give it a moment to settle
            Thread.sleep(2000);
        }
    }

    @Test
    @Order(1)
    @DisplayName("TC-58: Event Logging Timestamp")
    public void testEventLogging() {
        String msg = "TC58_EVENT_" + System.currentTimeMillis();
        Debug.log(msg);

        File logFile = new File(Main.logLocation);
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(msg) && line.contains(":")) {
                    found = true;
                    break;
                }
            }
        } catch (IOException e) {
            fail("Failed to read log file: " + e.getMessage());
        }

        assertTrue(found, "Log message with timestamp should be found in log file");
    }

    @Test
    @Order(2)
    @DisplayName("TC-59: Debug Window Visibility")
    public void testDebugWindowVisibility() throws Exception {
        SwingUtilities.invokeAndWait(() -> Debug.setDebugMode(true));

        assertTrue(Debug.frame != null && Debug.frame.isVisible(), "Debug frame should be visible when mode is true");

        SwingUtilities.invokeAndWait(() -> Debug.setDebugMode(false));
    }

    @Test
    @Order(3)
    @DisplayName("TC-60: Auto-scroll Stability")
    public void testAutoScrollStability() {
        assertDoesNotThrow(() -> {
            SwingUtilities.invokeAndWait(() -> Debug.setDebugMode(true));
            for (int i = 0; i < 20; i++) {
                Debug.log("Scroll Line " + i);
            }
            SwingUtilities.invokeAndWait(() -> Debug.setDebugMode(false));
        }, "Logging multiple lines should not throw exception");
    }

    @Test
    @Order(4)
    @DisplayName("TC-64: Debug Mode Toggle Logic")
    public void testDebugModeToggle() throws Exception {
        SwingUtilities.invokeAndWait(() -> Debug.setDebugMode(true));
        assertTrue(Debug.getDebugMode(), "Debug mode should be true");

        SwingUtilities.invokeAndWait(() -> Debug.setDebugMode(false));
        assertFalse(Debug.getDebugMode(), "Debug mode should be false");
    }

    @Test
    @Order(5)
    @DisplayName("TC-65: Log Persistence (Append)")
    public void testLogPersistence() {
        String uniqueID = "TC65_" + System.currentTimeMillis();
        Debug.log(uniqueID);

        File logFile = new File(Main.logLocation);
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(uniqueID)) {
                    found = true;
                    break;
                }
            }
        } catch (IOException e) {
            fail("Failed to read log file");
        }
        assertTrue(found, "New log entry should be appended to the file");
    }

    @Test
    @Order(6)
    @DisplayName("TC-61: Window Sync (UI)")
    public void testWindowSync() throws Exception {
        SwingUtilities.invokeAndWait(() -> Debug.setDebugMode(true));

        if (Debug.frame != null) {
            SwingUtilities.invokeAndWait(
                    () -> Debug.frame.dispatchEvent(new WindowEvent(Debug.frame, WindowEvent.WINDOW_CLOSING)));

            // Give event loop time to process
            Thread.sleep(500);

            assertFalse(Debug.getDebugMode(), "Debug mode should be false after closing the window");
        } else {
            fail("Debug frame was null");
        }
    }
}
