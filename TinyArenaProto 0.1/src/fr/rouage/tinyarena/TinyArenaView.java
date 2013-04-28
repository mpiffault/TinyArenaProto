package fr.rouage.tinyarena;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

public class TinyArenaView extends View implements OnTouchListener
{
	private TinyArena mArena;
	Paint paint = new Paint();
	int mWidth;
	int mHeight;
	boolean paused = false;
	float xDown;
	float yDown;

	public TinyArenaView(Context context)
	{
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);

		setup(context);
		this.setOnTouchListener(this);
	}

	private void setup(Context context)
	{
		Display dsp = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		mWidth = dsp.getWidth();
		mHeight = dsp.getHeight();
		if (mArena == null)
		{
			Toast.makeText(context, R.string.initial_toast, Toast.LENGTH_LONG)
					.show();
			createNewTinyArena(context);
		}
	}

	private void createNewTinyArena(Context context)
	{
		mArena = new TinyArena(mWidth, mHeight, context);

		startUpdatingThread();

		//force repaint
		invalidate();
	}

	private void startUpdatingThread()
	{
		Thread thread = new Thread()
		{
			@Override
			public void run()
			{
				while (true)
				{
					if (!paused)
					{
						mArena.update();
						postInvalidate();
						try
						{
							Thread.sleep(50l);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
					else
					{
						try
						{
							Thread.sleep(1000);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		};
		thread.start();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		switch (event.getAction())
		{
		case MotionEvent.ACTION_MOVE:

			boolean moved = mArena.touchMove(event.getX(), event.getY(), xDown,
					yDown);
			if (moved)
			{
				xDown = event.getX();
				yDown = event.getY();
				invalidate();
			}
			break;

		case MotionEvent.ACTION_DOWN:
			xDown = event.getX();
			yDown = event.getY();
			break;

		default:
			break;
		}
		invalidate();
		return true;
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		mArena.draw(canvas, paint);
	}

	public void pause()
	{
		paused = true;
	}

	public void resume()
	{
		paused = false;
	}

	public void reset()
	{
		mArena.reset();
	}

	public void serialize()
	{
		mArena.serialize();
	}

	public void deserialize()
	{
		mArena.deserialize();
	}
}
