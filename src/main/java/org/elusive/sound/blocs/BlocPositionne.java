package org.elusive.sound.blocs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.elusive.main.performance.Chrono;
import org.elusive.sound.effets.Effet;
import org.elusive.sound.flow.BlocUpdatedListener;
import org.elusive.sound.flow.Timestamp;
import org.elusive.ui.fenetre.ElusivePanel;
import org.elusive.ui.grille.Grid;
import org.elusive.ui.tools.DnDTabbedPane;
import org.elusive.ui.volume.VolumeController;

public class BlocPositionne implements BlocUpdatedListener {

	
	private transient Grid grille;
	private final Bloc bloc;
	private ArrayList<Effet> effets = new ArrayList<Effet>();
	private int ligne;
	private int offset;
	private float volume = 0.5f;

	private transient float[] data = null;

	// UI
	private transient ElusivePanel effectsPanel = null;
	private transient VolumeController volumeController = null;

	// Timestamps
	private transient Timestamp timestampBloc;
	private transient ArrayList<Timestamp> tmp_effets = null;
	private transient Timestamp timestampBloc_cache;
	private transient ArrayList<Timestamp> tmp_effets_cache = null;
	
	// static
	private static final float VOLUME_STEP = 0.1f;

	public BlocPositionne(Grid grille, Bloc bloc, int ligne, int debut) {
		this(grille, bloc, ligne, debut, new ArrayList<Effet>());
	}

	public BlocPositionne(Grid grille, Bloc bloc, int ligne, int offset,
			ArrayList<Effet> arrayList) {
		this.grille = grille;
		this.bloc = bloc;
		this.effets = arrayList;
		this.ligne = ligne;
		this.offset = offset;

		readResolve();
	}

	public Object readResolve() {
		tmp_effets = new ArrayList<Timestamp>();
		for (int i = 0; i < effets.size(); i++) {
			tmp_effets.add(new Timestamp());
		}
		timestampBloc = new Timestamp();

		tmp_effets_cache = new ArrayList<Timestamp>();
		for (int i = 0; i < effets.size(); i++) {
			tmp_effets_cache.add(new Timestamp());
		}
		timestampBloc_cache = new Timestamp();

		loadData();
		cache = new Cache();

		bloc.addBlocUpdatedListener(this);
		
//		effetsModifies();
		volumeController = new VolumeController(2f) {
			@Override
			public void volumeChanged(float vol) {
				volume = vol;
				updated();
			}
			@Override
			public float getVolume() {
				return volume;
			}
		};

		return this;
	}

	public int getLigne() {
		return ligne;
	}

	public void setLigne(int ligne) {
		this.ligne = ligne;
	}

	public int getDebut() {
		return offset;
	}

	public void setDebut(int debut) {
		this.offset = debut;
	}

	public int getFin() {
		return offset + getLength();
	}

	public void setFin(int fin) {
		this.offset = fin - getLength();
	}

	public float[] getData() {
		//TODO revoir le mécanisme d'update... (utilise plutôt des listeners que des timestamps...)
		if (hasChanged(timestampBloc, tmp_effets)) {
			loadData();
		}
		return data;
	}

	public void loadData() {
		System.out.println("### BlocPositionne.loadData() [" + bloc + "] ###");
		timestampBloc.isDifferentAndUpdate(bloc.getTimestamp());
		Runnable run = new Runnable() {
			@Override
			public void run() {
				data = bloc.generateData();
				for (int i = 0, n = effets.size(); i < n; i++) {
					Effet eff = effets.get(i);
					tmp_effets.get(i).isDifferentAndUpdate(eff.getTimestamp());
					data = eff.applyTo(data);
				}
				updated();
			}
		};
		new Thread(run).start();
	}

	public void setDataNull() {
		data = null;
	}

	public Bloc getBloc() {
		return bloc;
	}

	public void addEffect(Effet eff) {
		eff.setBloc(bloc);
		effets.add(eff);
		tmp_effets.add(new Timestamp(0));
		tmp_effets_cache.add(new Timestamp(0));
		effetsModifies();
	}

	public void enleveEffet(Effet eff) {
		eff.setBloc(null);
		int ind = effets.indexOf(eff);
		effets.remove(eff);
		tmp_effets.remove(ind);
		tmp_effets_cache.remove(ind);
		timestampBloc.update();
		timestampBloc_cache.update();
		effetsModifies();
	}

	public void resize(int left, int right) {
		int decalageLeft = bloc.resize(left, right);
		setDebut(getDebut() + decalageLeft);
		loadData();
	}

	public int getLength() {
		return data==null ? 0 : data.length;
	}

	public ElusivePanel getElusivePanel() {
		return bloc.getElusivePanel();
	}

	public ElusivePanel getEffectsPanel() {
		if (effectsPanel == null) {
			effectsPanel = new ElusivePanel("Effets - "
					+ bloc.getElusivePanel().getTitle());
		}
		return effectsPanel;
	}

	private void effetsModifies() {
		JTabbedPane tabbedPane = new DnDTabbedPane();
		for (Effet eff : effets) {
			// On peut ajouter un icone ici _\/_
			tabbedPane.addTab(eff.getName(), null, eff.getPanel(),
					eff.getName());
		}
		Dimension dimm = new Dimension(500, 200);
		tabbedPane.setPreferredSize(dimm);
		
		
		// LAYOUT
		JPanel panel = getEffectsPanel();
		panel.removeAll();
		
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		panel.add(tabbedPane, gbc);
		panel.revalidate();
		
		loadData();
	}

	public void volumeDown(){
		setVolume(volume - VOLUME_STEP);
	}
	public void volumeUp(){
		setVolume(volume + VOLUME_STEP);
	}
	public void volumeOn(){
		volumeController.turnVolumeOn();
	}
	public void volumeOff(){
		volumeController.turnVolumeOff();
	}
	
	public void setVolume(float newVolume) {
		volumeController.setVolume(newVolume);
	}
	
	public float getVolume(){
		return volume;
	}
	public VolumeController getVolumeController(){
		return volumeController;
	}



	// ********************** CACHE for PAINTING ******************* //
	transient Cache cache = new Cache();

	public float[] getMinFrames(int nbFramesEnUne) {
		cache.checkAndRegenerate(nbFramesEnUne);
		return cache.getMinCache();
	}

	public float[] getMaxFrames(int nbFramesEnUne) {
		cache.checkAndRegenerate(nbFramesEnUne);
		return cache.getMaxCache();
	}

	private boolean hasChanged(Timestamp timestampBloc,
			List<Timestamp> timestampEffets) {
		boolean hasChanged = false;
		for (int i = 0; i < effets.size(); i++) {
			if (timestampEffets.get(i).isDifferentAndUpdate(
					effets.get(i).getTimestamp())) {
				// System.out.println("l'Effet " + effets.get(i) + " CHANGED");
				hasChanged = true;
			}
		}
		if (timestampBloc.isDifferentAndUpdate(bloc.getTimestamp())) {
			hasChanged = true;
		}
		// StringBuffer tmpEffets = new StringBuffer();
		// for (Timestamp timestamp : timestampEffets) {
		// tmpEffets.append(timestamp.toString());
		// tmpEffets.append(", ");
		// }
		// System.out.println("HasChanged("+timestampBloc+",    "+tmpEffets+") : "+hasChanged);
		return hasChanged;
	}

	@Override
	public void blocUpdated(Bloc b) {
		loadData();
	}

	private void updated() {
		// invalide le cache
		timestampBloc_cache.update();
		if (grille != null) {
			grille.repaint();
		}
	}

	class Cache {
		private float[] minCache;
		private float[] maxCache;
		private int nbFramesEnUne = -1;

		public void regenerate() {
//			 System.out.println("............BlocPositionne.Cache.regenerate()...............");
			Chrono.tip();
			float[] data = getData();
			int cacheLength = data==null?0:data.length / nbFramesEnUne + 1;
			minCache = new float[cacheLength];
			maxCache = new float[cacheLength];
			for (int i = 0, l = minCache.length; i < l; i++) {
				float max = 0;
				float min = 0;
				int d = i * nbFramesEnUne;
				int last = Math.min(d + nbFramesEnUne, data.length);
				for (; d < last; d++) {
					float da = data[d]*volume;
					if (da > max) {
						max = da;
					} else if (da < min) {
						min = da;
					}
				}
				maxCache[i] = max;
				minCache[i] = min;
			}
			// Chrono.top(bloc+" min & max REGENERATED [data.length="+data.length+", nbFramesEnUne="+nbFramesEnUne+", cache.length="+minCache.length+"]   :::  "+MemoryStatus.getStatus());
		}

		public void checkAndRegenerate(int nbFramesEnUne2) {
			// System.out.println("BlocPositionne.Cache.checkAndRegenerate()");
			if (nbFramesEnUne != nbFramesEnUne2 || minCache == null
					|| hasChanged(timestampBloc_cache, tmp_effets_cache)) {
				nbFramesEnUne = nbFramesEnUne2;
				regenerate();
			} else {
				// System.out.println("NO");
			}
		}

		public float[] getMinCache() {
			return minCache;
		}

		public float[] getMaxCache() {
			return maxCache;
		}

	}

	// //////////////////////////////////////
	// UI
	// //////////////////////////////////////

	public Grid getGrille() {
		return grille;
	}

	public void setGrille(Grid grille) {
		this.grille = grille;
	}

	
}
