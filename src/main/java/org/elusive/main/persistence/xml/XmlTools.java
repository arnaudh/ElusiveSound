package org.elusive.main.persistence.xml;

import java.io.File;

import org.elusive.main.tools.IOtools;
import org.elusive.sound.blocs.BlocFichier;
import org.elusive.sound.blocs.BlocPositionne;
import org.elusive.sound.blocs.additive.AdditiveSynth;
import org.elusive.sound.blocs.additive.FrequenceAmplitude;
import org.elusive.sound.blocs.additive.TimeAmplitude;
import org.elusive.sound.blocs.fm.OperateurFM;
import org.elusive.sound.blocs.fm.FMSynthetizer;
import org.elusive.sound.blocs.frequenceur.Frequenceur;
import org.elusive.sound.blocs.frequenceur.NotePlacee;
import org.elusive.sound.blocs.karplusstrong.KarplusStrong;
import org.elusive.sound.blocs.noise.WhiteNoise;
import org.elusive.sound.blocs.wave.WaveShaper;
import org.elusive.sound.effets.Enveloppance;
import org.elusive.sound.effets.FiltrePasseBas;
import org.elusive.sound.effets.FiltrePasseHaut;
import org.elusive.sound.effets.Inverse;
import org.elusive.sound.effets.SmoothOperator;
import org.elusive.sound.enveloppes.Enveloppe;
import org.elusive.sound.enveloppes.PointSon;
import org.elusive.sound.melody.tabs.Couplet;
import org.elusive.sound.melody.tabs.GuitarTab;
import org.elusive.sound.melody.tabs.TabNote;
import org.elusive.sound.melody.tabs.TabPartition;
import org.elusive.sound.melody.tabs.Tranche;
import org.elusive.sound.rythm.Hit;
import org.elusive.sound.rythm.Rythm;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class XmlTools {
	
	
	public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n";
	
	
	public static XStream xstream;
	static{
		xstream = new XStream(new DomDriver());
		
		//Création des alias - sert dans le cas où on déplace / renomme les classes... l'alias ne change pas
		//NE PAS MODIFIER
		xstream.alias("bloc-positionne" , BlocPositionne.class);
		
		// ###################### Blocs ######################
		xstream.alias("sound-file" , BlocFichier.class);
		//Synth
		xstream.alias("fm-synth" , FMSynthetizer.class);
		xstream.alias("fm-operator" , OperateurFM.class);
		
		xstream.alias("additive-synth" , AdditiveSynth.class);
		xstream.alias("karplus-strong" , KarplusStrong.class);
		xstream.alias("wave-shaper" , WaveShaper.class);

		xstream.alias("frequencer" , Frequenceur.class);
		xstream.alias("frequence-amplitude" , FrequenceAmplitude.class);
		xstream.alias("time-amplitude" , TimeAmplitude.class);
		
		xstream.alias("white-noise" , WhiteNoise.class);
		
		// ###################### Effets ######################
		xstream.alias("inverse" , Inverse.class);
		xstream.alias("enveloppance" , Enveloppance.class);
		xstream.alias("low-pass-filter" , FiltrePasseBas.class);
		xstream.alias("high-pass-filter" , FiltrePasseHaut.class);
		xstream.alias("smooth-operator" , SmoothOperator.class);
		
		// ###################### Enveloppes ######################
		xstream.alias("enveloppe", Enveloppe.class);
		xstream.alias("sound-point", PointSon.class);
		
		// ###################### Melody ######################
		xstream.alias("placed-note", NotePlacee.class);
		xstream.alias("tab-partition", TabPartition.class);
		xstream.alias("guitar-tab", GuitarTab.class);
		xstream.alias("verse", Couplet.class);
		xstream.alias("slice", Tranche.class);
		xstream.alias("guitar-tab-note", TabNote.class);
		
		// ###################### Rythm ######################
		xstream.alias("rythm", Rythm.class);
		xstream.alias("rythm-hit", Hit.class);
		
		
	}
	
	
	public static Object fromXML( String xml ) throws XMLException{
		Object obj = null;
		try {
			obj = xstream.fromXML(xml);
		} catch (ConversionException e) {
			throw new XMLException(e, e.getMessage(), xml);
		}
//		System.out.println("************************");
//		System.out.println("******** FROM XML *********");
//		System.out.println("****** ENTREE  ************");
//		System.out.println(xml);
//		System.out.println("****** SORTIE ************");
//		System.out.println(obj);
//		System.out.println("************************");
		return obj;
	}

	public static String toXML(Object object){
		String xml = xstream.toXML(object);
//		System.out.println("************************");
//		System.out.println("******** toXML *********");
//		System.out.println("****** ENTREE ************");
//		System.out.println(obj);
//		System.out.println("****** SORTIE ************");
//		System.out.println(xml);
//		System.out.println("************************");
		return xml;
	}
	
	
	
	public static void toFile(Object object, File f){
		String xml = toXML(object);
		IOtools.writeToFile(f, xml);
	}
	
	
}
