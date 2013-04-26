package net.somethingsuperawesome.awesomebans;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class AwesomeBansPlayerMatcher 
{
	public static String getFullName(String name)
	{
		if (name.length() < 3)
			return null;

		for (Player player: Bukkit.getServer().getOnlinePlayers())
		{
			if (player.getName().equalsIgnoreCase(name))
				return player.getName();
		}

		for (Player player: Bukkit.getServer().getOnlinePlayers())
		{
			if (player.getName().toLowerCase().contains(name.toLowerCase()))
				return player.getName();
		}

		for (OfflinePlayer player: Bukkit.getServer().getOfflinePlayers())
		{
			if (player.getName().equalsIgnoreCase(name))
				return player.getName();
		}

		for (OfflinePlayer player: Bukkit.getServer().getOfflinePlayers())
		{
			if (player.getName().toLowerCase().contains(name.toLowerCase()))
				return player.getName();
		}
		return "";
	}
	public static OfflinePlayer getPlayer(String name)
	{
		if (name.length() < 3)
			return null;

		for (Player player: Bukkit.getServer().getOnlinePlayers())
		{
			if (player.getName().equalsIgnoreCase(name))
				return player;
		}

		for (Player player: Bukkit.getServer().getOnlinePlayers())
		{
			if (player.getName().toLowerCase().contains(name.toLowerCase()))
				return player;
		}

		for (OfflinePlayer player: Bukkit.getServer().getOfflinePlayers())
		{
			if (player.getName().equalsIgnoreCase(name))
				return player;
		}

		for (OfflinePlayer player: Bukkit.getServer().getOfflinePlayers())
		{
			if (player.getName().toLowerCase().contains(name.toLowerCase()))
				return player;
		}
		return null;
	}
	public static Player getOnlinePlayer(String name)
	{
		if (name.length() < 3)
			return null;

		for (Player player: Bukkit.getServer().getOnlinePlayers())
		{
			if (player.getName().equalsIgnoreCase(name))
				return player;
		}

		for (Player player: Bukkit.getServer().getOnlinePlayers())
		{
			if (player.getName().toLowerCase().contains(name.toLowerCase()))
				return player;
		}
		return null;
	}
	
}
