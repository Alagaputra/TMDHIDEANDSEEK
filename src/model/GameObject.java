package model;

import java.awt.Rectangle;

// Abstract Class: Konsep Abstraksi. Kelas ini tidak bisa dibuat langsung, harus diturunkan.
public abstract class GameObject {
    // Protected: Hanya bisa diakses oleh kelas ini dan anak-anaknya (Inheritance)
    protected double x, y;      // Koordinat posisi objek
    protected int width, height; // Ukuran dimensi objek

    // Constructor: Dijalankan saat objek pertama kali dibuat
    public GameObject(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // --- ENCAPSULATION (Pembungkusan Data) ---
    // Getter: Untuk mengambil nilai variabel private/protected
    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    // Setter: Untuk mengubah nilai variabel
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    // Method umum untuk mendapatkan area tabrakan (Hitbox)
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
}