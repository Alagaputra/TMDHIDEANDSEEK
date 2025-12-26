package model;

public class Player extends GameObject {
    private int direction = 0; 
    private double health = 3.0;
    private int ammo = 0;      
    
    public boolean isUp, isDown, isLeft, isRight;

    public Player(double x, double y) {
        super(x, y, 64, 64); 
    }

    public void addAmmo(int amount) { 
        this.ammo += amount; 
    }
    
    public void decreaseAmmo() { 
        if (this.ammo > 0) this.ammo--; 
    }
    
    public void takeDamage(double dmg) {
        this.health -= dmg;
        if (this.health < 0) this.health = 0; 
    }
    
    public void heal(double amount) {
        this.health += amount;
        if (this.health > 3.0) this.health = 3.0; 
    }

    public int getDirection() { return direction; }
    public void setDirection(int direction) { this.direction = direction; }
    
    public double getHealth() { return health; }
    public int getAmmo() { return ammo; }
}