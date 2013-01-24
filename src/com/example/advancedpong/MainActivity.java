package com.example.advancedpong;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.advancedpong.MainView.MainThread;

public class MainActivity extends Activity
{
    private MainThread mMainThread;
    private MainView mMainView;
    
    // Store initial pointer positons.
    private HashMap<Integer, Float> lastPositionX = new HashMap<Integer, Float>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // tell system to use the layout defined in our XML file
        setContentView(R.layout.activity_main);

        // get handles to the LunarView from XML, and its LunarThread
        mMainView = (MainView) findViewById(R.id.main);
        mMainThread = mMainView.getThread();
        
        // Draw player one score.
        TextView t = new TextView(this);
        int score = GameManager.Game.getPlayerAtIndex(1).getScore();
        t.setText(Integer.toString(score));
        t.setTop(100);
        t.setLeft(100);
        t.setTextSize(300);
        
        RelativeLayout l = (RelativeLayout) findViewById(R.id.layout);
        l.addView(t);
        
        // Draw player two score.
        t = new TextView(this);
        score = GameManager.Game.getPlayerAtIndex(2).getScore();
        t.setText(Integer.toString(score));
        t.setTop(100);
        t.setLeft(300);
        t.setTextSize(300);
        
        l = (RelativeLayout) findViewById(R.id.layout);
        l.addView(t);
        
		// Detect touch input.
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
            	
            	// Detects pointer input. 
            	for (int i = 0; i < event.getPointerCount(); i++)
            	{
            		int action = event.getActionMasked();
            		int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        			float x = event.getX(pointerIndex);
        			
    				if (action == MotionEvent.ACTION_POINTER_UP)
    				{
    					float lastPosX = lastPositionX.get(pointerIndex);
    					
    					if (lastPosX < width / 2)
    					{
    						mMainThread.leftPaddle.isPressed = false;
    					}
    					else
    					{
    						mMainThread.rightPaddle.isPressed = false;
    					}
    				}
    				else if (action == MotionEvent.ACTION_UP)
    				{
    					mMainThread.leftPaddle.isPressed = false;
    					mMainThread.rightPaddle.isPressed = false;
    				}
    					
    				if (action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_DOWN)
    				{
        				// LHS
        				if (x < width / 2)
        				{
        					mMainThread.leftPaddle.isPressed = true;
        				}
        				
        				// RHS
        				else
        				{
        					mMainThread.rightPaddle.isPressed = true;
        				}
        				
        				lastPositionX.put(pointerIndex, x);
    				}
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
