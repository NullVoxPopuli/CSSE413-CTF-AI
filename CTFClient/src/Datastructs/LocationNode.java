package Datastructs;

import java.awt.Point;
import java.util.LinkedList;

public class LocationNode
{
	private Point				location;
	private LinkedList<Point>	path;
	public double			distance;

	public LocationNode(Point location, LinkedList<Point> path)
	{
		this.setLocation(location);
		this.setPath(path);
	}

	public LocationNode(Point location, LinkedList<Point> path, float distance)
	{
		this.setLocation(location);
		this.setPath(path);
		this.distance = distance;
	}

	public LocationNode(Point location, LinkedList<Point> path, Point destination)
	{
		this.setLocation(location);
		this.setPath(path);
		this.distance = Point.distance(location.y, location.x, destination.y, destination.x);
	}

	public void setLocation(Point location2)
	{
		this.location = location2;
	}

	public Point getLocation()
	{
		return location;
	}

	public void setPath(LinkedList<Point> path2)
	{
		this.path = path2;
	}

	public LinkedList<Point> getPath()
	{
		return path;
	}
	
	public String toString()
	{
		return "(" + this.location.x + "," + this.location.y + ")";
	}
}
