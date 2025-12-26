package model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameState {
    public final int worldWidth = 1200;  
    public final int worldHeight = 900; 

    public int playerX = 600; 
    public int playerY = 450; 
    public int direction = 0; 
    public double health = 3.0;      
    public int playerAmmo = 0; // Awal main 0 peluru (Sesuai PDF)
    
    public int wave = 1;                
    public int enemiesSpawned = 0;      
    public int maxEnemiesInWave = 12;   
    
    public boolean isGameOver = false;
    public int skor = 0;
    public int peluruMeleset = 0; 

    public long tickCount = 0;  
    public boolean isUp, isDown, isLeft, isRight; 

    // --- SOUND SYSTEM ---
    public List<Integer> soundRequests = new ArrayList<>();

    public class Bullet {
        public double x, y, velX, velY; 
        public boolean isActive = true;
        public boolean isEnemyBullet; 
        public Bullet(double x, double y, double velX, double velY, boolean isEnemyBullet) {
            this.x = x; this.y = y; this.velX = velX; this.velY = velY; this.isEnemyBullet = isEnemyBullet;
        }
    }

    public class PowerUp {
        public int x, y;
        public boolean isActive = true;
        public int width = 24; public int height = 24;
        public PowerUp(int x, int y) { this.x = x; this.y = y; }
    }

    public class Enemy {
        public double x, y, speed; 
        public int type;
        public boolean isFacingRight = true;
        public int shootCooldown = 0; 
        
        public Enemy(double x, double y, double baseSpeed, int type) {
            this.x = x; this.y = y; this.type = type;
            if (type == 0) this.speed = baseSpeed;
            else if (type == 1) this.speed = baseSpeed + 1.5; 
            else if (type == 2) {
                this.speed = baseSpeed - 0.8; 
                if (this.speed < 1.0) this.speed = 1.0; 
            }
            this.shootCooldown = 10 + new Random().nextInt(20); 
        }
    }

    public List<Enemy> enemies = new ArrayList<>();
    public List<Bullet> bullets = new ArrayList<>();
    public List<PowerUp> powerUps = new ArrayList<>(); 
    public List<Rectangle> boundaries = new ArrayList<>(); 
    public List<Rectangle> obstacles = new ArrayList<>();  
    private int damageCooldown = 0; 

    public GameState() {
        setupForestMap();
        generateRandomObstacles(); 
    }
    
    public void requestSound(int id) {
        soundRequests.add(id);
    }

    private void setupForestMap() {
        boundaries.clear();
        boundaries.add(new Rectangle(0, 0, 50, worldHeight));            
        boundaries.add(new Rectangle(worldWidth - 50, 0, 50, worldHeight)); 
        boundaries.add(new Rectangle(0, 0, worldWidth, 50));             
        boundaries.add(new Rectangle(0, worldHeight - 50, worldWidth, 50)); 
    }

    private void generateRandomObstacles() {
        Random rand = new Random();
        obstacles.clear();
        int jumlahPohon = 12; int ukuran = 40; 
        int count = 0; int safety = 0;
        
        while (count < jumlahPohon && safety < 5000) {
            safety++;
            int rx = 80 + rand.nextInt(worldWidth - 160); 
            int ry = 80 + rand.nextInt(worldHeight - 160);
            double distToPlayer = Math.sqrt(Math.pow(rx - playerX, 2) + Math.pow(ry - playerY, 2));
            if (distToPlayer < 120) continue; 
            
            boolean terlaluDekat = false;
            for (Rectangle existing : obstacles) {
                double distToTree = Math.sqrt(Math.pow(rx - existing.x, 2) + Math.pow(ry - existing.y, 2));
                if (distToTree < 150) { terlaluDekat = true; break; }
            }
            if (terlaluDekat) continue;
            obstacles.add(new Rectangle(rx, ry, ukuran, ukuran));
            count++;
        }
    }

    private boolean cekTabrakan(int x, int y) {
        Rectangle futureObj = new Rectangle(x + 16, y + 16, 32, 32); 
        for (Rectangle wall : boundaries) { if (futureObj.intersects(wall)) return true; }
        for (Rectangle rock : obstacles) { if (futureObj.intersects(rock)) return true; }
        return false; 
    }

    public void playerShoot() {
        if (playerAmmo > 0) { 
            double speed = 10.0 + (wave * 1.5);
            if (speed > 25.0) speed = 25.0;

            double vx = 0, vy = 0;
            if (isUp)    vy -= speed; if (isDown)  vy += speed;
            if (isLeft)  vx -= speed; if (isRight) vx += speed;
            if (vx != 0 && vy != 0) { vx *= 0.707; vy *= 0.707; }
            
            if (vx == 0 && vy == 0) {
                if (direction == 0) vy = speed;      
                else if (direction == 1) vx = -speed; 
                else if (direction == 2) vx = speed;  
                else if (direction == 3) vy = -speed; 
            }

            bullets.add(new Bullet(playerX + 24, playerY + 24, vx, vy, false)); 
            playerAmmo--; 
            
            requestSound(1); // Suara Tembak Player
        }
    }

    public void update() {
        if (isGameOver) return;
        tickCount++; 
        if (damageCooldown > 0) damageCooldown--;

        if (enemies.isEmpty() && enemiesSpawned >= maxEnemiesInWave) {
            wave++; enemiesSpawned = 0; maxEnemiesInWave = 12 + (wave * 4); 
            generateRandomObstacles(); 
            System.out.println("WAVE " + wave + " STARTED!");
        }

        if (enemiesSpawned < maxEnemiesInWave && enemies.size() < 15) { 
            if (tickCount % 50 == 0 || enemies.isEmpty()) spawnEnemyBasedOnWave();
        }

        // --- PLAYER MOVEMENT ---
        double dx = 0; double dy = 0; int baseSpeed = 6; 
        if (isUp)    dy -= 1; if (isDown)  dy += 1;
        if (isLeft)  dx -= 1; if (isRight) dx += 1;
        if (dx != 0 && dy != 0) { dx *= 0.7071; dy *= 0.7071; }
        int velX = (int) (dx * baseSpeed); int velY = (int) (dy * baseSpeed);

        if (velX != 0) {
            if (!cekTabrakan(playerX + velX, playerY)) playerX += velX;
            if (velX > 0) direction = 2; if (velX < 0) direction = 1; 
        }
        if (velY != 0) {
            if (!cekTabrakan(playerX, playerY + velY)) playerY += velY;
            if (velY > 0) direction = 0; if (velY < 0) direction = 3; 
        }
        if (playerX < 0) playerX = 0; if (playerX > worldWidth - 64) playerX = worldWidth - 64;
        if (playerY < 0) playerY = 0; if (playerY > worldHeight - 64) playerY = worldHeight - 64;

        // --- BULLETS LOGIC ---
        for (Bullet b : bullets) {
            if (!b.isActive) continue;
            b.x += b.velX; b.y += b.velY;

            boolean hitWall = false;
            // Cek keluar batas layar
            if (b.x < -50 || b.x > worldWidth + 50 || b.y < -50 || b.y > worldHeight + 50) hitWall = true;
            
            // Cek tabrakan dinding/pohon
            for (Rectangle wall : boundaries) { if (new Rectangle((int)b.x, (int)b.y, 30, 30).intersects(wall)) hitWall = true; }
            for (Rectangle rock : obstacles) { if (new Rectangle((int)b.x, (int)b.y, 30, 30).intersects(rock)) hitWall = true; }

            if (hitWall) {
                b.isActive = false;
                
                // LOGIKA: Jika peluru MUSUH nabrak tembok/pohon = Player Jago Menghindar
                if (b.isEnemyBullet) {
                    peluruMeleset++; // Statistik naik
                    playerAmmo++;    // Dapat bonus peluru
                    // requestSound(4); // (Opsional) Suara bonus
                }
            }
        }

        // --- ENEMIES LOGIC ---
        Random rng = new Random();
        List<Enemy> deadEnemies = new ArrayList<>();
        
        for (Enemy e : enemies) {
            // Pergerakan Musuh
            if (e.x < playerX) { e.x += e.speed; e.isFacingRight = true; }
            if (e.x > playerX) { e.x -= e.speed; e.isFacingRight = false; }
            if (e.y < playerY) e.y += e.speed;
            if (e.y > playerY) e.y -= e.speed;

            // Logika Tembak
            double diffX = (playerX + 24) - (e.x + 24); 
            double diffY = (playerY + 24) - (e.y + 24); 
            double distance = Math.sqrt((diffX * diffX) + (diffY * diffY));

            if (e.shootCooldown > 0) { 
                e.shootCooldown--; 
            } else {
                boolean insideMap = (e.x > 50 && e.x < worldWidth - 50 && e.y > 50 && e.y < worldHeight - 50);
                if (insideMap && distance < 600) { 
                    double angle = Math.atan2(diffY, diffX);
                    double bSpeed = 4.0 + (wave * 0.75); 
                    if (bSpeed > 15.0) bSpeed = 15.0;
                    
                    bullets.add(new Bullet(e.x + 24, e.y + 24, bSpeed * Math.cos(angle), bSpeed * Math.sin(angle), true));
                    e.shootCooldown = 30 + rng.nextInt(30); 
                    // TIDAK ADA requestSound(1) di sini -> Musuh menembak bisu (biar gak berisik)
                }
            }

            // Tabrakan Musuh vs Peluru
            Rectangle rectEnemy = new Rectangle((int)e.x, (int)e.y, 50, 50);
            
            for (Bullet b : bullets) {
                // Peluru Player kena Musuh
                if (b.isActive && !b.isEnemyBullet && rectEnemy.intersects(new Rectangle((int)b.x, (int)b.y, 30, 30))) {
                    b.isActive = false; 
                    deadEnemies.add(e); 
                    skor += 10;
                    if (rng.nextInt(100) < 40) powerUps.add(new PowerUp((int)e.x, (int)e.y));
                }
                
                // Peluru Musuh kena Player
                if (b.isActive && b.isEnemyBullet) {
                     if (new Rectangle(playerX + 8, playerY + 8, 48, 48).intersects(new Rectangle((int)b.x, (int)b.y, 30, 30))) {
                         b.isActive = false;
                         if (damageCooldown == 0) { 
                             health -= 0.5; 
                             damageCooldown = 60; 
                             if (health <= 0) isGameOver = true; 
                         }
                     }
                }
            }
            
            // Tabrakan Badan Musuh kena Player
            if (damageCooldown == 0 && rectEnemy.intersects(new Rectangle(playerX + 8, playerY + 8, 48, 48))) {
                health -= 1.0; 
                damageCooldown = 60; 
                if (health <= 0) isGameOver = true;
            }
        }

        // Ambil PowerUp (Potion)
        for (PowerUp p : powerUps) {
            if (p.isActive && new Rectangle(playerX, playerY, 64, 64).intersects(new Rectangle(p.x, p.y, p.width, p.height))) {
                p.isActive = false; 
                if (health < 3.0) health += 1.0; 
            }
        }

        bullets.removeIf(b -> !b.isActive); 
        powerUps.removeIf(p -> !p.isActive); 
        enemies.removeAll(deadEnemies);
    }
    
    private void spawnEnemyBasedOnWave() {
        Random rng = new Random();
        double baseSpeed = 2.0; double speedIncrease = (wave - 1) * 0.25; 
        double calculatedSpeed = baseSpeed + speedIncrease;
        if (calculatedSpeed > 8.0) calculatedSpeed = 8.0; 
        
        int ey = worldHeight + 60; // Muncul dari bawah layar
        int ex = 50 + rng.nextInt(worldWidth - 100);
        
        enemies.add(new Enemy(ex, ey, calculatedSpeed, rng.nextInt(3)));
        enemiesSpawned++; 
    }

    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }
}