package com.cryptocheckertest.businesslogic.integration;

import com.cryptochecker.Main;
import org.junit.jupiter.api.*;
import java.io.*;
import java.lang.reflect.Method;
import javax.swing.SwingUtilities;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDriver_PanelPortfolio {

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
            Thread.sleep(3000);
        }
    }

    @Test
    @Order(1)
    @DisplayName("TC-04: Portfolio Save (Log Check) (Integration)")
    public void testPortfolioLogVerification() {
        if (Main.gui.panelPortfolio == null) {
            return;
        }

        try {
            Method serializeMethod = Main.gui.panelPortfolio.getClass().getDeclaredMethod("serializePortfolio");
            serializeMethod.setAccessible(true);
            serializeMethod.invoke(Main.gui.panelPortfolio);

            File logFile = new File(Main.logLocation);
            boolean found = false;

            if (logFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.contains("Serialized Portfolio To")) {
                            found = true;
                            break;
                        }
                    }
                }
            }
            assertTrue(found, "Portfolio serialization should be logged by Main system");

        } catch (Exception e) {
            fail("Integration failure during portfolio test: " + e.getMessage());
        }
    }
}
