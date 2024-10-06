package mainClock;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;

public class SoundPlayer {
    private static Clip clip;
    public static boolean isMuted = false; // Mute state
    public static boolean isRunning = false; // Running state

    public static void playSound(String soundFilePath) {
        try {
            InputStream audioSrc = SoundPlayer.class.getResourceAsStream(soundFilePath);
            if (audioSrc == null) {
                throw new IOException("Sound file not found: " + soundFilePath);
            }

            // Wrap the InputStream with a BufferedInputStream to support mark/reset
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the sound
            clip.start();  // Play the sound
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }

    public static void stopSound() {
        if (clip != null && clip.isRunning()) {
            clip.stop();  // Stop the sound
            clip.close(); // Close the clip
        }
    }
    
    public static void toggleMute() {
        isMuted = !isMuted; // Toggle the mute state
        if(isMuted && isRunning()) {
        	clip.stop();
        }
        else if(!isMuted && isRunning())
        	clip.start();
    }

    public static boolean isMuted() {
        return isMuted; // Check if muted
    }
    public static boolean isRunning() {
        return isRunning; // Check if muted
    }
}
