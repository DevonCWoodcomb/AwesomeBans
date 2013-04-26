package net.somethingsuperawesome.awesomebans.commands;

import java.io.File;

import net.somethingsuperawesome.awesomebans.AwesomeBans;
import net.somethingsuperawesome.awesomebans.AwesomeBansMessages;
import net.somethingsuperawesome.awesomebans.AwesomeBansPermissions;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class AwesomeBansCommandImportFC
{
	static FileConfiguration fc;
	public static boolean importFC(CommandSender sender, String[] args,	AwesomeBans plugin)
	{
		if(AwesomeBansPermissions.canImport(sender))
		{
			
			File fcfolder = new File(plugin.getDataFolder().getParentFile(), "FC_Bans");
			fcfolder = new File(fcfolder, "userinfo");
			for(File f: fcfolder.listFiles())
			{
				fc = YamlConfiguration.loadConfiguration(f);
				boolean banned = fc.getBoolean("FC_Bans.isPermaBanned");
				if(banned)
				{
					String name = f.getName().replace(".yml", "");
					AwesomeBansMessages.info(name);
					String[] args2 = {name, "perm", "Import from FC_Bans"};
					AwesomeBansCommandBan.ban(Bukkit.getConsoleSender(), args2, plugin);
				}
			}
			return true;
		}
		else
		{
			AwesomeBansMessages.noPermission(sender);
			return true;
		}
	}

}
