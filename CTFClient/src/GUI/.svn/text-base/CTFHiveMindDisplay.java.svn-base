package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JComponent;

import Core.CTFCore;
import Entities.Player;

/**
 * TODO Put here a description of what this class does.
 * 
 * @author lprestonsegoiii. Created Oct 1, 2010.
 */
public class CTFHiveMindDisplay extends JComponent
{
	/**
	 * TODO Put here a description of this field.
	 */
	private static final long	serialVersionUID	= 1L;

	static int					defaultWidth		= 0;
	static int					defaultHeight		= 0;

	int							windowWidth;
	int							windowHeight;

	// 10px border all the way around
	int							verticalOffset		= 10;
	int							horizontalOffset	= 10;

	// everything should go with in here
	int							availableHeight;
	int							availableWidth;

	private Byte				gridHeight;
	private Byte				gridWidth;

	private boolean				gridIsDrawn			= false;

	private int					cellHeight;

	private int					cellWidth;

	private CTFCore				core;

	static
	{

	}

	public CTFHiveMindDisplay(CTFCore core)
	{
		this(core, defaultWidth, defaultHeight);
	}

	public CTFHiveMindDisplay(CTFCore core, int windowWidth, int windowHeight)
	{
		defaultWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		defaultHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

		availableHeight = windowHeight - 20;// (2 * verticalOffset);
		availableWidth = windowWidth - 20;// (2 * horizontalOffset);

		this.windowHeight = windowHeight;
		this.windowWidth = windowWidth;

		this.gridHeight = core.height;
		this.gridWidth = core.width;
		this.core = core;
		this.cellHeight = this.availableHeight / (this.gridHeight);
		this.cellWidth = this.availableWidth / (this.gridWidth);

		this.cellHeight = (this.cellHeight < this.cellWidth ? this.cellHeight : this.cellWidth);
		this.cellWidth = (this.cellWidth < this.cellHeight ? this.cellWidth : this.cellHeight);

		this.setSize(this.windowWidth, this.windowHeight);
		this.setVisible(true);
	}

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		initBoard(g);
	}

	//
	// @Override
	// public void paintComponent(Graphics g)
	// {
	// drawGrid(g);
	// }

	public void initBoard(Graphics g)
	{
		drawGrid(g);

		drawBaseDropOffs(g, core.homeBase);

		for (Player p : core.hiveMind.ourPlayers)
		{
			drawFogOfWar(g, p.currentLocation);
			drawPlayer(g, p);
		}

		for (Point p : core.wallPositions)
		{
			drawWall(g, p);
		}

		drawBase(g, core.homeBase);

		for (Point p : core.hiveMind.flags)
		{
			drawFlag(g, p);
		}
	}

	public void drawPlayer(Graphics g, Player p)
	{
		// later, we should implement a trail or something
		drawInGrid(g, 'P', Color.cyan, p.currentLocation);
	}

	public void drawFlag(Graphics g, Point p)
	{
		drawInGrid(g, 'F', Color.magenta, p);
	}

	public void drawWall(Graphics g, Point p)
	{
		drawInGrid(g, 'W', Color.black, p);
	}

	public void drawFogOfWar(Graphics g, Point p)
	{

	}

	public void drawBaseDropOffs(Graphics g, Point p)
	{
		drawInGrid(g, 'B', Color.LIGHT_GRAY, new Point(p.x + 1, p.y));
		drawInGrid(g, 'B', Color.LIGHT_GRAY, new Point(p.x - 1, p.y));
		drawInGrid(g, 'B', Color.LIGHT_GRAY, new Point(p.x, p.y + 1));
		drawInGrid(g, 'B', Color.LIGHT_GRAY, new Point(p.x, p.y - 1));
	}

	public void drawBase(Graphics g, Point p)
	{
		drawInGrid(g, 'B', Color.green, p);
	}

	public void drawInGrid(Graphics g, char entType, Color color, Point pos)
	{
		int startX = horizontalOffset + (pos.x * cellWidth);
		int startY = verticalOffset + (pos.y * cellHeight);

		g.setColor(color);
		switch (entType)
		{
			case 'W': // Walls
				g.fillRect(startX, startY, cellWidth, cellHeight);
				break;
			case 'B': // our base
				g.fillRect(startX + 1, startY + 1, cellWidth - 1, cellHeight - 1);
				break;
			case 'P': // players
				g.fillOval(startX + 2, startY + 2, cellWidth - 4, cellHeight - 4);
				break;
			case 'F': // flags
				g.fillRect(startX + 4, startY + 4, cellWidth - 8, cellHeight - 8);
				break;
			case 'g': // fog of war
				break;
			default:
				// nothing
				break;
		}
	}

	public void drawGrid(Graphics g)
	{
		gridIsDrawn = true;
		int startX;
		int startY;
		int height = (int) cellHeight;
		int width = (int) cellWidth;
		for (int i = 0; i < this.gridHeight; i++)
		{
			for (int j = 0; j < this.gridWidth; j++)
			{
				startX = horizontalOffset + (j * width);
				startY = verticalOffset + (i * height);

				g.drawRect(startX, startY, width, height);
			}
		}
	}

}
