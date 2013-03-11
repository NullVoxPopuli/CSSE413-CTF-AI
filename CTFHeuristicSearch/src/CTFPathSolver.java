import java.awt.Point;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;


/**
 * TODO Put here a description of what this class does.
 *
 * @author vader.
 *         Created Sep 8, 2010.
 */
public class CTFPathSolver
{
	HashSet<Point> closedMoves = new HashSet<Point>();
	
	Comparator<LocationNode> shortestDistance = new Comparator<LocationNode>(){
		public int compare(LocationNode a, LocationNode b){
			return (a.distance > b.distance ? 1 : (a.distance < b.distance ? -1 : 0));
		}
	};
	
	Comparator<LocationNode> longDistance = new Comparator<LocationNode>(){
		public int compare(LocationNode a, LocationNode b){
			return (a.distance < b.distance ? 1 : (a.distance > b.distance ? -1 : 0));
		}
	};
	
	//PriorityQueue<LocationNode> openMoves = new PriorityQueue<LocationNode>(50, longDistance);
	PriorityQueue<LocationNode> openMoves = new PriorityQueue<LocationNode>(50, shortestDistance);
	//Queue<LocationNode> openMoves = new LinkedList<LocationNode>(); // BreadthFirst
	private Main main;
	int	numOfTouchedNodes;
	private Point	startLocation;
	private Point	endLocation;
	LinkedList<Point> solution;
	
	Thread thisThread;
	int	pathNumber;
	String message;
	Stack<LinkedList<Point>> solutions = new Stack<LinkedList<Point>>();

	
	public CTFPathSolver(Main main, Point startLocation, Point endLocation)
	{
		this.main = main;
		this.startLocation = startLocation;
		this.endLocation = endLocation;
	}

	LinkedList<Point> solve() throws Exception{
//		long before = System.currentTimeMillis();
		System.out.println("Solving path for points " + this.startLocation.x + "x" + this.startLocation.y + 
				" to " + this.endLocation.x + "x" + this.endLocation.y);

		LocationNode currentLocation = new LocationNode(this.startLocation, new LinkedList<Point>(), this.endLocation); // start where the player starts
		currentLocation.getPath().add(this.startLocation);
		this.openMoves.add(currentLocation);
		
		// LOOP!
		while(!this.openMoves.isEmpty()){
			currentLocation = this.openMoves.remove();
			if (this.closedMoves.contains(currentLocation.getLocation())){
				continue;
			} else {
				addNodeForLeft(currentLocation);
				addNodeForDown(currentLocation);
				addNodeForUp(currentLocation);
				addNodeForRight(currentLocation);
				this.numOfTouchedNodes++;
				this.main.numberOfTouchedNodes++;
				
				this.closedMoves.add(currentLocation.getLocation());
				
				if(isAtDestination(currentLocation)){
					//this.addSolutionIfShorter(currentLocation.getPath());
					//this.main.printSolutionPath(currentLocation.getPath());
					return currentLocation.getPath();
				}
			}		
			
		}
//		System.out.println("Number of closed Nodes: " + this.closedMoves.size());
//		System.out.println("Number of open Nodes: " + this.openMoves.size());
//		System.out.println("Solving took " + (System.currentTimeMillis() - before) + "ms");
//		
		this.main.mapIsUnsolvable = true;
		return null;
	}
	
	boolean isAtDestination(LocationNode currentLocation)
	{	
		Point curPoint = currentLocation.getLocation();

		return curPoint.equals(this.endLocation) || // for going to a flag
		(this.main.flags.contains(this.startLocation) && // make sure we are coming back from a flag
				this.main.flagDropOffLocations.contains(curPoint));
	}
	
	void addSolutionIfShorter(LinkedList<Point> path) {
		int pathLength = path.size();
		this.main.printPath(path);

		if (this.solutions.isEmpty() || this.solutions.peek().size() > pathLength){
			this.solutions.push(path);
		}
	}
	
	private void addNodeForRight(LocationNode currentLocation) {
		int x = (int) (currentLocation.getLocation().getX() + 1);
		int y = (int) currentLocation.getLocation().getY();
		Point loc = new Point(x,y);
		if(! this.main.wallPositions.contains(loc) && x < this.main.width && !this.closedMoves.contains(loc)){
			LinkedList<Point> path = new LinkedList<Point>();
			path.addAll(currentLocation.getPath());
			path.add(loc);
			this.openMoves.add(new LocationNode(new Point(x,y), path, this.endLocation));
		}
	}

	private void addNodeForUp(LocationNode currentLocation) {
		int x = (int) (currentLocation.getLocation().getX());
		int y = (int) currentLocation.getLocation().getY() - 1;
		Point loc = new Point(x,y);
		if(! this.main.wallPositions.contains(loc) && y >= 0 && !this.closedMoves.contains(loc)){
			LinkedList<Point> path = new LinkedList<Point>();
			path.addAll(currentLocation.getPath());
			path.add(loc);
			this.openMoves.add(new LocationNode(new Point(x,y), path, this.endLocation));
		}		
	}

	private void addNodeForDown(LocationNode currentLocation) {
		int x = (int) (currentLocation.getLocation().getX());
		int y = (int) currentLocation.getLocation().getY() + 1;
		Point loc = new Point(x,y);
		if(! this.main.wallPositions.contains(loc) && y < this.main.height && !this.closedMoves.contains(loc)){
			LinkedList<Point> path = new LinkedList<Point>();
			path.addAll(currentLocation.getPath());
			path.add(loc);
			this.openMoves.add(new LocationNode(new Point(x,y), path, this.endLocation));
			
		}		
	}

	private void addNodeForLeft(LocationNode currentLocation) {
		int x = (int) (currentLocation.getLocation().getX() - 1);
		int y = (int) currentLocation.getLocation().getY();
		Point loc = new Point(x,y);
		if(! this.main.wallPositions.contains(loc) && x >= 0 && !this.closedMoves.contains(loc)){
			LinkedList<Point> path = new LinkedList<Point>();
			path.addAll(currentLocation.getPath());
			path.add(loc);
			this.openMoves.add(new LocationNode(new Point(x,y), path, this.endLocation));
		}		
	}
}
