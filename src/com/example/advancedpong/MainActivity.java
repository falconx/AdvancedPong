package com.example.advancedpong;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Layout;
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
        
        // Add player scores.
        addPlayerScores();
        
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
    						mMainThread.leftPaddle.setIsPressed(false);
    					}
    					else
    					{
    						mMainThread.rightPaddle.setIsPressed(false);
    					}
    				}
    				else if (action == MotionEvent.ACTION_UP)
    				{
    					mMainThread.leftPaddle.setIsPressed(false);
    					mMainThread.rightPaddle.setIsPressed(false);
    				}
    					
    				if (action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_DOWN)
    				{
        				// LHS
        				if (x < width / 2)
        				{
        					mMainThread.leftPaddle.setIsPressed(true);
        				}
        				
        				// RHS
        				else
        				{
        					mMainThread.rightPaddle.setIsPressed(true);
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
    
    private void addPlayerScores()
    {
    	RelativeLayout l = (RelativeLayout) findViewById(R.id.layout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        
        GameManager.PlayerOneScore = new TextView(this);
        GameManager.PlayerOneScore.setText(Integer.toString(GameManager.Game.getPlayerAtIndex(1).getScore()));
        GameManager.PlayerOneScore.setTextSize(300);
        GameManager.PlayerOneScore.setLayoutParams(params);
        GameManager.PlayerOneScore.measure(0, 0);
        GameManager.PlayerOneScore.setX(GameManager.SCREEN_WIDTH / 4 - GameManager.PlayerOneScore.getMeasuredWidth() / 2);
        GameManager.PlayerOneScore.setY(GameManager.SCREEN_HEIGHT / 2 - GameManager.PlayerOneScore.getMeasuredHeight() / 2);
        GameManager.PlayerOneScore.setAlpha(200);
        l.addView(GameManager.PlayerOneScore);
        
        GameManager.PlayerTwoScore = new TextView(this);
        GameManager.PlayerTwoScore.setText(Integer.toString(GameManager.Game.getPlayerAtIndex(2).getScore()));
        GameManager.PlayerTwoScore.setTextSize(300);
        GameManager.PlayerTwoScore.setLayoutParams(params);
        GameManager.PlayerTwoScore.measure(0, 0);
        GameManager.PlayerTwoScore.setX((GameManager.SCREEN_WIDTH / 4) * 3 - GameManager.PlayerTwoScore.getMeasuredWidth() / 2);
        GameManager.PlayerTwoScore.setY(GameManager.SCREEN_HEIGHT / 2 - GameManager.PlayerTwoScore.getMeasuredHeight() / 2);
        GameManager.PlayerTwoScore.setAlpha(200);
        l.addView(GameManager.PlayerTwoScore);
    }
}
