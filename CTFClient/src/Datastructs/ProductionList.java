package Datastructs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import Core.CTFCore;
import Core.CTFProductionSolver;
import Entities.Player;

/**
 * The brute of the production system.
 *
 * @author lprestonsegoiii.
 *         Created Oct 4, 2010.
 */
public class ProductionList extends PriorityQueue<LocationNode>
{
	private CTFCore	core;
	private CTFProductionSolver	prod;

	public ProductionList(CTFCore core, CTFProductionSolver prod, Comparator<LocationNode> comparator)
	{
		super(50, comparator);
		this.core = core;
		this.prod = prod;
	}
	
	public void addLeft(LocationNode locNode)
	{
		LocationNode temp = testNode(locNode, 'L');
		if (temp != null) this.add(temp);
	}
	public void addDown(LocationNode locNode)
	{
		LocationNode temp = testNode(locNode, 'D');
		if (temp != null) this.add(temp);
	}
	public void addUp(LocationNode locNode)
	{
		LocationNode temp = testNode(locNode, 'U');
		if (temp != null) this.add(temp);
	}
	public void addRight(LocationNode locNode)
	{
		LocationNode temp = testNode(locNode, 'R');
		if (temp != null) this.add(temp);	
	}
	
	
	/**
	 * copied from CTFHeuristic Solver. Make sure they stay synced
	 *
	 * @param currentLocation
	 * @param direction
	 * @return
	 */
	private LocationNode testNode(LocationNode currentLocation, char direction)
	{
		int x = (int) currentLocation.getLocation().getX();
		int y = (int) currentLocation.getLocation().getY();	
		boolean inBounds = true;
		
		switch (direction)
		{
			case 'L': x--; inBounds = x >= 0; break;
			case 'D': y++; inBounds = y < this.core.height; break;
			case 'U': y--; inBounds = y >= 0; break;
			case 'R': x++; inBounds = x < this.core.width; break;
		}
		
		Point newLocation = new Point(x,y);
		
		if(!isThisSpaceCurrentlyOccupiedByAMinion(newLocation) &&
				!this.core.wallPositions.contains(newLocation) && 
				!this.prod.closedMoves.contains(newLocation) &&
				!isCloseToOpposingTeamMember(newLocation) &&
				inBounds)
		{
			LinkedList<Point> path = new LinkedList<Point>();
			path.addAll(currentLocation.getPath());
			path.add(newLocation);
			return new LocationNode(new Point(x, y), path, this.prod.endLocation);

		}
		return null; // invalid point
	}
	
	// we want this to return false
	private boolean isCloseToOpposingTeamMember(Point newLocation)
	{
//		return this.core.otherTeam.dangerZones.contains(newLocation);
		Point result = this.core.otherTeam.getClosestPlayer(newLocation);
		if (result != null)
			return  result.distance(newLocation) <= 2;
		else
			return false;
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

}
