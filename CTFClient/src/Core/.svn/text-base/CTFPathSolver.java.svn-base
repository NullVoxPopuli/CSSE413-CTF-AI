package Core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;

import CTF_AI.Main;
import Datastructs.LocationNode;
import Entities.Player;

/**
 * TODO Put here a description of what this class does.
 * 
 * @author vader. Created Sep 8, 2010.
 */
public class CTFPathSolver
{
	public HashSet<Point>				closedMoves;

	Comparator<LocationNode>	shortestDistance	= new Comparator<LocationNode>()
													{
														public int compare(LocationNode a, LocationNode b)
														{
															return (a.distance > b.distance ? 1 : (a.distance < b.distance ? -1 : 0));
														}
													};


	PriorityQueue<LocationNode>	openMoves;

	private Main				main;
	int							numOfTouchedNodes;
	protected Point				startLocation;
	public Point				endLocation;
	LinkedList<Point>			solution;

	Thread						thisThread;
	int							pathNumber;
	String						message;
	Stack<LinkedList<Point>>	solutions			= new Stack<LinkedList<Point>>();

	protected CTFCore				core;

	public CTFPathSolver(Main main, Point startLocation, Point endLocation)
	{
		this.main = main;
		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.core = this.main.core;
	}

	public CTFPathSolver(CTFCore core, Point startLocation, Point endLocation)
	{
		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.core = core;
	}

	public CTFPathSolver(CTFCore core)
	{
		this.core = core;
	}

	public LinkedList<Point> solve()
	{
		// reset the fields
		closedMoves = new HashSet<Point>();
		openMoves = new PriorityQueue<LocationNode>(50, shortestDistance);
		
		System.out.println("Solving path for points " + this.startLocation.x + "x" + this.startLocation.y + " to " + this.endLocation.x + "x" + this.endLocation.y);

		LocationNode currentLocation = new LocationNode(this.startLocation, new LinkedList<Point>(), this.endLocation); // start
																														// where
																														// the
																														// player
																														// starts
		currentLocation.getPath().add(this.startLocation);
		this.openMoves.add(currentLocation);

		// LOOP!
		while (!this.openMoves.isEmpty())
		{
			currentLocation = this.openMoves.remove();
			if (this.closedMoves.contains(currentLocation.getLocation()))
			{
				continue;
			}
			else
			{
				addNodeForLeft(currentLocation);
				addNodeForDown(currentLocation);
				addNodeForUp(currentLocation);
				addNodeForRight(currentLocation);

				this.closedMoves.add(currentLocation.getLocation());

				if (isAtDestination(currentLocation))
				{
					return currentLocation.getPath();
				}
			}

		}

		this.core.mapIsUnsolvable = true;
		return null;
	}

	boolean isAtDestination(LocationNode currentLocation)
	{
		Point curPoint = currentLocation.getLocation();

		return curPoint.equals(this.endLocation) || // for going to a flag
		(this.core.flags.contains(this.startLocation) && // make sure we are
															// coming back from
															// a flag
		this.core.flagDropOffLocations.contains(curPoint));
	}

	void addSolutionIfShorter(LinkedList<Point> path)
	{
		int pathLength = path.size();
		this.core.printPath(path);

		if (this.solutions.isEmpty() || this.solutions.peek().size() > pathLength)
		{
			this.solutions.push(path);
		}
	}

	private void addNodeForRight(LocationNode currentLocation)
	{
		int x = (int) (currentLocation.getLocation().getX() + 1);
		int y = (int) currentLocation.getLocation().getY();
		Point loc = new Point(x, y);
		if (!isThisSpaceCurrentlyOccupiedByAMinion(loc) && !this.core.wallPositions.contains(loc) && x < this.core.width && !this.closedMoves.contains(loc))
		{
			LinkedList<Point> path = new LinkedList<Point>();
			path.addAll(currentLocation.getPath());
			path.add(loc);
			this.openMoves.add(new LocationNode(new Point(x, y), path, this.endLocation));
		}
	}

	private void addNodeForUp(LocationNode currentLocation)
	{
		int x = (int) (currentLocation.getLocation().getX());
		int y = (int) currentLocation.getLocation().getY() - 1;
		Point loc = new Point(x, y);
		if (!isThisSpaceCurrentlyOccupiedByAMinion(loc) && !this.core.wallPositions.contains(loc) && y >= 0 && !this.closedMoves.contains(loc))
		{
			LinkedList<Point> path = new LinkedList<Point>();
			path.addAll(currentLocation.getPath());
			path.add(loc);
			this.openMoves.add(new LocationNode(new Point(x, y), path, this.endLocation));
		}
	}

	private void addNodeForDown(LocationNode currentLocation)
	{
		int x = (int) (currentLocation.getLocation().getX());
		int y = (int) currentLocation.getLocation().getY() + 1;
		Point loc = new Point(x, y);
		if (!isThisSpaceCurrentlyOccupiedByAMinion(loc) && !this.core.wallPositions.contains(loc) && y < this.core.height && !this.closedMoves.contains(loc))
		{
			LinkedList<Point> path = new LinkedList<Point>();
			path.addAll(currentLocation.getPath());
			path.add(loc);
			this.openMoves.add(new LocationNode(new Point(x, y), path, this.endLocation));

		}
	}

	private void addNodeForLeft(LocationNode currentLocation)
	{
		int x = (int) (currentLocation.getLocation().getX() - 1);
		int y = (int) currentLocation.getLocation().getY();
		Point loc = new Point(x, y);
		if (!isThisSpaceCurrentlyOccupiedByAMinion(loc) && !this.core.wallPositions.contains(loc) && x >= 0 && !this.closedMoves.contains(loc))
		{
			LinkedList<Point> path = new LinkedList<Point>();
			path.addAll(currentLocation.getPath());
			path.add(loc);
			this.openMoves.add(new LocationNode(new Point(x, y), path, this.endLocation));
		}
	}
	
	protected boolean isThisSpaceCurrentlyOccupiedByAMinion(Point loc)
	{
		ArrayList<Player> players = this.core.hiveMind.ourPlayers;
		for (Player player : players)
		{
			if (player.currentLocation.equals(loc)) return true;
		}
		
		return false;
	}

	public LinkedList<Point> solve(Point currentLocation, Point flag)
	{

		this.startLocation = currentLocation;
		this.endLocation = flag;
		return solve();
	}

}
