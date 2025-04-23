package com.library.ui;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonRendererEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
    private final JButton button;
    private String label;
    private boolean isEditor;
    private int row;
    private final ActionListener actionListener;

    public ButtonRendererEditor(String text, ActionListener listener) {
        this.label = text;
        this.button = new JButton(text);
        this.actionListener = listener;
        button.setOpaque(true);
        button.setBackground(new Color(255, 102, 102)); // Red for Delete, customizable
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.addActionListener(e -> {
            if (isEditor) {
                fireEditingStopped();
                actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, String.valueOf(row)));
            }
        });
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            button.setBackground(new Color(200, 50, 50));
        } else {
            button.setBackground(new Color(255, 102, 102));
        }
        return button;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        isEditor = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return label;
    }
}