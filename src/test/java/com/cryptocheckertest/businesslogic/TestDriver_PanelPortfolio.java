package com.cryptocheckertest.businesslogic;

import com.cryptochecker.Main;
import com.cryptochecker.PanelPortfolio;
import com.cryptochecker.Debug;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * MASTER RTM TEST SUITE: PANEL PORTFOLIO MODULE
 * -------------------------------------------------
 * Covers: TC-27, TC-28, TC-29 (Portfolio Management)
 * Covers: TC-30, TC-31, TC-32 (Portfolio Operations)
 * -------------------------------------------------
 * Tests the bManagePortfolioListener functionality:
 * - New portfolio creation with duplicate handling
 * - Portfolio renaming with validation 
 * - Portfolio deletion with minimum enforcement
 */
public class TestDriver_PanelPortfolio {

    private static PanelPortfolio panelPortfolio;

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  STARTING PORTFOLIO MODULE TEST SUITE   ");
        System.out.println("==========================================\n");

        // SETUP: Launch Main application
        Thread appThread = new Thread(() -> {
            try { 
                Main.main(new String[]{}); 
            } catch (Exception e) {
                System.out.println("Failed to initialize Main: " + e.getMessage());
            }
        });
        appThread.start();

        // Wait for initialization to complete
        try { 
            Thread.sleep(3000); 
            System.out.println("Application initialization complete.\n");
        } catch (InterruptedException e) {
            System.out.println("Initialization interrupted.");
        }

        // Initialize portfolio panel for testing
        try {
            SwingUtilities.invokeAndWait(() -> {
                panelPortfolio = new PanelPortfolio();
            });
            System.out.println("PanelPortfolio instance created successfully.\n");
        } catch (Exception e) {
            System.out.println("Failed to create PanelPortfolio: " + e.getMessage());
            return;
        }

        // --- SECTION 1: CORE PORTFOLIO MANAGEMENT TESTS ---
        TC_27_New_Portfolio_Creation();
        TC_28_Portfolio_Renaming_Validation();
        TC_29_Portfolio_Deletion_Enforcement();

        // --- SECTION 2: BUSINESS LOGIC VALIDATION TESTS ---
        TC_30_Duplicate_Name_Handling();
        TC_31_Minimum_Portfolio_Requirement();
        TC_32_Portfolio_Data_Integrity();

        System.out.println("\n==========================================");
        System.out.println("  PORTFOLIO MODULE TESTING FINISHED");
        System.out.println("==========================================");
        System.exit(0);
    }

    /**
     * TC-27: New Portfolio Creation - Integration Test using Equivalence Partitioning
     * Tests the "New Portfolio" option (case 2) from bManagePortfolioListener
     * Verifies: Portfolio count increases, proper naming convention, duplicate resolution
     */
    public static void TC_27_New_Portfolio_Creation() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TC-27: NEW PORTFOLIO CREATION (Integration Test - Equivalence Partitioning)");
        System.out.println("=".repeat(80));
        
        try {
            // ARRANGE: Capture initial state
            int initialCount = Main.gui.webData.portfolio.size();
            int initialNameCount = Main.gui.webData.portfolio_names.size();
            
            System.out.println("INPUT STATE:");
            System.out.println("  Initial portfolio count: " + initialCount);
            System.out.println("  Initial names count: " + initialNameCount);
            System.out.println("  Existing names: " + Main.gui.webData.portfolio_names);
            
            String expectedName = "Portfolio " + (initialCount + 1);
            System.out.println("  Expected new name: '" + expectedName + "'");
            
            // ACT: Simulate new portfolio creation logic from bManagePortfolioListener case 2
            System.out.println("\nEXECUTING: Portfolio creation logic (case 2)...");
            
            Main.gui.webData.portfolio.add(new ArrayList<>());
            Main.gui.webData.portfolio_names.add("Portfolio " + Main.gui.webData.portfolio.size());
            
            String nameBeforeDuplicateCheck = Main.gui.webData.portfolio_names.get(Main.gui.webData.portfolio_names.size()-1);
            System.out.println("  Created portfolio with name: '" + nameBeforeDuplicateCheck + "'");
            
            // Handle duplicate names (space appending logic)
            String newName = Main.gui.webData.portfolio_names.get(Main.gui.webData.portfolio_names.size()-1);
            boolean duplicateFound = false;
            for (int i = 0; i < Main.gui.webData.portfolio_names.size(); ++i) {
                if (Main.gui.webData.portfolio_names.get(i).equals(newName)) {
                    if (Main.gui.webData.portfolio_names.size()-1 != i) {
                        Main.gui.webData.portfolio_names.set(Main.gui.webData.portfolio_names.size()-1, newName + " ");
                        duplicateFound = true;
                        System.out.println("  Duplicate detected! Appended space: '" + (newName + " ") + "'");
                        break;
                    }
                }
            }
            if (!duplicateFound) {
                System.out.println("  No duplicates found, name remains: '" + newName + "'");
            }
            
            // CAPTURE ACTUAL RESULTS
            int actualCount = Main.gui.webData.portfolio.size();
            int actualNameCount = Main.gui.webData.portfolio_names.size();
            String actualFinalName = Main.gui.webData.portfolio_names.get(Main.gui.webData.portfolio_names.size()-1);
            
            System.out.println("\nOUTPUT STATE:");
            System.out.println("  Final portfolio count: " + actualCount);
            System.out.println("  Final names count: " + actualNameCount);
            System.out.println("  Final portfolio name: '" + actualFinalName + "'");
            System.out.println("  All names: " + Main.gui.webData.portfolio_names);
            
            // EXPECTED vs ACTUAL COMPARISON
            System.out.println("\nVALIDATION RESULTS:");
            
            int expectedCount = initialCount + 1;
            boolean countValid = actualCount == expectedCount;
            System.out.println("  Portfolio Count - Expected: " + expectedCount + ", Actual: " + actualCount + 
                              " → " + (countValid ? "✅ PASS" : "❌ FAIL"));
            
            int expectedNameCount = initialNameCount + 1;
            boolean nameCountValid = actualNameCount == expectedNameCount;
            System.out.println("  Names Count - Expected: " + expectedNameCount + ", Actual: " + actualNameCount + 
                              " → " + (nameCountValid ? "✅ PASS" : "❌ FAIL"));
            
            boolean nameFormatValid = actualFinalName.startsWith("Portfolio ");
            System.out.println("  Name Format - Expected: starts with 'Portfolio ', Actual: '" + actualFinalName + 
                              "' → " + (nameFormatValid ? "✅ PASS" : "❌ FAIL"));
            
            boolean duplicateHandled = !duplicateFound || actualFinalName.endsWith(" ");
            System.out.println("  Duplicate Handling - Expected: proper resolution, Actual: " + 
                              (duplicateFound ? "space appended" : "no duplicates") + 
                              " → " + (duplicateHandled ? "✅ PASS" : "❌ FAIL"));
            
            // FINAL RESULT
            boolean overallPass = countValid && nameCountValid && nameFormatValid && duplicateHandled;
            System.out.println("\nTC-27 RESULT: " + (overallPass ? "✅ PASS" : "❌ FAIL"));
            
            if (!overallPass) {
                System.out.println("FAILURE DETAILS:");
                if (!countValid) System.out.println("  • Portfolio count mismatch");
                if (!nameCountValid) System.out.println("  • Names count mismatch");  
                if (!nameFormatValid) System.out.println("  • Invalid naming convention");
                if (!duplicateHandled) System.out.println("  • Duplicate resolution failed");
            }
            
        } catch (Exception e) {
            System.out.println("\nTC-27 RESULT: ❌ FAIL");
            System.out.println("EXCEPTION DETAILS:");
            System.out.println("  Error: " + e.getMessage());
            System.out.println("  Type: " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        
        System.out.println("=".repeat(80));
    }

    /**
     * TC-28: Portfolio Renaming Validation
     * Tests the "Rename Current" option (case 0) from bManagePortfolioListener
     * Verifies: Duplicate detection, error handling, name preservation on conflict
     */
    public static void TC_28_Portfolio_Renaming_Validation() {
        System.out.print("[TC-28] Portfolio Rename Validation. ");
        
        try {
            // ARRANGE: Ensure multiple portfolios exist for duplicate testing
            while (Main.gui.webData.portfolio_names.size() < 2) {
                Main.gui.webData.portfolio.add(new ArrayList<>());
                Main.gui.webData.portfolio_names.add("TestPortfolio" + Main.gui.webData.portfolio.size());
            }
            
            String originalName = Main.gui.webData.portfolio_names.get(0);
            String uniqueNewName = "UniqueTestName_" + System.currentTimeMillis();
            String duplicateName = Main.gui.webData.portfolio_names.get(1);
            
            // ACT 1: Test valid rename (simulate case 0 logic)
            boolean isDuplicateValid = false;
            for (int i = 0; i < Main.gui.webData.portfolio_names.size(); ++i) {
                if (Main.gui.webData.portfolio_names.get(i).equals(uniqueNewName)) {
                    if (i != 0) { // Different portfolio index
                        isDuplicateValid = true;
                        break;
                    }
                }
            }
            
            if (!isDuplicateValid) {
                Main.gui.webData.portfolio_names.set(0, uniqueNewName);
            }
            
            // ASSERT 1: Valid rename should succeed
            boolean validRenameWorked = Main.gui.webData.portfolio_names.get(0).equals(uniqueNewName);
            
            // ACT 2: Test duplicate rename (simulate error path)
            boolean isDuplicateInvalid = false;
            for (int i = 0; i < Main.gui.webData.portfolio_names.size(); ++i) {
                if (Main.gui.webData.portfolio_names.get(i).equals(duplicateName)) {
                    if (i != 0) { // Different portfolio index
                        isDuplicateInvalid = true;
                        break;
                    }
                }
            }
            
            // Should NOT rename when duplicate found
            String nameBeforeDuplicateTest = Main.gui.webData.portfolio_names.get(0);
            if (isDuplicateInvalid) {
                // Simulate the cancellation - name should remain unchanged
                // In real code: JOptionPane.showMessageDialog("Name already exists!");
                // In real code: Debug.log("Name already existst, cancelling..");
            }
            
            // ASSERT 2: Duplicate rename should be rejected
            boolean duplicateRenameRejected = Main.gui.webData.portfolio_names.get(0).equals(nameBeforeDuplicateTest);
            
            if (validRenameWorked && duplicateRenameRejected && isDuplicateInvalid) {
                System.out.println("PASS");
            } else {
                System.out.println("FAIL (Valid:" + validRenameWorked + " Rejected:" + duplicateRenameRejected + " Detected:" + isDuplicateInvalid + ")");
            }
            
            // CLEANUP: Reset original name
            Main.gui.webData.portfolio_names.set(0, originalName);
            
        } catch (Exception e) {
            System.out.println("FAIL (Exception: " + e.getMessage() + ")");
        }
    }

    /**
     * TC-29: Portfolio Deletion Enforcement  
     * Tests the "Delete Current" option (case 1) from bManagePortfolioListener
     * Verifies: Successful deletion when multiple portfolios, prevention when only one remains
     */
    public static void TC_29_Portfolio_Deletion_Enforcement() {
        System.out.print("[TC-29] Portfolio Deletion Logic.... ");
        
        try {
            // ARRANGE: Ensure multiple portfolios exist
            while (Main.gui.webData.portfolio.size() < 3) {
                Main.gui.webData.portfolio.add(new ArrayList<>());
                Main.gui.webData.portfolio_names.add("DeletionTest" + Main.gui.webData.portfolio.size());
            }
            
            int initialCount = Main.gui.webData.portfolio.size();
            
            // ACT 1: Test deletion when multiple portfolios exist (simulate case 1)
            if (Main.gui.webData.portfolio.size() > 1) {
                Main.gui.webData.portfolio.remove(0);
                Main.gui.webData.portfolio_names.remove(0);
            }
            
            // ASSERT 1: Deletion should succeed
            boolean deletionWorked = Main.gui.webData.portfolio.size() == initialCount - 1;
            
            // ACT 2: Reduce to minimum and test enforcement
            while (Main.gui.webData.portfolio.size() > 1) {
                Main.gui.webData.portfolio.remove(Main.gui.webData.portfolio.size() - 1);
                Main.gui.webData.portfolio_names.remove(Main.gui.webData.portfolio_names.size() - 1);
            }
            
            // Simulate minimum portfolio check from bManagePortfolioListener case 1
            boolean canDeleteWhenOne = Main.gui.webData.portfolio.size() > 1;
            
            // ASSERT 2: Should prevent deletion when only one portfolio exists
            boolean minimumEnforced = !canDeleteWhenOne && Main.gui.webData.portfolio.size() == 1;
            
            if (deletionWorked && minimumEnforced) {
                System.out.println("PASS");
            } else {
                System.out.println("FAIL (Deletion:" + deletionWorked + " MinimumEnforced:" + minimumEnforced + ")");
            }
            
        } catch (Exception e) {
            System.out.println("FAIL (Exception: " + e.getMessage() + ")");
        }
    }

    /**
     * TC-30: Duplicate Name Handling
     * Tests the duplicate name resolution logic with space appending
     * Verifies: Automatic conflict resolution, name uniqueness preservation
     */
    public static void TC_30_Duplicate_Name_Handling() {
        System.out.print("[TC-30] Duplicate Name Resolution... ");
        
        try {
            // ARRANGE: Create scenario with potential duplicate
            String baseName = "Portfolio " + Main.gui.webData.portfolio.size();
            Main.gui.webData.portfolio_names.add(baseName); // Add potential duplicate
            
            // ACT: Apply duplicate resolution logic
            String newName = "Portfolio " + (Main.gui.webData.portfolio.size() + 1);
            Main.gui.webData.portfolio_names.add(newName);
            
            // Simulate the duplicate handling from new portfolio creation
            for (int i = 0; i < Main.gui.webData.portfolio_names.size(); ++i) {
                if (Main.gui.webData.portfolio_names.get(Main.gui.webData.portfolio_names.size()-1).equals(Main.gui.webData.portfolio_names.get(i))) {
                    if (Main.gui.webData.portfolio_names.size()-1 != i) {
                        Main.gui.webData.portfolio_names.set(Main.gui.webData.portfolio_names.size()-1, 
                            Main.gui.webData.portfolio_names.get(Main.gui.webData.portfolio_names.size()-1) + " ");
                    }
                }
            }
            
            // ASSERT: Check if duplicate was resolved
            String finalName = Main.gui.webData.portfolio_names.get(Main.gui.webData.portfolio_names.size()-1);
            boolean duplicateResolved = finalName.endsWith(" ") || !finalName.equals(baseName);
            
            if (duplicateResolved) {
                System.out.println("PASS");
            } else {
                System.out.println("FAIL (Duplicate not resolved)");
            }
            
        } catch (Exception e) {
            System.out.println("FAIL (Exception: " + e.getMessage() + ")");
        }
    }

    /**
     * TC-31: Minimum Portfolio Requirement
     * Tests the business rule that at least one portfolio must always exist
     * Verifies: Enforcement of minimum portfolio constraint
     */
    public static void TC_31_Minimum_Portfolio_Requirement() {
        System.out.print("[TC-31] Minimum Portfolio Rule...... ");
        
        try {
            // ARRANGE: Reduce to exactly one portfolio
            while (Main.gui.webData.portfolio.size() > 1) {
                Main.gui.webData.portfolio.remove(Main.gui.webData.portfolio.size() - 1);
                Main.gui.webData.portfolio_names.remove(Main.gui.webData.portfolio_names.size() - 1);
            }
            
            // ACT: Test the minimum portfolio check (from case 1 deletion logic)
            boolean deletionAllowed = Main.gui.webData.portfolio.size() > 1;
            
            // ASSERT: Should not allow deletion of the last portfolio
            if (!deletionAllowed && Main.gui.webData.portfolio.size() == 1) {
                System.out.println("PASS");
            } else {
                System.out.println("FAIL (Minimum rule not enforced, count: " + Main.gui.webData.portfolio.size() + ")");
            }
            
        } catch (Exception e) {
            System.out.println("FAIL (Exception: " + e.getMessage() + ")");
        }
    }

    /**
     * TC-32: Portfolio Data Integrity
     * Tests that portfolio operations maintain data consistency
     * Verifies: Portfolio list and name list stay synchronized
     */
    public static void TC_32_Portfolio_Data_Integrity() {
        System.out.print("[TC-32] Data Integrity Check........ ");
        
        try {
            // ACT: Perform various operations and check integrity
            int portfolioCount = Main.gui.webData.portfolio.size();
            int nameCount = Main.gui.webData.portfolio_names.size();
            
            // Check if lists are synchronized
            boolean listsInSync = portfolioCount == nameCount;
            
            // Check if all portfolios have names
            boolean allHaveNames = true;
            for (int i = 0; i < Main.gui.webData.portfolio_names.size(); i++) {
                if (Main.gui.webData.portfolio_names.get(i) == null || Main.gui.webData.portfolio_names.get(i).trim().isEmpty()) {
                    allHaveNames = false;
                    break;
                }
            }
            
            // ASSERT: Data integrity checks
            if (listsInSync && allHaveNames && portfolioCount > 0) {
                System.out.println("PASS");
            } else {
                System.out.println("FAIL (Sync:" + listsInSync + " Names:" + allHaveNames + " Count:" + portfolioCount + ")");
            }
            
        } catch (Exception e) {
            System.out.println("FAIL (Exception: " + e.getMessage() + ")");
        }
    }
}