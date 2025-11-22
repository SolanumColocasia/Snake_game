package sounds;
import javax.sound.sampled.*;
import java.io.File;

public class Background {
	private static Clip clip;
	public static void playMusic(String path) {
		try {
			
			if(clip!=null && clip.isRunning()) {
				clip.stop();
				clip.close();
			}
			
			
			File file = new File(path);
			AudioInputStream backgroundScore = AudioSystem.getAudioInputStream(file);
			
			Clip clip = AudioSystem.getClip();
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
