package model;

public class Bullet extends GameObject {
    private double velX, velY; // Vektor kecepatan (Velocity)
    private boolean isActive = true; // Status aktif (jika false akan dihapus)
    private boolean isEnemyBullet;   // Penanda: Punya musuh atau player?

    public Bullet(double x, double y, double velX, double velY, boolean isEnemyBullet) {
        super(x, y, 20, 20); // Ukuran peluru 20x20
        this.velX = velX;
        this.velY = velY;
        this.isEnemyBullet = isEnemyBullet;
    }

    // Method untuk menggerakkan peluru (Behavior)
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