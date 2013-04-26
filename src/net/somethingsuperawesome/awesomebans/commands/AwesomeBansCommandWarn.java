package net.somethingsuperawesome.awesomebans.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.somethingsuperawesome.awesomebans.AwesomeBans;
import net.somethingsuperawesome.awesomebans.AwesomeBansMessages;
import net.somethingsuperawesome.awesomebans.AwesomeBansPermissions;
import net.somethingsuperawesome.awesomebans.AwesomeBansPlayerMatcher;
import net.somethingsuperawesome.awesomebans.AwesomeBansSettings;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AwesomeBansCommandWarn
{
	private static PreparedStatement selectWarningLevel;
	private static PreparedStatement updateWarningLevel;
	private static PreparedStatement insertPunRec;
	
	private static String selectWarningLevelQ;
	private static String updateWarningLevelQ;
	private static String insertPunRecQ;
	
	public static boolean warn(CommandSender sender, String[] args, AwesomeBans plugin)
	{
		if(AwesomeBansPermissions.canWarn(sender))
		{
			if(args.length < 2)
			{
				AwesomeBansMessages.sendError(sender, "Not enough args. Please use the correct format.");
				return false;
			}
			Player target = AwesomeBansPlayerMatcher.getOnlinePlayer(args[0]);
			if(target == null)
			{
				AwesomeBansMessages.sendError(sender, "Player not found or is offline.");
				return true;
			}
			selectWarningLevelQ = "SELECT WarningLevel FROM "+AwesomeBansSettings.getPlayers()+" WHERE PlayerName = ?";
			updateWarningLevelQ = "UPDATE "+AwesomeBansSettings.getPlayers()+" SET WarningLevel = ? WHERE PlayerName = ?";
			insertPunRecQ = "INSERT INTO "+AwesomeBansSettings.getPunRec() +" (PunishedPlayer, Issuer, Type, Reason, ServerName, TimeIssued, EndTime, WarnLevel) " +
					"VALUES (?, ?, 'warn', ?, '"+ AwesomeBansSettings.getServerName() +"', ?, 0, 1)";
			try
			{
				selectWarningLevel = plugin.getMysql().prepare(selectWarningLevelQ);
				updateWarningLevel = plugin.getMysql().prepare(updateWarningLevelQ);
				insertPunRec = plugin.getMysql().prepare(insertPunRecQ);
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
			ResultSet result;
			int warningLevel = 0;
			long time = System.currentTimeMillis();
			StringBuilder reason = new StringBuilder();
			for(int k = 1; k<args.length; k++)
			{
				reason.append(args[k]+" ");			
			}
			String targetName = target.getName();
			try
			{
				selectWarningLevel.setString(1, targetName);
				AwesomeBansMessages.debug(selectWarningLevel.toString());
				result = plugin.getMysql().query(selectWarningLevel);
				if(result.next())
				{
					warningLevel = result.getInt("WarningLevel");
				}
				result.close();
				selectWarningLevel.close();
				warningLevel += 1;
				
				updateWarningLevel.setInt(1, warningLevel);
				updateWarningLevel.setString(2, targetName);
				AwesomeBansMessages.debug(updateWarningLevel.toString());
				plugin.getMysql().query(updateWarningLevel);
				updateWarningLevel.close();
				
				insertPunRec.setString(1, targetName);
				insertPunRec.setString(2, sender.getName());
				insertPunRec.setString(3, reason.toString());
				insertPunRec.setLong(4, time);
				AwesomeBansMessages.debug(insertPunRec.toString());
				plugin.getMysql().query(insertPunRec);
				insertPunRec.close();
				
				AwesomeBansMessages.warn(target, sender, reason.toString());
				return true;
			} catch (SQLException e)
			{
				e.printStackTrace();
			}	
		}
		else
		{
			AwesomeBansMessages.noPermission(sender);
			return true;
		}
		return false;
	}
}
