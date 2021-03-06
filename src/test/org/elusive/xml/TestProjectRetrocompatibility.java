/***********************************************************************************************
 * Egis
 * (C)opyright 2011 Ipsosenso - Tous droits réservés.
 * 
 * Code réalisé par Ipsosenso.
 *
 * @date 27 mai 2011 @author Arnaud -  ElusiveRetrocompatibility.java
 ***********************************************************************************************/

package org.elusive.xml;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.elusive.main.persistence.xml.XMLException;
import org.elusive.main.tools.IOtools;
import org.elusive.ui.fenetre.Fenetre;
import org.elusive.ui.grille.Grid;
import org.elusive.util.ReadSauvegardeDirectory;
import org.elusive.util.XmlTestTools;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Arnaud
 * 
 */
public class TestProjectRetrocompatibility extends ReadSauvegardeDirectory{

	
	static Grid grille;
	private static File dir = new File("sauvegardes");
	private static String prefix = ".__ElusiveTest__";

	@BeforeClass
	public static void setUpBeforeClass() {
		grille = new Grid(new Fenetre(), null);
	}
	
	@AfterClass
	public static void clearDirectoryOfTempFiles(){
		for (File f : dir.listFiles()) {
			if( f.getName().startsWith(prefix) ){
//				System.out.println("DELETE "+f);
				f.delete();
			}
		}		
	}


	public TestProjectRetrocompatibility(File file) {
		super(file);
	}

	@Test
	public void testOpen() throws XMLException {
		System.out.println("file : " + file);
		try {
			String str = IOtools.readFile(file);
			assertEquals(XmlTestTools.containsProjectPaths(str), false);
			grille.open(str);
		} catch (XMLException e) {
			e.setFilePath(file);
			throw e;
		}
	}
	
//	@Test
//	public void testSaveIsSame() throws XMLException{
//		testOpen();
//		File newFile = new File(dir.getPath()+"/"+prefix+file.getName());
//		fenetre.getOpenSaveManager().setSavedFile(newFile);
//		fenetre.getOpenSaveManager().writeToSavedFile();
//		String oldXML = IOtools.readFile(file);
//		String newXML = IOtools.readFile(newFile);
//
//		System.out.println("##### OLD XML ######");
//		System.out.println(oldXML);
//
//		System.out.println("##### NEW XML ######");
//		System.out.println(newXML);
//		
//		assertEquals(oldXML, newXML);
//	}
	

}
