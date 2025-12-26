package model;
import java.awt.Rectangle;

// ABSTRACT CLASS: Kelas dasar yang tidak bisa dibuat langsung (di-instansiasi).
// Harus diturunkan ke kelas lain (seperti Player, Enemy, Bullet).
public abstract class GameObject {
    
    // PROTECTED: Variabel ini hanya bisa diakses oleh kelas ini dan anak-anaknya (Inheritance).
    // Menggunakan tipe data 'double' agar pergerakan lebih halus (presisi).
    protected double x, y;      
    protected int width, height; 

    // CONSTRUCTOR: Dijalankan pertama kali saat objek dibuat.
    public GameObject(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // --- ENCAPSULATION (Pembungkusan Data) ---
    // Getter: Membiarkan kelas lain membaca data tanpa mengubahnya langsung.
    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    // Setter: Membiarkan kelas lain mengubah posisi objek secara aman.
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    // METHOD UMUM: Mendapatkan area kotak (Hitbox) untuk deteksi tabrakan.
    // Setiap objek punya hitbox sesuai posisi (x,y) dan ukurannya (width, height).
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
}