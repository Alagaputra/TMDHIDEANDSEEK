package model;

import java.util.Random;

// INHERITANCE: Enemy mewarisi semua sifat GameObject (punya x, y, width, height).
public class Enemy extends GameObject {
    
    // ENCAPSULATION: Data khusus musuh dibuat private.
    private double speed;          // Kecepatan gerak
    private int type;              // Tipe musuh (0, 1, atau 2)
    private boolean isFacingRight = true; // Arah hadap (untuk animasi gambar)
    private int shootCooldown = 0; // Timer jeda tembakan

    public Enemy(double x, double y, double baseSpeed, int type) {
        super(x, y, 50, 50); // Panggil constructor Induk (GameObject), set ukuran 50x50.
        this.type = type;
        
        // POLYMORPHISM (Banyak Bentuk): 
        // Perilaku musuh (kecepatan) berubah sesuai tipenya.
        if (type == 0) {
            this.speed = baseSpeed;          // Normal
        } else if (type == 1) {
            this.speed = baseSpeed + 1.5;    // Tipe 1: Lebih Cepat (Runner)
        } else if (type == 2) {
            // Tipe 2: Lebih Lambat tapi biasanya lebih kuat (Tank)
            // Math.max memastikan kecepatan tidak sampai 0 atau minus.
            this.speed = Math.max(1.0, baseSpeed - 0.8); 
        }
        
        // Randomize waktu tembak awal agar musuh tidak menembak serentak saat spawn.
        this.shootCooldown = 10 + new Random().nextInt(20);
    }

    // Method untuk mengurangi timer cooldown setiap frame.
    public void updateCooldown() {
        if (shootCooldown > 0) shootCooldown--;
    }
    
    // Reset timer setelah musuh menembak (memberi jeda acak).
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