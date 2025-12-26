package main;

import view.GameWindow;
import view.GamePanel;
import view.MenuPanel;
import presenter.GamePresenter;

public class Main {
    public static void main(String[] args) {
        // 1. Setup Panel (Menu & Game)
        MenuPanel menu = new MenuPanel();
        GamePanel game = new GamePanel();

        // 2. Setup Window (Masukkan kedua panel ke dalam Frame)
        GameWindow window = new GameWindow(menu, game);

        // 3. Setup Presenter (Sambungkan logika controller)
        GamePresenter presenter = new GamePresenter(window, menu, game);
        
        // 4. Load Data Database Awal
        presenter.loadTableData();
        
        // 5. Tampilkan Menu Awal
        window.showMenu();
    }
}