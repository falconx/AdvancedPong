package com.example.advancedpong;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.advancedpong.Paddle.ScreenSide;

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
            
            ball = new Ball(resources, 100, 100, 250, 100);
        	//ball = new Ball(resources, GameManager.SCREEN_WIDTH - 10, GameManager.SCREEN_HEIGHT - 50, 0, 50);
            leftPaddle = new Paddle(resources, ScreenSide.LEFT, 0, GameManager.SCREEN_HEIGHT / 2 - 50, 0, 250);
            rightPaddle = new Paddle(resources, ScreenSide.RIGHT, 0, GameManager.SCREEN_HEIGHT / 2 - 50, 0, -250);
            rightPaddle.x = GameManager.SCREEN_WIDTH - rightPaddle.width;
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
                    	doDraw(c);
                        updatePhysics();
                        checkCollision(c);
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
        
        Path p = new Path();

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
	        		Path newPath = new Path();
	        		newPath.moveTo(ball.lastX, ball.lastY);
	        		newPath.lineTo(ball.x, ball.y);
	        		p.addPath(newPath);
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
        	Paint paint = new Paint();
        	paint.setDither(true);
        	paint.setColor(Color.RED);
        	paint.setStyle(Paint.Style.STROKE);    
        	paint.setStrokeJoin(Paint.Join.ROUND);
        	paint.setStrokeCap(Paint.Cap.ROUND);
        	paint.setStrokeWidth(3);
        	
        	for (Ball ball : GameManager.Game.balls)
        	{
        		for (Paddle paddle : GameManager.Game.paddles)
        		{
        			Path[] paths = new Path[4];
            		
            		// L
            		Path p = new Path();
            		p.moveTo(paddle.x, paddle.y);
    	        	p.lineTo(paddle.x, paddle.bottom());
            		paths[0] = p;
            		
            		// T
            		p = new Path();
            		p.moveTo(paddle.x, paddle.y);
    	        	p.lineTo(paddle.right(), paddle.y);
            		paths[1] = p;
            		
            		// R
            		p = new Path();
            		p.moveTo(paddle.right(), paddle.y);
    	        	p.lineTo(paddle.right(), paddle.bottom());
            		paths[2] = p;
            		
            		// B
            		p = new Path();
            		p.moveTo(paddle.x, paddle.bottom());
    	        	p.lineTo(paddle.right(), paddle.bottom());
            		paths[3] = p;
		        	
		        	Path path1 = new Path();
		        	//path1.moveTo(ball.lastX, ball.lastY);
		        	//path1.lineTo(ball.x, ball.y);
		        	path1.addCircle(20, 50, 20, Path.Direction.CW);
		        	canvas.drawPath(path1, paint);
		        	
            		for (Path path : paths)
            		{
			        	canvas.drawPath(path, paint);

			        	//Region clip = new Region(0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
			        	
			        	Region region1 = new Region();
			        	region1.setPath(path1, new Region(20, 50, 40, 40));
			        	
			        	Region region2 = new Region();
			        	region2.setPath(path, new Region((int)paddle.x, (int)paddle.y, paddle.right(), paddle.bottom()));
			
			        	//if(p1.quickReject(p2)) // checks for intersection
			        	//if (!region1.quickReject(region2) && region1.op(region2, Region.Op.INTERSECT))
			        	if (region1.op(region2, Region.Op.INTERSECT))
			        	{
			        	    // Collision.
			        		GameManager.PlayerOneScore.setText("x");
			        		
			        		Path temp = new Path();
			        		temp.moveTo(100, 100);
				        	temp.lineTo(500, 100);
				        	canvas.drawPath(temp, paint);
			        	}
            		}
        		}
        	}
        	
        	
        	
        	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        	
        	
        	
        	//checkBallToPaddleCollision(canvas);
        }
        
        private void checkBallToPaddleCollision(Canvas canvas)
        {
        	// TEMP:
        	int CORNER_SIZE = 10;
        	
        	for (Ball ball : GameManager.Game.balls)
        	{
        		for (Paddle paddle : GameManager.Game.paddles)
        		{
        			if (paddle.Side() == ScreenSide.LEFT)
        			{
        				//
        			}
        			else
        			{
        				// Hit side of RHS paddle.
        				if (((ball.bottom()) > paddle.y) && (ball.y < paddle.bottom()) && (ball.right() >= paddle.x) && (ball.x <= paddle.right()))
        				{
        					if ((ball.bottom() >= paddle.y) && (ball.bottom() <= paddle.y + CORNER_SIZE))
        					{
        						// Hit Top.
        						
        						
        						/// TODO: TEST ///
        						if (ball.x <= paddle.x + paddle.width / 2)
        						{
        							// LHS.
        							ball.velocityX = -Math.abs(ball.velocityX);
        						}
        						else
        						{
        							// RHS.
        							ball.velocityX = Math.abs(ball.velocityX);
        						}
        						/// END TEST ////
        						
        						
        						// Set ball y to top of paddle.
        						ball.y = paddle.y - ball.height;
        						
        						// Force paddle to move downwards.
        						paddle.velocityY = Math.abs(paddle.velocityY);
        						
        						if (Math.abs(ball.velocityY) <= Math.abs(paddle.velocityY))
        						{
        							ball.velocityY = paddle.velocityY + 2;
        						}
        						
        						ball.velocityY *= -1;
        					}
        					else if ((ball.y <= paddle.bottom()) && (ball.y >= paddle.bottom() - CORNER_SIZE))
        					{
        						// Hit Bottom.
        						
        						
        						/// TODO: TEST ///
        						if (ball.x <= paddle.x + paddle.width / 2)
        						{
        							// LHS.
        							ball.velocityX = -Math.abs(ball.velocityX);
        						}
        						else
        						{
        							// RHS.
        							ball.velocityX = Math.abs(ball.velocityX);
        						}
        						/// END TEST ////
        						
        						
        						// Set ball y to top of paddle.
        						ball.y = paddle.bottom();
        						
        						// Force paddle to move downwards.
        						paddle.velocityY = -Math.abs(paddle.velocityY);
        						
        						if (Math.abs(ball.velocityY) <= Math.abs(paddle.velocityY))
        						{
        							ball.velocityY = -(Math.abs(paddle.velocityY) + 2);
        						}
        						
        						ball.velocityY *= -1;
        					}
        					else if ((ball.x >= paddle.x + (paddle.width / 2)) && (ball.x <= paddle.right()))
        					{
        						// Hit RHS.
        						
        						// Set ball x to RHS of paddle.
        						ball.x = paddle.right();
        						
        						// Ensure ball.x speed is greater than paddle.x return speed.
        						if (Math.abs(ball.velocityX) < Math.abs(paddle.velocityX))
        						{
        							ball.velocityX = Math.abs(paddle.velocityX + 2);
        						}
        					}
        					else
        					{
        						// Hit LHS.
        						
        						// Set ball x to LHS of paddle.
        						ball.x = paddle.x - ball.width;
        						
        						if (Math.abs(ball.velocityX) < Math.abs(paddle.velocityX))
        						{
        							ball.velocityX = -Math.abs(paddle.velocityX + 2);
        						}
        					}
        					
        					// Force the paddle back to it's original x position.
        					paddle.forceBack();
        				}
        			}
        		}
        	}
        }
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
