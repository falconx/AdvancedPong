package com.example.advancedpong;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;

public class Paddle extends Actor
{
	public enum ScreenSide { LEFT, RIGHT }
	
	private final int PADDLE_OUT_DIST = 40; // Percentage.
	
	private enum Direction
	{
		UP(270),
		RIGHT(0),
		DOWN(90),
		LEFT(180);
		
		private float value;
		
		private Direction(float value)
		{
		  this.value = value;
		}
		
		public float getValue()
		{
		  return value;
		}
	}
	
	private boolean isForcedBack, isPressed;
	private ScreenSide side;

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
	
	private void reverseX()
	{
		this.vx *= -1;
	}
	
	private void reverseY()
	{
		this.vy *= -1;
	}
	
	private void moveInDirection(Direction direction)
	{
		this.direction = direction.value;
		double radians = (Math.toRadians(this.direction));
		
		if (direction == Direction.LEFT || direction == Direction.RIGHT)
		{
			this.vx = this.speed * Math.cos(radians);
			this.vy = 0;
		}
		else
		{
			this.vx = 0;
			this.vy = this.speed * Math.sin(radians);
		}
		
		// Apply PADDLE_OUT_DIST constraint
		double limit = (GameManager.SCREEN_WIDTH / 100) * PADDLE_OUT_DIST;
		
		if (this.side == ScreenSide.LEFT)
		{
			if (this.right() >= limit)
			{
				this.position.x = (int)limit - this.width;
			}
		}
		else
		{
			limit = GameManager.SCREEN_WIDTH - limit;
			if (this.left() <= limit)
			{
				this.position.x = (int)limit;
			}
		}
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
		
		// Reverse Y
		if (this.position.y <= 0)
		{
			this.position.y = 0;
			reverseY();
		}
		else if ((this.position.y + this.height) >= GameManager.SCREEN_HEIGHT)
		{
			this.position.y = GameManager.SCREEN_HEIGHT - this.height;
			reverseY();
		}
		
		// Force back if necessary
		if (this.isForcedBack)
		{
			if (this.side == ScreenSide.LEFT)
			{
				if (this.position.x <= 0)
				{
					this.isForcedBack = false;
					moveInDirection(Direction.DOWN);
				}
				else
				{
					moveInDirection(Direction.LEFT);
				}
			}
			else
			{
				if (this.position.x >= GameManager.SCREEN_WIDTH - this.width)
				{
					this.isForcedBack = false;
					moveInDirection(Direction.DOWN); /////////////////////
				}
				else
				{
					moveInDirection(Direction.RIGHT);
				}
			}
		}
		
		// Move towards middle
		if (this.isPressed && !this.isForcedBack)
		{
			if (this.side == ScreenSide.LEFT)
			{
				moveInDirection(Direction.RIGHT);
			}
			else
			{
				moveInDirection(Direction.LEFT);
			}
		}
		
		// Move back if necessary
		if (!this.isPressed)
		{
			if (this.side == ScreenSide.LEFT)
			{
				if (this.position.x > 0)
				{
					moveInDirection(Direction.LEFT);
				}
				else if (this.direction != Direction.DOWN.value &&
						 this.direction != Direction.UP.value)
				{
					moveInDirection(Direction.DOWN); /////////////////////
				}
			}
			
			if (this.side == ScreenSide.RIGHT)
			{
				if (this.position.x < GameManager.SCREEN_WIDTH - this.width)
				{
					moveInDirection(Direction.RIGHT);
				}
				else if (this.direction != Direction.DOWN.value &&
						 this.direction != Direction.UP.value)
				{
					moveInDirection(Direction.DOWN); /////////////////////
				}
			}
		}
		
		
		
		this.position.x += this.vx;
		this.position.y += this.vy;
	}
}
