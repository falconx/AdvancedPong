package com.example.advancedpong;

import android.content.res.Resources;
import android.graphics.Canvas;

public class Ball extends Actor
{
	public Ball(Resources resources, float x, float y, double velocityX, double velocityY)
	{
		super(resources, R.drawable.ball, x, y, velocityX, velocityY);
	}
	
	public void Draw(Canvas canvas)
	{
		super.Draw(canvas);
	}
	
	public void Update(double timeElapsed)
	{
		super.Update(timeElapsed);
		
		if (x <= 0)
		{
			x = 0;
			velocityX *= -1;
			
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
		}
		else if (x + this.width >= GameManager.SCREEN_WIDTH)
		{
			x = GameManager.SCREEN_WIDTH - this.width;
			velocityX *= -1;
			
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
		}
		
		if (y <= 0)
		{
			y = 0;
			velocityY *= -1;
		}
		else if (y + this.height >= GameManager.SCREEN_HEIGHT)
		{
			y = GameManager.SCREEN_HEIGHT - this.height;
			velocityY *= -1;
		}
	}
}
