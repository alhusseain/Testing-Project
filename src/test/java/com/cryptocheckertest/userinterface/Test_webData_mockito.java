package com.cryptocheckertest.userinterface;

import com.cryptochecker.Main;
import com.cryptochecker.WebData;
import com.cryptochecker.Debug;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for WebData class with advanced Mockito techniques
 * This addresses common testing challenges with external dependencies and static methods
 */
@ExtendWith(MockitoExtension.class)
public class Test_webData_mockito {

    @TempDir
    Path tempDir;

    private WebData webData;

    @BeforeEach
    public void setUp() throws Exception {
        // Initialize Main static fields
        Main.currency = "USD";
        Main.frame = new JFrame();
        Main.dataSerLocation = tempDir.resolve("test.ser").toString();
    }

    @AfterEach
    public void tearDown() {
        if (Main.frame != null) {
            Main.frame.dispose();
        }
    }

    // ============================================
    // Coin Class Tests
    // ============================================

    @Test
    @DisplayName("Test coin getters and setters")
    public void testCoinGettersAndSetters() throws Exception {
        // Mock Debug.log to prevent NullPointerException
        try (MockedStatic<Debug> debugMock = mockStatic(Debug.class)) {
            debugMock.when(() -> Debug.log(anyString())).then(invocation -> null);

            webData = new WebData();
            WebData.Coin coin = webData.getCoin();

            // Test setters
            coin.setPortfolioAmount(10.5);
            coin.setPortfolioPrice(50000.0);
            coin.setPortfolioValue(525000.0);
            coin.setPortfolioGains(25000.0);
            coin.setPortfolioCurrency("USD");
            coin.setPortfolioPriceStart(48000.0);
            coin.setPortfolioValueStart(504000.0);

            // Test getters
            assertEquals(10.5, coin.getPortfolioAmount());
            assertEquals(50000.0, coin.getPortfolioPrice());
            assertEquals(525000.0, coin.getPortfolioValue());
            assertEquals(25000.0, coin.getPortfolioGains());
            assertEquals("USD", coin.getPortfolioCurrency());
            assertEquals(48000.0, coin.getPortfolioPriceStart());
            assertEquals(504000.0, coin.getPortfolioValueStart());
        }
    }

    @Test
    @DisplayName("Test trimPrice formatting")
    public void testCoinTrimPrice() throws Exception {
        try (MockedStatic<Debug> debugMock = mockStatic(Debug.class)) {
            debugMock.when(() -> Debug.log(anyString())).then(invocation -> null);

            webData = new WebData();
            WebData.Coin coin = webData.getCoin();

            // Test different price ranges
            String price1 = coin.trimPrice(100.5);
            assertTrue(price1.matches("\\d+\\.\\d{1,2}"));

            String price2 = coin.trimPrice(0.5);
            assertNotNull(price2);

            String price3 = coin.trimPrice(0.000001);
            assertNotNull(price3);
        }
    }

    @Test
    @DisplayName("Test coin cloning")
    public void testCoinClone() throws Exception {
        try (MockedStatic<Debug> debugMock = mockStatic(Debug.class)) {
            debugMock.when(() -> Debug.log(anyString())).then(invocation -> null);

            webData = new WebData();
            WebData.Coin originalCoin = webData.getCoin();
            originalCoin.name = "Bitcoin";
            originalCoin.symbol = "BTC";
            originalCoin.setPortfolioAmount(5.0);

            WebData.Coin clonedCoin = (WebData.Coin) originalCoin.copy();

            assertNotNull(clonedCoin);
            assertEquals(originalCoin.name, clonedCoin.name);
            assertEquals(originalCoin.symbol, clonedCoin.symbol);
            assertEquals(originalCoin.getPortfolioAmount(), clonedCoin.getPortfolioAmount());
        }
    }

    @Test
    @DisplayName("Test coin getInfo")
    public void testCoinGetInfo() throws Exception {
        try (MockedStatic<Debug> debugMock = mockStatic(Debug.class)) {
            debugMock.when(() -> Debug.log(anyString())).then(invocation -> null);

            webData = new WebData();
            WebData.Coin coin = webData.getCoin();
            coin.name = "Bitcoin";
            coin.symbol = "BTC";
            coin.id = "bitcoin";

            String info = coin.getInfo();

            assertNotNull(info);
            assertTrue(info.contains("Name: Bitcoin"));
            assertTrue(info.contains("Symbol: BTC"));
            assertTrue(info.contains("ID: bitcoin"));
        }
    }

    @Test
    @DisplayName("Test coin toString")
    public void testCoinToString() throws Exception {
        try (MockedStatic<Debug> debugMock = mockStatic(Debug.class)) {
            debugMock.when(() -> Debug.log(anyString())).then(invocation -> null);

            webData = new WebData();
            WebData.Coin coin = webData.getCoin();
            coin.name = "Ethereum";

            assertEquals("Ethereum", coin.toString());
        }
    }

    @Test
    @DisplayName("Test getPortfolio string generation")
    public void testCoinGetPortfolio() throws Exception {
        try (MockedStatic<Debug> debugMock = mockStatic(Debug.class)) {
            debugMock.when(() -> Debug.log(anyString())).then(invocation -> null);

            webData = new WebData();
            WebData.Coin coin = webData.getCoin();

            coin.name = "Polkadot";
            coin.setPortfolioAmount(100.0);
            coin.setPortfolioCurrency("GBP");

            String portfolioInfo = coin.getPortfolio();

            assertNotNull(portfolioInfo);
            assertTrue(portfolioInfo.contains("Portfolio Amount:"));
            assertTrue(portfolioInfo.contains("Portfolio Value:"));
            assertTrue(portfolioInfo.contains("Portfolio Currency: GBP"));
        }
    }

    // ============================================
    // Global_Data Class Tests
    // ============================================

    @Test
    @DisplayName("Test Global_Data serialization")
    public void testGlobalDataSerialization() throws Exception {
        try (MockedStatic<Debug> debugMock = mockStatic(Debug.class)) {
            debugMock.when(() -> Debug.log(anyString())).then(invocation -> null);

            webData = new WebData();
            WebData.Global_Data globalData = webData.new Global_Data();

            // Serialize
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(globalData);
            oos.close();

            // Deserialize
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            WebData.Global_Data deserialized = (WebData.Global_Data) ois.readObject();
            ois.close();

            assertNotNull(deserialized);
        }
    }

    @Test
    @DisplayName("Test Global_Data toString")
    public void testGlobalDataToString() throws Exception {
        try (MockedStatic<Debug> debugMock = mockStatic(Debug.class)) {
            debugMock.when(() -> Debug.log(anyString())).then(invocation -> null);

            webData = new WebData();
            WebData.Global_Data globalData = webData.new Global_Data();

            String result = globalData.toString();

            assertNotNull(result);
            assertTrue(result.contains("Total Market Cap:"));
            assertTrue(result.contains("Bitcoin Dominance:"));
        }
    }

    // ============================================
    // Serialization Tests
    // ============================================

    @Test
    @DisplayName("Test coin serialization")
    public void testCoinSerialization() throws Exception {
        try (MockedStatic<Debug> debugMock = mockStatic(Debug.class)) {
            debugMock.when(() -> Debug.log(anyString())).then(invocation -> null);

            File testFile = tempDir.resolve("coins.ser").toFile();
            Main.dataSerLocation = testFile.getAbsolutePath();

            webData = new WebData();
            webData.coin = new ArrayList<>();
            webData.global_data = webData.new Global_Data();

            WebData.Coin coin1 = webData.getCoin();
            coin1.name = "Bitcoin";
            coin1.symbol = "BTC";

            webData.coin.add(coin1);

            // Serialize
            try (FileOutputStream file = new FileOutputStream(testFile);
                 BufferedOutputStream buffer = new BufferedOutputStream(file);
                 ObjectOutputStream out = new ObjectOutputStream(buffer)) {

                out.writeObject(webData.global_data);
                out.writeObject(webData.coin);
            }

            assertTrue(testFile.exists());
            assertTrue(testFile.length() > 0);
        }
    }

    // ============================================
    // Edge Case Tests
    // ============================================

    @Test
    @DisplayName("Test coin with zero values")
    public void testCoinWithZeroValues() throws Exception {
        try (MockedStatic<Debug> debugMock = mockStatic(Debug.class)) {
            debugMock.when(() -> Debug.log(anyString())).then(invocation -> null);

            webData = new WebData();
            WebData.Coin coin = webData.getCoin();

            coin.setPortfolioAmount(0.0);
            coin.setPortfolioPrice(0.0);
            coin.setPortfolioValue(0.0);

            assertEquals(0.0, coin.getPortfolioAmount());
            assertEquals(0.0, coin.getPortfolioPrice());
            assertEquals(0.0, coin.getPortfolioValue());
        }
    }

    @Test
    @DisplayName("Test coin with negative values")
    public void testCoinWithNegativeValues() throws Exception {
        try (MockedStatic<Debug> debugMock = mockStatic(Debug.class)) {
            debugMock.when(() -> Debug.log(anyString())).then(invocation -> null);

            webData = new WebData();
            WebData.Coin coin = webData.getCoin();

            coin.setPortfolioGains(-1000.0);

            assertEquals(-1000.0, coin.getPortfolioGains());

            String info = coin.getPortfolio();
            assertTrue(info.contains("-1,000"));
        }
    }

    @Test
    @DisplayName("Test portfolio initialization")
    public void testPortfolioInitialization() throws Exception {
        try (MockedStatic<Debug> debugMock = mockStatic(Debug.class)) {
            debugMock.when(() -> Debug.log(anyString())).then(invocation -> null);

            webData = new WebData();

            assertNotNull(webData.portfolio_names);
            assertTrue(webData.portfolio_names instanceof ArrayList);
            assertEquals(0, webData.portfolio_nr);
        }
    }

    @Test
    @DisplayName("Test trimPrice edge cases")
    public void testTrimPriceEdgeCases() throws Exception {
        try (MockedStatic<Debug> debugMock = mockStatic(Debug.class)) {
            debugMock.when(() -> Debug.log(anyString())).then(invocation -> null);

            webData = new WebData();
            WebData.Coin coin = webData.getCoin();

            // Test zero
            assertNotNull(coin.trimPrice(0.0));

            // Test very small number
            assertNotNull(coin.trimPrice(0.0000000001));

            // Test very large number
            assertNotNull(coin.trimPrice(999999999.99));
        }
    }

    @Test
    @DisplayName("Test coin with null name")
    public void testCoinWithNullName() throws Exception {
        try (MockedStatic<Debug> debugMock = mockStatic(Debug.class)) {
            debugMock.when(() -> Debug.log(anyString())).then(invocation -> null);

            webData = new WebData();
            WebData.Coin coin = webData.getCoin();

            coin.name = null;

            // toString should handle null gracefully
            String result = coin.toString();
            // Will return "null" as a string, which is acceptable
            assertNotNull(result);
        }
    }

    @Test
    @DisplayName("Test multiple coins independently")
    public void testMultipleCoins() throws Exception {
        try (MockedStatic<Debug> debugMock = mockStatic(Debug.class)) {
            debugMock.when(() -> Debug.log(anyString())).then(invocation -> null);

            webData = new WebData();

            WebData.Coin coin1 = webData.getCoin();
            coin1.name = "Bitcoin";
            coin1.setPortfolioAmount(1.0);

            WebData.Coin coin2 = webData.getCoin();
            coin2.name = "Ethereum";
            coin2.setPortfolioAmount(10.0);

            // Verify coins are independent
            assertEquals(1.0, coin1.getPortfolioAmount());
            assertEquals(10.0, coin2.getPortfolioAmount());
            assertNotEquals(coin1.name, coin2.name);
        }
    }
}
