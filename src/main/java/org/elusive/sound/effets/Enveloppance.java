package org.elusive.sound.effets;

import java.util.ArrayList;

import javax.swing.JPanel;

import org.elusive.sound.enveloppes.Enveloppe;
import org.elusive.sound.enveloppes.PointSon;
import org.elusive.sound.flow.Timestamp;
import org.elusive.ui.grille.Grid;


public class Enveloppance extends Effet {

	private ArrayList<Enveloppe> enveloppes = new ArrayList<Enveloppe>();
	
	public Enveloppance() {
		enveloppes.add(Enveloppe.create(new int[]{0, 1000, 2000, 6000, 7000}, new float[]{0, 1, 0.5f, 0.5f, 0}, false));
	}

	public Enveloppance(Enveloppe enveloppe) {
		enveloppes.add(enveloppe);
	}

	public float[] applyTo(float[] data) {
		float[] data2 = data.clone();
		for (Enveloppe env : enveloppes) {
			//Attention : la taille de léenveloppe peut dépasser la periode du tempo...
			env.applyTo(data2, 0, Grid.getTempo().getIntervalleFrames());
		}
		return data2;
	}

	/*
	 * //TODO Mettre des enveloppes ou on veut sur la courbe du son. Quand on
	 * clique sur une enveloppe, elle est modifiable dans son panel. On peut
	 * faire répéter une enveloppe selon une fréquence (surtout tempo :) ) On
	 * peut faire qu'en dehors des enveloppes ce soit une certaine valeur (par
	 * défaut 0)
	 */

	@Override
	public JPanel createPanel() {
		if( enveloppes.isEmpty() ) return null;
		return enveloppes.get(0).getPanel();
	}

	public Timestamp getTimestamp(){
		long max = 0;
		for (Enveloppe env: enveloppes) {
			if( env.getTimestamp().getValue() > max){
				max = env.getTimestamp().getValue();
			}
		}
		return new Timestamp(max);
	}
	

}
