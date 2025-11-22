package com.cryptocheckertest.coreinfrastructure;

import com.cryptochecker.Main;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;

/**
 * MASTER TEST SUITE: MAIN & PORTFOLIO
 * -------------------------------------------------
 * Target: Main.java, PanelPortfolio.java
 * Type: Hybrid (Functional + White Box via Reflection)
 * -------------------------------------------------
 */
public class TestDriver_Main {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   STARTING MASTER TEST SUITE             ");
        System.out.println("==========================================\n");

        // 1. SETUP: Launch the App in background
        Thread appThread = new Thread(() -> {
            try { Main.main(new String[]{}); } catch (Exception e) {}
        });
        appThread.start();

        // Wait for initialization
        try { Thread.sleep(3000); } catch (InterruptedException e) {}

        // --- RUN ALL TESTS ---

        test_GUI_Initialization();
        test_FileSystemInitialization();
        test_DefaultCurrency();
        test_ThemeSwitchingLogic();
        test_CurrencyResetLogic();

        // This is the fixed test function
        test_Portfolio_Log_Verification();

        System.out.println("\n==========================================");
        System.out.println("   ALL TESTING FINISHED");
        System.out.println("==========================================");
        System.exit(0);
    }

    // ---------------------------------------------------------
    // GROUP 1: MAIN SYSTEM TESTS
    // ---------------------------------------------------------

    public static void test_GUI_Initialization() {
        System.out.print("[TC-01]      GUI Initialization.......... ");
        if (Main.frame != null && Main.frame.isVisible()) {
            if (Main.frame.getWidth() > 100 && Main.frame.getHeight() > 100) {
                System.out.println("PASS");
            } else {
                System.out.println("FAIL (Window too small)");
            }
        } else {
            System.out.println("FAIL (Frame not visible)");
        }
    }

    public static void test_FileSystemInitialization() {
        System.out.print("[TC-02]      Data Folder Creation........ ");
        String userHome = System.getProperty("user.home");
        File dataFolder = new File(userHome + "/.crypto-checker/");
        if (dataFolder.exists() && dataFolder.isDirectory()) {
            System.out.println("PASS");
        } else {
            System.out.println("FAIL (Folder missing)");
        }
    }

    public static void test_DefaultCurrency() {
        System.out.print("[TC-62] Default Currency Check...... ");
        if ("USD".equals(Main.currency) && "$".equals(Main.currencyChar)) {
            System.out.println("PASS");
        } else {
            System.out.println("FAIL (Expected USD/$, Got " + Main.currency + ")");
        }
    }

    public static void test_ThemeSwitchingLogic() {
        System.out.print("[TC-63] Theme RGB Verification...... ");
        if (Main.theme == null) { System.out.println("SKIP"); return; }

        Main.theme.change(Main.themes.DARK);
        Color actual = Main.getInternalThemeColor(); // Uses Hook from Main.java
        Color expected = new Color(78, 78, 78);

        if (expected.equals(actual)) System.out.println("PASS");
        else System.out.println("FAIL");
    }

    public static void test_CurrencyResetLogic() {
        System.out.print("[TC-MAIN-04] Factory Reset Logic......... ");
        Main.currency = "EUR";
        Main.resetConfiguration(); // Uses Hook from Main.java
        if ("USD".equals(Main.currency)) System.out.println("PASS");
        else System.out.println("FAIL");
    }

    // ---------------------------------------------------------
    // GROUP 2: PORTFOLIO TESTS (FIXED)
    // ---------------------------------------------------------

    public static void test_Portfolio_Log_Verification() {
        System.out.print("[TC-04]      Portfolio Save (Log Check).. ");

        try {
            if (Main.gui.panelPortfolio == null) {
                System.out.println("SKIP (PanelPortfolio not loaded)");
                return;
            }

            // 1. FIX: Target 'panelPortfolio' with method 'serializePortfolio'
            // This matches line 350 in your PanelPortfolio.java file
            Method serializeMethod = Main.gui.panelPortfolio.getClass().getDeclaredMethod("serializePortfolio");
            serializeMethod.setAccessible(true);
            serializeMethod.invoke(Main.gui.panelPortfolio);

            // 2. Read the actual log file from disk
            File logFile = new File(System.getProperty("user.home") + "/.crypto-checker/log.txt");
            boolean found = false;

            if (logFile.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(logFile));
                String line;
                while ((line = br.readLine()) != null) {
                    // This string matches line 357 in PanelPortfolio.java
                    if (line.contains("Serialized Portfolio To")) {
                        found = true;
                    }
                }
                br.close();
            }

            if (found) {
                System.out.println("PASS");
            } else {
                System.out.println("FAIL (Method ran, but 'Serialized Portfolio To' not found in log)");
            }

        } catch (NoSuchMethodException nsme) {
            System.out.println("FAIL (Method 'serializePortfolio' not found in PanelPortfolio)");
        } catch (Exception e) {
            System.out.println("FAIL (Error: " + e.getMessage() + ")");
        }
    }
}