package com.library.ui;

import java.awt.*;
import javax.swing.*;

public class ThemeManager {
    public enum Theme { LIGHT, DARK }

    private static Theme currentTheme = Theme.LIGHT;

    // Light theme colors
    private static final Color LIGHT_BACKGROUND = new Color(245, 245, 245); // #F5F5F5
    private static final Color LIGHT_PANEL = new Color(176, 196, 222); // #B0C4DE
    private static final Color LIGHT_TEXT = new Color(51, 51, 51); // #333333
    private static final Color LIGHT_BORDER = new Color(100, 149, 237); // #6495ED

    // Dark theme colors
    private static final Color DARK_BACKGROUND = new Color(44, 44, 44); // #2C2C2C
    private static final Color DARK_PANEL = new Color(60, 60, 60); // #3C3C3C
    private static final Color DARK_TEXT = new Color(211, 211, 211); // #D3D3D3
    private static final Color DARK_BORDER = new Color(70, 130, 180); // #4682B4

    // Common button colors
    public static final Color BUTTON_ACTION = new Color(50, 205, 50); // #32CD32
    public static final Color BUTTON_BACK = new Color(255, 165, 0); // #FFA500
    public static final Color BUTTON_DELETE = new Color(255, 69, 0); // #FF4500
    public static final Color BUTTON_TEXT = Color.WHITE;

    public static void setTheme(Theme theme) {
        currentTheme = theme;
    }

    public static Theme getCurrentTheme() {
        return currentTheme;
    }

    public static Color getBackgroundColor() {
        return currentTheme == Theme.LIGHT ? LIGHT_BACKGROUND : DARK_BACKGROUND;
    }

    public static Color getPanelColor() {
        return currentTheme == Theme.LIGHT ? LIGHT_PANEL : DARK_PANEL;
    }

    public static Color getTextColor() {
        return currentTheme == Theme.LIGHT ? LIGHT_TEXT : DARK_TEXT;
    }

    public static Color getBorderColor() {
        return currentTheme == Theme.LIGHT ? LIGHT_BORDER : DARK_BORDER;
    }

    public static void applyTheme(JFrame frame, Container container, Component... components) {
        frame.getContentPane().setBackground(getBackgroundColor());
        container.setBackground(getPanelColor());
        for (Component comp : components) {
            if (comp instanceof JLabel || comp instanceof JTextField || comp instanceof JComboBox || comp instanceof JTable) {
                comp.setForeground(getTextColor());
                comp.setBackground(getBackgroundColor());
            }
            if (comp instanceof JTable) {
                ((JTable) comp).setGridColor(getBorderColor());
            }
        }
    }
}