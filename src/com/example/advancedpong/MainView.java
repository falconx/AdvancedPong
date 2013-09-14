package com.example.advancedpong;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.advancedpong.Paddle.ScreenSide;

class Line
{
	public PointF a, b;
	
	public Line(PointF a, PointF b)
	{
		this.a = a;
		this.b = b;
	}
}

class MainView extends SurfaceView implements SurfaceHolder.Callback
{
    class MainThread extends Thread
    {
        /** Indicate whether the surface has been created & is ready to draw */
        private boolean mRun = false;
        
        /** Handle to the surface manager object we interact with */
        private SurfaceHolder mSurfaceHolder;
        
        /** Used to figure out elapsed time between frames */
        private long mLastTime;
        
        Ball ball;
        Paddle leftPaddle;
        Paddle rightPaddle;
        
        Resources resources = getResources();

        public MainThread(SurfaceHolder surfaceHolder, Context context, Handler handler)
        {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            
            ball = new Ball(resources, new Point(GameManager.SCREEN_WIDTH / 2, GameManager.SCREEN_HEIGHT / 2), 5, 135);
            leftPaddle = new Paddle(resources, ScreenSide.LEFT, new Point(0, GameManager.SCREEN_HEIGHT / 2 - 50), 10);
            rightPaddle = new Paddle(resources, ScreenSide.RIGHT, new Point(0, GameManager.SCREEN_HEIGHT / 2 - 50), 10);
            rightPaddle.position.x = GameManager.SCREEN_WIDTH - rightPaddle.width;
            
            //Ball ball2 = new Ball(resources, new Point(10, GameManager.SCREEN_HEIGHT - 20), 4, 270);
            //Ball ball3 = new Ball(resources, new Point(GameManager.SCREEN_WIDTH / 2, GameManager.SCREEN_HEIGHT / 2), 80, 180);
        }

        /**
         * Starts the game, setting parameters for the current difficulty.
         */
        public void doStart()
        {
            synchronized (mSurfaceHolder)
            {
                mLastTime = System.currentTimeMillis() + 100;
            }
        }

        @Override
        public void run()
        {
            while (mRun)
            {
                Canvas c = null;
                try
                {
                    c = mSurfaceHolder.lockCanvas(null);
                    c.drawColor(Color.WHITE);
                    
                    synchronized (mSurfaceHolder)
                    {
                        updatePhysics();
                        checkCollision(c);
                        doDraw(c);
                    }
                }
                finally
                {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null)
                    {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        public void setRunning(boolean b)
        {
            mRun = b;
        }
        
        //Path p = new Path();

        private void doDraw(Canvas canvas)
        {
        	// Draw balls.
            for (Ball ball : GameManager.Game.balls)
            {
            	ball.Draw(canvas);
            }
        	
        	leftPaddle.Draw(canvas);
        	rightPaddle.Draw(canvas);
        	
        	// Temp: PATHS //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        	/*
        	if (!GameManager.Game.balls.isEmpty())
        	{
	        	for (Ball ball : GameManager.Game.balls)
	            {
	        		if (ball.lastPosition != null) {
		        		Path newPath = new Path();
		        		newPath.moveTo(ball.lastPosition.x, ball.lastPosition.y);
		        		newPath.lineTo(ball.position.x, ball.position.y);
		        		p.addPath(newPath);
	        		}
	            }
	        	
	        	Paint paint = new Paint();
	        	paint.setDither(true);
	        	paint.setColor(Color.RED);
	        	paint.setStyle(Paint.Style.STROKE);    
	        	paint.setStrokeJoin(Paint.Join.ROUND);
	        	paint.setStrokeCap(Paint.Cap.ROUND);
	        	paint.setStrokeWidth(3);
	        	
	        	canvas.drawPath(p, paint);
        	}
        	*/
        }

        private void updatePhysics()
        {
            long now = System.currentTimeMillis();

            // Do nothing if mLastTime is in the future.
            // This allows the game-start to delay the start of the physics
            // by 100ms or whatever.
            if (mLastTime > now) return;

            double elapsed = (now - mLastTime) / 1000.0;
            
            // Update balls.
            for (Ball ball : GameManager.Game.balls)
            {
            	ball.Update(elapsed);
            }
            
            leftPaddle.Update(elapsed);
            rightPaddle.Update(elapsed);
            
            mLastTime = now;
        }
        
        private void checkCollision(Canvas canvas)
        {
        	Paddle paddle;
        	double distance, time;
        	boolean c = false;
        	
        	for (Ball ball : GameManager.Game.balls)
        	{
        		c = false;
        		
    			paddle = (ball.position.x <= GameManager.SCREEN_WIDTH / 2)
    				? GameManager.Game.paddles.get(0)
    				: GameManager.Game.paddles.get(1);
    			
    			// Paddle top collision
    			if ((ball.left() >= paddle.left() || ball.right() >= paddle.left()) &&
    				(ball.right() <= paddle.right() || ball.left() <= paddle.right()))
    			{
					distance = Math.abs(ball.bottom() - paddle.top());
	        		time = distance / ball.speed;
	        		
	        		if (time >= 0 && time <= 1.5)
	        		{
	        			//TODO: Push ball away, opposite direction to paddle
	        			ball.vy *= -1;
	        			paddle.vy *= -1;
	        			c = true;
	        	    }
    			}
    			
    			// Paddle bottom collision
    			if ((ball.left() >= paddle.left() || ball.right() >= paddle.left()) &&
    				(ball.right() <= paddle.right() || ball.left() <= paddle.right()))
    			{
					distance = Math.abs(ball.top() - paddle.bottom());
	        		time = distance / ball.speed;
	        		
	        		if (time >= 0 && time <= 1.5)
	        		{
	        			//TODO: Push ball away, opposite direction to paddle
	        			ball.vy *= -1;
	        			paddle.vy *= -1;
	        			c = true;
	        	    }
    			}
    			
    			// Paddle left collision
    			if (ball.right() >= paddle.left() &&
    				ball.bottom() >= paddle.top() &&
    				ball.top() <= paddle.bottom())
    			{
					distance = Math.abs(ball.right() - paddle.left());
	        		time = distance / ball.speed;
	        		
	        		if (time >= 0 && time <= 1.5)
	        		{
	        			//TODO: Push ball away, opposite direction to paddle
	        			ball.vx *= -1;
	        			paddle.vy *= -1;
	        			c = true;
	        	    }
    			}
    			
    			// Paddle right collision
    			if (ball.left() >= paddle.right() &&
    				ball.bottom() >= paddle.top() &&
    				ball.top() <= paddle.bottom())
    			{
					distance = Math.abs(ball.left() - paddle.right());
	        		time = distance / ball.speed;
	        		
	        		if (time >= 0 && time <= 1.5)
	        		{
	        			//TODO: Push ball away, opposite direction to paddle
	        			ball.vx *= -1;
	        			paddle.vy *= -1;
	        			c = true;
	        	    }
    			}
    			
    			if (c) {
    				paddle.position = paddle.lastPosition;
    				ball.position = ball.lastPosition;
    				paddle.forceBack();
    			}
        	}
        }
        
        /*
        private void drawLine(Canvas canvas, Line line)
        {
        	Paint paint = new Paint();
        	paint.setDither(true);
        	paint.setColor(Color.RED);
        	paint.setStyle(Paint.Style.STROKE);    
        	paint.setStrokeJoin(Paint.Join.ROUND);
        	paint.setStrokeCap(Paint.Cap.ROUND);
        	paint.setStrokeWidth(4);
        	
        	Path path = new Path();
			path.moveTo(line.a.x, line.a.y);
			path.lineTo(line.b.x, line.b.y);
			canvas.drawPath(path, paint);
        }
        */  
    }

    /** The thread that actually draws the animation */
    private MainThread thread;

    public MainView(Context context, AttributeSet attrs)
    {
    	super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new MainThread(holder, context, new Handler()
        {
            //
        });

        setFocusable(true); // make sure we get key events
    }

    /**
     * Fetches the animation thread corresponding to this LunarView.
     *
     * @return the animation thread
     */
    public MainThread getThread()
    {
        return thread;
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder)
    {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();
    }

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
	{
		// TODO Auto-generated method stub
	}
	
	public void surfaceDestroyed(SurfaceHolder arg0)
	{
		// TODO Auto-generated method stub
	}
}
