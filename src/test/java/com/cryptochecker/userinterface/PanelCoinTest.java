package com.cryptochecker.userinterface;

import static org.junit.jupiter.api.Assertions.*;

import com.cryptochecker.Main;
import com.cryptochecker.PanelCoin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class PanelCoinTest {
    PanelCoin panelCoin;
    JTable table;
    JTextField search;
    @BeforeEach
    public void setUp() throws InterruptedException, InvocationTargetException {
        Main.main(null);
        SwingUtilities.invokeAndWait(() -> {
            panelCoin = new PanelCoin();
            table = panelCoin.getTable();
            search = panelCoin.getHeaderSearchField();
        });

    }

    @ParameterizedTest
    @CsvSource({
            ",100","tether,2","sajfkdjk,0","xrp,1"
    })
    public void testSearchField(String query,int count)
    {
        search.setText(query);
        assertEquals(table.getRowCount(),count);
    }



}
