package org.elusive.main.historique.action;


import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.elusive.main.persistence.xml.XMLException;
import org.elusive.main.persistence.xml.XmlTools;
import org.elusive.sound.blocs.BlocPositionne;
import org.elusive.ui.grille.Grid;


public class ColleBlocs extends ActionHistoriqueGrille {

	private ArrayList<BlocPositionne> ancienneSelection = null;
	private ArrayList<BlocPositionne> blocsAColler = null;
	private int decalageLigne = 0;
	private int decalageOffset = 0;

	private int effectivementColles = 0;

	public ColleBlocs(Grid grille, List<String> xml, int offset, int ligne) throws XMLException {
		super(grille);
		this.blocsAColler = new ArrayList<BlocPositionne>();
		this.ancienneSelection = new ArrayList<BlocPositionne>();
		for( BlocPositionne bp : grille.getBlocsSelectionnes()){
			ancienneSelection.add(bp);
		}
		for(String  s : xml ){
				BlocPositionne bp = (BlocPositionne) XmlTools.fromXML(s);
				blocsAColler.add(bp);
		}

		// calcul du décalage (par rapport au premier bloc sélectionné)
		BlocPositionne bp0 = blocsAColler.get(0);
		decalageLigne = ligne - bp0.getLigne();
		decalageOffset = offset - bp0.getDebut();
		for(BlocPositionne bp : blocsAColler){
			bp.setLigne(bp.getLigne() + decalageLigne);
			bp.setDebut(bp.getDebut() + decalageOffset);			
		}
	}

	public boolean doAction() {
		grille.getBlocsSelectionnes().clear();
		int i = 0;
		for(; i < blocsAColler.size(); i++){
			BlocPositionne bp = blocsAColler.get(i);
//			bp.checkData(); //Si on REDO l'action, on a préalablement viré le data du bloc
			boolean b = grille.newBloc(bp);
			if (b) {
				grille.getBlocsSelectionnes().add(bp);
			} else {
				break;
			}
		}
		effectivementColles = i;
		if (i < blocsAColler.size()) {
			// on n'a pas pu coller tous les blocs
			System.out.println((blocsAColler.size() - effectivementColles)
					+ " blocs n'ont pas pu être collés");
		}
		if( i == 0){ //s'il n'y a aucun bloc de collé
			return false;
		}
		return true;
	}

	public void doReverse() {
		for(int i = 0; i < blocsAColler.size(); i++) {
			BlocPositionne bp = blocsAColler.get(i);
			boolean b = grille.getBlocs().remove(bp);
			bp.setDataNull(); //faire de la place en mémoire //OU PAS :p
			System.out.println("remove ("+bp+") : "+b);
		}
		grille.getBlocsSelectionnes().clear();
		for( BlocPositionne bp : ancienneSelection ){
			grille.getBlocsSelectionnes().add(bp);
		}
	}

	public String getName() {
		return "Colle Blocs";
	}

	public Icon getIcon() {
		return null;
	}

	public String getInfo() {
		if (effectivementColles < blocsAColler.size()) {
			return "collage partiel [" + effectivementColles + " sur "
					+ blocsAColler.size() + "]";
		}else{
			return "["+effectivementColles+"]";
		}
	}

}
