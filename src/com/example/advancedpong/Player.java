package com.example.advancedpong;

public class Player
{
	private int playerIndex;
	private int score;
	
	public int getPlayerIndex()
	{
		return this.playerIndex;
	}
	
	public int getScore()
	{
		return this.score;
	}

	public void addScore()
	{
		this.score++;
	}
	
	public void addScore(int score)
	{
		this.score += score;
	}
	
	public void resetScore()
	{
		this.score = 0;
	}
	
	public Player(int playerIndex)
	{
		this.playerIndex = playerIndex;
		this.score = 0;
	}
}
