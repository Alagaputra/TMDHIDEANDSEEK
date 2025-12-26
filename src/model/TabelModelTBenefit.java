package model;

import java.util.List;
import javax.swing.table.AbstractTableModel;

public class TabelModelTBenefit extends AbstractTableModel {
    private List<TBenefit> list; // Menyimpan list data player
    
    public TabelModelTBenefit(List<TBenefit> list) {
        this.list = list;
    }

    @Override
    public int getRowCount() {
        return list.size(); // Jumlah baris = jumlah player
    }

    @Override
    public int getColumnCount() {
        return 4; // Kita mau menampilkan 4 kolom saja
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // Mengambil data berdasarkan posisi kolom
        switch (columnIndex) {
            case 0: return list.get(rowIndex).getUsername();
            case 1: return list.get(rowIndex).getSkor();
            case 2: return list.get(rowIndex).getPeluruMeleset();
            case 3: return list.get(rowIndex).getSisaPeluru();
            default: return null;
        }
    }
    
    @Override
    public String getColumnName(int column) {
        // Judul Kolom Tabel
        switch (column) {
            case 0: return "Username";
            case 1: return "Skor";
            case 2: return "Peluru Meleset";
            case 3: return "Sisa Peluru";
            default: return null;
        }
    }
}