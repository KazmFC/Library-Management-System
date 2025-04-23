package com.library.ui;

import com.library.db.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AuthorBooksWindow extends JFrame {
    private JComboBox<String> authorComboBox, bookComboBox;
    private JTable table;
    private DefaultTableModel tableModel;
    private final Map<String, Integer> authorMap = new HashMap<>();
    private final Map<String, Integer> bookMap = new HashMap<>();
    private JPanel inputPanel; // Declare inputPanel as instance variable

    public AuthorBooksWindow() {
        setTitle("Assign Author Books");
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
        inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Author:"));
        authorComboBox = new JComboBox<>();
        authorComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(authorComboBox);

        inputPanel.add(new JLabel("Book:"));
        bookComboBox = new JComboBox<>();
        bookComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(bookComboBox);

        RoundedButton assignButton = new RoundedButton("Assign");
        assignButton.setBackground(ThemeManager.BUTTON_ACTION);
        inputPanel.add(assignButton);

        RoundedButton backButton = new RoundedButton("Back");
        backButton.setBackground(ThemeManager.BUTTON_BACK);
        inputPanel.add(backButton);

        // Table
        String[] columns = {"ID", "Author", "Book", "Edit", "Delete"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRendererEditor("Edit", e -> editAssignment(Integer.parseInt(e.getActionCommand()))));
        table.getColumnModel().getColumn(3).setCellEditor(new ButtonRendererEditor("Edit", e -> editAssignment(Integer.parseInt(e.getActionCommand()))));
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRendererEditor("Delete", e -> deleteAssignment(Integer.parseInt(e.getActionCommand()))));
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonRendererEditor("Delete", e -> deleteAssignment(Integer.parseInt(e.getActionCommand()))));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 2));

        // Add components
        add(topPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadAuthors();
        loadBooks();
        loadAssignments();

        // Apply initial theme
        applyTheme();

        // Button actions
        assignButton.addActionListener(e -> assignBook());
        backButton.addActionListener(e -> {
            new MainWindow().setVisible(true);
            dispose();
        });
    }

    private void applyTheme() {
        ThemeManager.applyTheme(this, inputPanel, authorComboBox, bookComboBox, table);
        repaint();
    }

    private void loadAuthors() {
        authorComboBox.removeAllItems();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM authors";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String name = rs.getString("name");
                int id = rs.getInt("id");
                authorMap.put(name, id);
                authorComboBox.addItem(name);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBooks() {
        bookComboBox.removeAllItems();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM books";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String name = rs.getString("name");
                int id = rs.getInt("id");
                bookMap.put(name, id);
                bookComboBox.addItem(name);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignBook() {
        String authorName = (String) authorComboBox.getSelectedItem();
        String bookName = (String) bookComboBox.getSelectedItem();

        if (authorName == null || bookName == null) {
            JOptionPane.showMessageDialog(this, "Please select an author and a book", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int authorId = authorMap.get(authorName);
        int bookId = bookMap.get(bookName);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO author_books (author_id, book_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, authorId);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                tableModel.addRow(new Object[]{id, authorName, bookName, "Edit", "Delete"});
            }

            JOptionPane.showMessageDialog(this, "Book assigned successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            authorComboBox.setSelectedIndex(-1);
            bookComboBox.setSelectedIndex(-1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editAssignment(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        String currentAuthor = (String) tableModel.getValueAt(row, 1);
        String currentBook = (String) tableModel.getValueAt(row, 2);
        String authorName = (String) JOptionPane.showInputDialog(this, "Select new author:", "Edit Author", JOptionPane.QUESTION_MESSAGE, null, UIUtils.getComboBoxItems(authorComboBox), currentAuthor);
        String bookName = (String) JOptionPane.showInputDialog(this, "Select new book:", "Edit Book", JOptionPane.QUESTION_MESSAGE, null, UIUtils.getComboBoxItems(bookComboBox), currentBook);

        if (authorName == null || bookName == null) {
            JOptionPane.showMessageDialog(this, "Please select an author and a book", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int authorId = authorMap.get(authorName);
        int bookId = bookMap.get(bookName);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE author_books SET author_id = ?, book_id = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, authorId);
            stmt.setInt(2, bookId);
            stmt.setInt(3, id);
            stmt.executeUpdate();

            tableModel.setValueAt(authorName, row, 1);
            tableModel.setValueAt(bookName, row, 2);
            JOptionPane.showMessageDialog(this, "Assignment updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAssignment(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this assignment?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM author_books WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();

            tableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Assignment deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAssignments() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT ab.id, a.name AS author_name, b.name AS book_name " +
                        "FROM author_books ab " +
                        "JOIN authors a ON ab.author_id = a.id " +
                        "JOIN books b ON ab.book_id = b.id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("author_name"),
                    rs.getString("book_name"),
                    "Edit",
                    "Delete"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}