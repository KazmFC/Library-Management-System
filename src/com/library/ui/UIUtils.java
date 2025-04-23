package com.library.ui;

import javax.swing.*;

public class UIUtils {
    public static Object[] getComboBoxItems(JComboBox<String> comboBox) {
        int itemCount = comboBox.getItemCount();
        Object[] items = new Object[itemCount];
        for (int i = 0; i < itemCount; i++) {
            items[i] = comboBox.getItemAt(i);
        }
        return items;
    }
}