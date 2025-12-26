package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;

public class MenuPanel extends JPanel {
    private JTextField txtUsername;
    private JTable table;
    private JButton btnPlay;
    private JButton btnQuit;

    public MenuPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. JUDUL
        JLabel lblTitle = new JLabel("HIDE AND SEEK THE CHALLENGE", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTitle, BorderLayout.NORTH);

        // 2. BAGIAN TENGAH (Form & Tabel)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);

        // Form Username
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        formPanel.setBackground(Color.WHITE);
        formPanel.add(new JLabel("Username: "));
        txtUsername = new JTextField(20);
        formPanel.add(txtUsername);
        centerPanel.add(formPanel, BorderLayout.NORTH);

        // Tabel Skor
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // 3. TOMBOL BAWAH
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        btnPlay = new JButton("Play");
        btnPlay.setFont(new Font("Arial", Font.BOLD, 16));
        btnPlay.setBackground(Color.WHITE);
        
        btnQuit = new JButton("Quit");
        btnQuit.setFont(new Font("Arial", Font.BOLD, 16));
        btnQuit.setBackground(Color.WHITE);

        buttonPanel.add(btnPlay);
        buttonPanel.add(btnQuit);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public String getUsername() {
        return txtUsername.getText();
    }

    public void setTableModel(TableModel model) {
        table.setModel(model);
    }

    public void setPlayAction(ActionListener action) {
        btnPlay.addActionListener(action);
    }

    public void setQuitAction(ActionListener action) {
        btnQuit.addActionListener(action);
    }
}