package presenter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import model.DB;
import model.GameState;
import model.TBenefit;
import model.TabelModelTBenefit;
import view.GamePanel;
import view.GameWindow;
import view.MenuPanel;

public class GamePresenter {
    private GameWindow window;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private GameState gameState;
    private Timer gameTimer;
    public static String activeUsername;

    public GamePresenter(GameWindow window, MenuPanel menuPanel, GamePanel gamePanel) {
        this.window = window; this.menuPanel = menuPanel; this.gamePanel = gamePanel;
        
        this.gameState = new GameState();
        this.gamePanel.setGameState(gameState);

        this.menuPanel.setPlayAction(e -> prosesLoginDanMain());
        this.menuPanel.setQuitAction(e -> System.exit(0));
        
        setupInput();
    }

    // --- FUNGSI STOP GAME & BALIK KE MENU (SESUAI PDF HAL 6) ---
    private void stopGame() {
        if (gameTimer != null) gameTimer.stop(); // Hentikan timer game
        
        // Simpan Data Terakhir ke Database sebelum keluar
        saveScoreToDB();
        
        // Refresh tabel di menu
        loadTableData();
        
        // Kembali ke Tampilan Awal (Menu)
        window.showMenu(); 
    }

    private void saveScoreToDB() {
        if (activeUsername == null) return;
        try {
            DB db = new DB();
            // Update skor dan data peluru ke database
            // (Asumsi skor bersifat akumulatif sesuai PDF hal 5 poin 89)
            String query = String.format(
                "UPDATE tbenefit SET skor = skor + %d, peluru_meleset = peluru_meleset + %d, sisa_peluru = %d WHERE username = '%s'",
                gameState.skor, 
                gameState.peluruMeleset, 
                gameState.playerAmmo, 
                activeUsername
            );
            db.updateQuery(query);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadTableData() {
        try {
            DB db = new DB();
            ResultSet rs = db.selectQuery("SELECT * FROM tbenefit");
            List<TBenefit> listData = new ArrayList<>();
            while (rs.next()) {
                listData.add(new TBenefit(rs.getInt("id"), rs.getString("username"), rs.getInt("skor"), rs.getInt("peluru_meleset"), rs.getInt("sisa_peluru")));
            }
            menuPanel.setTableModel(new TabelModelTBenefit(listData));
            db.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void prosesLoginDanMain() {
        String inputUsername = menuPanel.getUsername();
        if (inputUsername.trim().isEmpty()) { JOptionPane.showMessageDialog(window, "Username harus diisi!"); return; }
        try {
            DB db = new DB();
            ResultSet rs = db.selectQuery("SELECT * FROM tbenefit WHERE username = '" + inputUsername + "'");
            if (!rs.next()) {
                db.updateQuery("INSERT INTO tbenefit (username, skor, peluru_meleset, sisa_peluru) VALUES ('" + inputUsername + "', 0, 0, 0)");
            }
            db.close();
            loadTableData(); // Refresh data
            
            activeUsername = inputUsername;
            
            // Reset Game State Baru
            gameState = new GameState();
            gamePanel.setGameState(gameState);
            
            window.showGame();
            startGameLoop(); 
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setupInput() {
        this.gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                
                // --- 1. TOMBOL SPACE: STOP & KEMBALI KE MENU (SESUAI PDF) ---
                if (code == KeyEvent.VK_SPACE) {
                    stopGame();
                    return; 
                }

                // --- 2. TOMBOL Z: MENEMBAK (PENGGANTI SPACE) ---
                if (code == KeyEvent.VK_Z) {
                    if (!gameState.isGameOver) gameState.playerShoot();
                }

                // --- 3. TOMBOL PANAH / WASD: GERAK ---
                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) gameState.isUp = true;
                if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) gameState.isDown = true;
                if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) gameState.isLeft = true;
                if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) gameState.isRight = true;

                // --- 4. TOMBOL R: RESTART (OPSIONAL TAPI BAGUS) ---
                if (gameState.isGameOver && code == KeyEvent.VK_R) {
                     // Reset state tanpa kembali ke menu
                     gameState = new GameState();
                     gamePanel.setGameState(gameState);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) gameState.isUp = false;
                if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) gameState.isDown = false;
                if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) gameState.isLeft = false;
                if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) gameState.isRight = false;
            }
        });
    }

    private void startGameLoop() {
        if (gameTimer != null) gameTimer.stop();
        gameTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameState.update();
                gamePanel.repaint();
            }
        });
        gameTimer.start();
    }
}