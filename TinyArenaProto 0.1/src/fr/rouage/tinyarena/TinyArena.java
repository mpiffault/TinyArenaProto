package fr.rouage.tinyarena;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class TinyArena
{
	public enum CellState
	{
		VIDE, MUR, PERSO
	}

	public enum FogState
	{
		UNKNOWN, PARTIALY_KNOWN, KNOWN
	}

	static int GRID_SIDE = Defaults.GRID_SIDE;
	//model
	CellState[][] gameBoard = new CellState[GRID_SIDE][GRID_SIDE];
	FogState[][] warFog = new FogState[GRID_SIDE][GRID_SIDE];
	Player player;

	//Bitmaps
	Bitmap bonhomme;
	Bitmap dirt;
	Bitmap grass;
	Bitmap cobblestone;

	//Alpha fog levels
	public static int KNOW = 0;
	public static int KNOWN_FAR = 75;
	public static int PARTIALY_KNOWN_FAR = 175;
	public static int UNKNOWN = 255;

	// debug
	public int widthTest = 0;
	public int heightTest = 0;
	public float widthTD = 0;
	public float heightTD = 0;
	public float xMove = 0.0f;
	public float yMove = 0.0f;

	int mScaleOffset = 0;
	//gfx
	Rect mCellSize;

	public TinyArena()
	{

	}

	public TinyArena(int width, int height, Context context)
	{
		// Init Bitmaps
		bonhomme = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.char1);
		dirt = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.dirt);
		grass = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.grass);
		cobblestone = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.cobblestone);

		Random r = new Random();
		//debug
		widthTest = width;
		heightTest = height;

		generateGrid(Defaults.WALLS_PERCENT);

		int pX = r.nextInt(GRID_SIDE);
		int pY = r.nextInt(GRID_SIDE);

		while (gameBoard[pX][pY] == CellState.MUR)
		{
			pX = r.nextInt(GRID_SIDE);
			pY = r.nextInt(GRID_SIDE);
		}

		player = new Player("Bob", pX, pY, gameBoard);

		// Choose of the smallest side for calculating cells size
		int boardSideLength = width <= height ? width : height;
		Defaults.CELL_SIDE = boardSideLength / GRID_SIDE;
		mCellSize = new Rect(0, 0, Defaults.CELL_SIDE, Defaults.CELL_SIDE);

	}

	private void generateGrid(float wallPercentage)
	{
		Random r = new Random();
		for (int i = 0; i < GRID_SIDE; i++)
			for (int j = 0; j < GRID_SIDE; j++)
			{
				if (r.nextFloat() > wallPercentage)
					gameBoard[i][j] = CellState.VIDE;
				else
					gameBoard[i][j] = CellState.MUR;
				warFog[i][j] = FogState.UNKNOWN;
			}
		// Player's cell empty
		gameBoard[GRID_SIDE / 2][GRID_SIDE / 2] = CellState.VIDE;
	}

	public void update()
	{
	}

	public void draw(Canvas canvas, Paint paint)
	{
		Rect drawRect = new Rect();
		int x = player.getX();
		int y = player.getY();
		// draw board
		for (int i = 0; i < GRID_SIDE; i++)
		{
			for (int j = 0; j < GRID_SIDE; j++)
			{
				drawRect.set(mCellSize);
				drawRect.offset(i * Defaults.CELL_SIDE, j * Defaults.CELL_SIDE);
				if (Defaults.grid) drawGrid(canvas, paint, drawRect);
				int dX = Math.abs(x - i);
				int dY = Math.abs(y - j);

				switch (gameBoard[i][j])
				{
				case VIDE:
					canvas.drawBitmap(dirt, null, drawRect, null);
					break;
				case MUR:
					canvas.drawBitmap(cobblestone, null, drawRect, null);
					break;
				default:
					break;
				}

				// War fog circle
				if ((dX * dX + dY * dY) < 8)
					warFog[i][j] = FogState.KNOWN;
				else if ((dX * dX + dY * dY) < 15)
				{
					if (warFog[i][j] == FogState.UNKNOWN)
					{
						warFog[i][j] = FogState.PARTIALY_KNOWN;
						paint.setColor(Color.BLACK);
						paint.setAlpha(KNOWN_FAR);
						canvas.drawRect(drawRect, paint);
					}
					else
					{
						paint.setColor(Color.BLACK);
						paint.setAlpha(KNOWN_FAR);
						canvas.drawRect(drawRect, paint);
					}
				}
				else
				{
					paint.setColor(Color.BLACK);
					if (warFog[i][j] == FogState.KNOWN)
						paint.setAlpha(KNOWN_FAR);
					else if (warFog[i][j] == FogState.PARTIALY_KNOWN)
						paint.setAlpha(PARTIALY_KNOWN_FAR);
					else
						paint.setAlpha(UNKNOWN);
					canvas.drawRect(drawRect, paint);
				}
			}
		}
		// draw player
		drawRect.set(mCellSize);
		drawRect.offset(player.getX() * Defaults.CELL_SIDE, player.getY()
				* Defaults.CELL_SIDE);
		if (Defaults.grid) drawGrid(canvas, paint, drawRect);
		canvas.drawBitmap(bonhomme, null, drawRect, null);
		//paint.setColor(Color.BLACK);
		//canvas.drawRect(drawRect, paint);
		//
		//**** debug ****
		//		paint.setColor(Color.RED);
		//		String textA = "xMove:" + xMove + "   yMove:" + yMove;
		//		String textB = "xPos" + player.getX() + " yPos" + player.getY();
		//		canvas.drawText(textA, 0, textA.length(), 10, (GRID_SIDE / 2)
		//				* mCellSize.height(), paint);
		//		canvas.drawText(textB, 0, textB.length(), 10, (GRID_SIDE / 2)
		//				* mCellSize.height() + 20, paint);
	}

	private void drawGrid(Canvas canvas, Paint paint, Rect drawRect)
	{
		paint.setColor(Color.WHITE);
		canvas.drawRect(drawRect, paint);
		drawRect.set(drawRect.left + 1, drawRect.top + 1, drawRect.right - 1,
				drawRect.bottom - 1);
	}

	public boolean touchMove(float x, float y, float xDown, float yDown)
	{

		float deltaX = x - xDown;
		float deltaY = y - yDown;
		xMove = deltaX;
		yMove = deltaY;

		if (Math.abs(deltaX) > Defaults.SENSITIVITY)
		{
			player.addX((int) Math.signum(deltaX));
			return true;
		}
		else if (Math.abs(deltaY) > Defaults.SENSITIVITY)
		{
			player.addY((int) Math.signum(deltaY));
			return true;
		}
		return false;
	}

	public void reset()
	{
		for (int i = 0; i < GRID_SIDE; i++)
		{
			for (int j = 0; j < GRID_SIDE; j++)
			{
				gameBoard[i][j] = CellState.VIDE;
			}
		}
	}

	public void serialize()
	{
		// TODO : old version from Melodrone
		short one = 1;
		for (int i = 0; i < GRID_SIDE; i++)
		{
			short line = 0;
			for (int j = 0; j < GRID_SIDE; j++)
			{
				if (gameBoard[i][j] == CellState.MUR)
				{
					line = (short) (line | (one << j));
				}
			}
		}
	}

	public void deserialize()
	{
		// TODO : old version from Melodrone
		short source[] =
		{ 4, 5, 7, 9, 12, 8, 23, 8, 23, 8, 3, 8, 56, 9, 2, 9 };
		short one = 1;
		int on = 0;
		for (int i = 0; i < GRID_SIDE; i++)
		{
			for (int j = 0; j < GRID_SIDE; j++)
			{
				on = source[i] & (one << j);
				if (on > 0)
					gameBoard[i][j] = CellState.MUR;
				else
					gameBoard[i][j] = CellState.VIDE;
			}
		}
	}
}
