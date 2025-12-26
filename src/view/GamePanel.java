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
import model.GameState.Enemy;
// Import view.Sound dihapus karena satu package

public class GamePanel extends JPanel {
    // --- ASET GAMBAR ---
    private BufferedImage sheetIdle, imgMap;
    private BufferedImage imgTree; 
    private BufferedImage imgPotion; 
    
    private List<BufferedImage> bulletAnimPlayer = new ArrayList<>(); 
    private List<BufferedImage> bulletAnimEnemy  = new ArrayList<>(); 
    
    private BufferedImage heartFull, heartHalf, heartEmpty;
    private List<BufferedImage> enemyImages = new ArrayList<>();
    
    private GameState gameState;
    
    // --- AUDIO SYSTEM ---
    private Sound soundEffect = new Sound(); // Untuk SFX
    private Sound music = new Sound();       // Untuk BGM (Lagu Latar)

    public GamePanel() {
        setBackground(Color.BLACK);
        setDoubleBuffered(true); 
        
        try {
            // LOAD GAMBAR
            File fPlayer = new File("src/resources/images/Char_002_Idle.png");
            File fMap    = new File("src/resources/images/Map.png");
            
            if (fPlayer.exists()) sheetIdle = ImageIO.read(fPlayer);
            if (fMap.exists())    imgMap    = ImageIO.read(fMap);

            File fTree = new File("src/resources/images/Size_04.png");
            if (fTree.exists()) {
                BufferedImage treeSheet = ImageIO.read(fTree);
                int treeW = treeSheet.getWidth() / 5; 
                int treeH = treeSheet.getHeight() / 2; 
                imgTree = treeSheet.getSubimage(0, 0, treeW, treeH); 
            } else {
                File fRock = new File("src/resources/images/rock.png");
                if (fRock.exists()) imgTree = ImageIO.read(fRock);
            }

            File fPotion = new File("src/resources/images/potion_red.png");
            if (fPotion.exists()) {
                BufferedImage fullSheet = ImageIO.read(fPotion);
                int frameW = fullSheet.getWidth() / 3;  
                int frameH = fullSheet.getHeight() / 2; 
                imgPotion = fullSheet.getSubimage(0, 0, frameW, frameH); 
            }

            for (int i = 1; i <= 8; i++) {
                String namaFile = "Fire Arrow_Frame_0" + i + ".png";
                File fBullet = new File("src/resources/images/" + namaFile);
                if (fBullet.exists()) bulletAnimPlayer.add(ImageIO.read(fBullet));
            }
            
            for (int i = 1; i <= 8; i++) {
                String namaFile = "Water Arrow_Frame_0" + i + ".png";
                File fBullet = new File("src/resources/images/" + namaFile);
                if (fBullet.exists()) bulletAnimEnemy.add(ImageIO.read(fBullet));
            }

            File fHeart = new File("src/resources/images/Health_04_Heart_Red.png");
            if (fHeart.exists()) {
                BufferedImage heartStrip = ImageIO.read(fHeart);
                int frameW = heartStrip.getWidth() / 5;
                int frameH = heartStrip.getHeight();
                heartFull  = heartStrip.getSubimage(0 * frameW, 0, frameW, frameH);
                heartHalf  = heartStrip.getSubimage(2 * frameW, 0, frameW, frameH);
                heartEmpty = heartStrip.getSubimage(4 * frameW, 0, frameW, frameH);
            }

            String[] namaFileMusuh = { "enemy_0.png", "enemy_1.png", "enemy_2.png" };
            for (String nama : namaFileMusuh) {
                File f = new File("src/resources/images/" + nama);
                if (f.exists()) enemyImages.add(ImageIO.read(f));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }

        // --- PLAY BGM SAAT GAME START ---
        playMusic(0); 
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
    
    // --- METHOD AUDIO ---
    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop(); // Ulang terus
    }
    
    public void stopMusic() {
        music.stop();
    }

    public void playSE(int i) {
        soundEffect.setFile(i);
        soundEffect.play();
    }
    
    private class RenderItem {
        public int type; 
        public int sortY; 
        public Object object; 
        
        public RenderItem(int type, int sortY, Object object) {
            this.type = type;
            this.sortY = sortY;
            this.object = object;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameState == null) return;

        Graphics2D g2d = (Graphics2D) g;
        
        // --- PROSES REQUEST AUDIO SFX DARI GAMESTATE ---
        while (!gameState.soundRequests.isEmpty()) {
            int soundId = gameState.soundRequests.remove(0); 
            playSE(soundId); 
        }

        // 1. KAMERA
        double camX = gameState.playerX - 400; 
        double camY = gameState.playerY - 300;
        if (camX < 0) camX = 0; if (camY < 0) camY = 0;
        if (camX > gameState.worldWidth - 800) camX = gameState.worldWidth - 800;
        if (camY > gameState.worldHeight - 600) camY = gameState.worldHeight - 600;

        g2d.translate(-camX, -camY); 

        // 2. BACKGROUND
        if (imgMap != null) {
            g2d.drawImage(imgMap, 0, 0, gameState.worldWidth, gameState.worldHeight, null);
        } else { 
            g2d.setColor(Color.DARK_GRAY); 
            g2d.fillRect(0, 0, gameState.worldWidth, gameState.worldHeight); 
        }

        // 3. POTION
        for (model.GameState.PowerUp p : gameState.powerUps) {
            int floatOffset = (int) (Math.sin(gameState.tickCount * 0.1) * 5); 
            if (imgPotion != null) {
                g2d.setColor(new Color(0, 0, 0, 100)); 
                g2d.fillOval(p.x + 4, p.y + 28, 24, 6);
                g2d.drawImage(imgPotion, p.x, p.y + floatOffset, 32, 32, null);
            } else {
                g2d.setColor(Color.GREEN); 
                g2d.fillOval(p.x, p.y + floatOffset, p.width, p.height);
            }
        }

        // 4. SORTING Y (LAYER)
        List<RenderItem> renderList = new ArrayList<>();
        
        for (java.awt.Rectangle rock : gameState.obstacles) {
            renderList.add(new RenderItem(0, rock.y + rock.height, rock));
        }
        
        if (!gameState.isGameOver) {
            for (Enemy e : gameState.enemies) {
                renderList.add(new RenderItem(1, (int)e.y + 50, e));
            }
            renderList.add(new RenderItem(2, gameState.playerY + 64, "PLAYER"));
        }

        Collections.sort(renderList, new Comparator<RenderItem>() {
            @Override
            public int compare(RenderItem o1, RenderItem o2) {
                return Integer.compare(o1.sortY, o2.sortY);
            }
        });

        for (RenderItem item : renderList) {
            if (item.type == 0) { // POHON
                java.awt.Rectangle obstacle = (java.awt.Rectangle) item.object;
                if (imgTree != null) {
                    int drawWidth = 100; int drawHeight = 140;
                    int drawX = obstacle.x + (obstacle.width - drawWidth) / 2;
                    int drawY = (obstacle.y + obstacle.height) - drawHeight;
                    g2d.drawImage(imgTree, drawX, drawY, drawWidth, drawHeight, null);
                } else {
                    g2d.setColor(new Color(101, 67, 33));
                    g2d.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
                }
            }
            else if (item.type == 1) { // MUSUH
                Enemy e = (Enemy) item.object;
                BufferedImage img = null;
                if (!enemyImages.isEmpty()) img = enemyImages.get(e.type % enemyImages.size());
                
                if (img != null) {
                    int fw = img.getWidth() / 4; int fh = img.getHeight();
                    int col = (int)(gameState.tickCount / 10) % 4;
                    BufferedImage spr = img.getSubimage(col * fw, 0, fw, fh);
                    if (e.isFacingRight) g2d.drawImage(spr, (int)e.x, (int)e.y, 50, 50, null);
                    else g2d.drawImage(spr, (int)e.x + 50, (int)e.y, -50, 50, null);
                } else { 
                    g2d.setColor(Color.RED); g2d.fillRect((int)e.x, (int)e.y, 50, 50); 
                }
            }
            else if (item.type == 2) { // PLAYER
                if (sheetIdle != null) {
                    int fw = sheetIdle.getWidth()/4; int fh = sheetIdle.getHeight()/4;
                    int row = gameState.direction; 
                    int col = (int)(gameState.tickCount/10)%4;
                    g2d.drawImage(sheetIdle.getSubimage(col*fw, row*fh, fw, fh), gameState.playerX, gameState.playerY, 64, 64, null);
                } else { 
                    g2d.setColor(Color.BLUE); g2d.fillRect(gameState.playerX, gameState.playerY, 64, 64); 
                }
            }
        }

        // 5. PELURU
        for (model.GameState.Bullet b : gameState.bullets) {
            if (!b.isEnemyBullet) {
                drawRotatedBullet(g2d, b, bulletAnimPlayer);
            } else {
                if (!bulletAnimEnemy.isEmpty()) {
                    drawRotatedBullet(g2d, b, bulletAnimEnemy);
                } else {
                    g2d.setColor(Color.RED); g2d.fillOval((int)b.x, (int)b.y, 20, 20);
                }
            }
        }

        // 6. HUD
        g2d.translate(camX, camY); 
        int startX = 20; int startY = 20; int size = 32;

        for (int i = 0; i < 3; i++) {
            double val = gameState.health - i;
            if (val >= 1.0) { if (heartFull != null) g2d.drawImage(heartFull, startX + (i*35), startY, size, size, null); } 
            else if (val == 0.5) { if (heartHalf != null) g2d.drawImage(heartHalf, startX + (i*35), startY, size, size, null); } 
            else { if (heartEmpty != null) g2d.drawImage(heartEmpty, startX + (i*35), startY, size, size, null); }
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        g2d.drawString("Skor: " + gameState.skor, 20, 70);
        g2d.setColor(Color.YELLOW);
        g2d.drawString("Ammo: " + gameState.playerAmmo, 20, 90);
        g2d.setColor(Color.GREEN);
        g2d.drawString("Peluru Meleset: " + gameState.peluruMeleset, 20, 110);

        g2d.setColor(Color.CYAN);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        g2d.drawString("WAVE " + gameState.wave, 350, 40);
        
        int sisaMusuh = (gameState.maxEnemiesInWave - gameState.enemiesSpawned) + gameState.enemies.size();
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        g2d.drawString("Enemies Left: " + sisaMusuh, 350, 60);

        if (gameState.isGameOver) {
            g2d.setColor(new Color(0, 0, 0, 180)); 
            g2d.fillRect(0, 0, 800, 600);
            g2d.setColor(Color.RED); 
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 50));
            g2d.drawString("GAME OVER", 250, 250);
            g2d.setColor(Color.WHITE); 
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20));
            g2d.drawString("Skor Akhir: " + gameState.skor, 330, 300);
            g2d.drawString("Peluru Meleset: " + gameState.peluruMeleset, 310, 330);
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
            g2d.drawString("Tekan 'R' untuk Main Lagi", 280, 400);
            g2d.drawString("Tekan 'SPACE' untuk Kembali ke Menu", 220, 440);
        }
    }
    
    private void drawRotatedBullet(Graphics2D g2d, model.GameState.Bullet b, List<BufferedImage> animList) {
        if (!animList.isEmpty()) {
            int frameIndex = (int)(gameState.tickCount / 3) % animList.size();
            BufferedImage currentFrame = animList.get(frameIndex);
            
            double centerX = b.x + 32;
            double centerY = b.y + 32;
            double angle = Math.atan2(b.velY, b.velX);
            double rotationOffset = Math.PI; 
            
            AffineTransform oldTransform = g2d.getTransform();
            g2d.translate(centerX, centerY);
            g2d.rotate(angle + rotationOffset); 
            g2d.drawImage(currentFrame, -32, -32, 64, 64, null);
            g2d.setTransform(oldTransform);
        }
    }
}