package com.library.ui;

import com.library.db.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UsersWindow extends JFrame {
    private JTextField nameField, emailField;
    private JTable table;
    private DefaultTableModel tableModel;
    private JPanel inputPanel; // Declare inputPanel as instance variable

    public UsersWindow() {
        setTitle("Manage Users");
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

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(emailField);

        RoundedButton addButton = new RoundedButton("Add User");
        addButton.setBackground(ThemeManager.BUTTON_ACTION);
        inputPanel.add(addButton);

        RoundedButton backButton = new RoundedButton("Back");
        backButton.setBackground(ThemeManager.BUTTON_BACK);
        inputPanel.add(backButton);

        // Table
        String[] columns = {"ID", "Name", "Email", "Edit", "Delete"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRendererEditor("Edit", e -> editUser(Integer.parseInt(e.getActionCommand()))));
        table.getColumnModel().getColumn(3).setCellEditor(new ButtonRendererEditor("Edit", e -> editUser(Integer.parseInt(e.getActionCommand()))));
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRendererEditor("Delete", e -> deleteUser(Integer.parseInt(e.getActionCommand()))));
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonRendererEditor("Delete", e -> deleteUser(Integer.parseInt(e.getActionCommand()))));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 2));

        // Add components
        add(topPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load users
        loadUsers();

        // Apply initial theme
        applyTheme();

        // Button actions
        addButton.addActionListener(e -> addUser());
        backButton.addActionListener(e -> {
            new MainWindow().setVisible(true);
            dispose();
        });
    }

    private void applyTheme() {
        ThemeManager.applyTheme(this, inputPanel, nameField, emailField, table);
        repaint();
    }

    private void addUser() {
        String name = nameField.getText();
        String email = emailField.getText();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                tableModel.addRow(new Object[]{id, name, email, "Edit", "Delete"});
            }

            JOptionPane.showMessageDialog(this, "User added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            nameField.setText("");
            emailField.setText("");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editUser(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        String name = JOptionPane.showInputDialog(this, "Enter new name:", tableModel.getValueAt(row, 1));
        String email = JOptionPane.showInputDialog(this, "Enter new email:", tableModel.getValueAt(row, 2));

        if (name == null || email == null || name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, id);
            stmt.executeUpdate();

            tableModel.setValueAt(name, row, 1);
            tableModel.setValueAt(email, row, 2);
            JOptionPane.showMessageDialog(this, "User updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();

            tableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "User deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    "Edit",
                    "Delete"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}