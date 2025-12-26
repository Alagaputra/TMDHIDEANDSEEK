package model;

import java.util.Random;

// Inheritance: Enemy juga turunan GameObject
public class Enemy extends GameObject {
    private double speed;         // Kecepatan gerak
    private int type;             // Tipe musuh (menentukan gambar & sifat)
    private boolean isFacingRight = true; // Arah hadap untuk animasi
    private int shootCooldown = 0; // Timer agar musuh tidak menembak terus-menerus

    public Enemy(double x, double y, double baseSpeed, int type) {
        super(x, y, 50, 50); // Ukuran musuh 50x50
        this.type = type;
        
        // Polymorphism (Bentuk banyak): Beda tipe, beda kecepatan
        if (type == 0) {
            this.speed = baseSpeed;          // Normal
        } else if (type == 1) {
            this.speed = baseSpeed + 1.5;    // Cepat (Runner)
        } else if (type == 2) {
            this.speed = Math.max(1.0, baseSpeed - 0.8); // Lambat (Tank)
        }
        
        // Randomize waktu tembak awal
        this.shootCooldown = 10 + new Random().nextInt(20);
    }

    // Method untuk mengurangi timer cooldown
    public void updateCooldown() {
        if (shootCooldown > 0) shootCooldown--;
    }
    
    // Reset timer setelah menembak
    public void resetCooldown() {
        this.shootCooldown = 30 + new Random().nextInt(30);
    }

    // Getter & Setter
    public double getSpeed() { return speed; }
    public int getType() { return type; }
    public boolean isFacingRight() { return isFacingRight; }
    public void setFacingRight(boolean facing) { this.isFacingRight = facing; }
    public int getShootCooldown() { return shootCooldown; }
}