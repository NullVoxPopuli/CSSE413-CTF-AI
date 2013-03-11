package GUI;

import java.awt.Color;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Core.CTFException;
import Network.Connection;
import Network.Protocol;

/**
 * TODO Put here a description of what this class does.
 * 
 * @author lprestonsegoiii. Created Oct 2, 2010.
 */
public class ControlGUI
{
	private Protocol	protocol;
	private Connection	connection;

	DefaultListModel	model;
	JList				list;

	public ControlGUI(final Protocol protocol, final Connection connection) throws IOException, CTFException
	{
		this.protocol = protocol;
		this.connection = connection;
		connection.SendPacket(new Network.Packet(protocol, "ListScenarios", new Object[] {}));

		model = new DefaultListModel();
		list = new JList(model);

		JFrame win = new JFrame();
		win.add(list);

		win.setSize(200, 450);
		win.setBackground(Color.gray);

		win.setVisible(true);
		win.setResizable(false);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		list.addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				// Try to make a game.
				try
				{
					connection.SendPacket(new Network.Packet(protocol, "CreateScenario", new Object[] { model.get(e.getFirstIndex()) }));
					// Ask about the game state.
					connection.SendPacket(new Network.Packet(protocol, "RequestGameState", new Object[] {}));
				}
				catch (IOException exception)
				{
					System.err.println("List Listener");
					exception.printStackTrace();
				}
				catch (CTFException exception)
				{
					System.err.println("List Listener");
					exception.printStackTrace();
				}

			}

		});
	}

	public void updateScenarios(String scenarios)
	{
		String[] maps = scenarios.split(";");

		for (String map : maps)
		{
			model.add(0, map);
		}
	}
}
