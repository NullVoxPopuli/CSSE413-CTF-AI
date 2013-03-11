package CTF_AI;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;

import javax.swing.JFrame;

import Core.CTFCore;
import Core.CTFException;
import GUI.ASCII_GUI;
import GUI.CTFGUI;
import GUI.CTFHiveMindDisplay;
import GUI.ControlGUI;
import Network.Connection;
import Network.ConnectionListener;
import Network.Packet;
import Network.Protocol;

public class Main implements ConnectionListener
{

	public Map<String, Object>	currentPacket;
	public Protocol				protocol;

	// this is so I can develop either on my mac or my HP...
	// But mostly on my mac
	private static final String	HOST			= System.getProperty("os.name").contains("Mac") ? "137.112.146.88" : "localhost";
	private static final int	PORT			= 2837;
	public Connection			connection;

	public CTFCore				core;

	boolean						usingGUI		= false;

	// ArrayList<Point> flags;
	// HashSet<Point> wallPositions;

	// Stack<LinkedList<Point>> solutions;

	// Queue<Point> pathToSendToServer;
	// Queue<Point> pathSentToServer;

	// Map<Integer, LinkedList<Point>> paths;

	// HashSet<CTFPathThreadHelper> pathFinders;

	// private boolean printMapOnCrash = true;

	private boolean				useThreads		= false;
	private ControlGUI			controlGUI;
	private boolean				alreadyBegan	= false;
	public boolean				useTextGUI		= true;
	private ASCII_GUI			asciiGUI;
	private boolean	gameHasBegun;
	private boolean exitOnError = false;
	
	private static final String TEAM_ONE = "Team The Original Turkey Brothel";
	private static final String TEAM_TWO = "Team Gutter Cuntin' Slut Buckets";

	public static void main(String[] arstring) throws Exception
	{
		System.out.println("The server host is : " + HOST);

		new Main(arstring);
	}

	/**
	 * TODO Put here a description of what this constructor does.
	 * 
	 * @throws Exception
	 */
	public Main(String[] args) throws Exception
	{
		// Load the protocol.
		protocol = new Protocol("CTFProtocol.txt");

		// Create the connection and tell it to send incoming packets this way.
		connection = new Connection(protocol, HOST, PORT);
		connection.AddConnectionListener(this);

		core = new CTFCore(this, protocol, connection);

		if (useTextGUI)
		{
			this.asciiGUI = new ASCII_GUI();
		}

		init();
		
		System.out.println(args[0]);
		boolean isTeamOne = (args[0].equals("1") ? true : false);
		
		if (isTeamOne)
		{
			connection.SendPacket(new Packet(protocol, "SignIn", new Object[] { TEAM_ONE}));
			connection.SendPacket(new Packet(protocol, "SetConstantPlayer", new Object[] {}));
			connection.SendPacket(new Packet(protocol, "CreateTournament", new Object[]{"1337"}));
	
		} else {
			connection.SendPacket(new Packet(protocol, "SignIn", new Object[] { TEAM_TWO}));
			connection.SendPacket(new Packet(protocol, "SetConstantPlayer", new Object[] {}));
        	connection.SendPacket(new Packet(protocol, "JoinGame", new Object[]{TEAM_ONE}));

		}





		//connection.SendPacket(new Packet(protocol, "CreateScenario", new Object[] { "A3-P9" }));


	}

	public void init() throws IOException, CTFException
	{
		if (!usingGUI)
		{
			// Try to make a game.


			// Ask about the game state.
			//requestGameState();

		}
		else
		{
			controlGUI = new ControlGUI(protocol, connection);
		}

	}

	public void requestGameState() throws IOException, CTFException
	{
		connection.SendPacket(new Packet(protocol, "RequestGameState", new Object[] {}));
	}

	public void PacketReceived(Packet packet)
	{
		// System.out.println("Received: " + packet.Encode());
		this.currentPacket = packet.Data;
		String packetType = (String) this.currentPacket.get("Type");

		if (packetType.equals("GameState"))
		{
			System.out.println(this.currentPacket);
			if (!alreadyBegan)
			{
				core.printAndSetUpGame();
				try
				{
					core.beginSolving();
					if (usingGUI)
						new CTFGUI(core);
				}
				catch (Exception exception)
				{
					// TODO Auto-generated catch-block stub.
					exception.printStackTrace();
				}

				alreadyBegan = true;
			}
			else
			{
				core.printGameState();
			}

		}
		else if (packetType.equals("YourTurn"))
		{
			
			if(!gameHasBegun) {
				core.printAndSetUpGame();
				gameHasBegun = true;
			}
			
			if(this.useTextGUI) updateGUI((String) this.currentPacket.get("State"));

			core.updatePlayerPositions((String) this.currentPacket.get("State"));
			

			core.beginSolving();

		}
		else if (packetType.equals("ScenarioList"))
		{
			controlGUI.updateScenarios((String) currentPacket.get("List"));
		}
		else if (packetType.equals("GameOver"))
		{
			System.out.println("The winner is: " + this.currentPacket.get("Winner"));

			if (!this.currentPacket.get("Winner").equals("no winner"))
			{
				//core.printSolutionPath(core.pathSentToServer);
				if (exitOnError) System.exit(0);
				else connection = null;

			}
		}
		else if (packetType.equals("Fail") && ((String) this.currentPacket.get("Reason")).contains("Unable to select username."))
		{
			
			System.err.println("You are currently logged in...");
			// System.err.println("Let me log out for you...");
			System.err.println("Please restart this program...");

			if (exitOnError) System.exit(0);
			else connection = null;
		}
		else if (packetType.equals("Fail"))
		{
			System.out.println("Received: " + packet.Encode());
			// if (printMapOnCrash && !this.pathSentToServer.isEmpty())
			//core.printSolutionPath(core.pathSentToServer);
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException exception)
			{
				// TODO Auto-generated catch-block stub.
				exception.printStackTrace();
			}
			System.err.println("Something failed =(");;
			
			if (exitOnError) System.exit(1);
			else connection = null;

		}
		else
		{
			System.out.println(packet.Encode());
		}

	}

	private void updateGUI(String string)
	{
		String result = "";

		int curLinPos = 0;
		char curChar;
		for (int i = 0; i < core.height; i++)
		{ // change rows
			for (int j = 0; j < core.width; j++)
			{ // change cols
				curLinPos = j * core.width + i;
				curChar = string.charAt(curLinPos);
				if (curChar == 'W') result += "W";//"■";
				else if (curChar == '*') result += "*";//"·";
				else if (curChar == '-') result += "-";//"·";
				else result += (string.charAt(curLinPos) + "");
			}
			result += "\n";
		}
		if (result.length() > core.width) this.asciiGUI.setText(result);
	}

}
