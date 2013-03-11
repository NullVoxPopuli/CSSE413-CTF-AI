package Entities;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 
 * @author lprestonsegoiii. Created Sep 30, 2010.
 */
public class Player extends Entity
{

	public Point	currentLocation;
	private Point	destination;
	public boolean	hasFlag	= false;
	LinkedList<Point>	currentPlannedPath;

	public Player(Point startLocation)
	{
		super();

		this.startLocation = startLocation;
		this.currentLocation = (Point) startLocation.clone();

		motive = Motive.NONE;
		role = Role.ATTACKER;
	}

	public Point getNextLocation()
	{
		return currentLocation;
	}

	public void setLocation(Point last)
	{
		this.currentLocation = last;
	}

	public void setTarget(Point first)
	{
		this.destination = first;
		
	}

	public void setPlannedPath(LinkedList<Point> path)
	{
		if(path.getFirst().equals(currentLocation))
			 path.remove(0);
		this.currentPlannedPath = path;
	}

	public boolean equals(Player other)
	{
		return other.currentLocation == this.currentLocation &&
			other.hasFlag == this.hasFlag;
	}
}
