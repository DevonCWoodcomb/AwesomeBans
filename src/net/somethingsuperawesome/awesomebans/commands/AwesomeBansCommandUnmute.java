package net.somethingsuperawesome.awesomebans.commands;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.somethingsuperawesome.awesomebans.AwesomeBans;
import net.somethingsuperawesome.awesomebans.AwesomeBansMessages;
import net.somethingsuperawesome.awesomebans.AwesomeBansPermissions;
import net.somethingsuperawesome.awesomebans.AwesomeBansPlayerMatcher;
import net.somethingsuperawesome.awesomebans.AwesomeBansSettings;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class AwesomeBansCommandUnmute
{
	private static PreparedStatement setPlayerMuted = null;
	private static PreparedStatement deletePlayerMute = null;
	
	private static String setPlayerMutedQuery;
	private static String deletePlayerMuteQuery;
	public static boolean unmute(CommandSender sender, String[] args, AwesomeBans plugin)
	{
		if(AwesomeBansPermissions.canUnmute(sender))	
		{
			if(plugin.checkMySQL())
			{
				if(args.length > 1)
				{
					AwesomeBansMessages.sendMessage(sender, "Too many args. Please use the correct format.");
					return false;
				}				
				OfflinePlayer target = AwesomeBansPlayerMatcher.getPlayer(args[0]);
				if(target == null)
				{
					AwesomeBansMessages.sendMessage(sender, "Player not found");
					return true;
				}
				String targetName = target.getName();
				try
				{
					setPlayerMutedQuery = "UPDATE "+ AwesomeBansSettings.getPlayers()+" SET isMuted = 0 WHERE PlayerName = ?";
					deletePlayerMuteQuery = "DELETE FROM "+AwesomeBansSettings.getMutes()+" WHERE PlayerName = ?";
					setPlayerMuted = plugin.getMysql().prepare(setPlayerMutedQuery);
					deletePlayerMute = plugin.getMysql().prepare(deletePlayerMuteQuery);
					setPlayerMuted.setString(1, targetName);
					AwesomeBansMessages.debug(setPlayerMuted.toString());
					plugin.getMysql().query(setPlayerMuted);
					setPlayerMuted.close();
					deletePlayerMute.setString(1, targetName);
					AwesomeBansMessages.debug(deletePlayerMute.toString());
					plugin.getMysql().query(deletePlayerMute);
					deletePlayerMute.close();
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				if(target.getPlayer() != null)
					AwesomeBansMessages.sendMessage(target.getPlayer(), "You have been unmuted");
				AwesomeBansMessages.sendMessage(sender, ""+targetName+" has been unmuted");
				plugin.removeMuted(targetName);
				return true;
			}
			else
			{
				AwesomeBansMessages.sendMessage(sender, "MySQL could not be reached, please try again");
				return true;
			}
		}
		else
		{
			AwesomeBansMessages.sendMessage(sender, "You don't have permission for that command.");
			return true;
		}
	}

}
