package model;

public class TBenefit {
    private int id;
    private String username;
    private int skor;
    private int peluruMeleset;
    private int sisaPeluru;

    public TBenefit(int id, String username, int skor, int peluruMeleset, int sisaPeluru) {
        this.id = id;
        this.username = username;
        this.skor = skor;
        this.peluruMeleset = peluruMeleset;
        this.sisaPeluru = sisaPeluru;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public int getSkor() { return skor; }
    public int getPeluruMeleset() { return peluruMeleset; }
    public int getSisaPeluru() { return sisaPeluru; }
}