package net.somethingsuperawesome.awesomebans.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.somethingsuperawesome.awesomebans.AwesomeBans;
import net.somethingsuperawesome.awesomebans.AwesomeBansMessages;
import net.somethingsuperawesome.awesomebans.AwesomeBansPermissions;
import net.somethingsuperawesome.awesomebans.AwesomeBansPlayerMatcher;
import net.somethingsuperawesome.awesomebans.AwesomeBansSettings;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AwesomeBansCommandCheck
{
	private static PreparedStatement selectPlayerInfo = null;
	private static PreparedStatement selectPunRec = null;
	
	private static String selectPlayerInfoQ;
	private static String selectPunRecQ;
	
	public static boolean check(CommandSender sender, String[] args, AwesomeBans plugin)
	{
		
		if(AwesomeBansPermissions.canCheck(sender))
		{
			if(args.length < 1)
			{
				AwesomeBansMessages.sendError(sender, "Not enough args. Please use the correct format.");
				return false;
			}
			if((args[0].equalsIgnoreCase("detail")|| args[0].equalsIgnoreCase("d")) && args.length==2)
			{
				return AwesomeBansCommandCheckDetail.checkDetail(sender, args[1], plugin);
			}
			if(plugin.checkMySQL())
			{
				selectPlayerInfoQ = "SELECT WarningLevel, isBanned, isMuted, Lives FROM "+AwesomeBansSettings.getPlayers() + " WHERE PlayerName = ?";
				selectPunRecQ ="SELECT * FROM "+AwesomeBansSettings.getPunRec() + " WHERE PunishedPlayer = ?";
				
				try
				{
					selectPlayerInfo = plugin.getMysql().prepare(selectPlayerInfoQ);
					selectPunRec = plugin.getMysql().prepare(selectPunRecQ);
				} catch (SQLException e1)
				{
					e1.printStackTrace();
				}
				String targetName = AwesomeBansPlayerMatcher.getFullName(args[0]);
				if(targetName == "")
				{
					AwesomeBansMessages.sendError(sender, "Player not found");
					return true;
				}
				else
				{				
					boolean isBanned=false;
					boolean isMuted=false;
					int warningLevel = 0;
					//int lives = 0;
					String status;
					try
					{
						selectPlayerInfo.setString(1, targetName);
						AwesomeBansMessages.debug(selectPlayerInfo.toString());
						ResultSet result = plugin.getMysql().query(selectPlayerInfo);
						if(result.next())
						{
							warningLevel = result.getInt("WarningLevel");
							isBanned = result.getBoolean("isBanned");
							isMuted = result.getBoolean("isMuted");
							//lives = result.getInt("Lives");
						}
						selectPunRec.setString(1, targetName);
						AwesomeBansMessages.debug(selectPunRec.toString());
						result.close();
						selectPlayerInfo.close();
						result = null;
						result = plugin.getMysql().query(selectPunRec);
						while(result.next())
						{
							int recordID = result.getInt("PunishmentRecordId");
							//String punished = result.getString("PunishedPlayer");
							String issuer =result.getString("Issuer");
							String type = result.getString("Type");
							//String reason2 = result.getString("Reason");
							//String serverName2 = result.getString("ServerName");
							//Long timeIssued = result.getLong("TimeIssued");
							//Long endTime = result.getLong("EndTime");
							AwesomeBansMessages.sendMessage(sender, ChatColor.DARK_GREEN+"RecID: "+ ChatColor.YELLOW+recordID + ChatColor.DARK_GREEN+" Issuer: "+ ChatColor.YELLOW+issuer + ChatColor.DARK_GREEN+" Type: "+ChatColor.YELLOW+type);
						}
						if(isBanned)
						{
							status = "Banned";
						}
						else if(isMuted)
						{
							status = "Muted";
						}
						else
						{
							status = "Normal";
						}
						AwesomeBansMessages.sendMessage(sender, ChatColor.DARK_GREEN+"Warning Level: "+ChatColor.YELLOW+ warningLevel + ChatColor.DARK_GREEN+" Status: "+ChatColor.YELLOW+status);
						result.close();
						selectPunRec.close();
						return true;
					} 
					catch (SQLException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
			}
			else
			{
				AwesomeBansMessages.sendError(sender, "MYSQL could not be reached");
				return true;
			}
		}
		else
		{
			AwesomeBansMessages.noPermission(sender);
			return true;
		}
	}
}
