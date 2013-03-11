package GUI;

import java.awt.Color;

import javax.swing.JFrame;

import Core.CTFCore;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Oct 1, 2010.
 */
public class CTFGUI
{
	public CTFGUI(CTFCore core){
		CTFHiveMindDisplay dis = new CTFHiveMindDisplay(core, 900, 450);
		JFrame win = new JFrame();
		dis.setVisible(true);

		win.add(dis);
		win.setSize(900, 450);
		win.setBackground(Color.gray);

		win.setVisible(true);
		win.setResizable(false);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
