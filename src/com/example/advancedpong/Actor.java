package com.example.advancedpong;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public abstract class Actor
{
	protected float x;
	protected float y;
	protected double velocityX;
	protected double velocityY;
	protected int height;
	protected int width;
	protected Bitmap bitmap;
	
	public float getX()
	{
		return this.x;
	}
	
	public void setX(float x)
	{
		this.x = x;
	}
	
	public float getY()
	{
		return this.y;
	}

	public void setY(float y)
	{
		this.y = y;
	}
	
	public double getVelocityX()
	{
		return this.velocityX;
	}

	public void setVelocityX(double velocityX)
	{
		this.velocityX = velocityX;
	}
	
	public double getVelocityY()
	{
		return this.velocityY;
	}
	
	public void setVelocityY(double velocityY)
	{
		this.velocityY = velocityY;
	}
	
	public Bitmap getBitmap()
	{
		return this.bitmap;
	}
	
	public void setBitmap(Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}
	
	public int left()
	{
		return (int) this.x;
	}
	
	public int right()
	{
		return (int) this.x + this.width;
	}
	
	public int top()
	{
		return (int) this.y;
	}
	
	public int bottom()
	{
		return (int) this.y + this.height;
	}
	
	public Actor(Resources resources, int resourceID, float x, float y, double velocityX, double velocityY)
	{
		this.x = x;
		this.y = y;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.bitmap = BitmapFactory.decodeResource(resources, resourceID);
		this.height = bitmap.getHeight();
		this.width = bitmap.getWidth();
	}
	
	protected void Draw(Canvas canvas)
	{
		canvas.drawBitmap(this.bitmap, this.x, this.y, null);
	}
	
	protected void Update(double timeElapsed)
	{
		this.x += timeElapsed * this.velocityX;
		this.y += timeElapsed * this.velocityY;
	}
}
