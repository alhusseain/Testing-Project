package com.cryptocheckertest.businesslogic;

import com.cryptochecker.Debug;
import com.cryptochecker.Main;

import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * MASTER RTM TEST SUITE: DEBUG MODULE
 * -------------------------------------------------
 * Covers: TC-58, TC-59, TC-60, TC-61 (Functional)
 * Covers: TC-64, TC-65 (Structural)
 * -------------------------------------------------
 */
public class TestDriver_Debug {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   STARTING DEBUG MODULE TEST SUITE       ");
        System.out.println("==========================================\n");

        // SETUP: Launch Main
        Thread appThread = new Thread(() -> {
            try { Main.main(new String[]{}); } catch (Exception e) {}
        });
        appThread.start();

        // Wait for initialization
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        // --- SECTION 1: FUNCTIONAL TESTS ---
        TC_58_Event_Logging();
        TC_59_Debug_Window_UI();
        TC_60_Auto_Scroll_Stability();
        TC_61_Window_Sync(); // <--- NEW TEST

        // --- SECTION 2: STRUCTURAL TESTS ---
        TC_64_Debug_Mode_Toggle();
        TC_65_Log_Append_Logic();

        System.out.println("\n==========================================");
        System.out.println("   DEBUG MODULE TESTING FINISHED");
        System.out.println("==========================================");
        System.exit(0);
    }

    // ... (Keep TC-58, TC-59, TC-60 same as before) ...

    public static void TC_58_Event_Logging() {
        System.out.print("[TC-58] Event Logging Timestamp..... ");
        String msg = "TC58_EVENT";
        Debug.log(msg);
        File logFile = new File(System.getProperty("user.home") + "/.crypto-checker/log.txt");
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(msg) && line.contains(":")) found = true;
            }
        } catch (Exception e) {}
        if (found) System.out.println("PASS");
        else System.out.println("FAIL");
    }

    public static void TC_59_Debug_Window_UI() {
        System.out.print("[TC-59] Debug Window Visibility..... ");
        Debug.setDebugMode(true);
        if (Debug.frame != null && Debug.frame.isVisible()) System.out.println("PASS");
        else System.out.println("FAIL");
        Debug.setDebugMode(false);
    }

    public static void TC_60_Auto_Scroll_Stability() {
        System.out.print("[TC-60] Auto-scroll Stability....... ");
        try {
            Debug.setDebugMode(true);
            for (int i = 0; i < 20; i++) Debug.log("Scroll Line " + i);
            System.out.println("PASS");
        } catch (Exception e) {
            System.out.println("FAIL");
        }
        Debug.setDebugMode(false);
    }

    /**
     * TC-61: Window Sync
     * Simulates clicking the "X" button and checks if 'mode' becomes false.
     */
    public static void TC_61_Window_Sync() {
        System.out.print("[TC-61] Window Close Sync (UI)...... ");

        // 1. Open Window
        Debug.setDebugMode(true);

        if (Debug.frame != null) {
            // 2. Simulate the OS "Close Window" event
            Debug.frame.dispatchEvent(new WindowEvent(Debug.frame, WindowEvent.WINDOW_CLOSING));

            // Give the event loop a moment to process
            try { Thread.sleep(500); } catch (Exception e) {}

            // 3. Verify the mode variable automatically flipped to false
            if (Debug.getDebugMode() == false) {
                System.out.println("PASS");
            } else {
                System.out.println("FAIL (Mode remained TRUE after closing window)");
            }
        } else {
            System.out.println("SKIP (Frame not created)");
        }
    }

    // ... (Keep TC-64 and TC-65 same as before) ...

    public static void TC_64_Debug_Mode_Toggle() {
        System.out.print("[TC-64] Debug Logic Toggle.......... ");
        Debug.setDebugMode(true);
        if (Debug.getDebugMode()) {
            Debug.setDebugMode(false);
            if (!Debug.getDebugMode()) System.out.println("PASS");
            else System.out.println("FAIL");
        } else System.out.println("FAIL");
    }

    public static void TC_65_Log_Append_Logic() {
        System.out.print("[TC-65] Log Persistence (Append).... ");
        String uniqueID = "TC65_" + System.currentTimeMillis();
        Debug.log(uniqueID);
        File logFile = new File(System.getProperty("user.home") + "/.crypto-checker/log.txt");
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(uniqueID)) found = true;
            }
        } catch (Exception e) {}
        if (found) System.out.println("PASS");
        else System.out.println("FAIL");
    }
}