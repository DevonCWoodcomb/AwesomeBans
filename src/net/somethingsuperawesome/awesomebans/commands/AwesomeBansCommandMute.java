package net.somethingsuperawesome.awesomebans.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.somethingsuperawesome.awesomebans.AwesomeBans;
import net.somethingsuperawesome.awesomebans.AwesomeBansDurationParser;
import net.somethingsuperawesome.awesomebans.AwesomeBansMessages;
import net.somethingsuperawesome.awesomebans.AwesomeBansPermissions;
import net.somethingsuperawesome.awesomebans.AwesomeBansPlayerMatcher;
import net.somethingsuperawesome.awesomebans.AwesomeBansSettings;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AwesomeBansCommandMute
{
	private static PreparedStatement selectWarningLevel;
	private static PreparedStatement updatePlayerMuted;
	private static PreparedStatement insertPunRec;
	private static PreparedStatement selectPunRecID;
	private static PreparedStatement selectMute;
	private static PreparedStatement insertMute;
	private static PreparedStatement replaceMute;
	
	private static String selectWarningLevelQ;
	private static String updatePlayerMutedQ;
	private static String insertPunRecQ;
	private static String selectPunRecIDQ;
	private static String selectMuteQ;
	private static String insertMuteQ;
	private static String replaceMuteQ;
	
	public static boolean mute(CommandSender sender, String[] args, AwesomeBans plugin)
	{
		if(AwesomeBansPermissions.canMute(sender))
		{
			if(args.length < 3)
			{
				AwesomeBansMessages.sendError(sender, "Not enough args. Please use the correct format.");
				return false;
			}		
			if(plugin.checkMySQL())
			{			
				selectWarningLevelQ = "SELECT WarningLevel FROM "+AwesomeBansSettings.getPlayers() + " WHERE PlayerName = ?";
				updatePlayerMutedQ = "UPDATE "+AwesomeBansSettings.getPlayers()+" SET WarningLevel = ?, isMuted = 1 WHERE PlayerName = ?";
				insertPunRecQ = "INSERT INTO "+AwesomeBansSettings.getPunRec() +" (PunishedPlayer, Issuer, Type, Reason, ServerName, TimeIssued, EndTime, WarnLevel) " +
						"VALUES (?, ?, 'mute', ?, '"+ AwesomeBansSettings.getServerName() +"', ?, ?, 3)";
				selectPunRecIDQ = "SELECT PunishmentRecordId FROM "+AwesomeBansSettings.getPunRec() +" WHERE TimeIssued = ?";
				selectMuteQ = "SELECT * FROM "+AwesomeBansSettings.getMutes()+" WHERE PlayerName = ?";
				replaceMuteQ = "REPLACE INTO "+AwesomeBansSettings.getMutes()+" VALUES(?,?,?,?,?)";
				insertMuteQ = "INSERT INTO "+AwesomeBansSettings.getMutes()+" VALUES(?,?,?,?,?)";
				
				try
				{
					ResultSet result;
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
					StringBuilder reason = new StringBuilder();
					if(target == null)
					{
						AwesomeBansMessages.sendError(sender, "Player not found");
						return true;
					}
					else
					{
						for(int k = 2; k<args.length; k++)
						{
							reason.append(args[k]+" ");
						}
						String targetName = target.getName();
						selectWarningLevel = plugin.getMysql().prepare(selectWarningLevelQ);
						selectWarningLevel.setString(1, targetName);
						AwesomeBansMessages.debug(selectWarningLevel.toString());
						result = plugin.getMysql().query(selectWarningLevel);
						if(result.next())
							warningLevel = result.getInt("WarningLevel");
						warningLevel= warningLevel+3;
						selectWarningLevel.close();
						
						updatePlayerMuted = plugin.getMysql().prepare(updatePlayerMutedQ);
						updatePlayerMuted.setInt(1, warningLevel);
						updatePlayerMuted.setString(2, targetName);
						AwesomeBansMessages.debug(updatePlayerMuted.toString());
						plugin.getMysql().query(updatePlayerMuted);
						updatePlayerMuted.close();
						
						insertPunRec = plugin.getMysql().prepare(insertPunRecQ);
						insertPunRec.setString(1, targetName);
						insertPunRec.setString(2, sender.getName());
						insertPunRec.setString(3, reason.toString());
						insertPunRec.setLong(4, time);
						insertPunRec.setLong(5, endTime);
						AwesomeBansMessages.debug(insertPunRec.toString());
						plugin.getMysql().query(insertPunRec);
						insertPunRec.close();
						
						selectPunRecID = plugin.getMysql().prepare(selectPunRecIDQ);
						selectPunRecID.setLong(1,time);
						AwesomeBansMessages.debug(selectPunRecID.toString());
						result.close();
						result = null;
						result = plugin.getMysql().query(selectPunRecID);
						int punId = 0;
						if(result.next())
							punId = result.getInt("PunishmentRecordId");
						selectPunRecID.close();
						
						selectMute = plugin.getMysql().prepare(selectMuteQ);
						selectMute.setString(1, targetName);
						AwesomeBansMessages.debug(selectMute.toString());
						result.close();
						result = null;
						result = plugin.getMysql().query(selectMute);
						if(result.next())
						{
							replaceMute = plugin.getMysql().prepare(replaceMuteQ);
							replaceMute.setString(1, targetName);
							replaceMute.setLong(2, endTime);
							replaceMute.setString(3, sender.getName());
							replaceMute.setString(4, reason.toString());
							replaceMute.setInt(5, punId);
							AwesomeBansMessages.debug(replaceMute.toString());
							plugin.getMysql().query(replaceMute);
							replaceMute.close();
						}
						else
						{
							insertMute = plugin.getMysql().prepare(insertMuteQ);
							insertMute.setString(1, targetName);
							insertMute.setLong(2, endTime);
							insertMute.setString(3, sender.getName());
							insertMute.setString(4, reason.toString());
							insertMute.setInt(5, punId);
							AwesomeBansMessages.debug(insertMute.toString());
							plugin.getMysql().query(insertMute);
							insertMute.close();
						}
						selectMute.close();
						plugin.importMutes();
						if(target instanceof Player)
						{
							AwesomeBansMessages.muted(((Player)target), sender, reason.toString(), endTime);
						}									
					} 
					AwesomeBansMessages.broadcast("Muted", sender, target, reason.toString(), endTime);
				}
				catch (SQLException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return true;
			}
			else
			{
				AwesomeBansMessages.sendError(sender, "MySQL could not be reached, please try again");
				return true;
			}		
		}
		else
		{
			AwesomeBansMessages.sendError(sender, "You don't have permission for that command.");
			return true;
		}
	}
}
