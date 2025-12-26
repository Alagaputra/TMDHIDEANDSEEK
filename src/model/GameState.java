package model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameState {
    // --- KONSTANTA MAP ---
    public final int worldWidth = 1200;  
    public final int worldHeight = 900; 

    // --- OBJEK GAME (OOP) ---
    private Player player; // Menggunakan Class Player
    
    // Koleksi (List) untuk menyimpan banyak objek sekaligus
    private List<Enemy> enemies = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();
    private List<PowerUp> powerUps = new ArrayList<>(); 
    private List<Rectangle> boundaries = new ArrayList<>(); // Tembok pinggir
    private List<Rectangle> obstacles = new ArrayList<>();  // Pohon/Batu

    // --- STATUS PERMAINAN ---
    public int wave = 1;                
    public int enemiesSpawned = 0;      
    public int maxEnemiesInWave = 12;   
    public boolean isGameOver = false;
    public int skor = 0;
    public int peluruMeleset = 0; 

    public long tickCount = 0;  // Frame counter
    private int damageCooldown = 0; // Timer kebal
    
    // Sistem Request Suara
    public List<Integer> soundRequests = new ArrayList<>();

    // Constructor GameState
    public GameState() {
        // Buat objek Player baru di posisi tengah (600, 450)
        player = new Player(600, 450);
        
        setupForestMap();          // Buat tembok pinggir
        generateRandomObstacles(); // Buat rintangan acak
    }
    
    // Getter Player untuk diakses View/Presenter
    public Player getPlayer() { return player; } 
    
    // Method request suara
    public void requestSound(int id) { soundRequests.add(id); }

    // Setup Batas Map
    private void setupForestMap() {
        boundaries.clear();
        boundaries.add(new Rectangle(0, 0, 50, worldHeight));            
        boundaries.add(new Rectangle(worldWidth - 50, 0, 50, worldHeight)); 
        boundaries.add(new Rectangle(0, 0, worldWidth, 50));             
        boundaries.add(new Rectangle(0, worldHeight - 50, worldWidth, 50)); 
    }

    // Generate Pohon Acak (Anti-Numpuk)
    private void generateRandomObstacles() {
        Random rand = new Random();
        obstacles.clear();
        int count = 0; int safety = 0;
        
        while (count < 12 && safety < 5000) {
            safety++;
            int rx = 80 + rand.nextInt(worldWidth - 160); 
            int ry = 80 + rand.nextInt(worldHeight - 160);
            
            // Cek Jarak ke Player (Pakai Method getter getX/getY)
            double dist = Math.sqrt(Math.pow(rx - player.getX(), 2) + Math.pow(ry - player.getY(), 2));
            if (dist < 120) continue; 
            
            // Cek Jarak ke Pohon Lain
            boolean close = false;
            for (Rectangle obs : obstacles) {
                if (Math.sqrt(Math.pow(rx - obs.x, 2) + Math.pow(ry - obs.y, 2)) < 150) { close = true; break; }
            }
            if (close) continue;
            
            obstacles.add(new Rectangle(rx, ry, 40, 40));
            count++;
        }
    }

    // Cek Tabrakan Fisik (Tembok/Pohon)
    private boolean cekTabrakan(double x, double y) {
        Rectangle futureObj = new Rectangle((int)x + 16, (int)y + 16, 32, 32); 
        for (Rectangle wall : boundaries) if (futureObj.intersects(wall)) return true;
        for (Rectangle rock : obstacles) if (futureObj.intersects(rock)) return true;
        return false; 
    }

    // Logika Player Menembak
    public void playerShoot() {
        // Cek ammo lewat method getter
        if (player.getAmmo() > 0) { 
            double speed = 10.0 + (wave * 1.5);
            if (speed > 25.0) speed = 25.0;

            double vx = 0, vy = 0;
            // Akses input lewat atribut public di Player
            if (player.isUp)    vy -= speed; if (player.isDown)  vy += speed;
            if (player.isLeft)  vx -= speed; if (player.isRight) vx += speed;
            
            if (vx != 0 && vy != 0) { vx *= 0.707; vy *= 0.707; }
            
            if (vx == 0 && vy == 0) {
                // Akses direction lewat getter
                if (player.getDirection() == 0) vy = speed;      
                else if (player.getDirection() == 1) vx = -speed; 
                else if (player.getDirection() == 2) vx = speed;  
                else if (player.getDirection() == 3) vy = -speed; 
            }

            // Tambah Bullet baru ke list
            bullets.add(new Bullet(player.getX() + 24, player.getY() + 24, vx, vy, false)); 
            
            // Kurangi ammo lewat method
            player.decreaseAmmo();
            requestSound(1); 
        }
    }

    // UPDATE GAME LOOP
    public void update() {
        if (isGameOver) return;
        tickCount++; 
        if (damageCooldown > 0) damageCooldown--;

        // Cek Ganti Wave
        if (enemies.isEmpty() && enemiesSpawned >= maxEnemiesInWave) {
            wave++; enemiesSpawned = 0; maxEnemiesInWave = 12 + (wave * 4); 
            generateRandomObstacles(); 
        }
        if (enemiesSpawned < maxEnemiesInWave && enemies.size() < 15) { 
            if (tickCount % 50 == 0 || enemies.isEmpty()) spawnEnemyBasedOnWave();
        }

        // --- UPDATE PLAYER ---
        double dx = 0; double dy = 0; int baseSpeed = 6; 
        if (player.isUp)    dy -= 1; if (player.isDown)  dy += 1;
        if (player.isLeft)  dx -= 1; if (player.isRight) dx += 1;
        if (dx != 0 && dy != 0) { dx *= 0.7071; dy *= 0.7071; }
        
        double nextX = player.getX() + (dx * baseSpeed);
        double nextY = player.getY() + (dy * baseSpeed);

        // Update Posisi X dengan Cek Tabrakan
        if (dx * baseSpeed != 0) {
            if (!cekTabrakan(nextX, player.getY())) player.setX(nextX);
            // Set arah hadap
            if (dx > 0) player.setDirection(2); else if (dx < 0) player.setDirection(1);
        }
        // Update Posisi Y dengan Cek Tabrakan
        if (dy * baseSpeed != 0) {
            if (!cekTabrakan(player.getX(), nextY)) player.setY(nextY);
            if (dy > 0) player.setDirection(0); else if (dy < 0) player.setDirection(3);
        }
        
        // Batas Layar
        if (player.getX() < 0) player.setX(0); 
        if (player.getX() > worldWidth - 64) player.setX(worldWidth - 64);
        if (player.getY() < 0) player.setY(0); 
        if (player.getY() > worldHeight - 64) player.setY(worldHeight - 64);

        // --- UPDATE BULLETS ---
        for (Bullet b : bullets) {
            if (!b.isActive()) continue;
            b.move(); // Panggil method move() dari class Bullet

            boolean hitWall = false;
            // Cek Keluar Layar (Pakai getter)
            if (b.getX() < -50 || b.getX() > worldWidth + 50 || b.getY() < -50 || b.getY() > worldHeight + 50) hitWall = true;
            // Cek Tabrakan Objek
            for (Rectangle wall : boundaries) if (b.getBounds().intersects(wall)) hitWall = true;
            for (Rectangle rock : obstacles) if (b.getBounds().intersects(rock)) hitWall = true;

            if (hitWall) {
                b.setActive(false);
                // Jika peluru musuh nabrak tembok -> Dapat Ammo
                if (b.isEnemyBullet()) {
                    peluruMeleset++;
                    player.addAmmo(1);
                }
            }
        }

        // --- UPDATE ENEMIES ---
        Random rng = new Random();
        List<Enemy> deadEnemies = new ArrayList<>();
        
        for (Enemy e : enemies) {
            // AI Movement (Kejar Player)
            if (e.getX() < player.getX()) { e.setX(e.getX() + e.getSpeed()); e.setFacingRight(true); }
            if (e.getX() > player.getX()) { e.setX(e.getX() - e.getSpeed()); e.setFacingRight(false); }
            if (e.getY() < player.getY()) e.setY(e.getY() + e.getSpeed());
            if (e.getY() > player.getY()) e.setY(e.getY() - e.getSpeed());

            // AI Shoot
            double diffX = (player.getX() + 24) - (e.getX() + 24); 
            double diffY = (player.getY() + 24) - (e.getY() + 24); 
            double distance = Math.sqrt((diffX * diffX) + (diffY * diffY));

            if (e.getShootCooldown() > 0) { 
                e.updateCooldown();
            } else {
                boolean insideMap = (e.getX() > 50 && e.getX() < worldWidth - 50 && e.getY() > 50 && e.getY() < worldHeight - 50);
                if (insideMap && distance < 600) { 
                    double angle = Math.atan2(diffY, diffX);
                    double bSpeed = Math.min(15.0, 4.0 + (wave * 0.75));
                    
                    bullets.add(new Bullet(e.getX() + 24, e.getY() + 24, bSpeed * Math.cos(angle), bSpeed * Math.sin(angle), true));
                    e.resetCooldown();
                }
            }

            Rectangle rectEnemy = e.getBounds();
            // Cek Tabrakan Peluru
            for (Bullet b : bullets) {
                if (b.isActive() && !b.isEnemyBullet() && rectEnemy.intersects(b.getBounds())) {
                    b.setActive(false);
                    deadEnemies.add(e);
                    skor += 10;
                    if (rng.nextInt(100) < 40) powerUps.add(new PowerUp(e.getX(), e.getY()));
                }
                
                if (b.isActive() && b.isEnemyBullet()) {
                     Rectangle pRect = new Rectangle((int)player.getX()+8, (int)player.getY()+8, 48, 48);
                     if (pRect.intersects(b.getBounds())) {
                         b.setActive(false);
                         if (damageCooldown == 0) { 
                             player.takeDamage(0.5); // Kurangi darah player
                             damageCooldown = 60; 
                             if (player.getHealth() <= 0) isGameOver = true; 
                         }
                     }
                }
            }
            
            // Cek Tabrakan Badan Musuh
            if (damageCooldown == 0 && rectEnemy.intersects(new Rectangle((int)player.getX()+8, (int)player.getY()+8, 48, 48))) {
                player.takeDamage(1.0); 
                damageCooldown = 60; 
                if (player.getHealth() <= 0) isGameOver = true;
            }
        }

        // Cek PowerUp
        for (PowerUp p : powerUps) {
            if (p.isActive() && new Rectangle((int)player.getX(), (int)player.getY(), 64, 64).intersects(p.getBounds())) {
                p.setActive(false); 
                player.heal(1.0); // Tambah darah player
            }
        }

        // Bersihkan List
        bullets.removeIf(b -> !b.isActive()); 
        powerUps.removeIf(p -> !p.isActive()); 
        enemies.removeAll(deadEnemies);
    }
    
    // Spawn Musuh
    private void spawnEnemyBasedOnWave() {
        Random rng = new Random();
        double speed = Math.min(8.0, 2.0 + ((wave - 1) * 0.25));
        enemies.add(new Enemy(50 + rng.nextInt(worldWidth - 100), worldHeight + 60, speed, rng.nextInt(3)));
        enemiesSpawned++; 
    }
    
    // Getter List untuk View
    public List<Rectangle> getObstacles() { return obstacles; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<Bullet> getBullets() { return bullets; }
    public List<PowerUp> getPowerUps() { return powerUps; }
}