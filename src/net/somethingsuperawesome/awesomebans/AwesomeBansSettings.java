package net.somethingsuperawesome.awesomebans;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class AwesomeBansSettings
{
	private static HashMap<String, String> paths = new HashMap<String, String>();
	
	private static AwesomeBans plugin;
	private static FileConfiguration config = null;
	
	@Getter
	private static String dbHost = null;
	@Getter
	private static int dbPort = 0;
	@Getter
	private static String dbUser = null;
	@Getter
	private static String dbPass = null;
	@Getter
	private static String dbDatabase = null;
	@Getter
	private static String tablePrefix = null;
	
	@Getter
	private static String serverName = null;
	
	@Getter
	private static boolean useLives = false;
	@Getter
	private static int resetLives = 0;
	@Getter
	private static String lifeBanDuration = null;
	@Getter
	private static int defaultLives = 0;
	@Getter
	private static String buyURL = null;
	
	@Getter
	private static boolean broadcast;
	@Getter
	private static boolean overrideJoinMessages;
	
	@Setter
	@Getter
	private static boolean debugOn = false;
	
	//Formatting settings
	@Getter
	private static String chatPrefix = null;
	
	//tables
	@Getter
	private static String players = "Player";
	@Getter
	private static String punRec = "PunishmenRecord";
	@Getter
	private static String bans = "Ban";
	@Getter
	private static String mutes = "Mute";
	@Getter
	private static String ips = "PlayerIP";
	@Getter
	private static String lifeBans = "LifeBan";
	
	public AwesomeBansSettings(){}
	
	public static void setOption(String setting, Object value)
	{
		//config.set(path, value);
	}
	
	public static void init(AwesomeBans plug)
	{
		plugin = plug;
		plugin.saveDefaultConfig();
		config = plugin.getConfig();
		paths.put("dbHost", "MySQL.Host");
		paths.put("dbPort", "MySQL.Port");
		paths.put("dbUser", "MySQL.Username");
		paths.put("dbPass", "MySQL.Password");
		paths.put("dbDatabase", "MySQL.Database");
		paths.put("tablePrefix", "MySQL.TablePrefix");
		paths.put("serverName", "ServerName");
		paths.put("useLives", "Lives.UseLives");
		paths.put("resetLives", "Lives.ResetLives");
		paths.put("defaultLives", "Lives.DefaultLives");
		paths.put("buyURL", "Lives.BuyURL");
		paths.put("lifeBanDuration", "Lives.BanDuration");
		paths.put("broadcast", "Broadcast");
		paths.put("overrideJoinMessages", "OverrideJoinMessages");
		paths.put("chatPrefix", "Formatting.Prefix");
	}
	
	public static void loadSettings()
	{
		dbHost = config.getString(paths.get("dbHost"));
		dbPort = config.getInt(paths.get("dbPort"));
		dbUser = config.getString(paths.get("dbUser"));
		dbPass = config.getString(paths.get("dbPass"));
		dbDatabase = config.getString(paths.get("dbDatabase"));
		tablePrefix = config.getString(paths.get("tablePrefix"));
		serverName = config.getString(paths.get("serverName"));
		useLives = config.getBoolean(paths.get("useLives"));
		resetLives = config.getInt(paths.get("resetLives"));
		defaultLives = config.getInt(paths.get("defaultLives"));
		buyURL = config.getString(paths.get("buyURL"));
		lifeBanDuration = config.getString(paths.get("lifeBanDuration"));
		broadcast = config.getBoolean(paths.get("broadcast"));
		overrideJoinMessages = config.getBoolean(paths.get("overrideJoinMessages"));
		chatPrefix = colorize(config.getString(paths.get("chatPrefix")))+" "+ChatColor.RESET;
		players = tablePrefix + players;
		punRec = tablePrefix + punRec;
		bans = tablePrefix + bans;
		mutes = tablePrefix + mutes;
		ips = tablePrefix + ips;
		lifeBans = tablePrefix + lifeBans;
	}
	
	public static void reloadSettings()
	{
		serverName = config.getString(paths.get("serverName"));
		useLives = config.getBoolean(paths.get("useLives"));
		resetLives = config.getInt(paths.get("dailyLives"));
		defaultLives = config.getInt(paths.get("defaultLives"));
		buyURL = config.getString(paths.get("buyURL"));
		lifeBanDuration = config.getString(paths.get("lifeBanDuration"));
		broadcast = config.getBoolean(paths.get("broadcast"));
		overrideJoinMessages = config.getBoolean(paths.get("overrideJoinMessages"));
		chatPrefix = config.getString(paths.get("chatPrefix"));
	}
	
	public static void saveConfig()
	{
		plugin.saveConfig();	
	}
	
	public static String colorize(String s)
	{
	    if(s == null) 
	    	return null;
	    return s.replaceAll("&([0-9a-f])", "\u00A7$1");
	}
	
}
