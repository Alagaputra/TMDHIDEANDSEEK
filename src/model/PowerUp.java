package model;

public class PowerUp extends GameObject {
    private boolean isActive = true; // Status aktif (belum diambil)

    public PowerUp(double x, double y) {
        super(x, y, 24, 24); // Set ukuran Potion 24x24
    }

    // Getter & Setter
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
}