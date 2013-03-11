package Entities;

import java.awt.Point;

/**
 * TODO Put here a description of what this class does.
 *
 * @author vader.
 *         Created Oct 20, 2010.
 */
public class Enemy extends Point
{
	boolean hasFlag = false;

	
	/**
	 * TODO Put here a description of what this constructor does.
	 *
	 * @param j
	 * @param i
	 */
	public Enemy(int j, int i)
	{
		super(j, i);
	}

	/**
	 * TODO Put here a description of what this constructor does.
	 *
	 * @param j
	 * @param i
	 * @param b
	 */
	public Enemy(int j, int i, boolean b)
	{
		super(j, i);
		this.hasFlag = b;
	}

}
