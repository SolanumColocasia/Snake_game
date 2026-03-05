package sounds;
import javax.sound.sampled.*;
import java.net.URL;

public class Background {
	private static Clip clip;
	public static void playMusic(URL url) {
		try {
			
			if(clip!=null && clip.isRunning()) {
				clip.stop();
				clip.close();
			}
			
			AudioInputStream backgroundScore = AudioSystem.getAudioInputStream(url);
			
			clip = AudioSystem.getClip();
			clip.open(backgroundScore);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			clip.start();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stop() {
		if(clip!=null && clip.isRunning()) {
			clip.stop();
		}
	}
}
