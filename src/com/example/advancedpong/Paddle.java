package com.example.advancedpong;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class Paddle extends Actor
{
	public enum ScreenSide { LEFT, RIGHT }
	
	boolean isPressed;
	ScreenSide side;
	
	public boolean IsPressed()
	{
		return this.isPressed;
	}
	
	public ScreenSide Side()
	{
		return this.side;
	}
	
	public Paddle(Resources resources, ScreenSide side, float x, float y, double velocityX, double velocityY)
	{
		super(resources, R.drawable.paddle, x, y, velocityX, velocityY);
		this.side = side;
	}
	
	public void Draw(Canvas canvas)
	{
		super.Draw(canvas);
	}
	
	public void Update(double timeElapsed)
	{
		super.Update(timeElapsed);
		
		Context context = MainApplication.getContext();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
    	Point size = new Point();
    	display.getSize(size);
    	int height = size.y;
		
		if (y <= 0 || y + this.height >= height)
		{
			velocityY *= -1;
		}
		
		// Move X
		if (this.isPressed)
		{
			if (this.side == ScreenSide.LEFT)
			{
				this.x += 5;
			}
			else
			{
				this.x -= 5;
			}
		}
	}
}
