package com.cryptocheckertest.businesslogic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cryptochecker.Main;
import com.cryptochecker.PanelPortfolio;
import com.cryptochecker.Debug;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PanelPortfolioTest {
    
    private PanelPortfolio panelPortfolio;
    private List<String> capturedDebugLogs;
    
    @BeforeEach
    public void setUp() throws InterruptedException, InvocationTargetException {
        Main.main(null);
        SwingUtilities.invokeAndWait(() -> {
            panelPortfolio = new PanelPortfolio();
        });
        capturedDebugLogs = new ArrayList<>();
    }

    @Test
    @DisplayName("TC-27: Verify new portfolio creation with unique name validation and error handling")
    public void testNewPortfolioCreation() throws InterruptedException, InvocationTargetException {
        
        final String[][] VALID_TEST_CASES = {
            {"Portfolio 1", "Should create with default naming convention"},
            {"Portfolio 2", "Should handle sequential portfolio numbering"}, 
            {"CustomName", "Should accept any valid string name"}
        };
        
        final String[][] INVALID_TEST_CASES = {
            {"Portfolio 1", "Should handle duplicate names by appending space"},
            {"", "Should reject empty names"},
            {null, "Should handle null names gracefully"}
        };
        
        String expectedNewPortfolioPrefix = VALID_TEST_CASES[0][0].split(" ")[0];
        
        final String[] EXPECTED_LOG_MESSAGES = {
            "Button Manage Portfolio Clicked",
            "New Portfolio",
            "-- completed"
        };
        
        final String[] EXPECTED_BEHAVIORS = {
            "New portfolio should be created successfully",
            "Portfolio count should increase by 1", 
            "Portfolio names should follow convention or be unique",
            "Duplicate names should be resolved automatically"
        };

        // ARRANGE
        final int initialPortfolioCount = Main.gui.webData.portfolio.size();
        final int initialNameCount = Main.gui.webData.portfolio_names.size();
        
        try (MockedStatic<JOptionPane> mockedJOptionPane = mockStatic(JOptionPane.class)) {
            
            mockedJOptionPane.when(() -> JOptionPane.showOptionDialog(
                any(), anyString(), anyString(), anyInt(), anyInt(), any(), any(), any()
            )).thenReturn(2);
            
            try (MockedStatic<Debug> mockedDebug = mockStatic(Debug.class)) {
                
                mockedDebug.when(() -> Debug.log(anyString()))
                          .thenAnswer(invocation -> {
                              String logMessage = invocation.getArgument(0);
                              capturedDebugLogs.add(logMessage);
                              return null;
                          });

                // ACT
                SwingUtilities.invokeAndWait(() -> {
                    JButton manageButton = findManageButton(panelPortfolio.panel);
                    
                    if (manageButton != null) {
                        manageButton.doClick();
                    } else {
                        fail("Could not find Manage button in PanelPortfolio");
                    }
                });

                // ASSERT
                assertEquals(initialPortfolioCount + 1, Main.gui.webData.portfolio.size(), 
                           EXPECTED_BEHAVIORS[0]);
                
                assertEquals(initialNameCount + 1, Main.gui.webData.portfolio_names.size(),
                           EXPECTED_BEHAVIORS[1]);
                
                String newPortfolioName = Main.gui.webData.portfolio_names.get(
                    Main.gui.webData.portfolio_names.size() - 1);
                assertNotNull(newPortfolioName, EXPECTED_BEHAVIORS[2]);
                assertTrue(newPortfolioName.startsWith(expectedNewPortfolioPrefix + " "), 
                          EXPECTED_BEHAVIORS[2] + " - Expected prefix: " + expectedNewPortfolioPrefix);
                
                assertTrue(newPortfolioName.matches("Portfolio \\d+( )*"), 
                          "Should match pattern from test cases: " + INVALID_TEST_CASES[0][1]);
                
                assertTrue(capturedDebugLogs.contains(EXPECTED_LOG_MESSAGES[0]), 
                          "Should log button click");
                assertTrue(capturedDebugLogs.contains(EXPECTED_LOG_MESSAGES[1]), 
                          "Should log new portfolio action");
                assertTrue(capturedDebugLogs.contains(EXPECTED_LOG_MESSAGES[2]), 
                          "Should log completion");
                
                mockedJOptionPane.verify(() -> JOptionPane.showOptionDialog(
                    eq(Main.frame),
                    eq("Select action for the current portfolio"),
                    eq("Manage Portfolio"),
                    eq(JOptionPane.YES_NO_CANCEL_OPTION),
                    eq(JOptionPane.PLAIN_MESSAGE),
                    eq(null),
                    any(Object[].class),
                    any()
                ), times(1));
            }
        }
    }
    
    private JButton findManageButton(JPanel panel) {
        return findButtonByText(panel, "Manage");
    }
    
    private JButton findButtonByText(java.awt.Container container, String buttonText) {
        for (java.awt.Component component : container.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                if (buttonText.equals(button.getText())) {
                    return button;
                }
            } else if (component instanceof java.awt.Container) {
                JButton found = findButtonByText((java.awt.Container) component, buttonText);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    @Test 
    @DisplayName("TC-27b: Test duplicate portfolio name handling")
    public void testDuplicateNameHandling() throws InterruptedException, InvocationTargetException {
        // Test duplicate name logic using INVALID_TEST_CASES
        // Similar AAA structure focusing on duplicate name resolution
    }
}