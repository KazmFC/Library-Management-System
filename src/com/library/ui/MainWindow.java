package com.library.ui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private JPanel panel; // Declare panel as instance variable

    public MainWindow() {
        setTitle("Library Management System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Theme toggle
        JToggleButton themeToggle = new JToggleButton("Dark Mode");
        themeToggle.setFont(new Font("Arial", Font.BOLD, 12));
        themeToggle.setBackground(ThemeManager.BUTTON_ACTION);
        themeToggle.setForeground(ThemeManager.BUTTON_TEXT);
        themeToggle.addActionListener(e -> {
            ThemeManager.setTheme(themeToggle.isSelected() ? ThemeManager.Theme.DARK : ThemeManager.Theme.LIGHT);
            applyTheme();
            themeToggle.setText(themeToggle.isSelected() ? "Light Mode" : "Dark Mode");
        });

        // Top panel for theme toggle
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        topPanel.add(themeToggle);

        // Main panel
        panel = new JPanel(new GridLayout(4, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Buttons
        RoundedButton authorsButton = new RoundedButton("Manage Authors");
        authorsButton.setBackground(ThemeManager.BUTTON_ACTION);
        RoundedButton booksButton = new RoundedButton("Manage Books");
        booksButton.setBackground(ThemeManager.BUTTON_ACTION);
        RoundedButton usersButton = new RoundedButton("Manage Users");
        usersButton.setBackground(ThemeManager.BUTTON_ACTION);
        RoundedButton studentsButton = new RoundedButton("Manage Students");
        studentsButton.setBackground(ThemeManager.BUTTON_ACTION);
        RoundedButton authorBooksButton = new RoundedButton("Assign Author Books");
        authorBooksButton.setBackground(ThemeManager.BUTTON_ACTION);
        RoundedButton studentBooksButton = new RoundedButton("Assign Student Books");
        studentBooksButton.setBackground(ThemeManager.BUTTON_ACTION);
        RoundedButton exitButton = new RoundedButton("Exit");
        exitButton.setBackground(ThemeManager.BUTTON_BACK);

        // Add buttons to panel
        panel.add(authorsButton);
        panel.add(booksButton);
        panel.add(usersButton);
        panel.add(studentsButton);
        panel.add(authorBooksButton);
        panel.add(studentBooksButton);
        panel.add(exitButton);

        // Add components
        add(topPanel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        // Apply initial theme
        applyTheme();

        // Button actions
        authorsButton.addActionListener(e -> new AuthorsWindow().setVisible(true));
        booksButton.addActionListener(e -> new BooksWindow().setVisible(true));
        usersButton.addActionListener(e -> new UsersWindow().setVisible(true));
        studentsButton.addActionListener(e -> new StudentsWindow().setVisible(true));
        authorBooksButton.addActionListener(e -> new AuthorBooksWindow().setVisible(true));
        studentBooksButton.addActionListener(e -> new StudentBooksWindow().setVisible(true));
        exitButton.addActionListener(e -> System.exit(0));
    }

    private void applyTheme() {
        ThemeManager.applyTheme(this, panel, panel.getComponents());
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}