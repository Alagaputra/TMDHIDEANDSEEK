package view;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

public class GameWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public GameWindow(MenuPanel menuPanel, GamePanel gamePanel) {
        setTitle("Hide and Seek The Challenge");
        setSize(800, 635); // Sedikit dilebihkan untuk border
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Tambahkan Panel Menu dan Game
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");

        add(mainPanel);
        setVisible(true);
    }

    public void showGame() {
        cardLayout.show(mainPanel, "GAME");
        // Request focus agar keyboard terbaca di GamePanel
        mainPanel.getComponent(1).requestFocus(); 
    }

    public void showMenu() {
        cardLayout.show(mainPanel, "MENU");
    }
}