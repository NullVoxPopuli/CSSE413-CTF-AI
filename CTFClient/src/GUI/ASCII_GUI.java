package GUI;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Oct 3, 2010.
 */
public class ASCII_GUI
{
	JTextArea text;
	public ASCII_GUI(){
		JFrame win = new JFrame();

		win.setSize(300, 600);
		win.setBackground(Color.gray);
		
		text = new JTextArea();
		text.setFont(Font.decode("monospaced-12"));

		text.setVisible(true);
		win.add(text);
		text.setText("Welcome");
		
		

		win.setVisible(true);
		win.setResizable(false);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void setText(String text)
	{
		this.text.setText(text);
	}
}
