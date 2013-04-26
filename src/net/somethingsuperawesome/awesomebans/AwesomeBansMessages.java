package net.somethingsuperawesome.awesomebans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AwesomeBansMessages
{
	private static DateFormat yearToMinute = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	//private static DateFormat dayMonth = new SimpleDateFormat("MM/dd/yyyy HH:mm");
	@Setter
	private static Logger log;
	private static String primary = ChatColor.DARK_GREEN.toString();
	private static String secondary = ChatColor.YELLOW.toString();
	private static String error = ChatColor.RED.toString();
	private static String punishment = ChatColor.RED.toString();
	
	
	public static void broadcast(String type, CommandSender sender, OfflinePlayer target, String reason, long endTime)
	{
		if(AwesomeBansSettings.isBroadcast())
		{	
			String targetName, senderName, end;
			if(target instanceof Player)
				targetName = ((Player)target).getDisplayName();
			else
				targetName = target.getName();
			if(sender instanceof Player)
				senderName = ((Player)sender).getDisplayName();
			else
				senderName = sender.getName();
			if(endTime == Long.MAX_VALUE)
				end = "Forever";
			else
				end = yearToMinute.format(endTime);
			Bukkit.getServer().broadcastMessage(AwesomeBansSettings.getChatPrefix()+secondary+targetName + primary+" has been "+punishment+type+ primary+" by "+secondary+senderName+ primary+" Until: "+secondary+end+ primary+" for reason: "+secondary+reason);
		}
	}
	public static void broadcast(String type, CommandSender sender, OfflinePlayer target, String reason)
	{
		if(AwesomeBansSettings.isBroadcast())
		{
			String targetName, senderName;
			if(target instanceof Player)
				targetName = ((Player)target).getDisplayName();
			else
				targetName = target.getName();
			if(sender instanceof Player)
				senderName = ((Player)sender).getDisplayName();
			else
				senderName = sender.getName();
			Bukkit.getServer().broadcastMessage(AwesomeBansSettings.getChatPrefix()+secondary+targetName + primary+" has been "+punishment+type+ primary+" by "+secondary+senderName+ primary+" for reason: "+secondary+reason);
		}
	}
	public static void sendMessage(CommandSender sender, String message)
	{
		sender.sendMessage(AwesomeBansSettings.getChatPrefix()+secondary+message);
	}
	public static void sendMessage(Player player, String message)
	{
		player.sendMessage(AwesomeBansSettings.getChatPrefix()+ secondary+message);
	}
	public static void kickBan(Player target, CommandSender sender, long endTime, String reason)
	{
		target.kickPlayer("You were banned by: "+sender.getName()+" Until: "+yearToMinute.format(endTime) + " For: "+reason);
	}
	public static void kick(Player target, CommandSender sender, String reason)
	{
		target.kickPlayer("You were kicked by: "+sender.getName()+" For: "+reason);
	}
	public static void muted(Player player, CommandSender sender, String reason, long mutedTil)
	{
		player.sendMessage(AwesomeBansSettings.getChatPrefix()+error+"You are muted and cannot chat. Muted by: "+ secondary+sender.getName()+ error+" For: "+secondary+reason+ error+" Until: " + secondary+yearToMinute.format(mutedTil));		
	}
	public static void debug(String message)
	{
		if(AwesomeBansSettings.isDebugOn())
		{
			log.info("AwesomeBansDebug: "+secondary+message);
		}
	}
	public static void info(String message)
	{
		log.info(AwesomeBansSettings.getChatPrefix()+secondary+message);
	}
	public static void warn(Player target, CommandSender sender, String reason)
	{
		String senderName;
		if(sender instanceof Player)
			senderName = ((Player)sender).getDisplayName();
		else
			senderName = sender.getName();
		target.sendMessage(AwesomeBansSettings.getChatPrefix()+punishment+" You were warned by "+secondary+senderName+punishment+" For: "+secondary+reason);
		if(AwesomeBansSettings.isBroadcast())
		{
			broadcast("Warned", sender, target, reason);
		}
		else
		{
			sendMessage(sender, "You have warned "+target.getDisplayName());
		}
		
	}
	public static void muted(Player player, String mutedBy, String reason, long mutedTil)
	{
		player.sendMessage(AwesomeBansSettings.getChatPrefix()+punishment+"You are muted and cannot chat. Muted by: "+ secondary+mutedBy+ primary+" For: "+secondary+reason+ primary+" Until: " + secondary+yearToMinute.format(mutedTil));
	}
	public static void noPermission(CommandSender sender)
	{
		sender.sendMessage(AwesomeBansSettings.getChatPrefix()+error+"You do not have permission for that command");	
	}
	public static void sendError(CommandSender sender, String message)
	{
		sender.sendMessage(AwesomeBansSettings.getChatPrefix()+error+message);	
	}
	public static void broadcast(String type, CommandSender sender,	String targetName, String reason, long endTime)
	{
		String senderName, end;
		if(sender instanceof Player)
			senderName = ((Player)sender).getDisplayName();
		else
			senderName = sender.getName();
		if(endTime == Long.MAX_VALUE)
			end = "Forever";
		else
			end = yearToMinute.format(endTime);
		Bukkit.getServer().broadcastMessage(AwesomeBansSettings.getChatPrefix()+secondary+targetName + primary+" has been "+punishment+type+primary+" by "+secondary+senderName+primary+" Until: "+secondary+end+ primary+" for reason: "+secondary+reason);	
	}
	public static void sendDetail(CommandSender sender, String issuer, String type, String punishedPlayer, String serverName)
	{
		sendMessage(sender, primary+"Issuer: "+secondary+issuer+primary+" Type: "+punishment+type+primary+" Punished Player: "+secondary+punishedPlayer+primary+" Server: "+secondary+serverName);		
	}
	public static void sendDetail(CommandSender sender, Long timeIssued, Long endTime, int warnLevel)
	{
		if(endTime > 0)
			sendMessage(sender, primary+"Issued: "+secondary+ yearToMinute.format(timeIssued)+primary+" End Time: "+secondary+yearToMinute.format(endTime)+ primary+" Warning Level Applied: "+secondary+warnLevel);
		else
			sendMessage(sender, primary+"Issued: "+secondary+ yearToMinute.format(timeIssued)+primary+" Warning Level Applied: "+secondary+warnLevel);
	}
	public static void sendDetail(CommandSender sender, String reason)
	{
		sendMessage(sender, primary+"Reason: "+secondary+reason);		
	}
	public static void sendMySQLError(CommandSender sender)
	{
		sender.sendMessage(AwesomeBansSettings.getChatPrefix()+error+"MySQL could not be reached, please try again");	
	}
	public static void loginNotify(Player p, Player player, int warningLevel, boolean isMuted)
	{
		p.sendMessage(AwesomeBansSettings.getChatPrefix()+secondary+player.getDisplayName()+primary+" logged in with Warning Level: "+secondary+warningLevel+primary+" Muted: "+secondary+isMuted);		
	}
	public static void loginNotifyBanned(Player p, String player)
	{
		p.sendMessage(AwesomeBansSettings.getChatPrefix()+secondary+player+primary+" tried to login, but is banned");
	}
	public static void loginAnnounce(Player p, Player player)
	{
		p.sendMessage(ChatColor.GREEN+player.getDisplayName()+" has logged in.");
	}
}
