package com.example.advancedpong;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;

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

        public MainThread(SurfaceHolder surfaceHolder, Context context, Handler handler)
        {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            
            Resources resources = getResources();
            
    		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
        	Point size = new Point();
        	display.getSize(size);
        	int height = size.y;
        	int width = size.x;
            
            ball = new Ball(resources, 100, 100, 300, 100);
            leftPaddle = new Paddle(resources, ScreenSide.LEFT, 0, height / 2 - 50, 0, 250);
            rightPaddle = new Paddle(resources, ScreenSide.RIGHT, 0, height / 2 - 50, 0, -250);
            rightPaddle.x = width - rightPaddle.width;
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

        private void doDraw(Canvas canvas)
        {
        	ball.Draw(canvas);
        	leftPaddle.Draw(canvas);
        	rightPaddle.Draw(canvas);
        }

        private void updatePhysics()
        {
            long now = System.currentTimeMillis();

            // Do nothing if mLastTime is in the future.
            // This allows the game-start to delay the start of the physics
            // by 100ms or whatever.
            if (mLastTime > now) return;

            double elapsed = (now - mLastTime) / 1000.0;
            
            ball.Update(elapsed);
            leftPaddle.Update(elapsed);
            rightPaddle.Update(elapsed);
            
            mLastTime = now;
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
