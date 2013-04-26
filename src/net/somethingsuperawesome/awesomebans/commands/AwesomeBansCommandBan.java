package net.somethingsuperawesome.awesomebans.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.somethingsuperawesome.awesomebans.AwesomeBansDurationParser;
import net.somethingsuperawesome.awesomebans.AwesomeBansMessages;
import net.somethingsuperawesome.awesomebans.AwesomeBansPlayerMatcher;
import net.somethingsuperawesome.awesomebans.AwesomeBans;
import net.somethingsuperawesome.awesomebans.AwesomeBansPermissions;
import net.somethingsuperawesome.awesomebans.AwesomeBansSettings;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AwesomeBansCommandBan
{
	private static PreparedStatement selectWarnLevel;
	private static PreparedStatement updatePlayerBanned;
	private static PreparedStatement insertPunRec;
	private static PreparedStatement selectPunID;
	private static PreparedStatement selectPlayerBan;
	private static PreparedStatement replacePlayerBan;
	private static PreparedStatement insertPlayerBan;
	private static PreparedStatement addPlayer = null;
	
	private static String addPlayerQuery;
	private static String selectWarnLevelQ;
	private static String updatePlayerBannedQ;
	private static String insertPunRecQ;
	private static String selectPunIDQ;
	private static String selectPlayerBanQ;
	private static String replacePlayerBanQ;
	private static String insertPlayerBanQ;
	
	public static boolean ban(CommandSender sender, String[] args, AwesomeBans plugin)
	{
		if(AwesomeBansPermissions.canBan(sender))
		{
			if(args.length < 3)
			{
					AwesomeBansMessages.sendError(sender, "Not enough args. Please use the correct format.");
					return false;
			}
			if(plugin.checkMySQL())
			{			
				int warningLevel = 0;
				long duration = 0;
				long endTime = 0;
				long time = System.currentTimeMillis();	
				if(args[1].toLowerCase().startsWith("perm"))
				{
					endTime = Long.MAX_VALUE;
				}
				else
				{
					duration = AwesomeBansDurationParser.getDuration(args[1]);
					endTime = time + duration;
					if(duration == 0)
					{
						AwesomeBansMessages.sendError(sender, "Invalid duration entered, a valid duration is required.");
						return false;
					}
				}
				
				OfflinePlayer target = AwesomeBansPlayerMatcher.getPlayer(args[0]);
				String targetName;
				if(target == null)
					targetName = args[0];
				else
					targetName = target.getName();
				
				selectWarnLevelQ = "SELECT WarningLevel FROM "+AwesomeBansSettings.getPlayers() + " WHERE PlayerName = ?";
				updatePlayerBannedQ = "UPDATE "+AwesomeBansSettings.getPlayers()+" SET WarningLevel = ?, isBanned = 1 WHERE PlayerName = ?";
				insertPunRecQ = "INSERT INTO "+AwesomeBansSettings.getPunRec() +" (PunishedPlayer, Issuer, Type, Reason, ServerName, TimeIssued, EndTime, WarnLevel) " +
						"VALUES (?, ?, 'ban', ?, '"+ AwesomeBansSettings.getServerName() +"', ?, ?, 10)";
				selectPunIDQ = "SELECT PunishmentRecordId FROM "+AwesomeBansSettings.getPunRec() +" WHERE TimeIssued = ?";
				selectPlayerBanQ = "SELECT * FROM "+AwesomeBansSettings.getBans()+" WHERE PlayerName = ?";
				replacePlayerBanQ = "REPLACE INTO "+AwesomeBansSettings.getBans()+" VALUES(?,?,?,?,?)";
				insertPlayerBanQ = "INSERT INTO "+AwesomeBansSettings.getBans()+" VALUES(?,?,?,?,?)";
				try
				{			
					ResultSet result;
					StringBuilder reason = new StringBuilder();
					for(int k = 2; k<args.length; k++)
					{
						reason.append(args[k]+" ");
					}
					selectWarnLevel = plugin.getMysql().prepare(selectWarnLevelQ);
					selectWarnLevel.setString(1, targetName);
					AwesomeBansMessages.debug(selectWarnLevel.toString());
					result = plugin.getMysql().query(selectWarnLevel);
					if(result.next())
						warningLevel = result.getInt("WarningLevel");
					else
						addPlayer(targetName, plugin);
					selectWarnLevel.close();
					warningLevel= warningLevel+10;
					updatePlayerBanned = plugin.getMysql().prepare(updatePlayerBannedQ);
					updatePlayerBanned.setInt(1, warningLevel);
					updatePlayerBanned.setString(2, targetName);
					AwesomeBansMessages.debug(updatePlayerBanned.toString());
					plugin.getMysql().query(updatePlayerBanned);
					updatePlayerBanned.close();
					insertPunRec = plugin.getMysql().prepare(insertPunRecQ);
					insertPunRec.setString(1, targetName);
					insertPunRec.setString(2, sender.getName());
					insertPunRec.setString(3, reason.toString());
					insertPunRec.setLong(4, time);
					insertPunRec.setLong(5, endTime);
					AwesomeBansMessages.debug(insertPunRec.toString());
					plugin.getMysql().query(insertPunRec);
					insertPunRec.close();
					selectPunID = plugin.getMysql().prepare(selectPunIDQ);
					selectPunID.setLong(1,time);
					AwesomeBansMessages.debug(selectPunID.toString());
					result.close();
					result = null;
					result = plugin.getMysql().query(selectPunID);
					int punId = 0;
					if(result.next())
						punId = result.getInt("PunishmentRecordId");
					selectPunID.close();
					selectPlayerBan = plugin.getMysql().prepare(selectPlayerBanQ);
					selectPlayerBan.setString(1, targetName);
					AwesomeBansMessages.debug(selectPlayerBan.toString());
					result.close();
					result = null;
					result = plugin.getMysql().query(selectPlayerBan);
					if(result.next())
					{
						replacePlayerBan = plugin.getMysql().prepare(replacePlayerBanQ);
						replacePlayerBan.setString(1, targetName);
						replacePlayerBan.setLong(2, endTime);
						replacePlayerBan.setString(3, sender.getName());
						replacePlayerBan.setString(4, reason.toString());
						replacePlayerBan.setInt(5, punId);
						AwesomeBansMessages.debug(replacePlayerBan.toString());
						plugin.getMysql().query(replacePlayerBan);
						replacePlayerBan.close();
					}
					else
					{
						insertPlayerBan = plugin.getMysql().prepare(insertPlayerBanQ);
						insertPlayerBan.setString(1, targetName);
						insertPlayerBan.setLong(2, endTime);
						insertPlayerBan.setString(3, sender.getName());
						insertPlayerBan.setString(4, reason.toString());
						insertPlayerBan.setInt(5, punId);
						AwesomeBansMessages.debug(insertPlayerBan.toString());
						plugin.getMysql().query(insertPlayerBan);
						insertPlayerBan.close();
					}
					selectPlayerBan.close();
					
					if(target instanceof Player)
					{
						AwesomeBansMessages.kickBan(((Player)target), sender, endTime, reason.toString());
					}
					if(target != null)
						AwesomeBansMessages.broadcast("Banned", sender, target, reason.toString(), endTime);
					else
						AwesomeBansMessages.broadcast("Banned", sender, targetName, reason.toString(), endTime);				
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					AwesomeBansMessages.sendMySQLError(sender);
				}
				return true;
			}	
			else
			{
				AwesomeBansMessages.sendMySQLError(sender);
				return true;
			}
		}
		else
		{
			AwesomeBansMessages.noPermission(sender);
			return true;
		}			
	}

	private static void addPlayer(String player, AwesomeBans plugin)
	{
		addPlayerQuery = "INSERT INTO "+AwesomeBansSettings.getPlayers()+" VALUES (?, 0, 0, 0, "+AwesomeBansSettings.getDefaultLives()+", 0, 0)";
		try
		{
			addPlayer = plugin.getMysql().prepare(addPlayerQuery);
			addPlayer.setString(1, player);
			plugin.getMysql().query(addPlayer);
			addPlayer.close();
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AwesomeBansMessages.info("Creating new player record for: "+ player);
	}
}
