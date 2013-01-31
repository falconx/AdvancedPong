package com.example.advancedpong;

import java.util.ArrayList;
import java.util.List;

public class Game
{
	private List<Player> players;
	
	public List<Ball> balls;
	public List<Paddle> paddles;
	
	public Game()
	{
		this.players = new ArrayList<Player>();
		this.players.add(new Player(1));
		this.players.add(new Player(2));
		
		this.balls = new ArrayList<Ball>();
		this.paddles = new ArrayList<Paddle>();
	}
	
	/**
	 * Gets the player at the specified playerIndex.
	 */
	public Player getPlayerAtIndex(int playerIndex)
	{
		for (Player p : this.players)
		{
			if (p.getPlayerIndex() == playerIndex)
			{
				return p;
			}
		}
		
		return null;
	}
}
