package view;

import java.io.File;
import java.net.URL;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl; 

public class Sound {
    
    Clip clip;
    URL soundURL[] = new URL[30]; 
    FloatControl fc; 

    public Sound() {
        // --- DAFTAR SUARA ---
        soundURL[0] = loadSound("bgm.wav");   // Index 0: Musik Latar
        soundURL[1] = loadSound("shoot.wav"); // Index 1: Tembakan
    }

    private URL loadSound(String filename) {
        URL url = null;
        
        // Cek 1: Lewat resource path (standar jar)
        url = getClass().getResource("/resources/audio/" + filename);
        
        // Cek 2: Lewat file path manual (untuk VSCode/IDE)
        if (url == null) {
            try {
                File file = new File("src/resources/audio/" + filename);
                if (file.exists()) {
                    url = file.toURI().toURL();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (url != null) {
            System.out.println("✅ SUKSES: File " + filename + " ditemukan!");
        } else {
            System.err.println("❌ GAGAL: File " + filename + " TIDAK DITEMUKAN di folder src/resources/audio/");
        }
        
        return url;
    }

    public void setFile(int i) {
        try {
            if (soundURL[i] != null) {
                // Konversi Format Audio ke 16-bit (Anti-Error)
                AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
                AudioFormat baseFormat = ais.getFormat();
                AudioFormat decodeFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
                );
                AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
                
                clip = AudioSystem.getClip();
                clip.open(dais);
                
                // --- ATUR VOLUME ---
                // Khusus Musik (Index 0), volume dikecilkan biar gak berisik (-15dB)
                if (i == 0) { 
                    fc = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                    fc.setValue(-15.0f); 
                }
            }
        } catch (Exception e) {
            System.err.println("Error saat setFile index: " + i);
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip != null) {
            if (clip.isRunning()) clip.stop(); 
            clip.setFramePosition(0); 
            clip.start();
        }
    }

    public void loop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }
}