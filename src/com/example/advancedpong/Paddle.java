package com.example.advancedpong;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;

public class Paddle extends Actor
{
	public enum ScreenSide { LEFT, RIGHT }
	
	private final int PADDLE_OUT_DIST = 40; // Percentage.
	
	private boolean isForcedBack;
	private boolean isPressed;
	private ScreenSide side;
	
	double prevSpeedY;

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
	
	public Paddle(Resources resources, ScreenSide side, Point position, float speed)
	{
		super(resources, R.drawable.paddle, position, speed, 90);
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
		
		if (this.position.x <= 0)
		{
			this.position.y = 0;
			//this.speed *= -1;
			
			this.direction = (this.direction == 90) ? 270 : 90;
		}
		else if ((this.position.y + this.height) >= GameManager.SCREEN_HEIGHT)
		{
			this.position.y = GameManager.SCREEN_HEIGHT - this.height;
			//this.speed *= -1;
			
			this.direction = (this.direction == 90) ? 270 : 90;
		}
		
		// Move X
		if (this.isForcedBack)
		{
			// Force backwards to start x point.
			if (this.side == ScreenSide.LEFT)
			{
				if (this.position.x == 0)
				{
					this.isForcedBack = false;
				}
				else
				{
					this.speed = -Math.abs(this.speed);
				}
			}
			else
			{
				if (this.position.x == GameManager.SCREEN_WIDTH - this.width)
				{
					this.isForcedBack = false;
				}
				else
				{
					this.speed = Math.abs(this.speed);
				}
			}
		}
		
		if (this.isPressed && !this.isForcedBack)
		{
			if (this.side == ScreenSide.LEFT)
			{
				if (this.position.x <= (GameManager.SCREEN_WIDTH / 100 * PADDLE_OUT_DIST))
				{
					//this.position.x += 5;
					//this.velocityX = 100;
					
					if (this.speed != 0)
					{
						prevSpeedY = this.speed;
						//this.speed = 0;
						
						this.direction = (this.direction == 90) ? 270 : 90;
					}
				}
			}
			else
			{
				if (this.position.x >= (GameManager.SCREEN_WIDTH / 100 * (100 - PADDLE_OUT_DIST)))
				{
					//this.position.x -= 5;
					//this.velocityX = -100;
					
					if (this.speed != 0)
					{
						prevSpeedY = this.speed;
						//this.speed = 0;
						
						this.direction = (this.direction == 90) ? 270 : 90;
					}
				}
			}
		}
		else
		{
			// Move back to original X position.
			if (this.side == ScreenSide.LEFT && this.position.x > 0)
			{
				this.position.x -= 5;
				//this.velocityX = -100;
				
				if (this.position.x <= 0)
				{
					this.speed = (float)prevSpeedY;
				}
			}
			else if (this.side == ScreenSide.RIGHT && this.position.x < (GameManager.SCREEN_WIDTH - this.width))
			{
				this.position.x += 5;
				
				if (this.position.x == (GameManager.SCREEN_WIDTH - this.width))
				{
					this.speed = (float)prevSpeedY;
				}
			}
		}
	}
}
