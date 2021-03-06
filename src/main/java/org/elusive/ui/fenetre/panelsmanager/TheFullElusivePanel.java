package org.elusive.ui.fenetre.panelsmanager;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;

import org.elusive.sound.blocs.BlocPositionne;
import org.elusive.ui.action.ActionCategory;
import org.elusive.ui.action.MyAction;
import org.elusive.ui.action.MyCheckAction;
import org.elusive.ui.config.Colors;
import org.elusive.ui.resources.Icons;

public class TheFullElusivePanel extends JPanel {

	private TheBlocPanelsManager panelManager;
	private BlocPositionne bloc;
	private MyAction closeAction;
	private MyAction dockOutAction;
	private MyAction dockInAction;
	private MyCheckAction stayOnTopAction;
	
	//UI
	private boolean dockedOut;
	private boolean firstDockOut = true;
	private JFrame frame; //if docked out

	public TheFullElusivePanel(TheBlocPanelsManager theBlocPanelsManager, BlocPositionne bloc) {
		super();
		this.panelManager = theBlocPanelsManager;
		closeAction = new MyAction("Close", null, "Close "+bloc.getElusivePanel().getTitle(), Icons.CLOSE_ICON) {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		};
		dockOutAction = new MyAction("Dock Out", null, "Dock out "+bloc.getElusivePanel().getTitle(), Icons.DOCK_OUT_ICON) {
			@Override
			public void actionPerformed(ActionEvent e) {
				dockOut();
			}
		};
		dockInAction = new MyAction("Dock In", null, "Dock in "+bloc.getElusivePanel().getTitle(), Icons.DOCK_IN_ICON) {
			@Override
			public void actionPerformed(ActionEvent e) {
				dockIn();
			}
		};
		stayOnTopAction = new MyCheckAction("On top", null, "Always show "+bloc.getElusivePanel().getTitle(), Icons.ALWAYS_SHOW_ICON, ActionCategory.VIEW) {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setAlwaysOnTop(stayOnTopAction.isSelected());
			}
		};
		stayOnTopAction.setSelected(false);
				
		this.setLayout(new GridBagLayout());
		setBloc(bloc);

		frame = new JFrame(bloc.getElusivePanel().getTitle());
		frame.setLocationRelativeTo(this);
		frame.getContentPane().add(this);
		frame.pack();
	}


	public BlocPositionne getBloc(){
		return bloc;
	}
	
	public void setBloc(BlocPositionne bloc) {
		this.bloc = bloc;
		Border border1 = BorderFactory.createLineBorder(bloc.getBloc().getOutsideColor(), 2);
//		Border border2 = BorderFactory.createTitledBorder(border1, bloc.getElusivePanel().getTitle());
		this.setBorder(border1);
		this.setBackground(Colors.MAIN_BACKGROUND_1);
//		this.setBackground(bloc.getBloc().getOutsideColor());
		redoLayout();
	}
	
	private void redoLayout(){
		this.removeAll();
		// Layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		if( dockedOut ){
			this.add(dockInAction.createSimpleIconButton(), gbc);
			gbc.gridy++;
			JCheckBox checkbox = stayOnTopAction.createSimpleCheckbox();
			checkbox.setForeground(Colors.MAIN_FOREGROUND_1);
			this.add(checkbox, gbc);
		}else{
			this.add(closeAction.createSimpleIconButton(), gbc);
			gbc.gridy++;
			this.add(dockOutAction.createSimpleIconButton(), gbc);
		}
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridy = 0;
		gbc.gridx ++;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(bloc.getElusivePanel(), gbc);
		gbc.weightx = 0;
		gbc.gridx ++;
		gbc.gridheight = GridBagConstraints.RELATIVE;
		this.add(bloc.getVolumeController().createVolumeSlider(JSlider.VERTICAL), gbc);
		gbc.gridy ++;
		gbc.gridheight = 1;
		gbc.weighty = 0;
		this.add(bloc.getVolumeController().getVolumeButton(), gbc);
		gbc.gridx ++;
		gbc.gridy = 0;
		gbc.weighty = 1;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		this.add(bloc.getEffectsPanel(), gbc);
	}
	
	public void close(){
		panelManager.fullPanels.remove(this);
		panelManager.updatePanel();
		frame.setVisible(false);
	}
	
	public void dockOut(){
		dockedOut = true;
		redoLayout();
		if( firstDockOut ){
			firstDockOut = false;
			Dimension dim = this.getSize();
			System.out.println("dim="+dim);
			frame.setSize(dim);
		}
		frame.getContentPane().add(this);
		frame.setVisible(true);		
		panelManager.updatePanel();
	}
	
	public void dockIn(){
		dockedOut = false;
		redoLayout();
		frame.setVisible(false);
		panelManager.updatePanel();
//		panelManager.dockIn(bloc);
	}

//	public void setFrame(JFrame frame) {
//		if( this.frame != null ){
//			this.frame.setVisible(false);
//		}
//		this.frame = frame;
//		redoLayout();
//	}
//	public JFrame getFrame() {
//		return frame;
//	}
	
	public void showFrame(){
		frame.getContentPane().add(this);
		frame.pack();
		frame.setVisible(true);		
	}

	public boolean isDockedOut() {
		return dockedOut;
	}

	public void showDockedOut() {
		frame.setVisible(true);
	}


	
}
