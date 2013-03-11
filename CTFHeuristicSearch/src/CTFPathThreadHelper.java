import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;


/**
 * TODO Put here a description of what this class does.
 *
 * @author vader.
 *         Created Sep 8, 2010.
 */
public class CTFPathThreadHelper implements Runnable
{
	HashSet<Point> closedMoves = new HashSet<Point>();
	Queue<LocationNode> openMoves = new LinkedList<LocationNode>();
	private Main main;
	int	numOfTouchedNodes;
	private Point	startLocation;
	private Point	endLocation;
	LinkedList<Point> solution;
	
	Thread thisThread;
	int	pathNumber;
	String message;
	Stack<LinkedList<Point>> solutions = new Stack<LinkedList<Point>>();

	public CTFPathThreadHelper(
			Main main, 
			Point startLocation, 
			Point endLocation, int pathNumber,
			String message)	{
		this.main = main;
		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.pathNumber = pathNumber;
		this.message = message;
		thisThread = new Thread(this);
		thisThread.start();
	}
	
	
	void solve() throws Exception{
		long before = System.currentTimeMillis();
		CTFPathSolver solver = new CTFPathSolver(this.main, this.startLocation, this.endLocation);
		this.solution = solver.solve();

		finish();
		
		System.out.println("Solving took " + (System.currentTimeMillis() - before) + "ms");	
		this.quitThisProcess();
	
	}

	private void finish(){
		
//		if (this.solution.get(0).equals(this.main.homeBase)){
//			this.solution.remove(0);
//		}
//		if (this.solution.get(this.solution.size() - 1).equals(this.main.homeBase)){
//			this.solution.remove(this.solution.size() - 1);
//		}
		
		this.main.paths.put(this.pathNumber, this.solution);
	}
	

	private void quitThisProcess()
	{

//		System.out.println(this.message);
		System.out.println("Thread Completed for path (" + 
				this.startLocation.x + "," + this.startLocation.y + ") to (" +
				this.endLocation.x + "," + this.endLocation.y + ")");
//		System.out.println("Number of touched Nodes: " + this.numOfTouchedNodes);
//		System.out.println("Number of closed Nodes: " + this.closedMoves.size());
//		System.out.println("Number of open Nodes: " + this.openMoves.size());
		this.main.pathFinders.remove(this);
		this.stop();
	}

	private void stop(){
		this.thisThread = null;
		
	}

	@Override
	public void run(){
		try{
			this.solve();
		}
		catch (Exception exception){
			exception.printStackTrace();
		}
		
	}
}
