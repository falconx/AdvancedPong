package com.example.advancedpong;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
        
        private PointF findLineIntersection(PointF start1, PointF end1, PointF start2, PointF end2)
        {
        	float denom = ((end1.x - start1.x) * (end2.y - start2.y)) - ((end1.y - start1.y) * (end2.x - start2.x));

        	//  AB & CD are parallel 
        	if (denom == 0)
        	{
        		return new PointF(0, 0);
        	}

        	float numer = ((start1.y - start2.y) * (end2.x - start2.x)) - ((start1.x - start2.x) * (end2.y - start2.y));

        	float r = numer / denom;

        	float numer2 = ((start1.y - start2.y) * (end1.x - start1.x)) - ((start1.x - start2.x) * (end1.y - start1.y));

        	float s = numer2 / denom;

            if ((r < 0 || r > 1) || (s < 0 || s > 1))
            {
        		return new PointF(0, 0);
            }

        	// Find intersection point
        	PointF result = new PointF();
        	result.x = start1.x + (r * (end1.x - start1.x));
        	result.y = start1.y + (r * (end1.y - start1.y));

        	return result;
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
        			Line[] lines = new Line[4];
        			
            		// L
            		Path p = new Path();
            		p.moveTo(paddle.x, paddle.y);
    	        	p.lineTo(paddle.x, paddle.bottom());
            		paths[0] = p;
            		lines[0] = new Line(new PointF(paddle.x, paddle.y), new PointF(paddle.x, paddle.bottom()));
            		
            		// T
            		p = new Path();
            		p.moveTo(paddle.x, paddle.y);
    	        	p.lineTo(paddle.right(), paddle.y);
            		paths[1] = p;
            		lines[1] = new Line(new PointF(paddle.x, paddle.y), new PointF(paddle.right(), paddle.y));
            		
            		// R
            		p = new Path();
            		p.moveTo(paddle.right(), paddle.y);
    	        	p.lineTo(paddle.right(), paddle.bottom());
            		paths[2] = p;
            		lines[2] = new Line(new PointF(paddle.right(), paddle.y), new PointF(paddle.right(), paddle.bottom()));
            		
            		// B
            		p = new Path();
            		p.moveTo(paddle.x, paddle.bottom());
    	        	p.lineTo(paddle.right(), paddle.bottom());
            		paths[3] = p;
            		lines[3] = new Line(new PointF(paddle.x, paddle.bottom()), new PointF(paddle.right(), paddle.bottom()));
            		
            		Path bpath = new Path();
            		bpath.moveTo(ball.lastX, ball.lastY);
            		bpath.lineTo(ball.x, ball.y);
            		canvas.drawPath(bpath, paint);
		        	
		        	int i = 0;
            		for (Path path : paths)
            		{
			        	canvas.drawPath(path, paint);
			        	
			        	PointF intersect = findLineIntersection(
			        		new PointF((float)ball.lastX, (float)ball.lastY),
			        		new PointF((float)ball.x, (float)ball.y),
			        		lines[i].a, lines[i].b);
			        	
			        	if (intersect.x != 0 && intersect.y != 0)
			        	{
			        		Log.d("Intersect", Float.toString(intersect.x) + ", " + Float.toString(intersect.y));
			        	}
			        	
			        	i++;
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
