# Hide and Seek The Challenge

Game 2D petualangan *top-down shooter* berbasis **Java Swing** dengan arsitektur **MVP (Model-View-Presenter)** dan koneksi database **MySQL**.

Dibuat oleh **Ajipati Alaga Putra (2409682)** sebagai Tugas Mata Kuliah DPBO.

---

## Daftar Isi

- [Gameplay & Mekanik](#gameplay--mekanik)
- [Kontrol](#kontrol)
- [Alur Game](#alur-game)
- [OOP Concepts](#oop-concepts)
- [Arsitektur MVP](#arsitektur-mvp)
- [Struktur Folder](#struktur-folder)
- [Penjelasan Class](#penjelasan-class)
- [Database](#database)
- [Sistem Audio](#sistem-audio)
- [Cara Menjalankan](#cara-menjalankan)
- [Credits](#credits)

---

## Gameplay & Mekanik

### Pemain (Player)
- Memiliki **3 nyawa** (health point), direpresentasikan dengan icon hati (full, half, empty).
- Bergerak 8 arah (WASD / Arrow) dengan kecepatan tetap.
- Jika terkena tembakan musuh: kehilangan **0.5 HP**, cooldown invincibility **60 frame** (~1 detik).
- Jika bertabrakan tubuh dengan musuh: kehilangan **1 HP**.
- Nyawa bisa dipulihkan dengan mengambil **Power-Up Potion** (mengembalikan **1 HP**, max 3 HP).

### Sistem Ammo & Menembak
- Pemain menembak dengan tombol **Z**.
- Arah tembakan mengikuti arah gerakan terakhir / arah hadap.
- Kecepatan peluru meningkat setiap wave: `10 + (wave * 1.5)`, maksimal **25**.
- Ammo awal sesuai sisa peluru dari database (user lama) atau 0 (user baru).
- Ammo bertambah otomatis saat peluru musuh mengenai tembok/pohon.

### Musuh (Enemy)
- Muncul dari **bawah layar** (y = worldHeight + 60).
- **AI**: Mengejar pemain secara langsung (membandingkan posisi x dan y).
- Menembak jika jarak ke pemain < **600px** dan berada dalam map.
- Terdapat **3 tipe musuh** dengan perilaku berbeda (Polymorphism):

| Tipe | Nama | Kecepatan | Karakteristik |
|------|------|-----------|---------------|
| 0 | **Normal** | `baseSpeed` | Kecepatan standar |
| 1 | **Runner** | `baseSpeed + 1.5` | Lebih cepat, agresif |
| 2 | **Tank** | `max(1.0, baseSpeed - 0.8)` | Lambat tapi tidak mudah dihalau |

- Base speed musuh meningkat setiap wave: `min(8.0, 2.0 + ((wave - 1) * 0.25))`.
- Cooldown tembak musuh: acak antara 30-60 frame setelah menembak.

### Sistem Wave
- **Wave 1**: 12 musuh.
- Wave berikutnya: `12 + (wave * 4)` musuh.
- Maksimal 15 musuh aktif di layar dalam satu waktu.
- Spawn bertahap: setiap 50 frame spawn 1 musuh.
- Saat wave baru: posisi pohon/power-up diacak ulang.

### Skor & Statistik
- **+10 skor** setiap berhasil mengalahkan musuh.
- **Peluru Meleset**: bertambah jika peluru musuh mengenai tembok/pohon.
- Saat peluru musuh meleset, pemain mendapat **+1 ammo** (mekanik dari spesifikasi tugas).
- Skor tersimpan di database per username.

### Power-Up
- **40% chance** muncul dari musuh yang dikalahkan.
- Berupa potion merah yang melayang (animasi float dengan sinus).
- Mengambilnya memulihkan **1 HP**.

### Sistem Kamera
- Kamera mengikuti pemain dengan offset tengah (400, 300).
- Terbatas pada dunia game berukuran **1200x900** pixel.
- Scroll halus tanpa interpolasi (snap).

### Collision Detection
- Deteksi tabrakan menggunakan `Rectangle.intersects()`.
- Player tidak bisa menembus **tembok batas** (boundaries) dan **pohon** (obstacles).
- Pohon digambar dengan **Y-sorting** (objek di bawah tampil di depan) agar efek 3D semu.
- Peluru player & musuh dihancurkan saat mengenai tembok, batas layar, atau pohon.

### Game Loop
- Berjalan di `javax.swing.Timer` dengan delay **16ms** (~60 FPS).
- Setiap tick: update posisi player, update peluru, AI musuh, spawn musuh, cek collision, garbage collection.

### Procedural Obstacle Generation
- 12 pohon ditempatkan secara acak dengan jaminan:
  - Jarak minimal **120px** dari posisi awal player.
  - Jarak minimal **150px** antar pohon.
- Posisi diacak ulang setiap wave baru.

---

## Kontrol

| Tombol | Aksi |
|--------|------|
| `W` / `↑` | Gerak ke atas |
| `S` / `↓` | Gerak ke bawah |
| `A` / `←` | Gerak ke kiri |
| `D` / `→` | Gerak ke kanan |
| `Z` | Tembak |
| `R` | Restart game (saat Game Over) |
| `SPACE` | Kembali ke menu utama (kapan saja) |

---

## Alur Game

```
Main.java
  │
  ├── Buat MenuPanel dan GamePanel
  ├── Buat GameWindow (CardLayout: "MENU" ↔ "GAME")
  ├── Buat GamePresenter (menghubungkan Model & View)
  ├── loadTableData() → tampilkan leaderboard dari DB
  └── window.showMenu()
        │
        ├── User input username, klik PLAY
        │     │
        │     ├── Cek username di database
        │     │   ├── User baru → INSERT data baru (ammo = 0)
        │     │   └── User lama → SELECT sisa peluru terakhir
        │     │
        │     ├── GameState baru dengan ammo dari DB
        │     ├── window.showGame()
        │     └── startGameLoop() (Timer 16ms)
        │           │
        │           └── Loop: gameState.update() → gamePanel.repaint()
        │                 ├── Update posisi player (input keyboard)
        │                 ├── Update peluru (move + collision)
        │                 ├── AI musuh (kejar + tembak)
        │                 ├── Update power-up (ambil)
        │                 ├── Cek game over (health ≤ 0)
        │                 ├── Spawn wave baru jika musuh habis
        │                 └── Garbage collection (buang objek mati)
        │
        └── SPACE → stopGame()
              ├── Simpan skor ke DB (UPDATE)
              └── Kembali ke menu
```

---

## OOP Concepts

### Inheritance (Pewarisan)

```
GameObject (abstract)
  ├── Player
  ├── Enemy
  ├── Bullet
  └── PowerUp
```

Semua entitas game mewarisi properti `x`, `y`, `width`, `height` dan method `getBounds()` dari `GameObject`.

### Abstract Class

`GameObject` adalah abstract class — tidak bisa diinstansiasi langsung, hanya sebagai kerangka bagi kelas turunannya.

### Encapsulation (Enkapsulasi)

Semua atribut dalam `GameObject`, `Player`, `Enemy`, `Bullet`, `PowerUp` menggunakan **protected/private** dengan getter dan setter publik.

### Polymorphism (Polimorfisme)

- **Enemy types**: 3 tipe musuh (`type 0/1/2`) dengan kecepatan berbeda di constructor — satu class `Enemy` bisa berperilaku berbeda.
- **Render sorting**: Semua objek game dirender dengan Y-sorting melalui `RenderItem` — player, enemy, dan obstacle diperlakukan seragam dalam satu List.
- **Bullet rendering**: Method `drawRotatedBullet()` digunakan untuk kedua jenis peluru (player dan enemy) dengan animasi sprite berbeda.

### Composition (Komposisi)

`GameState` "memiliki" (`has-a`) Player, List of Enemy, List of Bullet, List of PowerUp.

### Polymorphic List

Semua objek game dikelola lewat `List<Enemy>`, `List<Bullet>`, `List<PowerUp>` — struktur data dinamis yang memungkinkan penambahan/pengurangan objek saat runtime.

---

## Arsitektur MVP

```
┌─────────────────────────────────────────────────────────┐
│                       MAIN                              │
│              (Entry point / Controller)                  │
└──────────┬──────────────────────────────────┬───────────┘
           │                                  │
           ▼                                  ▼
┌─────────────────────┐         ┌─────────────────────────┐
│      VIEW           │         │        MODEL            │
│                     │         │                         │
│  GameWindow (JFrame)│◄────────┤  GameState              │
│  MenuPanel (Menu)   │         │    ├── Player           │
│  GamePanel (Render) │         │    ├── Enemy[]          │
│  Sound (Audio)      │         │    ├── Bullet[]         │
│                     │         │    ├── PowerUp[]        │
│                     │         │    ├── Obstacles[]      │
│                     │         │    └── Boundaries[]     │
│                     │         │                         │
│                     │         │  DB (MySQL)             │
│                     │         │  TBenefit (DTO)         │
│                     │         │  TabelModelTBenefit     │
└──────────┬──────────┘         └──────────┬──────────────┘
           │                               │
           │         ┌───────────┐         │
           └────────►│ PRESENTER │◄────────┘
                     │           │
                     │GamePresenter│
                     │           │
                     │ - handle input keyboard
                     │ - logika login
                     │ - save/load score
                     │ - game loop timer
                     └───────────┘
```

### Alur Data MVP

1. **View** (MenuPanel) menerima input username → **Presenter** memproses login.
2. **Presenter** membaca/menulis data ke **Model** (DB, GameState).
3. **Model** (GameState) mengupdate logic game (posisi, collision, wave).
4. **Presenter** menjalankan game loop (Timer) yang memanggil `GameState.update()` dan `GamePanel.repaint()`.
5. **View** (GamePanel) membaca data dari **Model** (GameState) untuk rendering.

---

## Struktur Folder

```
TMDHIDEANDSEEK/
├── .vscode/
│   └── settings.json               # Konfigurasi VS Code (referensi library)
├── bin/                             # Compiled .class files
├── lib/
│   └── mysql-connector-j-9.4.0.jar # JDBC MySQL Connector
├── src/
│   ├── main/
│   │   └── Main.java               # Entry point aplikasi
│   ├── model/
│   │   ├── GameObject.java         # Abstract class (induk semua entity game)
│   │   ├── Player.java             # Entity pemain
│   │   ├── Enemy.java              # Entity musuh (3 tipe)
│   │   ├── Bullet.java             # Entity peluru
│   │   ├── PowerUp.java            # Entity power-up (potion)
│   │   ├── GameState.java          # Logic game utama (update, collision, wave)
│   │   ├── DB.java                 # Koneksi & query MySQL
│   │   ├── TBenefit.java           # DTO/Model untuk tabel tbenefit
│   │   └── TabelModelTBenefit.java # Custom TableModel untuk JTable
│   ├── presenter/
│   │   └── GamePresenter.java      # Presenter (logic presentasi, input, timer)
│   ├── view/
│   │   ├── GameWindow.java         # JFrame utama dengan CardLayout
│   │   ├── MenuPanel.java          # Panel menu & leaderboard
│   │   ├── GamePanel.java          # Panel game (rendering 2D)
│   │   └── Sound.java              # Manajemen audio (play, loop, stop)
│   └── resources/
│       ├── images/                 # 25 file sprite & aset grafis
│       │   ├── map.png             # Background map
│       │   ├── Char_002_Idle.png   # Sprite sheet player (4 arah x 4 frame)
│       │   ├── enemy_[0-2].png     # Sprite sheet 3 tipe musuh
│       │   ├── Fire Arrow_Frame_0[1-8].png  # Animasi peluru player
│       │   ├── Water Arrow_Frame_0[1-8].png # Animasi peluru enemy
│       │   ├── Size_04.png         # Sprite sheet pohon
│       │   ├── potion_red.png      # Sprite sheet potion
│       │   ├── Health_04_Heart_Red.png  # Sprite sheet heart (5 frame)
│       │   └── Char_002.png        # Additional player sprite
│       └── audio/
│           ├── bgm.wav             # Musik latar
│           └── shoot.wav           # Efek suara tembakan
├── dbhide_seek.sql                 # Dump database MySQL
└── README.md
```

---

## Penjelasan Class

### `Main.java`
Entry point. Membuat instance MenuPanel, GamePanel, GameWindow, dan GamePresenter. Memanggil `loadTableData()` untuk mengisi leaderboard, lalu menampilkan menu.

### `GameObject.java` (Abstract Class)
Kelas abstrak sebagai induk semua entity game. Memiliki:
- `x, y` (double) — posisi dengan presisi floating point.
- `width, height` (int) — ukuran hitbox.
- `getBounds()` — mengembalikan `Rectangle` untuk deteksi tabrakan.

### `Player.java` (extends GameObject)
Entity pemain. Memiliki:
- `direction` (0=down, 1=left, 2=right, 3=up) — arah hadap untuk animasi.
- `health` (double, max 3.0) — nyawa.
- `ammo` (int) — jumlah peluru saat ini.
- `isUp, isDown, isLeft, isRight` (boolean) — state input keyboard.
- Method: `addAmmo()`, `decreaseAmmo()`, `takeDamage()`, `heal()`.

### `Enemy.java` (extends GameObject)
Entity musuh. Memiliki:
- `speed` — kecepatan gerak, bervariasi berdasarkan tipe.
- `type` (0/1/2) — menentukan perilaku (polymorphism via constructor).
- `isFacingRight` — arah hadap untuk flip sprite.
- `shootCooldown` — timer jeda antar tembakan.

### `Bullet.java` (extends GameObject)
Entity peluru. Memiliki:
- `velX, velY` — kecepatan di sumbu X dan Y.
- `isActive` — status aktif (false → akan dihapus).
- `isEnemyBullet` — pembeda peluru player vs musuh.
- Method `move()` — mengupdate posisi setiap frame.

### `PowerUp.java` (extends GameObject)
Entity power-up (potion). Memiliki `isActive` — status apakah sudah diambil pemain.

### `GameState.java`
Inti logic game. Mengelola:
- **World**: ukuran dunia 1200x900.
- **Player** (composition): satu instance player.
- **Enemies, Bullets, PowerUps**: List dinamis.
- **Boundaries** (4 tembok pinggir) dan **Obstacles** (12 pohon acak).
- **Wave system**: wave counter, spawn timer, scaling.
- **Collision detection**: player-obstacle, bullet-obstacle, bullet-enemy, bullet-player, enemy-player.
- **Garbage collection**: pembersihan objek mati tiap frame.
- **Sound requests**: queue sound effect ID yang dikirim ke view.

### `DB.java`
Koneksi MySQL via JDBC (Connector J 9.4.0). Method:
- `selectQuery()` → mengembalikan ResultSet.
- `updateQuery()` → INSERT/UPDATE/DELETE.
- `close()` → menutup koneksi.

### `TBenefit.java`
DTO (Data Transfer Object) untuk tabel `tbenefit`. Fields: id, username, skor, peluru_meleset, sisaPeluru.

### `TabelModelTBenefit.java`
Custom `AbstractTableModel` untuk menampilkan data leaderboard di JTable (4 kolom: Username, Skor, Peluru Meleset, Sisa Peluru).

### `GamePresenter.java`
Presenter dalam arsitektur MVP. Tugas:
- Menangani event tombol PLAY dan QUIT di menu.
- **Login flow**: validasi username, cek user baru/lama di DB, set ammo awal.
- KeyListener untuk input game (gerak, tembak, restart, kembali).
- **Game loop timer** (16ms) yang memanggil `update()` + `repaint()`.
- **Save score** ke DB saat game berhenti (SPACE).
- **Load table data** untuk leaderboard.

### `GameWindow.java`
JFrame utama menggunakan `CardLayout` untuk navigasi antara MenuPanel ("MENU") dan GamePanel ("GAME"). Method `showMenu()` dan `showGame()` untuk switching panel.

### `MenuPanel.java`
Panel menu utama dengan:
- Background gambar map.
- Judul game "HIDE AND SEEK THE CHALLENGE".
- Form input username.
- JTable leaderboard dengan fitur **auto-fill** (klik baris → username terisi otomatis).
- Tombol PLAY (hijau) dan QUIT (merah) dengan styling kustom.
- Mengoverride `paintComponent()` untuk menggambar background.

### `GamePanel.java`
Panel rendering game. Tugas:
- **Load semua aset grafis** (sprite sheet, map, pohon, potion, heart, dll).
- **Sistem kamera**: offset translate berdasarkan posisi player.
- **Y-sorting**: urutan render berdasarkan posisi Y untuk efek kedalaman.
- **Render semua entity**: background → power-up → obstacle/enemy/player (sorted) → bullets → HUD.
- **HUD**: nyawa (3 heart icon), skor, ammo, peluru meleset, wave, sisa musuh.
- **Game Over screen**: overlay hitam transparan dengan skor akhir dan instruksi.
- **Sound requests**: memproses queue sound dari GameState.
- **Animasi**: sprite sheet slicing (4 frame per arah), rotating bullet.

### `Sound.java`
Manajemen audio menggunakan `javax.sound.sampled`:
- Menyimpan URL file audio dalam array.
- Loading dari resource path atau file path (fallback untuk IDE).
- Konversi format audio ke PCM 16-bit (kompatibilitas).
- Volume musik latar dikurangi -15dB agar tidak mengganggu.
- Method: `play()`, `loop()`, `stop()`.

---

## Database

Database `dbhide_seek` dengan tabel `tbenefit`:

```sql
CREATE TABLE tbenefit (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  skor INT DEFAULT 0,
  peluru_meleset INT DEFAULT 0,
  sisa_peluru INT DEFAULT 0
);
```

| Kolom | Tipe | Deskripsi |
|-------|------|-----------|
| id | INT (PK, AUTO_INCREMENT) | ID unik |
| username | VARCHAR(50) | Nama pemain |
| skor | INT | Akumulasi skor |
| peluru_meleset | INT | Total peluru musuh yang meleset |
| sisa_peluru | INT | Sisa ammo saat terakhir bermain |

### Interaksi Database

| Aksi | Query |
|------|-------|
| Login (user baru) | `INSERT INTO tbenefit (username, skor, peluru_meleset, sisa_peluru) VALUES (...)`
| Login (user lama) | `SELECT sisa_peluru FROM tbenefit WHERE username = '...'`
| Simpan skor | `UPDATE tbenefit SET skor = skor + ..., peluru_meleset = peluru_meleset + ..., sisa_peluru = ... WHERE username = '...'`
| Load leaderboard | `SELECT * FROM tbenefit`

Konfigurasi koneksi di `src/model/DB.java`:
- URL: `jdbc:mysql://localhost/dbhide_seek`
- User: `root`
- Password: `""` (default XAMPP)

---

## Sistem Audio

- **bgm.wav**: Musik latar (loop), volume -15dB.
- **shoot.wav**: Efek suara setiap kali pemain menembak.

Sound dipicu dari `GameState` melalui queue `soundRequests` (pattern request-response):
1. GameState menambahkan ID suara ke list.
2. GamePanel membaca list di method `paintComponent()` dan memainkan suara.
3. List dikosongkan setelah diproses.

---

## Cara Menjalankan

### Prasyarat
- **Java JDK 17+** (atau versi yang kompatibel dengan MySQL Connector J 9.4.0).
- **MySQL Server** (XAMPP / Laragon / standalone).
- **VS Code** dengan **Extension Pack for Java**.

### Langkah-langkah

1. **Setup Database**
   ```bash
   mysql -u root < dbhide_seek.sql
   ```
   Atau import `dbhide_seek.sql` via phpMyAdmin.

2. **Buka Project**
   Buka folder `TMDHIDEANDSEEK` di VS Code.

3. **Pastikan Library Terdaftar**
   Periksa `.vscode/settings.json` — pastikan path `mysql-connector-j-9.4.0.jar` benar:
   ```json
   {
     "java.project.referencedLibraries": [
       "lib/**/*.jar"
     ]
   }
   ```

4. **Sesuaikan Koneksi DB (jika perlu)**
   Edit `src/model/DB.java` jika user/password MySQL berbeda.

5. **Jalankan**
   Buka `src/main/Main.java` → klik **Run** (▷) di VS Code.

### Troubleshooting

| Masalah | Solusi |
|---------|--------|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Pastikan MySQL Connector J terdaftar di referenced libraries |
| `SQLException: Access denied` | Cek user/password di DB.java |
| Gambar tidak muncul | Jalankan dari folder project root (agar path `src/resources/images/` valid) |
| Suara tidak muncul | Pastikan file `.wav` ada di `src/resources/audio/` |

---

## Credits

| Aset | Sumber |
|------|--------|
| Map | ChatGPT AI |
| Character | [elvgames.itch.io - Free Fantasy Dreamland Sprites](https://elvgames.itch.io/free-fantasy-dreamland-sprites) |
| Bullet (Fire/Water Arrow) | [craftpix.net - Free Water and Fire Magic Sprite Vector Pack](https://craftpix.net/freebies/free-water-and-fire-magic-sprite-vector-pack/) |
| Obstacle (Trees) | [anokolisa.itch.io - Free Pixel Art Asset Pack Topdown Tileset RPG 16x16](https://anokolisa.itch.io/free-pixel-art-asset-pack-topdown-tileset-rpg-16x16-sprites) |
| Monster (Enemy) | [deepdivegamestudio.itch.io - Demon Sprite Pack](https://deepdivegamestudio.itch.io/demon-sprite-pack) |
| Music (BGM) | [pixabay.com - Pixel Quest](https://pixabay.com/id/music/petualangan-pixel-quest-364092/) |
| Sound Effect (Shoot) | [leohpaz.itch.io - 50 RPG Battle Magic SFX](https://leohpaz.itch.io/50-rpg-battle-magic-sfx) |
| Potion | Gemini AI |
| Health Icon (Heart) | [elvgames.itch.io - Free Inventory Asset Pack](https://elvgames.itch.io/free-inventory-asset-pack) |
