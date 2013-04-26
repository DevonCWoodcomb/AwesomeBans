package net.somethingsuperawesome.awesomebans;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class AwesomeBansPermissions 
{	
	public static boolean loginNotify(Player p)
	{
		if(isMod(p))
			return true;
		return p.hasPermission("AwesomeBans.notify");
	}
	public static boolean isAdmin(Player p)
	{
		if(p.isOp())
			return true;
		return p.hasPermission("AwesomeBans.admin");
	}
	public static boolean isMod(Player p)
	{
		if(isAdmin(p))
			return true;
		return p.hasPermission("AwesomeBans.mod");
	}
	public static boolean isAdmin(CommandSender p)
	{
		if(isConsole(p))
			return true;
		if(p.isOp())
			return true;
		return p.hasPermission("AwesomeBans.admin");
	}
	private static boolean isConsole(CommandSender p)
	{
		if(p instanceof ConsoleCommandSender)
			return true;
		return false;
	}
	public static boolean isMod(CommandSender p)
	{
		if(isAdmin(p))
			return true;
		return p.hasPermission("AwesomeBans.mod");
	}
	public static boolean canWarn(CommandSender p)
	{
		if(isMod(p))
			return true;
		return p.hasPermission("AwesomeBans.warn");
	}
	public static boolean canCheck(CommandSender p)
	{
		if(isMod(p))
			return true;
		return p.hasPermission("AwesomeBans.check");
	}
	public static boolean canBan(CommandSender p)
	{
		if(isMod(p))
			return true;
		return p.hasPermission("AwesomeBans.ban");
	}
	public static boolean canKick(CommandSender p)
	{
		if(isMod(p))
			return true;
		return p.hasPermission("AwesomeBans.kick");
	}
	public static boolean canMute(CommandSender p)
	{
		if(isMod(p))
			return true;
		return p.hasPermission("AwesomeBans.mute");
	}
	public static boolean canUnban(CommandSender p)
	{
		if(isMod(p))
			return true;
		return p.hasPermission("AwesomeBans.unban");
	}
	public static boolean canUnmute(CommandSender p)
	{
		if(isMod(p))
			return true;
		return p.hasPermission("AwesomeBans.unmute");
	}
	public static boolean canImport(CommandSender p)
	{
		if(isAdmin(p))
			return true;
		return false;
	}
	
	
}
