package com.cryptochecker;

import org.junit.jupiter.api.*;
import java.io.*;
import javax.swing.SwingUtilities;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MainTest {

    @BeforeAll
    public static void setupApp() throws Exception {
        if (Main.frame == null) {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    Main.main(new String[] {});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            Thread.sleep(3000); // Wait for initialization
        }
    }

    @Test
    @Order(1)
    @DisplayName("TC-01: GUI Initialization")
    public void testGUIInitialization() {
        assertNotNull(Main.frame, "Main frame should not be null");
        assertTrue(Main.frame.isVisible(), "Main frame should be visible");
        assertTrue(Main.frame.getWidth() > 100, "Frame width should be valid");
        assertTrue(Main.frame.getHeight() > 100, "Frame height should be valid");
    }

    @Test
    @Order(2)
    @DisplayName("TC-02: File System Initialization")
    public void testFileSystemInitialization() {
        String userHome = System.getProperty("user.home");
        File dataFolder = new File(userHome + "/.crypto-checker/");
        assertTrue(dataFolder.exists(), "Data folder should exist");
        assertTrue(dataFolder.isDirectory(), "Data folder should be a directory");
    }

    @Test
    @Order(3)
    @DisplayName("TC-62: Default Currency Check")
    public void testDefaultCurrency() {
        assertEquals("USD", Main.currency, "Default currency should be USD");
        assertEquals("$", Main.currencyChar, "Default currency char should be $");
    }
}
