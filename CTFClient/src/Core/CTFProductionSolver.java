package Core;

import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import Datastructs.LocationNode;
import Datastructs.ProductionList;

/**
 * This class is called per player, since players can move any direction at
 * anytime, I think it would be best to not do the same production on all the
 * players all at once.
 * 
 * @author lprestonsegoiii. Created Oct 3, 2010.
 */
public class CTFProductionSolver extends CTFHeuristicSolver
{
	ProductionList openMoves;
	public CTFProductionSolver(CTFCore core)
	{
		super(core);
	}

	@Override
	public LinkedList<Point> solve()
	{
		openMoves = new ProductionList(this.core, this, shortestDistance);;
		closedMoves = new HashSet<Point>();
		System.out.println("Productioning from: " + this.startLocation.x + "x" + this.startLocation.y + " to " + this.endLocation.x + "x" + this.endLocation.y);

		LocationNode currentLocation, temp;

		currentLocation = new LocationNode(this.startLocation, new LinkedList<Point>(), this.endLocation);

		currentLocation.getPath().add(this.startLocation);
		this.openMoves.add(currentLocation);


		while (!this.openMoves.isEmpty())
		{
			currentLocation = this.openMoves.remove();
			if (this.closedMoves.contains(currentLocation.getLocation()))
			{
				continue;
			}
			else
			{
				
				this.openMoves.addLeft(currentLocation);
				this.openMoves.addDown(currentLocation);
				this.openMoves.addUp(currentLocation);
				this.openMoves.addRight(currentLocation);
				
				this.closedMoves.add(currentLocation.getLocation());
				
				if (isAtDestination(currentLocation))
				{
					return currentLocation.getPath();
				}
			}
		}
		
		return null;
	}

}
