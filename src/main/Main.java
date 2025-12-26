// saya Ajipati Alaga Putra dengan NIM 2409682
// mengerjakan UTS dalam mata kuliah DPBO
// untuk keberkahannya maka saya tidak akan melakukan kecurangan
// sepertu yang telah di spesifikasikan Aamiin.


// CREDIT GAME ASSET
// MAP.............(GEMINI AI)
// CHARACTER.............(https://elvgames.itch.io/free-fantasy-dreamland-sprites)
// BULLET(PELURU).............(https://craftpix.net/freebies/free-water-and-fire-magic-sprite-vector-pack/)
// OBSTACLE(RINTANGAN).............(https://anokolisa.itch.io/free-pixel-art-asset-pack-topdown-tileset-rpg-16x16-sprites)
// MONSTER.............(https://deepdivegamestudio.itch.io/demon-sprite-pack)
// MUSIC.............(https://pixabay.com/id/music/petualangan-pixel-quest-364092/)
// SOUND EFECT TEMBAKAN.............(https://leohpaz.itch.io/50-rpg-battle-magic-sfx)
// POTION.............(GEMINI AI)
// HEALTH POINT ICON.............(https://elvgames.itch.io/free-inventory-asset-pack)

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