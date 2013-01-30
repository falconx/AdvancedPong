package com.example.advancedpong;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class MainApplication extends Application
{
	private static Context context;
	
	public static Context getContext()
	{
		return MainApplication.context;
	}
	
	public void onCreate()
	{
		super.onCreate();
		MainApplication.context = getApplicationContext();
		
		// Set screen height and width properties.
		WindowManager wm = (WindowManager) MainApplication.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
    	Point size = new Point();
    	display.getSize(size);
    	
    	GameManager.SCREEN_HEIGHT = size.y;
    	GameManager.SCREEN_WIDTH = size.x;
	}
}
