package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
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
    private Image bgImage; // Variabel untuk menyimpan gambar background

    public MenuPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Memberi jarak pinggir

        // --- 1. LOAD BACKGROUND IMAGE ---
        try {
            // Kita pakai gambar map sebagai background menu agar serasi
            File fBg = new File("src/resources/images/map.png"); 
            if (fBg.exists()) {
                bgImage = ImageIO.read(fBg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- 2. JUDUL (Bagian Atas) ---
        JLabel lblTitle = new JLabel("HIDE AND SEEK THE CHALLENGE", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36)); // Font lebih besar & modern
        lblTitle.setForeground(Color.WHITE); // Warna putih agar kontras dengan map
        
        // Panel judul transparan
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false); // Agar background gambar terlihat
        titlePanel.add(lblTitle);
        add(titlePanel, BorderLayout.NORTH);

        // --- 3. TENGAH (Input & Tabel) ---
        // Panel pembungkus utama di tengah
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setOpaque(false); // Transparan

        // Panel semi-transparan hitam untuk wadah Tabel & Form (Agar tulisan terbaca)
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(new Color(0, 0, 0, 150)); // Hitam Alpha 150 (Transparan)
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // -- Form Input Username --
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        formPanel.setOpaque(false);
        
        JLabel lblUser = new JLabel("Username: ");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblUser.setForeground(Color.WHITE);
        
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUsername.setPreferredSize(new Dimension(200, 30));
        
        formPanel.add(lblUser);
        formPanel.add(txtUsername);
        contentPanel.add(formPanel, BorderLayout.NORTH);

        // -- Tabel Data Skor --
        table = new JTable();
        table.setRowHeight(25); // Baris lebih tinggi
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // FITUR AUTO-FILL: Saat tabel diklik, isi username ke textfield
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                // Pastikan ada baris yang dipilih
                if (row != -1) {
                    // Ambil data dari kolom ke-0 (Username)
                    Object val = table.getValueAt(row, 0);
                    if (val != null) {
                        txtUsername.setText(val.toString());
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        centerContainer.add(contentPanel, BorderLayout.CENTER);
        // Beri jarak antara panel tengah dengan pinggir layar
        JPanel paddingPanel = new JPanel(new BorderLayout());
        paddingPanel.setOpaque(false);
        paddingPanel.setBorder(new EmptyBorder(10, 50, 10, 50)); // Margin kiri kanan
        paddingPanel.add(centerContainer);
        
        add(paddingPanel, BorderLayout.CENTER);

        // --- 4. BAWAH (Tombol) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        buttonPanel.setOpaque(false);
        
        // Custom Button Style
        btnPlay = createStyledButton("PLAY", new Color(34, 139, 34)); // Hijau Hutan
        btnQuit = createStyledButton("QUIT", new Color(178, 34, 34)); // Merah Bata

        buttonPanel.add(btnPlay);
        buttonPanel.add(btnQuit);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Helper Method: Membuat tombol yang cantik
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); // Hilangkan garis fokus
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(10, 40, 10, 40) // Padding tombol
        ));
        return btn;
    }

    // Override paintComponent untuk menggambar Background Image
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            // Gambar background memenuhi panel
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            // Warna cadangan jika gambar tidak ketemu
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public String getUsername() { return txtUsername.getText(); }
    public void setTableModel(TableModel model) { table.setModel(model); }
    public void setPlayAction(ActionListener action) { btnPlay.addActionListener(action); }
    public void setQuitAction(ActionListener action) { btnQuit.addActionListener(action); }
}