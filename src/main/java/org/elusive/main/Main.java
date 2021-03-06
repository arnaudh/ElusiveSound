package org.elusive.main;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.elusive.main.persistence.ElusiveProperties;
import org.elusive.sound.blocs.additive.AdditiveSynth;
import org.elusive.sound.blocs.additive.FrequenceAmplitude;
import org.elusive.sound.blocs.additive.TimeAmplitude;
import org.elusive.ui.fenetre.Fenetre;
import org.elusive.ui.grille.Grid;

public class Main {
	
	public static boolean isMacOSX() {
	    return System.getProperty("os.name").startsWith("Mac OS X");
	}

	public static void main(String[] args) {
		// *
		// Prépare pour Mac
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "ElusiveSound");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String projet = (String) ElusiveProperties.get(ElusiveProperties.KEY_PROJECT);
				System.out.println("projet : " + projet);
				File file = null;
				if (projet != null && new File(projet).exists()) {
					file = new File(projet);
				}
				Fenetre fenetre = new Fenetre();
				
				Grid grille = new Grid(fenetre, null); //TODO put file
//				AdditiveSynth s = new AdditiveSynth();
//				List<TimeAmplitude> tas = new ArrayList<TimeAmplitude>();
//				tas.add(new TimeAmplitude(0, 0));
//				tas.add(new TimeAmplitude(4000, 200));
//				tas.add(new TimeAmplitude(12000, 0));
//				tas.add(new TimeAmplitude(16000, 100));
//				tas.add(new TimeAmplitude(24000, 0));
//				tas.add(new TimeAmplitude(28000, 100));
//				s.addFrequenceAmplitude(new FrequenceAmplitude(200, tas));
//				List<TimeAmplitude> tas2 = new ArrayList<TimeAmplitude>();
//				tas2.add(new TimeAmplitude(0, 100));
//				tas2.add(new TimeAmplitude(4000, 0));
//				tas2.add(new TimeAmplitude(8000, 100));
//				tas2.add(new TimeAmplitude(12000, 0));
//				tas2.add(new TimeAmplitude(16000, 100));
//				tas2.add(new TimeAmplitude(20000, 0));
//				tas2.add(new TimeAmplitude(24000, 100));
//				tas2.add(new TimeAmplitude(28000, 0));
//				s.addFrequenceAmplitude(new FrequenceAmplitude(600, tas2));
//				grille.newBloc(s);
				
				fenetre.setGrille(grille);
				
				fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//				fenetre.setSize(new Dimension(500, 300));
				fenetre.setExtendedState(JFrame.MAXIMIZED_BOTH);
				fenetre.setLocationRelativeTo(null);
				fenetre.setVisible(true);
			}
		});

	}

}
