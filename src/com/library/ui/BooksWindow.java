package com.library.ui;

import com.library.db.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class BooksWindow extends JFrame {
    private JTextField nameField, countField, papersField;
    private JComboBox<String> authorComboBox;
    private JTable table;
    private DefaultTableModel tableModel;
    private final Map<Integer, String> authorMap = new HashMap<>();
    private JPanel inputPanel; // Declare inputPanel as instance variable

    public BooksWindow() {
        setTitle("Manage Books");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        // Input panel
        inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Count:"));
        countField = new JTextField();
        countField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(countField);

        inputPanel.add(new JLabel("Papers:"));
        papersField = new JTextField();
        papersField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(papersField);

        inputPanel.add(new JLabel("Author:"));
        authorComboBox = new JComboBox<>();
        authorComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(authorComboBox);

        RoundedButton addButton = new RoundedButton("Add Book");
        addButton.setBackground(ThemeManager.BUTTON_ACTION);
        inputPanel.add(addButton);

        RoundedButton backButton = new RoundedButton("Back");
        backButton.setBackground(ThemeManager.BUTTON_BACK);
        inputPanel.add(backButton);

        // Table
        String[] columns = {"ID", "Name", "Count", "Papers", "Author", "Edit", "Delete"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRendererEditor("Edit", e -> editBook(Integer.parseInt(e.getActionCommand()))));
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonRendererEditor("Edit", e -> editBook(Integer.parseInt(e.getActionCommand()))));
        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRendererEditor("Delete", e -> deleteBook(Integer.parseInt(e.getActionCommand()))));
        table.getColumnModel().getColumn(6).setCellEditor(new ButtonRendererEditor("Delete", e -> deleteBook(Integer.parseInt(e.getActionCommand()))));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 2));

        // Add components
        add(topPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load authors and books
        loadAuthors();
        loadBooks();

        // Apply initial theme
        applyTheme();

        // Button actions
        addButton.addActionListener(e -> addBook());
        backButton.addActionListener(e -> {
            new MainWindow().setVisible(true);
            dispose();
        });
    }

    private void applyTheme() {
        ThemeManager.applyTheme(this, inputPanel, nameField, countField, papersField, authorComboBox, table);
        repaint();
    }

    private void loadAuthors() {
        authorComboBox.removeAllItems();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM authors";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                authorMap.put(id, name);
                authorComboBox.addItem(name);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBook() {
        String name = nameField.getText();
        String countStr = countField.getText();
        String papersStr = papersField.getText();
        String authorName = (String) authorComboBox.getSelectedItem();

        if (name.isEmpty() || countStr.isEmpty() || papersStr.isEmpty() || authorName == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int count, papers;
        try {
            count = Integer.parseInt(countStr);
            papers = Integer.parseInt(papersStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Count and Papers must be numbers", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO books (name, count, papers, author) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.setInt(2, count);
            stmt.setInt(3, papers);
            stmt.setString(4, authorName);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                tableModel.addRow(new Object[]{id, name, count, papers, authorName, "Edit", "Delete"});
            }

            JOptionPane.showMessageDialog(this, "Book added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editBook(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        String name = JOptionPane.showInputDialog(this, "Enter new name:", tableModel.getValueAt(row, 1));
        String countStr = JOptionPane.showInputDialog(this, "Enter new count:", tableModel.getValueAt(row, 2));
        String papersStr = JOptionPane.showInputDialog(this, "Enter new papers:", tableModel.getValueAt(row, 3));
        String currentAuthor = (String) tableModel.getValueAt(row, 4);
        String authorName = (String) JOptionPane.showInputDialog(this, "Select new author:", "Edit Author", JOptionPane.QUESTION_MESSAGE, null, UIUtils.getComboBoxItems(authorComboBox), currentAuthor);

        if (name == null || countStr == null || papersStr == null || authorName == null || name.isEmpty() || countStr.isEmpty() || papersStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int count, papers;
        try {
            count = Integer.parseInt(countStr);
            papers = Integer.parseInt(papersStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Count and Papers must be numbers", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE books SET name = ?, count = ?, papers = ?, author = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setInt(2, count);
            stmt.setInt(3, papers);
            stmt.setString(4, authorName);
            stmt.setInt(5, id);
            stmt.executeUpdate();

            tableModel.setValueAt(name, row, 1);
            tableModel.setValueAt(count, row, 2);
            tableModel.setValueAt(papers, row, 3);
            tableModel.setValueAt(authorName, row, 4);
            JOptionPane.showMessageDialog(this, "Book updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBook(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM books WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();

            tableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Book deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBooks() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM books";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("count"),
                    rs.getInt("papers"),
                    rs.getString("author"),
                    "Edit",
                    "Delete"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        nameField.setText("");
        countField.setText("");
        papersField.setText("");
        authorComboBox.setSelectedIndex(-1);
    }
}