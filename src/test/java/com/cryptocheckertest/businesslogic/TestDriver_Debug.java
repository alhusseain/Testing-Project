package com.cryptocheckertest.businesslogic;


import com.cryptochecker.Debug;
import com.cryptochecker.Main;

import java.io.BufferedReader;
import java.io.FileReader;


/**
 * BLACK BOX TEST SUITE: DEBUG MODULE
 * -------------------------------------------------
 * Target: Debug.java
 * Type: Functional / IO Testing
 * Access: External (Public API only)
 * -------------------------------------------------
 */
public class TestDriver_Debug {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   STARTING BLACK BOX TEST: DEBUG MODULE  ");
        System.out.println("==========================================\n");

        // SETUP: Launch Main to ensure 'Main.logLocation' or folder paths are set
        Thread appThread = new Thread(() -> {
            try { Main.main(new String[]{}); } catch (Exception e) {}
        });
        appThread.start();

        try { Thread.sleep(1500); } catch (InterruptedException e) {}

        // --- EXECUTE TEST CASES ---
        test_DebugModeToggle();
        test_LogPersistence();

        System.out.println("\n==========================================");
        System.out.println("   DEBUG MODULE TESTING FINISHED");
        System.out.println("==========================================");
        System.exit(0);
    }

    /**
     * TC-DBG-01: Verify Debug Mode Toggle
     * Requirement: FR-053 (Existing)
     */
    public static void test_DebugModeToggle() {
        System.out.print("[TC-DBG-01]  Checking Debug Mode Toggle...... ");

        // Set to True
        Debug.mode = true;
        if (Debug.mode == true) {
            // Set to False
            Debug.mode = false;
            if (Debug.mode == false) {
                System.out.println("PASS");
                return;
            }
        }
        System.out.println("FAIL (Variable did not update)");
    }

    /**
     * TC-DBG-02: Verify Log File Writing (Persistence)
     * Requirement: FR-BB-02
     */
    public static void test_LogPersistence() {
        System.out.print("[TC-DBG-02]  Checking Log File Persistence... ");

        // 1. Generate a unique key to search for
        String uniqueTestKey = "TEST_ID_" + System.currentTimeMillis();

        // 2. INPUT: Send data to the Black Box (Debug Class)
        try {
            Debug.log("AUTOMATED TEST: " + uniqueTestKey);
        } catch (Exception e) {
            System.out.println("FAIL (Exception during logging: " + e.getMessage() + ")");
            return;
        }

        // 3. VERIFY: Open the file externally and search for the key
        // Note: Reconstructing path based on your Main.java logic
        String logPath = System.getProperty("user.home") + "/.crypto-checker/log.txt";
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(logPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(uniqueTestKey)) {
                    found = true;
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("FAIL (Could not read log file at " + logPath + ")");
            return;
        }

        if (found) {
            System.out.println("PASS");
        } else {
            System.out.println("FAIL (Unique key '" + uniqueTestKey + "' not found in log)");
        }
    }
}
