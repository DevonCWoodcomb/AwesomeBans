package net.somethingsuperawesome.awesomebans.commands;

import net.somethingsuperawesome.awesomebans.AwesomeBans;
import net.somethingsuperawesome.awesomebans.AwesomeBansMessages;
import net.somethingsuperawesome.awesomebans.AwesomeBansPermissions;

import org.bukkit.command.CommandSender;

public class AwesomeBansCommandAwesomeBans
{
	private static String banFormat = "/ban <player> <duration> <reason>";
	private static String unbanFormat = "/unban <player>";
	private static String muteFormat = "/mute <player> <duration> <reason>";
	private static String unmuteFormat = "/unmute <player>";
	private static String kickFormat = "/kick <player> <reason>";
	private static String warnFormat = "/warn <player> <reason>";
	private static String checkFormat = "/check <player>";
	private static String checkDetailFormat = "/checkdetail <record id>";
	
	public static boolean awesomeBans(CommandSender sender, String[] args, AwesomeBans plugin)
	{
		if(args.length==0)
		{
			AwesomeBansMessages.sendMessage(sender, "AwesomeBans is a punishment management plugin that issues and tracks bans, mutes, kicks and warnings");
			AwesomeBansMessages.sendMessage(sender, "For a list of commands type /awesomebans help");
			return true;
		}
		else if(args[0].equalsIgnoreCase("help")||args[0].equalsIgnoreCase("list"))
		{
			if(AwesomeBansPermissions.canBan(sender))
				AwesomeBansMessages.sendMessage(sender, banFormat);
			if(AwesomeBansPermissions.canUnban(sender))
				AwesomeBansMessages.sendMessage(sender, unbanFormat);
			if(AwesomeBansPermissions.canMute(sender))
				AwesomeBansMessages.sendMessage(sender, muteFormat);
			if(AwesomeBansPermissions.canUnmute(sender))
				AwesomeBansMessages.sendMessage(sender, unmuteFormat);
			if(AwesomeBansPermissions.canKick(sender))
				AwesomeBansMessages.sendMessage(sender, kickFormat);
			if(AwesomeBansPermissions.canWarn(sender))
				AwesomeBansMessages.sendMessage(sender, warnFormat);
			if(AwesomeBansPermissions.canCheck(sender))
				AwesomeBansMessages.sendMessage(sender, checkFormat);
			if(AwesomeBansPermissions.canCheck(sender))
				AwesomeBansMessages.sendMessage(sender, checkDetailFormat);
			return true;
		}
		
		
		return false;
	}
	
	
}
