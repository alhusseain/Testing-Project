package com.cryptocheckertest.userinterface;

import static org.junit.jupiter.api.Assertions.*;


import com.cryptochecker.Main;
import com.cryptochecker.PanelCoin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.InputEvent;
import java.lang.reflect.InvocationTargetException;

public class PanelCoinTest {
    PanelCoin panelCoin;
    JTable table;
    JTextField search;
    TableModel tableModel;

    @BeforeEach
    public void setUp() throws InterruptedException, InvocationTargetException {
        Main.main(null);

        SwingUtilities.invokeAndWait(() -> {
            panelCoin = new PanelCoin();
            table = panelCoin.getTable();
            search = panelCoin.getHeaderSearchField();
            tableModel = panelCoin.getModel();

            // Add panel to a visible frame so Robot can click
            JFrame frame = new JFrame();
            frame.setContentPane(panelCoin.panel);
            frame.pack();
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null); // center on screen
            frame.setVisible(true);
        });
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @ParameterizedTest
    @CsvSource({
            ",100", "tether,2", "sajfkdjk,0", "xrp,1"
    })
    public void testSearchField(String query, int count) {
        search.setText(query);
        assertEquals(table.getRowCount(), count);
    }

    @ParameterizedTest
    @ValueSource(ints =  {0, 1, 2, 3, 4, 5, 6})
    public void testSortColumnValue(int colIndex) throws Exception {

        JTableHeader header = table.getTableHeader();
        Rectangle headerRect = header.getHeaderRect(colIndex);

        Point headerOnScreen = header.getLocationOnScreen();
        int x = headerOnScreen.x + headerRect.x + headerRect.width / 2;
        int y = headerOnScreen.y + headerRect.y + headerRect.height / 2;


        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        robot.setAutoWaitForIdle(true);

        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Double previous = Double.NEGATIVE_INFINITY;
        for (int row = 0; row < table.getRowCount(); row++) {
            Object value = table.getValueAt(row, colIndex);
            if (value instanceof Number) {
                double current = ((Number) value).doubleValue();
                boolean greater = current>=previous;
                System.out.println(greater);
                System.out.println(current + " " + previous);
                assertEquals(true,greater);
                previous = current;
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);


        previous = Double.POSITIVE_INFINITY;
        for (int row = 0; row < table.getRowCount(); row++) {
            Object value = table.getValueAt(row, colIndex);
            if (value instanceof Number) {
                double current = ((Number) value).doubleValue();
                assertTrue(current <= previous,
                        "Row " + row + " value " + current + " is greater than previous " + previous);
                previous = current;
            }
        }

    }
}

