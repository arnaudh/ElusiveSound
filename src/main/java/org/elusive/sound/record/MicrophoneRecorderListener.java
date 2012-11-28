package org.elusive.sound.record;

import java.io.File;

import org.elusive.sound.blocs.BlocFichier;

public interface MicrophoneRecorderListener {
	
	public void startedRecording();

	public void soundRecorded( File audioFile );
}
