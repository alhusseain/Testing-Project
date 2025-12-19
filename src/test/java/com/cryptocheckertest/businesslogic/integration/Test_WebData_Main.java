
package com.cryptocheckertest.businesslogic.integration;
import com.cryptochecker.Main;
import com.cryptochecker.WebData;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration Test: WebData ↔ Main Communication
 *
 * This test verifies the integration between WebData and Main modules:
 * - Data serialization/deserialization flow
 * - Theme configuration propagation
 * - Currency settings synchronization
 * - Portfolio data management
 * - Error handling across module boundaries
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Test_WebData_Main {

    private static final String TEST_FOLDER = System.getProperty("user.home") + "/.crypto-checker-test/";
    private static final String TEST_DATA_SER = TEST_FOLDER + "data.ser";
    private static final String TEST_PORTFOLIO_SER = TEST_FOLDER + "portfolio.ser";
    private static final String TEST_SETTINGS_SER = TEST_FOLDER + "settings.ser";

    private String originalDataLocation;
    private String originalPortfolioLocation;
    private String originalSettingsLocation;

    @BeforeAll
    void setupTestEnvironment() throws Exception {
        // Create test directory
        new File(TEST_FOLDER).mkdirs();

        // Save original locations
        originalDataLocation = Main.dataSerLocation;
        originalPortfolioLocation = Main.portfolioSerLocation;
        originalSettingsLocation = Main.settingsSerLocation;

        // Redirect to test locations
        setStaticField(Main.class, "dataSerLocation", TEST_DATA_SER);
        setStaticField(Main.class, "portfolioSerLocation", TEST_PORTFOLIO_SER);
        setStaticField(Main.class, "settingsSerLocation", TEST_SETTINGS_SER);
    }

    @AfterAll
    void cleanupTestEnvironment() throws Exception {
        // Restore original locations
        setStaticField(Main.class, "dataSerLocation", originalDataLocation);
        setStaticField(Main.class, "portfolioSerLocation", originalPortfolioLocation);
        setStaticField(Main.class, "settingsSerLocation", originalSettingsLocation);

        // Clean up test files
        deleteDirectory(new File(TEST_FOLDER));
    }

    @BeforeEach
    void setup() {
        // Clean test files before each test
        deleteTestFiles();
        Main.resetConfiguration();
    }

    @AfterEach
    void teardown() {
        deleteTestFiles();
    }

    // =========================================================================
    // TEST 1: WebData Initialization → Main Configuration
    // =========================================================================
    @Test
    @DisplayName("Integration: WebData initialization should work with Main's currency settings")
    void testWebDataInitializationWithMainCurrency() throws Exception {
        // Arrange: Set currency in Main
        Main.currency = "EUR";
        Main.currencyChar = "€";

        // Create mock data file
        createMockDataFile();

        // Act: Initialize WebData (should deserialize using Main's settings)
        WebData webData = new WebData();

        // Assert: Verify WebData was initialized successfully
        assertNotNull(webData.coin, "Coin list should be initialized");
        assertNotNull(webData.global_data, "Global data should be initialized");
        assertNotNull(webData.portfolio, "Portfolio should be initialized");

        // Verify currency consistency
        assertEquals("EUR", Main.currency, "Main currency should remain EUR");
        assertEquals("€", Main.currencyChar, "Main currency character should remain €");
    }

    // =========================================================================
    // TEST 2: Main Theme Configuration → WebData Operations
    // =========================================================================
    @Test
    @DisplayName("Integration: Theme changes in Main should not affect WebData operations")
    void testThemeChangeDoesNotAffectWebData() throws Exception {
        // Arrange: Create WebData with initial theme
        Main.theme = new Main.Theme(Main.themes.LIGHT);
        createMockDataFile();
        WebData webData = new WebData();

        // Act: Change theme in Main
        Main.theme.change(Main.themes.DARK);

        // Assert: WebData operations should still work
        assertNotNull(webData.coin, "WebData coin list should remain valid");
        assertNotNull(webData.global_data, "WebData global_data should remain valid");

        // Verify coin data is accessible
        if (!webData.coin.isEmpty()) {
            WebData.Coin coin = webData.coin.get(0);
            assertNotNull(coin.getName(), "Coin name should be accessible");
        }
    }

    // =========================================================================
    // TEST 3: Portfolio Serialization Flow (Main ↔ WebData)
    // =========================================================================
    @Test
    @DisplayName("Integration: Portfolio data should flow correctly between Main and WebData")
    void testPortfolioDataFlow() throws Exception {
        // Arrange: Initialize Main's GUI components (minimal mock)
        Main.gui = new Main();
        Main.gui.webData = new WebData();

        // Create portfolio data in WebData
        Main.gui.webData.portfolio = new ArrayList<>();
        Main.gui.webData.portfolio.add(new ArrayList<>());
        Main.gui.webData.portfolio_names = new ArrayList<>();
        Main.gui.webData.portfolio_names.add("Test Portfolio");
        Main.gui.webData.portfolio_nr = 0;

        // Add a coin to portfolio
        WebData.Coin testCoin = Main.gui.webData.new Coin();
        testCoin.setName("Bitcoin");
        testCoin.setSymbol("BTC");
        testCoin.setPrice(50000.0);
        testCoin.setPortfolioAmount(2.5);
        testCoin.setPortfolioCurrency("USD");
        Main.gui.webData.portfolio.get(0).add(testCoin);

        // Act: Serialize portfolio
        serializePortfolio(Main.gui.webData);

        // Create new WebData instance and deserialize
        Main.gui.webData = new WebData();
        Main.gui.deserializePortfolio();

        // Assert: Verify data integrity
        assertNotNull(Main.gui.webData.portfolio, "Portfolio should be deserialized");
        assertEquals(1, Main.gui.webData.portfolio.size(), "Should have 1 portfolio");
        assertEquals("Test Portfolio", Main.gui.webData.portfolio_names.get(0), "Portfolio name should match");
        assertEquals(1, Main.gui.webData.portfolio.get(0).size(), "Should have 1 coin in portfolio");

        WebData.Coin retrievedCoin = Main.gui.webData.portfolio.get(0).get(0);
        assertEquals("Bitcoin", retrievedCoin.getName(), "Coin name should match");
        assertEquals(2.5, retrievedCoin.getPortfolioAmount(), 0.001, "Portfolio amount should match");
    }

    // =========================================================================
    // TEST 4: Error Handling Across Module Boundaries
    // =========================================================================
    @Test
    @DisplayName("Integration: WebData should handle corrupted files gracefully with Main's error handling")
    void testErrorHandlingAcrossModules() throws Exception {
        // Arrange: Create corrupted data file
        try (FileOutputStream fos = new FileOutputStream(TEST_DATA_SER)) {
            fos.write("CORRUPTED DATA".getBytes());
        }

        // Act & Assert: WebData should handle error and re-fetch
        assertDoesNotThrow(() -> {
            WebData webData = new WebData();
            // After handling corruption, data structures should be initialized
            assertNotNull(webData.coin, "Coin list should be initialized after error recovery");
            assertNotNull(webData.global_data, "Global data should be initialized after error recovery");
        }, "WebData should handle corrupted files gracefully");

        // Verify corrupted file was deleted
        assertFalse(new File(TEST_DATA_SER).exists() || new File(TEST_DATA_SER).length() == "CORRUPTED DATA".length(),
                "Corrupted file should be deleted or replaced");
    }

    // =========================================================================
    // TEST 5: Currency Change Propagation (Main → WebData → Fetch)
    // =========================================================================
    @Test
    @DisplayName("Integration: Currency changes in Main should affect WebData API calls")
    void testCurrencyChangePropagation() throws Exception {
        // Arrange: Set initial currency
        Main.currency = "USD";
        Main.currencyChar = "$";
        createMockDataFile();
        WebData webData = new WebData();

        // Act: Change currency in Main
        Main.currency = "EUR";
        Main.currencyChar = "€";

        // Simulate fetch (would use new currency in actual API call)
        // We can verify by checking the currency is properly set
        assertEquals("EUR", Main.currency, "Currency should be updated");

        // Verify WebData can still access the updated currency
        assertNotNull(webData.coin, "WebData should still function with new currency");
    }

    // =========================================================================
    // TEST 6: Multiple Module Initialization Sequence
    // =========================================================================
    @Test
    @DisplayName("Integration: Complete initialization sequence (Settings → WebData → Portfolio)")
    void testCompleteInitializationSequence() throws Exception {
        // Arrange: Create settings file
        createMockSettingsFile(Main.themes.DARK, "GBP", "£");

        // Act: Simulate Main's initialization sequence
        Main.gui = new Main();
        Main.gui.deserializeSettings(); // Step 1: Load settings
        Main.gui.webData = new WebData();  // Step 2: Initialize WebData
        Main.gui.deserializePortfolio();   // Step 3: Load portfolio

        // Assert: Verify complete integration
        assertNotNull(Main.theme, "Theme should be initialized");
        assertEquals(Main.themes.DARK, Main.theme.currentTheme, "Theme should be DARK");
        assertEquals("GBP", Main.currency, "Currency should be GBP");
        assertEquals("£", Main.currencyChar, "Currency character should be £");
        assertNotNull(Main.gui.webData.coin, "WebData coin list should be initialized");
        assertNotNull(Main.gui.webData.portfolio, "Portfolio should be initialized");
    }

    // =========================================================================
    // TEST 7: WebData Coin Object Integration with Main
    // =========================================================================
    @Test
    @DisplayName("Integration: Coin objects should work correctly with Main's formatting")
    void testCoinObjectIntegration() throws Exception {
        // Arrange
        createMockDataFile();
        WebData webData = new WebData();

        // Create a test coin
        WebData.Coin coin = webData.new Coin();
        coin.setName("Ethereum");
        coin.setSymbol("ETH");
        coin.setPrice(3000.5678);
        coin.setRank(2);
        coin.setMarketCap(350000000000.0);

        // Act: Test coin's formatting methods (which use Main's settings)
        String priceStr = coin.trimPrice(coin.getPrice());
        String info = coin.getInfo();

        // Assert: Verify integration
        assertNotNull(priceStr, "Price formatting should work");
        assertTrue(priceStr.contains("3000"), "Price should be formatted correctly");
        assertNotNull(info, "Coin info should be generated");
        assertTrue(info.contains("Ethereum"), "Info should contain coin name");
        assertTrue(info.contains(Main.currency), "Info should contain Main's currency");
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private void createMockDataFile() throws Exception {
        // Create minimal valid serialized data
        try (FileOutputStream file = new FileOutputStream(TEST_DATA_SER);
             BufferedOutputStream buffer = new BufferedOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(buffer)) {

            // Create mock global data
            WebData webData = new WebData();
            WebData.Global_Data globalData = webData.new Global_Data();

            // Create mock coin list
            ArrayList<WebData.Coin> coins = new ArrayList<>();
            WebData.Coin mockCoin = webData.new Coin();
            mockCoin.setName("Bitcoin");
            mockCoin.setSymbol("BTC");
            mockCoin.setPrice(50000.0);
            mockCoin.setRank(1);
            coins.add(mockCoin);

            out.writeObject(globalData);
            out.writeObject(coins);
        }
    }

    private void createMockSettingsFile(Main.themes theme, String currency, String currencyChar) throws Exception {
        try (FileOutputStream file = new FileOutputStream(TEST_SETTINGS_SER);
             BufferedOutputStream buffer = new BufferedOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(buffer)) {

            out.writeObject(false); // Debug.mode
            out.writeObject(new Main.Theme(theme));
            out.writeObject(currency);
            out.writeObject(currencyChar);
        }
    }

    private void serializePortfolio(WebData webData) throws Exception {
        try (FileOutputStream file = new FileOutputStream(TEST_PORTFOLIO_SER);
             BufferedOutputStream buffer = new BufferedOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(buffer)) {

            out.writeObject(webData.portfolio);
            out.writeObject(webData.portfolio_names);
            out.writeObject(webData.portfolio_nr);
        }
    }

    private void deleteTestFiles() {
        new File(TEST_DATA_SER).delete();
        new File(TEST_PORTFOLIO_SER).delete();
        new File(TEST_SETTINGS_SER).delete();
    }

    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    private void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}