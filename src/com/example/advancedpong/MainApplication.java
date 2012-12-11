package com.example.advancedpong;

import android.app.Application;
import android.content.Context;

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
	}
}
