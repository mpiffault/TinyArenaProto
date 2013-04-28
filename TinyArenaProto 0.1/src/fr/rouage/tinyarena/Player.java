package fr.rouage.tinyarena;

import fr.rouage.tinyarena.TinyArena.CellState;

public class Player
{
	private int xPos;
	private int yPos;
	private TinyArena.CellState[][] mCells;

	public Player(String name, int xStart, int yStart,
			TinyArena.CellState[][] mCells)
	{
		this.xPos = xStart;
		this.yPos = yStart;
		this.mCells = mCells;
	}

	public int getX()
	{
		return xPos;
	}

	public int getY()
	{
		return yPos;
	}

	public void setX(int xPos)
	{
		if (xPos < 0) xPos = 0;
		else if (xPos >= Defaults.GRID_SIDE) xPos = Defaults.GRID_SIDE - 1;
		else
			this.xPos = xPos;
	}

	public void setY(int yPos)
	{
		if (yPos < 0) yPos = 0;
		else if (yPos >= Defaults.GRID_SIDE) yPos = Defaults.GRID_SIDE - 1;
		else
			this.yPos = yPos;
	}

	public void addX(int xToAdd)
	{
		int newXPos = xPos + xToAdd;
		if (newXPos >= 0 && newXPos < Defaults.GRID_SIDE)
			if (mCells[newXPos][yPos] != CellState.MUR
					&& mCells[newXPos][yPos] != CellState.PERSO)
				xPos = newXPos;
	}

	public void addY(int yToAdd)
	{
		int newYPos = yPos + yToAdd;
		if (newYPos >= 0 && newYPos < Defaults.GRID_SIDE)
			if (mCells[xPos][newYPos] != CellState.MUR
					&& mCells[xPos][newYPos] != CellState.PERSO)
				yPos = newYPos;
	}

}
