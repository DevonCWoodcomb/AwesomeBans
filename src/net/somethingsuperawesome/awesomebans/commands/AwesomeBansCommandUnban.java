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

public class AwesomeBansCommandUnban
{
	private static PreparedStatement updatePlayerBanned;
	private static PreparedStatement deletePlayerBan;
	
	private static String updatePlayerBannedQuery;
	private static String deletePlayerBanQuery;
	
	public static boolean unban(CommandSender sender, String[] args, AwesomeBans plugin)
	{
		if(AwesomeBansPermissions.canUnban(sender))
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
				
				updatePlayerBannedQuery = "UPDATE "+AwesomeBansSettings.getPlayers()+" SET isBanned = 0 WHERE PlayerName = ?";;
				deletePlayerBanQuery = "DELETE FROM "+AwesomeBansSettings.getBans()+" WHERE PlayerName = ?";				
				try
				{
					updatePlayerBanned = plugin.getMysql().prepare(updatePlayerBannedQuery);
					deletePlayerBan = plugin.getMysql().prepare(deletePlayerBanQuery);
				} 
				catch (SQLException e1)
				{
					e1.printStackTrace();
				}
				String targetName = target.getName();
				try
				{
					updatePlayerBanned.setString(1, targetName);
					AwesomeBansMessages.debug(updatePlayerBanned.toString());
					plugin.getMysql().query(updatePlayerBanned);
					updatePlayerBanned.close();
					
					deletePlayerBan.setString(1, targetName);
					AwesomeBansMessages.debug(deletePlayerBan.toString());
					plugin.getMysql().query(deletePlayerBan);
					deletePlayerBan.close();
					
				} catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
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
