package com.cryptocheckertest.businesslogic.integration;

import com.cryptochecker.Main;
import org.junit.jupiter.api.*;
import java.awt.Color;
import javax.swing.SwingUtilities;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Test_Settings_Main {

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
    @DisplayName("TC-63: Theme RGB Verification (Integration)")
    public void testThemeSwitchingLogic() {
        if (Main.theme == null)
            return;

        Main.theme.change(Main.themes.DARK);
        Color actual = Main.getInternalThemeColor();
        Color expected = new Color(78, 78, 78);

        assertEquals(expected, actual, "Theme color should integrate with Main UI");

        Main.theme.change(Main.themes.LIGHT);
    }

    @Test
    @Order(2)
    @DisplayName("TC-MAIN-04: Factory Reset Logic (Integration)")
    public void testCurrencyResetLogic() {
        Main.currency = "EUR";
        Main.resetConfiguration();
        assertEquals("USD", Main.currency, "Reset logic should restore default currency");
    }
}
