package Entities;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;

import Core.CTFCore;


/**
 * TODO Put here a description of what this class does.
 *
 * @author vader.
 *         Created Oct 20, 2010.
 */
public class OtherTeam extends HiveMind
{
	public ArrayList<Enemy> players = new ArrayList<Enemy>();
	public HashSet<Point> dangerZones = new HashSet<Point>();
	
	
	public OtherTeam(CTFCore ctfCore)
	{
		super(ctfCore);
	}

	public void resetPlayers(){
		players.clear();
		dangerZones.clear();;
	}
	
	public void addEnemyPlayer(Enemy enemy)
	{
		players.add(enemy);
		dangerZones.add(new Point(enemy.x + 1, enemy.y)); //right
		dangerZones.add(new Point(enemy.x - 1, enemy.y)); //left
		dangerZones.add(new Point(enemy.x, enemy.y + 1)); // down
		dangerZones.add(new Point(enemy.x, enemy.y - 1)); // up
		
		dangerZones.add(new Point(enemy.x + 3, enemy.y));
		dangerZones.add(new Point(enemy.x - 3, enemy.y));
		dangerZones.add(new Point(enemy.x, enemy.y + 3));
		dangerZones.add(new Point(enemy.x, enemy.y - 3));
		dangerZones.add(enemy);
	}

	public Point getClosestPlayer(Point p)
	{
		Point result = null;
		double shortest = 1000;
		for (Enemy player : players)
		{
			if (result == null || player.distance(p) < shortest){
				result = player;
				shortest = player.distance(p);
			}
		}
		return result;
	}
	
	
	
}
