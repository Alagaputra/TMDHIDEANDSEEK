package main;

import view.GameWindow;
import view.GamePanel;
import view.MenuPanel;
import presenter.GamePresenter;

public class Main {
    public static void main(String[] args) {
        // 1. SETUP VIEW: Membuat objek tampilan
        MenuPanel menu = new MenuPanel();
        GamePanel game = new GamePanel();

        // 2. SETUP WINDOW: Memasukkan tampilan ke jendela utama
        GameWindow window = new GameWindow(menu, game);

        // 3. SETUP PRESENTER: Menghubungkan logika (Model) dengan tampilan (View)
        GamePresenter presenter = new GamePresenter(window, menu, game);
        
        // 4. INIT: Memuat data awal dari database
        presenter.loadTableData();
        
        // 5. START: Tampilkan menu utama
        window.showMenu();
    }
}