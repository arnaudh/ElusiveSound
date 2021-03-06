package org.elusive.sound.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.elusive.main.listeners.ProgressListener;
import org.elusive.main.tools.IOtools;
import org.elusive.ui.grille.Grid;

public class SoundFileReader {

	private static float MAX = -Short.MIN_VALUE;

	// input
	private File file;

	// output
	private float[] leftData;
	private float[] rightData;

	// other
	private List<ProgressListener> progressListeners;

	public SoundFileReader(File file) {
		this.file = file;

		progressListeners = new ArrayList<ProgressListener>();
	}

	public void addProgressListener(ProgressListener lis) {
		progressListeners.add(lis);
	}

	public void read() throws IOException, UnsupportedAudioFileException {

		byte[] data = null;
		AudioInputStream in;

		long nbFrames = 0;
		int end = 0;

		in = AudioSystem.getAudioInputStream(file);
		AudioFormat format = in.getFormat();
		System.out.println("FORMAT = " + format);

		if (!format.getEncoding().equals(Encoding.PCM_SIGNED)) {
			// Pour que ça marche, il faut s'assurer d'avoir les librairies
			// suivantes dans le CLASSPATH :
			// jl1.0.1.jar
			// mp3spi.9.5.jar
			// tritonus_share-0.3.6.jar

			//TODO some mp3s are too fast (high pitched voice & stuff)
			
			System.out.println("FORMAT SPECIAL : ");
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2,
					baseFormat.getSampleRate(), false);
			AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in);
			format = decodedFormat;

			int bloc = 10000;
			System.out.println("File length : " + file.length() + ", allocating " + (file.length() * 12) + " bytes");
			data = new byte[(int) file.length() * 12]; // calcul en
			// fonction de
			// la taille du
			// fichier (un
			// peu plus
			// qu'il ne
			// faut...)

			byte[] d = new byte[4096];
			int nBytesRead = 0;
			int offset = 0;
			while (nBytesRead != -1) {
				nBytesRead = din.read(data, offset, bloc); // Lit 4608
				// frames (quoi
				// qu'on mette
				// ?)
				if (nBytesRead < bloc) {
					bloc = nBytesRead;
					System.out.println("change bloc [offset=" + offset + "] = " + nBytesRead);
				}
				offset += nBytesRead;
				double progress = offset / (double) data.length;
				for (ProgressListener lis : progressListeners) {
					lis.progressChanged(progress);
				}
			}
			nbFrames = offset / format.getFrameSize();
			System.out.println("offset : " + offset + ", alors que prévu : " + data.length);

			if (end == 0) {
				// il faut initialiser end
				end = (int) nbFrames / 4;
			}

		} else {
			// On peut récupérer le nombre de frames directement
			nbFrames = in.getFrameLength();

			int frameSize = format.getFrameSize();
			int taille = (int) (nbFrames * frameSize);
			System.out.println("data = new byte[" + nbFrames + " * " + frameSize + "]");
			data = new byte[taille]; // TODO NegativeSizeArrayException quand
										// enregistre son
			int numBytesRead = in.read(data);

			if (end == 0) {
				// il faut initialiser end
				end = (int) nbFrames;
			}

		}

		int frameSize = format.getFrameSize();
		double durationInSeconds = (nbFrames + 0.0) / format.getFrameRate();
		int sampleSizeInBits = format.getSampleSizeInBits();
		boolean isBigEndian = format.isBigEndian();
		int nbChannels = format.getChannels();
		// *
		System.out.println("|format = " + format);
		System.out.println("|isBigEndian = " + isBigEndian);
		System.out.println("|nbChannels = " + nbChannels);
		System.out.println("|frameSize (bytes) = " + frameSize);
		System.out.println("|sampleSizeInBits = " + format.getSampleSizeInBits());
		System.out.println("| to bytes = " + format.getSampleSizeInBits() / 4);
		System.out.println("|*nbFrames = " + nbFrames);
		System.out.println("|*duration = " + durationInSeconds + " s");
		// */

		// System.out.println("taille = "+taille);
		// System.out.println("numBytesRead = "+numBytesRead); //

		System.out.println("|*data.length (bytes) = " + data.length);
		System.out.println("|*data.length / 4 = " + data.length / 4);
		System.out.println("|*grille.frameMax = " + Grid.frameMax);

		// if( format.getSampleRate() != 44100 ){
		// System.out.println(" ==> Sampling rate different from 44100Hz :: CONVERTING ...");
		// float targetSamplingRate = 44100;
		// float targetFrameRate = 44100;
		// AudioFormat targetFormat = new AudioFormat(
		// format.getEncoding(),
		// targetSamplingRate,
		// format.getSampleSizeInBits(),
		// format.getChannels(),
		// format.getFrameSize(),
		// targetFrameRate,
		// format.isBigEndian());
		// if( AudioSystem.isConversionSupported(format, targetFormat)){
		// AudioInputStream targetStream =
		// AudioSystem.getAudioInputStream(targetFormat, in);
		// String fileName = file.getName();
		// String name = fileName;
		// String extension = "";
		// int dotPosition = fileName.lastIndexOf(".");
		// if (dotPosition != -1) {
		// extension = fileName.substring(dotPosition);
		// name = fileName.substring(0, dotPosition);
		// }
		// File targetFile =
		// IOtools.createTempFile(name+" - resampled to 44100Hz", extension,
		// IOtools.getTemporaryFolder());
		// AudioSystem.write(targetStream,
		// AudioSystem.getAudioFileFormat(file).getType(), targetFile);
		// file = targetFile;
		// read();
		// return;
		// }else{
		// System.err.println("Cannot convert sampling rate from "+format.getSampleRate()+" to "+targetFormat.getSampleRate());
		// }
		// }

		int longueur = (int) nbFrames;
		System.out.println("|*longueur = " + longueur);
		leftData = new float[longueur];
		rightData = new float[longueur];

		if (nbChannels == 1) { // MONO
			// System.out.println("MONOOOOOOOOOO, longueur du data : "+longueur);
			// System.out.println(" $$$ for n=0; n<"+longueur+" bytes[2*n] ... leftData[n] = ");
			if (sampleSizeInBits == 16) {
				int n = 0;
				for (; n < longueur; n++) {
					byte b1 = data[2 * n];
					byte b2 = data[2 * n + 1];
					int i1;
					int i2;
					if (isBigEndian) {
						i1 = (b2) & 0xFF;
						i2 = ((b1) << 8);
					} else {
						i1 = (b1) & 0xFF;
						i2 = ((b2) << 8);
					}
					leftData[n] = ((short) (i1 + i2)) / (MAX);
				}
				// System.out.println("fin du for : n="+n+", 2n="+(2*n)+" data.size="+data.length+", leftData.size = "+leftData.length);
				// System.out.println(data[2*n]);
				// System.out.println(data[2*n+1]);
				// System.out.println(data[2*n]+2);
				// System.out.println(data[2*n+3]);
			} else {
				System.err.println("Format Not Supported Yet");
			}
		} else if (nbChannels == 2) { // STEREO
			if (sampleSizeInBits == 16) {
				// System.out.println("ICIIIIIIIIIIIIIIi : "+0+", "+end);

				for (int n = 0; n < longueur; n++) {
					byte b1 = data[4 * n];
					byte b2 = data[4 * n + 1];
					// byte b3 = data[4*n+2];
					// byte b4 = data[4*n+3];

					int i1;
					int i2;
					if (isBigEndian) {
						i1 = (b2) & 0xFF;
						i2 = ((b1) << 8);
					} else {
						i1 = (b1) & 0xFF;
						i2 = ((b2) << 8);
					}

					// System.out.println("b1 = "+byteToHex(b1)+", i1 = "+i1+", b2 = "+byteToHex(b2)+", i2 = "+i2+", tot = "+(i1+i2));
					leftData[n] = ((short) (i1 + i2)) / (MAX);
				}

				if (format.getSampleRate() != 44100) {
					if (format.getSampleRate() < 44100) {
						// interpolation
						if (format.getSampleRate() == 22050) {
							System.out.println(":format is 22050 !!");
						}
					}
				}

				System.out.println("fin : 4*n=" + (4 * longueur) + ", data.length=" + data.length);
			} else {

				System.err.println("Format Not Supported Yet");
			}
			// update() TODO ?
		} else {
			System.err.println("BlocFichier : nbChannels=" + nbChannels + " non traité");
		}

	}

	public float[] getLeftData() {
		return leftData;
	}

	public float[] getRightData() {
		return rightData;
	}

}
