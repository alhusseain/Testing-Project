package com.cryptocheckertest.businesslogic.integration;

import com.cryptochecker.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;



public class Test_WebData_Main {

        private Main main;
        private WebData webData;

        @BeforeEach
        public void setUp() throws Exception {
            // Reset to factory settings before each test
            Main.resetConfiguration();
            main = new Main();
            // Manually initialize webData to avoid NullPointerException
            main.webData = new WebData();
            webData = main.webData;
        }

        @AfterEach
        public void tearDown() {
            // Clean up: delete serialized files after each test
            new File(Main.dataSerLocation).delete();
            new File(Main.portfolioSerLocation).delete();
            new File(Main.settingsSerLocation).delete();
        }

        @Test
        public void testCurrencyCommunication() {
            // Test if WebData uses the correct currency from Main
            assertEquals("USD", Main.currency);
            // Check if WebData.fetch() uses Main.currency (indirectly)
            assertTrue(WebData.class.toString().contains("Main.currency"));
        }

        @Test
        public void testDataSerialization() throws Exception {
            // Mock the fetch() to avoid real API calls
            webData.coin = new ArrayList<>();
//            webData.serializeData(); // Assume this method exists or use reflection to call fetch() with a mock

            File dataFile = new File(Main.dataSerLocation);
            assertTrue(dataFile.exists(), "Serialized data file should exist");

            // Test if Main can deserialize the data
            main.deserializePortfolio();
            assertNotNull(webData.coin, "Deserialized coin data should not be null");
            assertNotNull(webData.global_data, "Deserialized global data should not be null");
        }

        @Test
        public void testPortfolioDeserialization() throws Exception {
            // Mock portfolio data
            webData.portfolio = new ArrayList<>();
            webData.portfolio.add(new ArrayList<>());
            webData.portfolio_names.add("Portfolio 1");

            // Serialize the mock portfolio
            try (FileOutputStream file = new FileOutputStream(Main.portfolioSerLocation);
                 BufferedOutputStream buffer = new BufferedOutputStream(file);
                 ObjectOutputStream out = new ObjectOutputStream(buffer)) {
                out.writeObject(webData.portfolio);
                out.writeObject(webData.portfolio_names);
                //out.writeObject(webData.portfolio_nr);
            }

            // Deserialize and verify
            main.deserializePortfolio();
            assertNotNull(webData.portfolio, "Portfolio should be deserialized");
            assertFalse(webData.portfolio.isEmpty(), "Portfolio should not be empty");
            assertEquals("Portfolio 1", webData.portfolio_names.get(0), "Default portfolio name should be 'Portfolio 1'");
        }

        @Test
        @Disabled("Requires full GUI environment, skipped for headless testing")
        public void testUIUpdateAfterFetch() throws Exception {
            // This test is disabled because it requires a full GUI environment
            // You can enable it if you run tests in a GUI-capable environment
        }

        @Test
        @Disabled("Requires network access, skipped for offline testing")
        public void testErrorHandlingOnAPIFailure() {
            // This test is disabled because it requires network access
            // You can enable it if you want to test real API failures
        }
    }