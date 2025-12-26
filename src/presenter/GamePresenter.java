package presenter;

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
import model.Player;
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

    private void stopGame() {
        if (gameTimer != null) gameTimer.stop(); 
        saveScoreToDB();
        loadTableData();
        window.showMenu(); 
    }

    private void saveScoreToDB() {
        if (activeUsername == null) return;
        try {
            DB db = new DB();
            String query = String.format(
                "UPDATE tbenefit SET skor = skor + %d, peluru_meleset = peluru_meleset + %d, sisa_peluru = %d WHERE username = '%s'",
                gameState.skor, gameState.peluruMeleset, gameState.getPlayer().getAmmo(), activeUsername
            );
            db.updateQuery(query);
            db.close();
        } catch (Exception e) { e.printStackTrace(); }
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
        
        int initialAmmo = 0; 

        try {
            DB db = new DB();
            ResultSet rs = db.selectQuery("SELECT * FROM tbenefit WHERE username = '" + inputUsername + "'");
            
            if (rs.next()) {
                // USER LAMA: Ambil sisa peluru terakhir
                initialAmmo = rs.getInt("sisa_peluru");
            } else {
                // USER BARU: Buat data baru, peluru awal 0
                db.updateQuery("INSERT INTO tbenefit (username, skor, peluru_meleset, sisa_peluru) VALUES ('" + inputUsername + "', 0, 0, 0)");
                initialAmmo = 0;
            }
            db.close();
            loadTableData();
            
            activeUsername = inputUsername;
            
            // --- MULAI GAME ---
            gameState = new GameState();
            // SET PELURU DARI DATABASE
            gameState.getPlayer().addAmmo(initialAmmo); 
            
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
                Player p = gameState.getPlayer();

                if (code == KeyEvent.VK_SPACE) { stopGame(); return; }
                if (code == KeyEvent.VK_Z && !gameState.isGameOver) gameState.playerShoot();
                if (code == KeyEvent.VK_R && gameState.isGameOver) {
                     gameState = new GameState();
                     gamePanel.setGameState(gameState);
                }

                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) p.isUp = true;
                if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) p.isDown = true;
                if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) p.isLeft = true;
                if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) p.isRight = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                Player p = gameState.getPlayer();

                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) p.isUp = false;
                if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) p.isDown = false;
                if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) p.isLeft = false;
                if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) p.isRight = false;
            }
        });
    }

    private void startGameLoop() {
        if (gameTimer != null) gameTimer.stop();
        // Lambda expression otomatis mengimplementasikan ActionListener
        gameTimer = new Timer(16, e -> {
            gameState.update(); 
            gamePanel.repaint(); 
        });
        gameTimer.start();
    }
}