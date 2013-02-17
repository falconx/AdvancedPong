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
            
            ball = new Ball(resources, new Point(GameManager.SCREEN_WIDTH - 10, 10), 10, 90);
            leftPaddle = new Paddle(resources, ScreenSide.LEFT, new Point(0, GameManager.SCREEN_HEIGHT / 2 - 50), 10);
            rightPaddle = new Paddle(resources, ScreenSide.RIGHT, new Point(0, GameManager.SCREEN_HEIGHT / 2 - 50), 0);
            rightPaddle.position.x = GameManager.SCREEN_WIDTH - rightPaddle.width;
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
        	for (Ball ball : GameManager.Game.balls)
        	{
        		for (Paddle paddle : GameManager.Game.paddles)
        		{
					double distance = Math.abs(ball.bottom() - paddle.position.y);
	        		double time = distance / ball.speed;
	        		
	        		if (time > 0 && time < 1)
	        		{
	        			ball.vy *= -1;
	        	    }
        		}
        	}
        	
        	
        	
        	
        	//checkBallToPaddleCollision(canvas);
        }
        
        private void drawLines(Canvas canvas, Line[] lines)
        {
	        for (Line line : lines)
			{
				drawLine(canvas, line);
			}
        }
        
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
        
        private void checkBallToPaddleCollision(Canvas canvas)
        {
        	for (Ball ball : GameManager.Game.balls)
        	{
        		Line[] ballLines = new Line[4];
        		ballLines[0] = new Line(new PointF(ball.lastPosition.x, ball.lastPosition.y), new PointF(ball.position.x, ball.position.y)); // TL
        		ballLines[1] = new Line(new PointF(ball.lastPosition.x + ball.width, ball.lastPosition.y), new PointF(ball.right(), ball.position.y)); // TR
        		ballLines[2] = new Line(new PointF(ball.lastPosition.x + ball.width, ball.lastPosition.y + ball.height), new PointF(ball.right(), ball.bottom())); // BR
        		ballLines[3] = new Line(new PointF(ball.lastPosition.x, ball.lastPosition.y + ball.height), new PointF(ball.position.x, ball.bottom())); // BL
        		
        		drawLines(canvas, ballLines);
        		
        		for (Paddle paddle : GameManager.Game.paddles)
        		{
        			Map<String, Line> paddleLines = new HashMap<String, Line>();
        			paddleLines.put("Top", 		new Line(new PointF(paddle.position.x, paddle.position.y), new PointF(paddle.right(), paddle.position.y)));
        			paddleLines.put("Right", 	new Line(new PointF(paddle.right(), paddle.position.y), new PointF(paddle.right(), paddle.bottom())));
        			paddleLines.put("Bottom", 	new Line(new PointF(paddle.right(), paddle.bottom()), new PointF(paddle.position.x, paddle.bottom())));
        			paddleLines.put("Left", 	new Line(new PointF(paddle.position.x, paddle.bottom()), new PointF(paddle.position.x, paddle.position.y)));
        			
        			// Check collisions.
        			
        			boolean topHit = false;
        			boolean rightHit = false;
        			boolean bottomHit = false;
        			boolean leftHit = false;
        			
        			PointF topIntersect = new PointF(0, 0);
        			PointF rightIntersect = new PointF(0, 0);
        			PointF bottomIntersect = new PointF(0, 0);
        			PointF leftIntersect = new PointF(0, 0);
        			
        			for (Map.Entry<String, Line> paddleLine : paddleLines.entrySet())
        			{
        				for (Line ballLine : ballLines)
        				{
        					PointF intersect = findLineIntersection(
        						new PointF(ballLine.a.x, ballLine.a.y),
        						new PointF(ballLine.b.x, ballLine.b.y + 1),
        						new PointF(paddleLine.getValue().a.x, paddleLine.getValue().a.y),
        						new PointF(paddleLine.getValue().b.x, paddleLine.getValue().b.y));
        					
        					if (intersect.x != 0 && intersect.y != 0)
        					{
        						// A collision has occured.
        						
        						String side = paddleLine.getKey();
        						if (side.equals("Top"))
        						{
        							topHit = true;
        							topIntersect = new PointF(intersect.x, intersect.y);
        						}
        						else if (side.equals("Right"))
        						{
        							rightHit = true;
        							rightIntersect = new PointF(intersect.x, intersect.y);
        						}
        						else if (side.equals("Bottom"))
        						{
        							bottomHit = true;
        							bottomIntersect = new PointF(intersect.x, intersect.y);
        						}
        						else if (side.equals("Left"))
        						{
        							leftHit = true;
        							leftIntersect = new PointF(intersect.x, intersect.y);
        						}
        					}
        				}
        			}
        			
        			drawLine(canvas, paddleLines.get("Top"));
        			
        			if (topHit)
        			{
        				ball.position.x = (int)topIntersect.x;
        				ball.position.y = (int)topIntersect.y - ball.height;
        				
        				ball.speed = -Math.abs(ball.speed);
        				
        				// Force paddle to move downwards.
						paddle.speed = Math.abs(paddle.speed);
						
						Log.w("COLLISION HERE", "COLLISION!!!!!!!!!!!!!!");
        			}
        			else if (leftHit)
        			{
        				
        			}
        			else if (rightHit)
        			{
        				
        			}
        			else if (bottomHit)
        			{
        				
        			}
        		}
        	}
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	/*
        	// TEMP:
        	int CORNER_SIZE = 10;
        	
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
        						//ball.y = paddle.bottom();
        						
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
        						//ball.x = paddle.x - ball.width;
        						
        						if (Math.abs(ball.velocityX) < Math.abs(paddle.velocityX))
        						{
        							ball.velocityX = -Math.abs(paddle.velocityX + 2);
        						}
        						
        						
        						
        						int i = 0;
    				        	canvas.drawPath(paths[0], paint);
    				        	
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
        					
        					// Force the paddle back to it's original x position.
        					paddle.forceBack();
        				}
        			}
		        			
		        	
            		
            		
            		
        		}
        	}
        	*/
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
