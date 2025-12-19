package com.cryptocheckertest.businesslogic.integration;

import com.cryptochecker.Main;
import com.cryptochecker.PanelSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Test_Settings_Main {
    PanelSettings panelSettings;

    @BeforeEach
    public void setUp() throws InterruptedException, InvocationTargetException {
        Main.main(null);

        SwingUtilities.invokeAndWait(() -> panelSettings = new PanelSettings());
    }

    @Test
    @Order(1)
    public void Test_currency_change() {
        // simulate selecting USD
        Main.currency = "USD";
        Main.currencyChar = "$";

        JButton fakeButton = new JButton();
        panelSettings.getbCurrencyListener().actionPerformed(
                new java.awt.event.ActionEvent(fakeButton, 0, "currency")
        );

        assertEquals(panelSettings.selectedValue, Main.currency);
    }

    @Test
    @Order(2)
    public void Test_theme_change() {
        JButton fakeButton = new JButton();

        Main.theme.change(Main.themes.LIGHT);

        panelSettings.getbThemeListener().actionPerformed(
                new java.awt.event.ActionEvent(fakeButton, 0, "theme")
        );

        assertEquals(Main.themes.DARK, Main.theme.currentTheme);

        panelSettings.getbThemeListener().actionPerformed(
                new java.awt.event.ActionEvent(fakeButton, 0, "theme")
        );
        assertEquals(Main.themes.CUSTOM, Main.theme.currentTheme);

        panelSettings.getbThemeListener().actionPerformed(
                new java.awt.event.ActionEvent(fakeButton, 0, "theme")
        );
        assertEquals(Main.themes.LIGHT, Main.theme.currentTheme);
    }

}
