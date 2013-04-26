package net.somethingsuperawesome.awesomebans.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.somethingsuperawesome.awesomebans.AwesomeBans;
import net.somethingsuperawesome.awesomebans.AwesomeBansMessages;
import net.somethingsuperawesome.awesomebans.AwesomeBansPermissions;
import net.somethingsuperawesome.awesomebans.AwesomeBansSettings;

import org.bukkit.command.CommandSender;

public class AwesomeBansCommandCheckDetail
{
	private static PreparedStatement selectPunRec = null;
	private static String selectPunRecQ;
	
	public static boolean checkDetail(CommandSender sender, String arg, AwesomeBans plugin)
	{
		if(AwesomeBansPermissions.canCheck(sender))
		{
			int recordid;
			try
			{
				recordid = Integer.parseInt(arg);
			}
			catch(Exception e)
			{
				AwesomeBansMessages.sendError(sender,"Invalid Record ID");
				return true;
			}
			if(plugin.checkMySQL())
			{
				selectPunRecQ ="SELECT * FROM "+AwesomeBansSettings.getPunRec() + " WHERE PunishmentRecordId = ?";	
				try
				{
					selectPunRec = plugin.getMysql().prepare(selectPunRecQ);
					selectPunRec.setInt(1, recordid);
					ResultSet result = plugin.getMysql().query(selectPunRec);
					if(result.next())
					{
						String PunishedPlayer = result.getString("PunishedPlayer");
						String Issuer = result.getString("Issuer");
						String Type = result.getString("Type");
						String Reason = result.getString("Reason");
						String ServerName = result.getString("ServerName");
						Long TimeIssued = result.getLong("TimeIssued");
						Long EndTime = result.getLong("EndTime");
						int WarnLevel = result.getInt("WarnLevel");
						
						AwesomeBansMessages.sendDetail(sender, Issuer,  Type, PunishedPlayer, ServerName);
						AwesomeBansMessages.sendDetail(sender, TimeIssued, EndTime, WarnLevel);
						AwesomeBansMessages.sendDetail(sender, Reason);
						
						result.close();
						selectPunRec.close();
						return true;
					}
					else
					{
						AwesomeBansMessages.sendError(sender, "No record found with that ID");
						return true;
					}
				} 
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
			else
			{
				AwesomeBansMessages.sendError(sender, "MySQL could not be reached, please try again");
				return true;
			}
		}
		else
		{
			AwesomeBansMessages.noPermission(sender);
			return true;
		}
		return false;
	}

	public static boolean checkDetail(CommandSender sender, String[] args, AwesomeBans plugin)
	{
		if(AwesomeBansPermissions.canCheck(sender))
		{
			if(args.length>0)
			{
				return checkDetail(sender, args[0], plugin);
			}
			else
			{
				AwesomeBansMessages.sendError(sender, "Not enough args. Please use the correct format.");
				return false;
			}
		}
		else
		{
			AwesomeBansMessages.noPermission(sender);
			return true;
		}
	}

}
