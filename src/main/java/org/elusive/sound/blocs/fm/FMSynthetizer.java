package org.elusive.sound.blocs.fm;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.elusive.main.persistence.xml.XmlTools;
import org.elusive.sound.blocs.BlocGenerateur;
import org.elusive.sound.blocs.frequenceur.Frequencable;
import org.elusive.sound.flow.Timestamp;
import org.elusive.sound.genetics.Geneticable;
import org.elusive.ui.fenetre.ElusivePanel;
import org.elusive.ui.tools.PopupListener;
import org.elusive.ui.tools.file.FileOpenDialog;
import org.elusive.ui.tools.file.OpenSaveManager;


public class FMSynthetizer extends BlocGenerateur implements Frequencable {
	
	private OperateurFM racine = new OperateurFM(this);

	public FMSynthetizer() {
	}
	
	private transient int offset;
	@Override
	public float[] generateNextData(int size) {
		float[] data = new float[size];

		racine.decalagePhase = racine.angle;
		for (int n = 0; n < size; n++) {
			data[n] += (racine.calcule(n + offset) / 100.0);
		}
		offset += size;
		return data;
	}

	@Override
	public void setFrequenceNoUpdate(double freq) {
		racine.setFrequenceNoUpdate(freq);
		for (int i = 0; i < racine.getChildCount(); i++) {
			((OperateurFM) racine.getChildAt(i)).setFrequenceNoUpdate(freq/4);
		}
	}
	
	

	@Override
	public double getFrequence() {
		return racine.getFrequence();
	}

	public OperateurFM getRacine() {
		return racine;
	}

	public void setRacine(OperateurFM racine) {
		this.racine = racine;
		update();
	}


	@Override
	protected ElusivePanel createElusivePanel() {
		ElusivePanel frame = new ElusivePanel("Synthétiseur FM");

		JPanel panel;
		JTree arbre = new JTree(racine);

		arbre.setEditable(true);
		arbre.setCellRenderer(new OperateurRenderer());
		arbre.setCellEditor(new OperateurEditor());
		arbre.setRowHeight(0);

		JScrollPane scrollPane = new JScrollPane(arbre);

//		Dimension dim = new Dimension(300, 0);
//		scrollPane.setMinimumSize(dim);
//		scrollPane.setPreferredSize(dim);
		
		//TODO : popup, save...

		panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = gbc.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		panel.add(scrollPane, gbc);

		for (int i = 0; i < 100; i++) {
			arbre.expandRow(0);
		}
		
		frame.add(panel);
//		frame.setMinimumSize(new Dimension(400, 200));
		
		
		return frame;
	}
	
	public String toString(){
		return "FMSynth "+racine.toRecursiveString();
	}
	
	@Override
	public Timestamp getTimestamp() {
		long tmp = super.getTimestamp().getValue();
		tmp = Math.max(tmp, racine.getTimestamp().getValue());
		return new Timestamp(tmp);
	}

	@Override
	public Geneticable mutate(double strength) {
		FMSynthetizer synth = new FMSynthetizer();
		synth.setRacine(racine.mutate(strength));
		return synth;
	}
	

}
