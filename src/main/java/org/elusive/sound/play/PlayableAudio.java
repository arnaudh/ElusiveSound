package org.elusive.sound.play;

import org.elusive.sound.tools.MyFormats;

public class PlayableAudio implements Playable {

	private boolean playing;
	private DataPlayer dp;
	private BufferedData pd;
	
	public PlayableAudio(BufferedData pd){
		playing = false;
		dp = new DataPlayer(MyFormats.out());
		this.pd = pd;
	}
	
	public boolean isPlaying() {
		return playing;
	}

	public void pause() {
		playing = false;
		dp.stop();
		dp.flush();
	}

	public void stop() {
		pause();
	}

	public void run() {
		playing = true;
		float[] data = null;
		pd.initData();
		while(playing && (data = pd.getNextData()) != null ){
			dp.play(data);
		}
		//Here ends the thread
	}

}
