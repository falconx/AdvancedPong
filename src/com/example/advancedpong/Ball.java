package com.example.advancedpong;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;

public class Ball extends Actor
{
	public Ball(Resources resources, Point position, float speed, float angle)
	{
		super(resources, R.drawable.ball, position, speed, angle);
		
		GameManager.Game.balls.add(this);
	}
	
	public void Draw(Canvas canvas)
	{
		super.Draw(canvas);
	}
	
	public void Update(double timeElapsed)
	{
		super.Update(timeElapsed);
		
		if (this.position.x + this.width < 0)
		{
			this.position.x = 0;			
			this.vx = Math.abs(this.vx);
			
			// Awards player two a point.
			GameManager.Game.getPlayerAtIndex(2).addScore();
			
			// Update score display.
			runOnUiThread(new Runnable() {
				public void run() {
					GameManager.PlayerTwoScore.setText(Integer.toString(GameManager.Game.getPlayerAtIndex(2).getScore()));
					GameManager.PlayerTwoScore.measure(0, 0);
			        GameManager.PlayerTwoScore.setX((GameManager.SCREEN_WIDTH / 4) * 3 - GameManager.PlayerTwoScore.getMeasuredWidth() / 2);
			    }
			});
			
			// Destroy ball.
			//GameManager.Game.balls.remove(this);
		}
		else if (this.position.x > GameManager.SCREEN_WIDTH)
		{
			this.position.x = GameManager.SCREEN_WIDTH - this.width;
			this.vx = -Math.abs(this.vx);
			
			// Awards player one a point.
			GameManager.Game.getPlayerAtIndex(1).addScore();
			
			// Update score display.
			runOnUiThread(new Runnable() {
				public void run() {
					GameManager.PlayerOneScore.setText(Integer.toString(GameManager.Game.getPlayerAtIndex(1).getScore()));
					GameManager.PlayerOneScore.measure(0, 0);
			        GameManager.PlayerOneScore.setX(GameManager.SCREEN_WIDTH / 4 - GameManager.PlayerOneScore.getMeasuredWidth() / 2);
			    }
			});
			
			// Destroy ball.
			//GameManager.Game.balls.remove(this);
		}
		
		if (this.position.y <= 0)
		{
			this.position.y = 0;
			this.vy = Math.abs(this.vy);
		}
		else if (this.position.y + this.height >= GameManager.SCREEN_HEIGHT)
		{
			this.position.y = GameManager.SCREEN_HEIGHT - this.height;
			this.vy = -Math.abs(this.vy);
		}
		
		
		
			
		this.position.x += this.vx;
		this.position.y += this.vy;
	}
}
