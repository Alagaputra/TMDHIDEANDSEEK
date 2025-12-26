package model;

// INHERITANCE: Bullet juga adalah GameObject.
public class Bullet extends GameObject {
    
    private double velX, velY;       // Kecepatan Horizontal & Vertikal
    private boolean isActive = true; // Status aktif. Jika false, akan dihapus dari memori.
    private boolean isEnemyBullet;   // Penanda: Apakah ini peluru musuh atau player?

    public Bullet(double x, double y, double velX, double velY, boolean isEnemyBullet) {
        super(x, y, 20, 20); // Set ukuran peluru 20x20
        this.velX = velX;
        this.velY = velY;
        this.isEnemyBullet = isEnemyBullet;
    }

    // BEHAVIOR: Method khusus untuk menggerakkan peluru.
    public void move() {
        this.x += velX;
        this.y += velY;
    }

    // Getter & Setter
    public double getVelX() { return velX; }
    public double getVelY() { return velY; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    public boolean isEnemyBullet() { return isEnemyBullet; }
}