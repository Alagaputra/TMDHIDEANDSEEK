package model;

public class PowerUp extends GameObject {
    private boolean isActive = true;

    public PowerUp(double x, double y) {
        super(x, y, 24, 24); // Ukuran potion
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
}