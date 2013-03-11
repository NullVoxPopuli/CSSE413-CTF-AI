package Datastructs;

import java.awt.Point;
import java.util.ArrayList;

import Entities.Player;

/**
 * Used for production sets
 *
 * @author lprestonsegoiii.
 *         Created Oct 3, 2010.
 */
public class GameState
{
	private ArrayList<Player>	players;
	private ArrayList<Point>	flags;

	public GameState(ArrayList<Player> players, ArrayList<Point> flags)
	{
		this.players = players;
		this.flags = flags;
	}
	
	public GameState(ArrayList<Player> players)
	{
		this.players = players;
	}
	
	public boolean equals(GameState other)
	{
		if (other.players.size() == this.players.size() &&
				other.flags.size() == this.flags.size())
		{
			for (Player curPlayer : players)
			{
				if (!other.players.contains(curPlayer)){
					return false;
				}
			}
			for (Point curFlag : flags){
				if (!other.flags.contains(curFlag)){
					return false;
				}
			}
		}else{
			return false;
		}
		
		return true;
	}
	
	public boolean equalsWithoutFlagPositions(GameState other)
	{
		if (other.players.size() == this.players.size())
		{
			for (Player curPlayer : players)
			{
				if (!other.players.contains(curPlayer)){
					return false;
				}
			}
		}else{
			return false;
		}
		
		return true;
	}
}
