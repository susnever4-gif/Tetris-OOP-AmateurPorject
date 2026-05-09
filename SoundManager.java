import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {
    private Clip gameTitleMusic;
    private Clip gameMusic;
    private Clip gamePlayingMusic; 
    private Clip gameOverMusic;
    private boolean isGameMusicPlaying = false; 

    private void playSound(String filename) {
    try {
        File soundFile = new File("Assets/"+ filename);
        if (soundFile.exists()) {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        }
    } catch (Exception e) {
    }
}

    public void playBsound() {
        playSound("Beep.wav");
    }
 
    public void playMenuSelect() {
        playSound("Ding.wav");
    }

    public void playEX() {
        playSound("Ex.wav");
    }

    public void playTitleMusic() {
        if (gameTitleMusic != null && gameTitleMusic.isRunning()) {
            return;
        }
        
        File musicFile = new File("Assets/Title.wav");
        try {
            stopClip(gameMusic);
            stopClip(gameOverMusic);
            stopClip(gamePlayingMusic);
            if (musicFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
                gameTitleMusic = AudioSystem.getClip();
                gameTitleMusic.open(audioStream);
                gameTitleMusic.start();
            }
        } catch (Exception e) {
            System.out.println("Title Music Error: " + e.getMessage());
        }
    }
    
    public void playGameMusic() {
        try {
            stopClip(gameTitleMusic);
            stopClip(gameOverMusic);
            stopClip(gamePlayingMusic);
                
            if (gameMusic != null && gameMusic.isRunning()) return;
    
            File musicFile = new File("Assets/Menu.wav");
            if (musicFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
                gameMusic = AudioSystem.getClip();
                gameMusic.open(audioStream);
                gameMusic.loop(Clip.LOOP_CONTINUOUSLY);
                gameMusic.start();
            }
        } catch (Exception e) {}
    }

    public void playGamePlayingMusic() {
        try {
            stopClip(gameMusic); 
            stopClip(gameTitleMusic);

            if (gamePlayingMusic != null && gamePlayingMusic.isRunning()) return;

            File musicFile = new File("Assets/Playing.wav");
            if (musicFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
                gamePlayingMusic = AudioSystem.getClip();
                gamePlayingMusic.open(audioStream);
                gamePlayingMusic.loop(Clip.LOOP_CONTINUOUSLY);
                gamePlayingMusic.start();
            }
        } catch (Exception e) {}
    }
    
    public void playGameOverMusic() {
        try {
            stopClip(gameMusic);
            stopClip(gamePlayingMusic);
            
            if (gameOverMusic != null && gameOverMusic.isRunning()) return;
            
            File musicFile = new File("Assets/Clear.wav");
            if (musicFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
                gameOverMusic = AudioSystem.getClip();
                gameOverMusic.open(audioStream);
                gameOverMusic.start();
            }
        } catch (Exception e) {}
    }
    
    private void stopClip(Clip clip) {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }

    public void stopAllMusic() {
        stopClip(gameMusic);
        stopClip(gameTitleMusic);
        stopClip(gamePlayingMusic);
        stopClip(gameOverMusic);
        isGameMusicPlaying = false; 
    }
}