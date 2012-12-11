package com.example.advancedpong;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

import com.example.advancedpong.MainView.MainThread;

public class MainActivity extends Activity
{
    private MainThread mMainThread;
    private MainView mMainView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // tell system to use the layout defined in our XML file
        setContentView(R.layout.activity_main);

        // get handles to the LunarView from XML, and its LunarThread
        mMainView = (MainView) findViewById(R.id.main);
        mMainThread = mMainView.getThread();
        
        mMainView.setOnTouchListener(new OnTouchListener()
        {
        	public boolean onTouch(View v, MotionEvent event)
        	{
        		Context context = MainApplication.getContext();
        		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
            	Point size = new Point();
            	display.getSize(size);
            	int width = size.x;
            	
            	// LHS
        		if (event.getX() < width / 2 &&
        			event.getAction() == MotionEvent.ACTION_DOWN)
        		{
        			mMainThread.leftPaddle.isPressed = true;
        		}
        		else if (event.getAction() == MotionEvent.ACTION_UP)
        		{
        			mMainThread.leftPaddle.isPressed = false;
        		}
        		
        		// RHS
        		if (event.getX() > width / 2 &&
        			event.getAction() == MotionEvent.ACTION_DOWN)
        		{
        			mMainThread.rightPaddle.isPressed = true;
        		}
        		else if (event.getAction() == MotionEvent.ACTION_UP)
        		{
        			mMainThread.rightPaddle.isPressed = false;
        		}
        		
        		return true;
        	}
        });
        
        // hide action bar
        getActionBar().hide();
        
        // start thread
        mMainThread.doStart();
    }
}
