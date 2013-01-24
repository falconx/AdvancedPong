package com.example.advancedpong;

import java.util.ArrayList;
import java.util.List;

public class Game
{
	private List<Player> players;
	
	public Game()
	{
		this.players = new ArrayList<Player>();
		this.players.add(new Player(1));
		this.players.add(new Player(2));
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
