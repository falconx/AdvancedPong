package com.example.advancedpong;

import android.content.res.Resources;
import android.graphics.Canvas;

public class Paddle extends Actor
{
	public enum ScreenSide { LEFT, RIGHT }
	
	private final int PADDLE_OUT_DIST = 40; // Percentage.
	
	private boolean isForcedBack;
	private boolean isPressed;
	private ScreenSide side;
	
	double prevVelocityY;

	public boolean getIsPressed()
	{
		return this.isPressed;
	}
	
	public void setIsPressed(boolean isPressed)
	{
		this.isPressed = isPressed;
	}
	
	public ScreenSide Side()
	{
		return this.side;
	}
	
	public boolean getIsForcedBack()
	{
		return this.isForcedBack;
	}
	
	public void forceBack()
	{
		this.isForcedBack = true;
	}
	
	public Paddle(Resources resources, ScreenSide side, float x, float y, double velocityX, double velocityY)
	{
		super(resources, R.drawable.paddle, x, y, velocityX, velocityY);
		this.side = side;
		this.isForcedBack = false;
		
		GameManager.Game.paddles.add(this);
	}
	
	public void Draw(Canvas canvas)
	{
		super.Draw(canvas);
	}
	
	public void Update(double timeElapsed)
	{
		super.Update(timeElapsed);
		
		if (y <= 0)
		{
			y = 0;
			velocityY *= -1;
		}
		else if ((y + this.height) >= GameManager.SCREEN_HEIGHT)
		{
			y = GameManager.SCREEN_HEIGHT - this.height;
			velocityY *= -1;
		}
		
		// Move X
		if (this.isForcedBack)
		{
			// Force backwards to start x point.
			if (this.side == ScreenSide.LEFT)
			{
				if (this.x == 0)
				{
					this.isForcedBack = false;
				}
				else
				{
					this.velocityX = -Math.abs(this.velocityX);
				}
			}
			else
			{
				if (this.x == GameManager.SCREEN_WIDTH - this.width)
				{
					this.isForcedBack = false;
				}
				else
				{
					this.velocityX = Math.abs(this.velocityX);
				}
			}
		}
		
		if (this.isPressed && !this.isForcedBack)
		{
			if (this.side == ScreenSide.LEFT)
			{
				if (this.x <= (GameManager.SCREEN_WIDTH / 100 * PADDLE_OUT_DIST))
				{
					this.x += 5;
					//this.velocityX = 100;
					
					if (this.velocityY != 0)
					{
						prevVelocityY = this.velocityY;
						this.velocityY = 0;
					}
				}
			}
			else
			{
				if (this.x >= (GameManager.SCREEN_WIDTH / 100 * (100 - PADDLE_OUT_DIST)))
				{
					this.x -= 5;
					//this.velocityX = -100;
					
					if (this.velocityY != 0)
					{
						prevVelocityY = this.velocityY;
						this.velocityY = 0;
					}
				}
			}
		}
		else
		{
			// Move back to original X position.
			if (this.side == ScreenSide.LEFT && this.x > 0)
			{
				this.x -= 5;
				//this.velocityX = -100;
				
				if (this.x <= 0)
				{
					this.velocityY = prevVelocityY;
				}
			}
			else if (this.side == ScreenSide.RIGHT && this.x < (GameManager.SCREEN_WIDTH - this.width))
			{
				this.x += 5;
				
				if (this.x == (GameManager.SCREEN_WIDTH - this.width))
				{
					this.velocityY = prevVelocityY;
				}
			}
		}
	}
}
