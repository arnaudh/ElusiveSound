package org.elusive.ui.exception;

import java.awt.FileDialog;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.elusive.ui.tools.file.FileOpenDialog;

public class FileNotFoundManager {

	private File fileNotFound;

	public FileNotFoundManager(File fileNotFound) {
		this.fileNotFound = fileNotFound;
	}

	public File manage() {
		int response = JOptionPane.showConfirmDialog(null, "The file \""+fileNotFound.getName()+"\" is missing, would you like to link it to an existing file ?", "File Not Found", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
		if (response == JOptionPane.YES_OPTION) {
			File parent = getFirstExistingParent(fileNotFound);
			String path = parent == null ? "" : parent.getPath();
			File dir = new File(path);
			FileOpenDialog dial = new FileOpenDialog("File not found : "+fileNotFound.getName(), null, dir, FileOpenDialog.getSupportedAudioFileExtensions());
			File selectedFile = dial.getSelectedFile();

			return  selectedFile;
		}else{
			return null;
		}

	}
	
	public static File getFirstExistingParent(File f){
		while( f != null ){
			if( f.exists() ){
				return f;
			}
			f = f.getParentFile();
		}
		return null;
	}

}
