import java.awt.Point;
import java.util.LinkedList;


public class DistanceFromGoalNode extends LocationNode{

	float distance;
	/**
	 *  Put here a description of what this constructor does.
	 *
	 * @param location
	 * @param path
	 */
	public DistanceFromGoalNode(Point location, LinkedList<Point> path, float distance)
	{
		super(location, path);
		this.setDistance(distance);
	}
	/**
	 * Returns the value of the field called 'distance'.
	 * @return Returns the distance.
	 */
	public float getDistance()
	{
		return this.distance;
	}
	/**
	 * Sets the field called 'distance' to the given value.
	 * @param distance The distance to set.
	 */
	public void setDistance(float distance)
	{
		this.distance = distance;
	}

}
