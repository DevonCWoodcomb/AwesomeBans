package net.somethingsuperawesome.awesomebans;

import net.somethingsuperawesome.awesomebans.commands.AwesomeBansCommandAddlives;
import net.somethingsuperawesome.awesomebans.commands.AwesomeBansCommandAwesomeBans;
import net.somethingsuperawesome.awesomebans.commands.AwesomeBansCommandBan;
import net.somethingsuperawesome.awesomebans.commands.AwesomeBansCommandCheck;
import net.somethingsuperawesome.awesomebans.commands.AwesomeBansCommandCheckDetail;
import net.somethingsuperawesome.awesomebans.commands.AwesomeBansCommandImportFC;
import net.somethingsuperawesome.awesomebans.commands.AwesomeBansCommandKick;
import net.somethingsuperawesome.awesomebans.commands.AwesomeBansCommandMute;
import net.somethingsuperawesome.awesomebans.commands.AwesomeBansCommandUnban;
import net.somethingsuperawesome.awesomebans.commands.AwesomeBansCommandUnmute;
import net.somethingsuperawesome.awesomebans.commands.AwesomeBansCommandWarn;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AwesomeBansCommands implements CommandExecutor
{
	private AwesomeBans plugin;
	int warningLevel = 0;
	
	public AwesomeBansCommands(AwesomeBans plug)
	{
		plugin = plug;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
		if(cmd.getName().equalsIgnoreCase("check"))
    	{
    		return AwesomeBansCommandCheck.check(sender, args, plugin);
    	}	
		else if(cmd.getName().equalsIgnoreCase("checkdetail"))
    	{
    		return AwesomeBansCommandCheckDetail.checkDetail(sender, args, plugin);
    	}	
		else if(cmd.getName().equalsIgnoreCase("warn"))
    	{
    		return AwesomeBansCommandWarn.warn(sender, args, plugin);
    	}
		else if(cmd.getName().equalsIgnoreCase("mute"))
    	{
    		return AwesomeBansCommandMute.mute(sender, args, plugin);
    	}
		else if(cmd.getName().equalsIgnoreCase("kick"))
    	{
    		return AwesomeBansCommandKick.kick(sender, args, plugin);
    	}
		else if(cmd.getName().equalsIgnoreCase("ban"))
    	{
    		return AwesomeBansCommandBan.ban(sender, args, plugin);
    	}
		else if(cmd.getName().equalsIgnoreCase("unban"))
    	{
    		return AwesomeBansCommandUnban.unban(sender, args, plugin);
    	}
		else if(cmd.getName().equalsIgnoreCase("unmute"))
    	{
    		return AwesomeBansCommandUnmute.unmute(sender, args, plugin);
    	}
		else if(cmd.getName().equalsIgnoreCase("addlives"))
    	{
    		return AwesomeBansCommandAddlives.addlives(sender, args, plugin);
    	}
		else if(cmd.getName().equalsIgnoreCase("importfc"))
    	{
    		return AwesomeBansCommandImportFC.importFC(sender, args, plugin);
    	}
		else if(cmd.getName().equalsIgnoreCase("awesomebans"))
    	{
    		return AwesomeBansCommandAwesomeBans.awesomeBans(sender, args, plugin);
    	}
    	return false; 
    }
}
