import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public class Main implements ConnectionListener {

	private Map<String, Object> currentPacket;
	Protocol protocol = new Protocol("CTFProtocol.txt");
	Connection connection = new Connection(this.protocol, "localhost", 2837);
	
	Point startPosition;
	Point endPosition;
	Point currentPosition;
	HashSet<Point> flagDropOffLocations = new HashSet<Point>();
	Point homeBase;
	int numOfPlayers;
	int numOfBases;
	int numOfFlags = 0;
	
	ArrayList<Point> flags = new ArrayList<Point>();
	HashSet<Point> wallPositions = new HashSet<Point>();
	
	Byte height;
	Byte width;
	
	Stack<LinkedList<Point>> solutions = new Stack<LinkedList<Point>>();
	
	Queue<Point> pathToSendToServer = new LinkedList<Point>();
	Queue<Point> pathSentToServer = new LinkedList<Point>();
	
	Map<Integer, LinkedList<Point>> paths = new HashMap<Integer, LinkedList<Point>>();

	int numberOfTouchedNodes = 0;
	
	private String	startState;
	HashSet<CTFPathThreadHelper> pathFinders = new HashSet<CTFPathThreadHelper>() ;
	boolean	mapIsUnsolvable = false;
	private boolean	useThreads = false;
	
	public static void main(String[] arstring) throws Exception {
		new Main();
	}
	
	/**
	 * TODO Put here a description of what this constructor does.
	 *
	 * @throws Exception
	 */
	public Main() throws Exception
	{
		// Load the protocol.
		
		// Create the connection and tell it to send incoming packets this way.
		this.connection.AddConnectionListener(this);
		
		// Send a "hello" message.
		this.connection.SendPacket(new Packet(this.protocol, "SignIn", new Object[]{"L. Preston Sego III"}));
		
		// Request the scenario list and player list.
		// This is mainly for testing purposes, but could be useful.
		this.connection.SendPacket(new Packet(this.protocol, "ListScenarios", new Object[]{}));
		this.connection.SendPacket(new Packet(this.protocol, "ListPlayers", new Object[]{}));
		
		// Try to make a game.
		this.connection.SendPacket(new Packet(this.protocol, "CreateScenario", new Object[]{"A2-P4"}));
		
		// Ask about the game state.
		
		this.connection.SendPacket(new Packet(this.protocol, "RequestGameState", new Object[]{}));

	}

	void sendMoveToServer() throws IOException, CTFException {
		if (!pathToSendToServer.isEmpty()){
			Point nextPosition = pathToSendToServer.remove();
			this.pathSentToServer.add(nextPosition);
			this.connection.SendPacket(
					new Packet(
							this.protocol, 
							"MakeMove", 
							new Object[]{
									this.currentPosition.x + "x" + this.currentPosition.y + "-" +
									nextPosition.x + "x" + nextPosition.y
									}));
			this.currentPosition = nextPosition;
		}	
	}


	void printPath(LinkedList<Point> path) {
		for (Point point : path) {
			System.out.print((int)point.getX() + "x" + (int)point.getY() + " to ");
		}
		System.out.println("");
		
	}


	@Override
	public void PacketReceived(Packet packet) {
		//System.out.println("Received: " + packet.Encode());
		this.currentPacket = packet.Data;
		String packetType = (String)this.currentPacket.get("Type");
		
		if (packetType.equals("GameState")){
			printAndSetUpGame();
			try
			{
				if (useThreads ) beginSolvingWithThreads();
				else beginSolving();
			}
			catch (Exception exception)
			{
				// TODO Auto-generated catch-block stub.
				exception.printStackTrace();
			}
		} else if (packetType.equals("YourTurn")){
			try{
				sendMoveToServer();
			}
			catch (IOException exception){
				exception.printStackTrace();
			}
			catch (CTFException exception){
				exception.printStackTrace();
			}
			
			if (this.pathToSendToServer.size() < 1){
					System.err.println("Game was not completed");
					printSolutionPath(this.pathSentToServer);
					System.exit(0);
			}
		} else if(packetType.equals("GameOver")){
			System.out.println("The winner is: " + this.currentPacket.get("Winner"));
			System.out.println("Number of touched Nodes: " + this.numberOfTouchedNodes);

			if(!this.currentPacket.get("Winner").equals("no winner")){
				printSolutionPath(this.pathSentToServer);
				System.exit(0);

			} 
		} else if (packetType.equals("Fail") && ((String)this.currentPacket.get("Reason")).contains("Unable to select username.")){
			System.err.println("You are currently logged in...");
			System.err.println("Let me log out for you...");
			System.err.println("Please restart this program...");
			System.exit(0);
		} else if (packetType.equals("Fail")){
			System.err.println("Something failed");
			System.out.println("Received: " + packet.Encode());

		}
		
		
	}

	void beginSolving(){

		if (this.numOfBases > 0){
			try
			{
				LinkedList<Point> temp;
				Point lastStart = null;
				long before = System.currentTimeMillis();

				temp = new CTFPathSolver(this, this.startPosition, this.flags.get(0)).solve();
				if (temp == null) { sendUnSolvable(); return; }
				this.pathToSendToServer.addAll(temp);
				
				temp = new CTFPathSolver(this, this.flags.get(0), this.homeBase).solve();
				if (temp == null) { sendUnSolvable(); return; }
				lastStart = temp.getLast();
				this.pathToSendToServer.addAll(temp);
				
				for(int curFlag = 1; curFlag < this.flags.size(); curFlag++){
					LinkedList<Point> tem = new CTFPathSolver(this, this.flags.get(curFlag), this.homeBase).solve();
					if (tem == null) { sendUnSolvable(); return; }
					this.pathToSendToServer.addAll(this.getReversedPath(tem));
					this.pathToSendToServer.addAll(tem);
					lastStart = tem.getLast();
				}
				
				// go to base
				this.wallPositions.remove(this.homeBase);
				temp = new CTFPathSolver(this, lastStart, this.homeBase).solve();
				if (temp == null) { sendUnSolvable(); return; }
				this.pathToSendToServer.addAll(temp);

				System.out.println("Completing the whole thing took: " + (System.currentTimeMillis() - before) + "ms");

				this.currentPosition = this.startPosition;

				sendMoveToServer();

			}
			catch (Exception exception)
			{
				// TODO Auto-generated catch-block stub.
				exception.printStackTrace();
			}
		}

	}

	private void beginSolvingWithThreads() throws Exception, Exception{
		LinkedList<Point> temp;
		Point lastStart = null;
		long before = System.currentTimeMillis();


		if(this.numOfBases > 0){
			int pathNumber = 2;
			this.pathFinders.add(new CTFPathThreadHelper(this, this.startPosition,this.flags.get(0), 0, "Path 0: Going to flag 0 from start"));
			this.pathFinders.add(new CTFPathThreadHelper(this, this.flags.get(0),this.homeBase, 1, "Path 1: Going to base from flag 0"));
			
			for(int curFlag = 1; curFlag < this.flags.size(); curFlag++){
				this.pathFinders.add(new CTFPathThreadHelper(this, this.homeBase,this.flags.get(curFlag), pathNumber,
						"Path: " + pathNumber + " Going to flag from " + curFlag + " from base"));
				pathNumber++;
			}

		}
		while(!this.pathFinders.isEmpty() && !this.mapIsUnsolvable){
			try{
				Thread.sleep(10);
			}
			catch (InterruptedException exception){
				exception.printStackTrace();
			}
		}
		
		if (this.mapIsUnsolvable){
			sendUnSolvable();
			this.pathFinders = null;
			return;
		}
		
		temp = this.paths.get(0);
		this.pathToSendToServer.addAll(temp);
		
		temp = this.paths.get(1);
		this.pathToSendToServer.addAll(temp);
		lastStart = temp.getLast();


		for(int i = 2; i < this.paths.size(); i++){
			LinkedList<Point> tem = this.paths.get(i);
			this.pathToSendToServer.addAll(getReversedPath(tem));
			this.pathToSendToServer.addAll(tem);
			lastStart = tem.getLast();
		}
		
		// go to base
		this.wallPositions.remove(this.homeBase);
		temp = new CTFPathSolver(this, lastStart, this.homeBase).solve();
		if (temp == null) { sendUnSolvable(); return; }
		this.pathToSendToServer.addAll(temp);

		
		
		System.out.println("aCompleting the whole thing took: " + (System.currentTimeMillis() - before) + "ms");
		

		this.currentPosition = this.startPosition;

		sendMoveToServer();

	}
	
	private void sendUnSolvable() throws Exception, CTFException
	{
		System.out.println("Sending UNSOLVABLE");
		this.connection.SendPacket(new Packet(this.protocol, "MakeMove", new Object[]{"Unsolvable"}));		
	}

	private LinkedList<Point> getReversedPath(LinkedList<Point> linkedList)
	{
		LinkedList<Point> result = new LinkedList<Point>();
		for (Point point : linkedList){
			result.add(0, point);
		}
		return result;
	}

	void printSolutionPath(Queue<Point> path){
		int curLinPos = 0;
		char curChar;
		for(int i = 0; i < this.height; i++){
			for (int j = 0; j < this.width; j++){
				curLinPos = j*this.width + i;
				curChar = this.startState.charAt(curLinPos);
				if(curChar != 'F' && curChar != '0' && path.contains(new Point(j, i))){
					System.out.print("•");
				} else {
					System.out.print(curChar);
				}
			}
			System.out.println("");
		}
	}

	private void printAndSetUpGame() {
		this.height = (Byte) this.currentPacket.get("Height");
		this.width =(Byte) this.currentPacket.get("Width");
		
		String state = (String)this.currentPacket.get("State");
		this.startState = state;
		System.out.println(this.width +"x" +this.height);
		int curLinPos = 0;
		char curChar;
		for(int i = 0; i < this.height; i++){ // change rows
			for (int j = 0; j < this.width; j++){ // change cols
				curLinPos = j*this.width + i;
				//System.out.print(state.charAt(curLinPos));
				curChar = state.charAt(curLinPos);
				if (curChar == '0'){
					this.startPosition = new Point(j,i);
				} else if (curChar == 'F'){
					this.flags.add(new Point(j,i));
					this.numOfFlags++;
				} else if (curChar == 'a'){
					addAdjacentPointsToDropOffLocations(new Point(j, i));
					this.homeBase = new Point(j,i);
					this.wallPositions.add(new Point(j, i));
					this.numOfBases++;
				} else if (curChar == 'W'){
					this.wallPositions.add(new Point(j,i));
				}
			}
			//System.out.println("");
		}
	}

	private void addAdjacentPointsToDropOffLocations(Point p){
		this.flagDropOffLocations.add(new Point(p.x - 1, p.y)); 
		this.flagDropOffLocations.add(new Point(p.x, p.y + 1)); 
		this.flagDropOffLocations.add(new Point(p.x, p.y - 1)); 
		this.flagDropOffLocations.add(new Point(p.x + 1, p.y)); 
	}


}
