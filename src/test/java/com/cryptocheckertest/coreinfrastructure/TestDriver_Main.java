package com.cryptocheckertest.coreinfrastructure;

import com.cryptochecker.Main;

import java.awt.Color;
import java.io.File;


/**
 * BLACK BOX TEST SUITE: MAIN MODULE
 * -------------------------------------------------
 * Target: Main.java
 * Type: Functional / System Testing
 * Access: External (Public API only)
 * -------------------------------------------------
 */
public class TestDriver_Main {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   STARTING BLACK BOX TEST: MAIN MODULE   ");
        System.out.println("==========================================\n");

        // SETUP: Launch the Application in a separate thread to initialize variables
        // We do this because Main.main() triggers the GUI and might block execution.
        Thread appThread = new Thread(() -> {
            try {
                Main.main(new String[]{});
            } catch (Exception e) {
                // Ignore GUI exceptions during headless testing
            }
        });
        appThread.start();

        // Wait 2 seconds for the App to fully initialize (Folder creation, Theme init)
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        // --- EXECUTE TEST CASES ---
        test_FileSystemInitialization();
        test_DefaultCurrency();
        test_ThemeSwitchingLogic();

        System.out.println("\n==========================================");
        System.out.println("   MAIN MODULE TESTING FINISHED");
        System.out.println("==========================================");

        // TEARDOWN: Force close the Swing Application
        System.exit(0);
    }

    /**
     * TC-MAIN-01: Verify Data Folder Creation
     * Requirement: FR-BB-01
     */
    public static void test_FileSystemInitialization() {
        System.out.print("[TC-MAIN-01] Checking Data Folder Creation... ");

        String userHome = System.getProperty("user.home");
        File dataFolder = new File(userHome + "/.crypto-checker/");

        if (dataFolder.exists() && dataFolder.isDirectory()) {
            System.out.println("PASS");
        } else {
            System.out.println("FAIL (Folder not found at " + dataFolder.getAbsolutePath() + ")");
        }
    }

    /**
     * TC-MAIN-02: Verify Default Currency Settings
     * Requirement: FR-BB-04
     */
    public static void test_DefaultCurrency() {
        System.out.print("[TC-MAIN-02] Checking Default Currency....... ");

        // Accessing public static variables from Main
        boolean isUSD = "USD".equals(Main.currency);
        boolean isSign = "$".equals(Main.currencyChar);

        if (isUSD && isSign) {
            System.out.println("PASS");
        } else {
            System.out.println("FAIL (Expected USD/$, Got " + Main.currency + "/" + Main.currencyChar + ")");
        }
    }

    /**
     * TC-MAIN-03: Verify Visual Theme Logic
     * Requirement: FR-BB-03
     */
    public static void test_ThemeSwitchingLogic() {
        System.out.print("[TC-MAIN-03] Checking Theme Logic............ ");

        if (Main.theme == null) {
            System.out.println("SKIPPED (Main.theme not initialized)");
            return;
        }

        // ACTION: Switch to Dark Mode via public interface
        Main.theme.change(Main.themes.DARK);

        // VERIFY: Check if the public color variable 'emptyBackground' updated correctly
        Color expectedDark = new Color(78, 78, 78); // Value taken from your source code logic
        Color actualColor = Main.theme.emptyBackground;

        if (expectedDark.equals(actualColor)) {
            System.out.println("PASS");
        } else {
            System.out.println("FAIL (Color mismatch. Expected RGB 78,78,78)");
        }
    }
}