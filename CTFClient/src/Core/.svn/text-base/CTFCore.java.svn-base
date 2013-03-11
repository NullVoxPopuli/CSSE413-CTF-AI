package Core;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import CTF_AI.Main;
import Entities.Enemy;
import Entities.HiveMind;
import Entities.OtherTeam;
import Entities.Player;
import Network.Connection;
import Network.Packet;
import Network.Protocol;

/**
 * TODO Put here a description of what this class does.
 * 
 * @author lprestonsegoiii. Created Sep 30, 2010.
 */
public class CTFCore
{

	public Main							main;
	private Protocol					protocol;
	private Connection					connection;

	// Team stuff
	public HiveMind						hiveMind;
	public OtherTeam					otherTeam;

	Point								startPosition;
	Point								endPosition;
	Point								currentPosition;
	public Point						homeBase;
	int									numOfPlayers			= 1;
	int									numOfBases				= 0;
	int									numOfFlags				= 0;
	public Byte							height;
	public Byte							width;
	Byte								numberOfTeams;
	Byte								numberOfPlayers;
	boolean								mapIsUnsolvable			= false;

	private String						startState;

	private boolean						printInitialGameState	= true;

	public HashSet<Point>						flagDropOffLocations;
	ArrayList<Point>					flags;
	public HashSet<Point>				wallPositions;
	Stack<LinkedList<Point>>			solutions;
	public LinkedList<Point>			pathToSendToServer;
	public LinkedList<Point>			pathSentToServer;
	HashMap<Integer, LinkedList<Point>>	paths;
	HashSet<CTFPathThreadHelper>		pathFinders;
	private int	numOfMovesMade = 0;

	public CTFCore(Main main, Protocol protocol, Connection connection)
	{
		this.main = main;
		this.protocol = protocol;
		this.connection = connection;

		// init fields
		flagDropOffLocations = new HashSet<Point>();
		flags = new ArrayList<Point>();
		wallPositions = new HashSet<Point>();

		solutions = new Stack<LinkedList<Point>>();

		pathToSendToServer = new LinkedList<Point>();
		pathSentToServer = new LinkedList<Point>();

		paths = new HashMap<Integer, LinkedList<Point>>();
		pathFinders = new HashSet<CTFPathThreadHelper>();

		hiveMind = new HiveMind(this);
		otherTeam = new OtherTeam(this);
	}

	public void beginSolving()
	{

		if (this.numOfBases > 0)
		{
			try
			{
				long before = System.currentTimeMillis();

				String teamMoves = hiveMind.takeTurn();
				this.numOfMovesMade ++;
				System.out.println("Completing this round took: " + (System.currentTimeMillis() - before) + "ms");
				System.out.println("Moves so far: " + this.numOfMovesMade);

				if(this.main.useTextGUI) Thread.sleep(100);
				sendTurnToServer(teamMoves);

				

			}
			catch (Exception exception)
			{
				exception.printStackTrace();
			}
		}

	}


	public void sendTurnToServer(String turn) throws IOException, CTFException
	{
		this.connection.SendPacket(new Packet(this.protocol, "MakeMove", new Object[] { turn }));
	}

	public void sendUnSolvable() throws Exception, CTFException
	{
		System.out.println("Sending UNSOLVABLE");
		this.connection.SendPacket(new Packet(this.protocol, "MakeMove", new Object[] { "Unsolvable" }));
	}

//	private LinkedList<Point> getReversedPath(LinkedList<Point> linkedList)
//	{
//		LinkedList<Point> result = new LinkedList<Point>();
//		for (Point point : linkedList)
//		{
//			result.add(0, point);
//		}
//		return result;
//	}

	public void printPath(LinkedList<Point> path)
	{
		for (Point point : path)
		{
			System.out.print((int) point.getX() + "x" + (int) point.getY() + " to ");
		}
		System.out.println("");

	}

	public void printSolutionPath(Queue<Point> path)
	{
		int curLinPos = 0;
		char curChar;
		for (int i = 0; i < this.height; i++)
		{
			for (int j = 0; j < this.width; j++)
			{
				curLinPos = j * this.width + i;
				curChar = this.startState.charAt(curLinPos);
				if (curChar != 'F' && curChar != '0' && path.contains(new Point(j, i)))
				{
					System.out.print("¥");
				}
				else
				{
					System.out.print(curChar);
				}
			}
			System.out.println("");
		}
	}

	
	/**
	 * 
	 * Here we update the enemy positions, and while were at it, 
	 * check to see if one of our players is captured
	 *
	 * @param state
	 */
	public void updatePlayerPositions(String state){
		this.otherTeam.resetPlayers();
		
		int curLinPos = 0;
		char curChar;
		for (int i = 0; i < this.height; i++)
		{ // change rows
			for (int j = 0; j < this.width; j++)
			{ // change cols
				curLinPos = j * this.width + i;

				curChar = state.charAt(curLinPos);
				if (curChar == 'b')
				{
					this.otherTeam.addEnemyPlayer(new Enemy(j, i));
				}
				else if (curChar == 'B')
				{
					this.otherTeam.addEnemyPlayer(new Enemy(j, i, true));
				}

			}
		}

	
	}
	
	public void printAndSetUpGame()
	{
		Map<String, Object> currentPacket = this.main.currentPacket;
		this.height = (Byte) currentPacket.get("Height");
		this.width = (Byte) currentPacket.get("Width");
		this.numberOfTeams = (Byte) currentPacket.get("Teams");
		this.numberOfPlayers = (Byte) currentPacket.get("Players");

		String state = (String) currentPacket.get("State");

		this.startState = state;
		System.out.println(this.width + "x" + this.height);
		// System.out.println("Solvable: " + currentPacket.get("Solvable"));
		// System.out.println("Teams: " + currentPacket.get("Teams"));
		// System.out.println("Bases:" + currentPacket.get("Bases"));
		// System.out.println("Flags: " + currentPacket.get("Flags"));
		// System.out.println("Players: " + currentPacket.get("Players"));

		int curLinPos = 0;
		char curChar;
		for (int i = 0; i < this.height; i++)
		{ // change rows
			for (int j = 0; j < this.width; j++)
			{ // change cols
				curLinPos = j * this.width + i;
				if (printInitialGameState)
					System.out.print(state.charAt(curLinPos));
				curChar = state.charAt(curLinPos);
				if (curChar == 'a')
				{
					// this.startPosition = new Point(j,i);
					this.hiveMind.addPlayer(new Player(new Point(j, i)));
				}
				else if (curChar == 'b'){
					this.otherTeam.addPlayer(new Player(new Point(j, i)));
				}
				else if (curChar == 'F')
				{
					this.flags.add(new Point(j, i));
					this.hiveMind.addFlag(new Point(this.width - j, i));
					this.hiveMind.addFlagOwnedByUs(new Point(j, i));
					this.wallPositions.add(new Point(j,i));
					this.numOfFlags++;
				}
				else if (curChar == '0')
				{
					addAdjacentPointsToDropOffLocations(new Point(j, i));
					this.homeBase = new Point(j, i);
					this.wallPositions.add(new Point(j, i));
					this.numOfBases++;
					this.hiveMind.overMind = new Point(j, i);
					
					//tell us where the other base is
					
				}
				else if (curChar == 'W')
				{
					this.wallPositions.add(new Point(j, i));
				}
			}
			if (printInitialGameState)
				System.out.println("");
		}

	}

	private void addAdjacentPointsToDropOffLocations(Point p)
	{
		this.flagDropOffLocations.add(new Point(p.x - 1, p.y));
		this.flagDropOffLocations.add(new Point(p.x, p.y + 1));
		this.flagDropOffLocations.add(new Point(p.x, p.y - 1));
		this.flagDropOffLocations.add(new Point(p.x + 1, p.y));
	}

	public void printGameState()
	{
		Map<String, Object> currentPacket = this.main.currentPacket;
		this.height = (Byte) currentPacket.get("Height");
		this.width = (Byte) currentPacket.get("Width");
		String state = (String) currentPacket.get("State");
		System.out.println("Current Game State");
		int curLinPos = 0;
		char curChar;
		for (int i = 0; i < this.height; i++)
		{ // change rows
			for (int j = 0; j < this.width; j++)
			{ // change cols
				curLinPos = j * this.width + i;
				System.out.print(state.charAt(curLinPos));
			}
			System.out.println("");
		}

	}
}
