package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import model.GameState;
import model.Enemy;
import model.Bullet;
import model.PowerUp;
import model.Player;

public class GamePanel extends JPanel {
    private BufferedImage sheetIdle, imgMap, imgTree, imgPotion;
    private List<BufferedImage> bulletAnimPlayer = new ArrayList<>(); 
    private List<BufferedImage> bulletAnimEnemy  = new ArrayList<>(); 
    private BufferedImage heartFull, heartHalf, heartEmpty;
    private List<BufferedImage> enemyImages = new ArrayList<>();
    
    private GameState gameState;
    private Sound soundEffect = new Sound(); 
    private Sound music = new Sound();       

    public GamePanel() {
        setBackground(Color.BLACK);
        setDoubleBuffered(true); 
        
        try {
            // Load Gambar-gambar
            File fPlayer = new File("src/resources/images/Char_002_Idle.png");
            File fMap    = new File("src/resources/images/Map.png");
            File fTree   = new File("src/resources/images/Size_04.png");
            File fPotion = new File("src/resources/images/potion_red.png");
            
            if (fPlayer.exists()) sheetIdle = ImageIO.read(fPlayer);
            if (fMap.exists())    imgMap    = ImageIO.read(fMap);
            
            if (fTree.exists()) {
                BufferedImage treeSheet = ImageIO.read(fTree);
                imgTree = treeSheet.getSubimage(0, 0, treeSheet.getWidth() / 5, treeSheet.getHeight() / 2); 
            }

            if (fPotion.exists()) {
                BufferedImage fullSheet = ImageIO.read(fPotion);
                imgPotion = fullSheet.getSubimage(0, 0, fullSheet.getWidth() / 3, fullSheet.getHeight() / 2); 
            }

            for (int i = 1; i <= 8; i++) {
                File f1 = new File("src/resources/images/Fire Arrow_Frame_0" + i + ".png");
                if (f1.exists()) bulletAnimPlayer.add(ImageIO.read(f1));
                
                File f2 = new File("src/resources/images/Water Arrow_Frame_0" + i + ".png");
                if (f2.exists()) bulletAnimEnemy.add(ImageIO.read(f2));
            }

            File fHeart = new File("src/resources/images/Health_04_Heart_Red.png");
            if (fHeart.exists()) {
                BufferedImage heartStrip = ImageIO.read(fHeart);
                int w = heartStrip.getWidth() / 5;
                int h = heartStrip.getHeight();
                heartFull  = heartStrip.getSubimage(0, 0, w, h);
                heartHalf  = heartStrip.getSubimage(2 * w, 0, w, h);
                heartEmpty = heartStrip.getSubimage(4 * w, 0, w, h);
            }

            String[] enemies = { "enemy_0.png", "enemy_1.png", "enemy_2.png" };
            for (String nama : enemies) {
                File f = new File("src/resources/images/" + nama);
                if (f.exists()) enemyImages.add(ImageIO.read(f));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }

        playMusic(0); 
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
    
    public void playMusic(int i) { music.setFile(i); music.play(); music.loop(); }
    public void stopMusic() { music.stop(); }
    public void playSE(int i) { soundEffect.setFile(i); soundEffect.play(); }
    
    private class RenderItem {
        public int sortY; public Object object; public int type;
        public RenderItem(int type, int sortY, Object object) { this.type = type; this.sortY = sortY; this.object = object; }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameState == null) return;
        Graphics2D g2d = (Graphics2D) g;
        
        while (!gameState.soundRequests.isEmpty()) {
            playSE(gameState.soundRequests.remove(0)); 
        }

        Player pObj = gameState.getPlayer();

        // 1. KAMERA
        double camX = Math.max(0, Math.min(pObj.getX() - 400, gameState.worldWidth - 800));
        double camY = Math.max(0, Math.min(pObj.getY() - 300, gameState.worldHeight - 600));
        g2d.translate(-camX, -camY); 

        // 2. BACKGROUND
        if (imgMap != null) g2d.drawImage(imgMap, 0, 0, gameState.worldWidth, gameState.worldHeight, null);
        else { g2d.setColor(Color.DARK_GRAY); g2d.fillRect(0, 0, gameState.worldWidth, gameState.worldHeight); }

        // 3. POWERUP
        for (PowerUp p : gameState.getPowerUps()) {
            int floatOffset = (int) (Math.sin(gameState.tickCount * 0.1) * 5); 
            if (imgPotion != null) g2d.drawImage(imgPotion, (int)p.getX(), (int)p.getY() + floatOffset, 32, 32, null);
            else { g2d.setColor(Color.GREEN); g2d.fillOval((int)p.getX(), (int)p.getY(), p.getWidth(), p.getHeight()); }
        }

        // 4. SORTING
        List<RenderItem> renderList = new ArrayList<>();
        for (java.awt.Rectangle rock : gameState.getObstacles()) renderList.add(new RenderItem(0, rock.y + rock.height, rock));
        
        if (!gameState.isGameOver) {
            for (Enemy e : gameState.getEnemies()) renderList.add(new RenderItem(1, (int)e.getY() + 50, e));
            renderList.add(new RenderItem(2, (int)pObj.getY() + 64, "PLAYER"));
        }
        Collections.sort(renderList, Comparator.comparingInt(o -> o.sortY));

        for (RenderItem item : renderList) {
            if (item.type == 0) { // Pohon
                java.awt.Rectangle obs = (java.awt.Rectangle) item.object;
                if (imgTree != null) g2d.drawImage(imgTree, obs.x + (obs.width-100)/2, obs.y + obs.height - 140, 100, 140, null);
            } 
            else if (item.type == 1) { // Musuh
                Enemy e = (Enemy) item.object;
                BufferedImage img = enemyImages.isEmpty() ? null : enemyImages.get(e.getType() % enemyImages.size());
                if (img != null) {
                    int frameW = img.getWidth() / 4;
                    int frameH = img.getHeight();
                    int col = (int)(gameState.tickCount / 10) % 4;
                    BufferedImage spr = img.getSubimage(col * frameW, 0, frameW, frameH);
                    
                    if (e.isFacingRight()) g2d.drawImage(spr, (int)e.getX(), (int)e.getY(), 50, 50, null);
                    else g2d.drawImage(spr, (int)e.getX() + 50, (int)e.getY(), -50, 50, null);
                } else { g2d.setColor(Color.RED); g2d.fillRect((int)e.getX(), (int)e.getY(), 50, 50); }
            } 
            else if (item.type == 2) { // Player
                if (sheetIdle != null) {
                    int frameW = sheetIdle.getWidth() / 4;
                    int frameH = sheetIdle.getHeight() / 4;
                    int col = (int)(gameState.tickCount/10) % 4;
                    int row = pObj.getDirection();
                    BufferedImage spr = sheetIdle.getSubimage(col * frameW, row * frameH, frameW, frameH);
                    g2d.drawImage(spr, (int)pObj.getX(), (int)pObj.getY(), 64, 64, null);
                } else { 
                    g2d.setColor(Color.BLUE); g2d.fillRect((int)pObj.getX(), (int)pObj.getY(), 64, 64); 
                }
            }
        }

        // 5. PELURU
        for (Bullet b : gameState.getBullets()) {
            List<BufferedImage> anim = b.isEnemyBullet() ? bulletAnimEnemy : bulletAnimPlayer;
            drawRotatedBullet(g2d, b, anim);
        }

        // 6. HUD
        g2d.translate(camX, camY); 
        
        // Nyawa
        for (int i = 0; i < 3; i++) {
            double val = pObj.getHealth() - i;
            if (val >= 1.0 && heartFull != null) g2d.drawImage(heartFull, 20 + (i*35), 20, 32, 32, null);
            else if (val == 0.5 && heartHalf != null) g2d.drawImage(heartHalf, 20 + (i*35), 20, 32, 32, null);
            else if (heartEmpty != null) g2d.drawImage(heartEmpty, 20 + (i*35), 20, 32, 32, null);
        }

        // Statistik
        g2d.setColor(Color.WHITE); g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        g2d.drawString("Skor: " + gameState.skor, 20, 70);
        g2d.setColor(Color.YELLOW); g2d.drawString("Ammo: " + pObj.getAmmo(), 20, 90);
        g2d.setColor(Color.GREEN);  g2d.drawString("Peluru Meleset: " + gameState.peluruMeleset, 20, 110);
        
        // WAVE & SISA MUSUH (Ini yang tadi hilang)
        g2d.setColor(Color.CYAN);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        g2d.drawString("WAVE " + gameState.wave, 350, 40);
        
        // Hitung sisa musuh
        // Total target wave - yang sudah spawn + yang masih hidup di layar
        int sisaMusuh = (gameState.maxEnemiesInWave - gameState.enemiesSpawned) + gameState.getEnemies().size();
        
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        g2d.drawString("Enemies Left: " + sisaMusuh, 350, 60);

        // GAME OVER SCREEN
        if (gameState.isGameOver) {
            g2d.setColor(new Color(0, 0, 0, 180)); g2d.fillRect(0, 0, 800, 600);
            g2d.setColor(Color.RED); g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 50));
            g2d.drawString("GAME OVER", 250, 250);
            g2d.setColor(Color.WHITE); g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20));
            g2d.drawString("Skor Akhir: " + gameState.skor, 330, 300);
            g2d.drawString("Peluru Meleset: " + gameState.peluruMeleset, 310, 330);
            g2d.setColor(Color.YELLOW); g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
            g2d.drawString("Tekan 'R' untuk Main Lagi", 280, 400);
            g2d.drawString("Tekan 'SPACE' untuk Kembali ke Menu", 220, 440);
        }
    }
    
    private void drawRotatedBullet(Graphics2D g2d, Bullet b, List<BufferedImage> animList) {
        if (!animList.isEmpty()) {
            int frame = (int)(gameState.tickCount / 3) % animList.size();
            BufferedImage sprite = animList.get(frame);
            AffineTransform old = g2d.getTransform();
            g2d.translate(b.getX() + 32, b.getY() + 32); 
            g2d.rotate(Math.atan2(b.getVelY(), b.getVelX()) + Math.PI); 
            g2d.drawImage(sprite, -32, -32, 64, 64, null);
            g2d.setTransform(old);
        } else {
            g2d.setColor(Color.RED); g2d.fillOval((int)b.getX(), (int)b.getY(), 20, 20);
        }
    }
}