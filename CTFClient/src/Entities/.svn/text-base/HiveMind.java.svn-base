package Entities;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import Core.CTFCore;
import Core.CTFException;
import Core.CTFPathSolver;
import Core.CTFHeuristicSolver;
import Core.CTFProductionSolver;
import Entities.Entity.Motive;

/**
 * TODO Put here a description of what this class does.
 * 
 * @author lprestonsegoiii. Created Sep 30, 2010.
 */
public class HiveMind
{
	public ArrayList<Player>	ourPlayers				= new ArrayList<Player>();
	ArrayList<Player>			lastSeenVisibleEnemies	= new ArrayList<Player>();
	public ArrayList<Point>		flags					= new ArrayList<Point>();	// original
																					// flag
	public ArrayList<Point>		ourFlags				= new ArrayList<Point>();
																					// locations
	public ArrayList<Point>		availableFlags			= new ArrayList<Point>();	// flags
																					// not
																					// claimed
	public Point				overMind;											// Base
	private CTFCore				core;
	CTFPathSolver				pathSolver;

	int							remainingFlags			= 0;
	
//	HashMap<Player, LinkedList<Point>> playerPaths = new HashMap<Player, LinkedList<Point>>();


	/**
	 * 
	 * @param ctfCore
	 */
	public HiveMind(CTFCore ctfCore)
	{
		core = ctfCore;
		pathSolver = new CTFProductionSolver(core);

	}
	

	public void addPlayer(Player player)
	{
		ourPlayers.add(player);
	}

	public void removePlayer(Player player)
	{
		ourPlayers.remove(player);
	}

	public void addFlag(Point p)
	{
		flags.add(p);
		availableFlags.add(p);
		remainingFlags++;
	}

	/**
	 * This is where we want to optimize the players finding the flags, one move
	 * at a time. So, just calculate the path, and return the first move for
	 * each player. Since, normally, there are going to be more flags than
	 * players, we need to Iterate through the flags and players, and find the
	 * closest flag to each player.
	 *
	 * @return the shortest path
	 */
	public LinkedList<Point> findNextOptimalPathToFlag()
	{
		LinkedList<Point> result = null;
		LinkedList<Point> temp;
		// find closest player / flag combo
		for (Player curPlayer : ourPlayers)
		{
			if (curPlayer.hasFlag == false && curPlayer.motive != Motive.GET_FLAG)
			{
				for (Point flag : this.availableFlags)
				{
					temp = pathSolver.solve(curPlayer.currentLocation, flag);
					if (temp != null)
					{
						if (result != null)
						{
							if (temp.size() < result.size())
							{
								result = temp;
							}
						}
						else
						{
							result = temp;
						}
					}
				}	
			}
			
		}

		if (result != null && !result.isEmpty())
		{
			Player p = getPlayerAtPoint(result.getFirst());
			p.setTarget(result.getFirst());
			p.motive = Motive.GET_FLAG;
			p.setPlannedPath(result);
			availableFlags.remove(result.get(result.size() - 1));
			remainingFlags--;			
		}


		return result;
	}
	
	public void sendAllThatNeedToGoToTheBaseToTheBase()
	{
		LinkedList<Point> temp;

		for (Player curPlayer : ourPlayers)
		{
			if (curPlayer.hasFlag || curPlayer.motive == Motive.GOTO_BASE)
			{
				temp = sendMinionToHachery(curPlayer);
				if (temp != null) curPlayer.setPlannedPath(temp);
			}
		}
	}

	public LinkedList<Point> sendMinionToHachery(Player p)
	{
		return sendMinionToHacheryByPoint(p.currentLocation);
	}
	
	public LinkedList<Point> sendMinionToHacheryByPoint(Point p)
	{
		LinkedList<Point> result;
		result = pathSolver.solve(p, overMind);

		//if (result != null)
		//	getPlayerAtPoint(result.getFirst()).setLocation(result.getLast());

		return result;
	}

	public Player getPlayerAtPoint(Point p)
	{
		for (Player player : ourPlayers)
		{
			if (player.currentLocation.equals(p))
			{
				return player;
			}
		}

		return null;
	}

	/**
	 * 
	 * @return a move string to be sent to the server in the format of
	 *         XxY-X2xY2;X3xY3-X4xY4 or as many moves as players (assuming the
	 *         A.I. decides that all players should move this turn)
	 */
	public String takeTurn()
	{
		String result = "";
		LinkedList<Point> temp;
		ArrayList<String> moves = new ArrayList<String>();

		if (remainingFlags > 0)
		{
			findNextOptimalPathToFlag();
		}

		// only executed if a player has a flag, or the motive
		// to go to the baes
		sendAllThatNeedToGoToTheBaseToTheBase(); 


		// construt the string that will be sent to the server
		for (Player curPlayer : ourPlayers)
		{
			temp = curPlayer.currentPlannedPath;
			if (temp != null && !temp.isEmpty())
			{
				Point start = curPlayer.currentLocation;
				Point end = temp.get(0);

				moves.add(start.x + "x" + start.y + "-" + end.x + "x" + end.y);
				
				// for output
				this.core.pathSentToServer.add(end);
				
				// we have stepped on a flag
				if (flags.contains(end))
				{
					curPlayer.motive = Motive.GOTO_BASE;
					curPlayer.hasFlag = true;
				}
				// if we have returned the flag to base 
				if (core.flagDropOffLocations.contains(end)){
					curPlayer.motive = Motive.NONE;
					curPlayer.hasFlag = false;
				}
				curPlayer.setLocation(temp.get(0));
				curPlayer.currentPlannedPath.remove(0);
			}

		}

		// format the string for the server
		result = moves.toString().replace(", ", ";").replace("[", "").replace("]", "");
		System.err.println("Sending: " + result);
		if(result.equals(""))
			try
			{
				core.sendUnSolvable();
			}
			catch (CTFException exception)
			{
				// TODO Auto-generated catch-block stub.
				exception.printStackTrace();
			}
			catch (Exception exception)
			{
				// TODO Auto-generated catch-block stub.
				exception.printStackTrace();
			}
		return result;
	}


	/**
	 * finds the closest
	 */
	public LinkedList<Point> findClosestFlagToPlayer(Player p)
	{
		LinkedList<Point> result = null, temp;

		for (Point flag : availableFlags)
		{
			temp = pathSolver.solve(p.currentLocation, flag);
			if (temp != null)
			{
				if (result != null)
				{
					if (temp.size() < result.size())
					{
						result = temp;
					}
				}
				else
				{
					result = temp;
				}
			}
		}

		if (result != null)
		{
			availableFlags.remove(result.get(result.size() - 1));
			remainingFlags--;
		}

		return result;
	}

	public void addFlagOwnedByUs(Point point)
	{
		this.ourFlags.add(point);
		
	}


}
