package Core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import Datastructs.LocationNode;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Oct 3, 2010.
 */
public class CTFHeuristicSolver extends CTFPathSolver
{
	public CTFHeuristicSolver(CTFCore core)
	{
		super(core);
	}
	
	@Override
	public LinkedList<Point> solve()
	{
		// reset the fields
		closedMoves = new HashSet<Point>();
		openMoves = new PriorityQueue<LocationNode>(50, shortestDistance);
		
		System.out.println("Solving path for points " + this.startLocation.x + "x" + this.startLocation.y + " to " + this.endLocation.x + "x" + this.endLocation.y);

		LocationNode currentLocation = new LocationNode(this.startLocation, new LinkedList<Point>(), this.endLocation); // start
		LocationNode tempNode;																											// where
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
				ArrayList<LocationNode> temp = new ArrayList<LocationNode>();
				// test production rules
				tempNode = testNode(currentLocation, 'L');
				if (tempNode != null) temp.add(tempNode);
				tempNode = testNode(currentLocation, 'D');
				if (tempNode != null) temp.add(tempNode);
				tempNode = testNode(currentLocation, 'U');
				if (tempNode != null) temp.add(tempNode);
				tempNode = testNode(currentLocation, 'R');
				if (tempNode != null) temp.add(tempNode);

				
				// heurisitc for choosing which node to do first
				Collections.sort(temp, shortestDistance);
				this.openMoves.addAll(temp);
				
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

	/**
	 * The is the production set method that includes the rules
	 *
	 * @param currentLocation
	 * @param c
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
				!this.closedMoves.contains(newLocation) &&
				inBounds)
		{
			LinkedList<Point> path = new LinkedList<Point>();
			path.addAll(currentLocation.getPath());
			path.add(newLocation);
			return new LocationNode(new Point(x, y), path, this.endLocation);

		}
		return null; // invalid point
	}


}
