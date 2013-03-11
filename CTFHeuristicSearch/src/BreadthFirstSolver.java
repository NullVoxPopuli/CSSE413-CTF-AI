import java.awt.Point;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;


/**
 * TODO Put here a description of what this class does.
 *
 * @author vader.
 *         Created Sep 8, 2010.
 */
public class BreadthFirstSolver
{
	HashSet<Point> closedMoves = new HashSet<Point>();
	Queue<LocationNode> openMoves = new LinkedList<LocationNode>();
	private Main main;
	int	numOfTouchedNodes;
	
	
	/**
	 * TODO Put here a description of what this constructor does.
	 *
	 * @param main
	 */
	public BreadthFirstSolver(Main main)
	{
		this.main = main;
	}

	void solve() throws Exception{
		long before = System.currentTimeMillis();
		System.out.println("Beginning CTF solver NOW!");

		LocationNode currentLocation = new LocationNode(this.main.startPosition, new LinkedList<Point>()); // start where the player starts
		currentLocation.getPath().add(this.main.startPosition);
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

				
				this.closedMoves.add(currentLocation.getLocation());
				
				if (currentLocation.getLocation().equals(this.main.endPosition)){
					System.out.println("Found A Solution");
					
					this.addSolutionIfShorter(currentLocation.getPath());
				}
			}		
			
		}
		
		
		sendQuickestSolutionToServer();
		System.out.println("Number of touched Nodes: " + this.numOfTouchedNodes);
		System.out.println("Number of closed Nodes: " + this.closedMoves.size());
		System.out.println("Number of open Nodes: " + this.openMoves.size());
		System.out.println("Solving took " + (System.currentTimeMillis() - before) + "ms");
	}
	
	void addSolutionIfShorter(LinkedList<Point> path) {
		int pathLength = path.size();
		this.main.printPath(path);
		if (this.main.solutions.isEmpty() || this.main.solutions.peek().size() > pathLength){
			this.main.solutions.push(path);
		}
	}
	
	void sendQuickestSolutionToServer() throws IOException, CTFException {
		if (this.main.solutions.isEmpty()){
			System.out.println("No Solutions");
			this.main.connection.SendPacket(new Packet(this.main.protocol, "MakeMove", new Object[]{"Unsolvable"}));
		}else {
			this.main.pathToSendToServer.addAll(this.main.solutions.pop());
			this.main.pathSentToServer.addAll(this.main.pathToSendToServer);
			this.main.currentPosition = this.main.startPosition;
			this.main.sendMoveToServer();
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
			this.openMoves.add(new LocationNode(new Point(x,y), path));
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
			this.openMoves.add(new LocationNode(new Point(x,y), path));
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
			this.openMoves.add(new LocationNode(new Point(x,y), path));
			
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
			this.openMoves.add(new LocationNode(new Point(x,y), path));
		}		
	}
}
