package net.somethingsuperawesome.awesomebans;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import net.somethingsuperawesome.awesomebans.commands.AwesomeBansCommandBan;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;


public class AwesomeBansPlayerListener implements Listener
{
	private AwesomeBans plugin;
	private PreparedStatement addPlayerIp = null;
	private PreparedStatement selectPlayer = null;
	private PreparedStatement selectPlayerBan = null;
	private PreparedStatement selectPlayerLifeBan = null;
	private PreparedStatement setPlayerBanned = null;
	private PreparedStatement deletePlayerBan = null;
	private PreparedStatement setPlayerLifeBanned = null;
	private PreparedStatement deletePlayerLifeBan = null;
	private PreparedStatement addPlayer = null;
	private PreparedStatement setPlayerMuted = null;
	private PreparedStatement deletePlayerMute = null;
	private PreparedStatement selectBannedIps = null;

	private String addPlayerIpQuery;
	private String getPlayerQuery;
	private String getPlayerBanQuery;
	private String getPlayerLifeBanQuery;
	private String setPlayerBannedQuery;
	private String deletePlayerBanQuery;
	private String setPlayerLifeBannedQuery;
	private String deletePlayerLifeBanQuery;
	private String addPlayerQuery;
	private String setPlayerMutedQuery;
	private String deletePlayerMuteQuery;
	private String selectBannedIpsQuery;
	
	ResultSet result = null;
	
	private long banDate=0, date=0;
		
	public AwesomeBansPlayerListener(AwesomeBans plugina)
	{
		this.plugin = plugina;
		addPlayerIpQuery = "INSERT IGNORE INTO "+AwesomeBansSettings.getIps()+" VALUES (?, ?)";
		getPlayerQuery = "SELECT * FROM "+AwesomeBansSettings.getPlayers()+" WHERE PlayerName = ?";
		getPlayerBanQuery = "SELECT * FROM "+AwesomeBansSettings.getBans()+" WHERE PlayerName = ?";
		getPlayerLifeBanQuery = "SELECT * FROM "+AwesomeBansSettings.getLifeBans()+" WHERE PlayerName = ?";
		setPlayerBannedQuery = "UPDATE "+ AwesomeBansSettings.getPlayers()+" SET isBanned = 0 WHERE PlayerName = ?";
		deletePlayerBanQuery = "DELETE FROM "+AwesomeBansSettings.getBans()+" WHERE PlayerName = ?";
		setPlayerLifeBannedQuery = "UPDATE "+ AwesomeBansSettings.getPlayers()+" SET isLifeBanned = 0 WHERE PlayerName = ?";
		deletePlayerLifeBanQuery = "DELETE FROM "+AwesomeBansSettings.getLifeBans()+" WHERE PlayerName = ?";
		addPlayerQuery = "INSERT INTO "+AwesomeBansSettings.getPlayers()+" VALUES (?, 0, 0, 0, "+AwesomeBansSettings.getDefaultLives()+", 0, 0)";		
		setPlayerMutedQuery = "UPDATE "+ AwesomeBansSettings.getPlayers()+" SET isMuted = 0 WHERE PlayerName = ?";
		deletePlayerMuteQuery = "DELETE FROM "+AwesomeBansSettings.getMutes()+" WHERE PlayerName = ?";
		selectBannedIpsQuery = "SELECT Ip FROM "+AwesomeBansSettings.getIps()+", "+AwesomeBansSettings.getBans()+
				" WHERE "+AwesomeBansSettings.getIps()+".PlayerName =  "+AwesomeBansSettings.getBans()+".PlayerName"; 
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void playerChat(AsyncPlayerChatEvent event)
	{
		for(AwesomeBansMutedPlayer p : plugin.getMutedPlayers())
		{
			if(p.getPlayerName().equalsIgnoreCase(event.getPlayer().getName()))
			{
				if(p.getMutedTil() > System.currentTimeMillis())
				{
					event.setCancelled(true);
					AwesomeBansMessages.muted(event.getPlayer(), p.getMutedBy(), p.getReason(), p.getMutedTil());
					return;
				}
				else
				{
					plugin.getMutedPlayers().remove(p);
					if(plugin.checkMySQL())
					{
						try
						{
							setPlayerMuted = plugin.getMysql().prepare(setPlayerMutedQuery);
							deletePlayerMute = plugin.getMysql().prepare(deletePlayerMuteQuery);
							setPlayerMuted.setString(1, event.getPlayer().getName());
							AwesomeBansMessages.debug(setPlayerMuted.toString());
							plugin.getMysql().query(setPlayerMuted);
							setPlayerMuted.close();
							deletePlayerMute.setString(1, event.getPlayer().getName());
							AwesomeBansMessages.debug(deletePlayerMute.toString());
							plugin.getMysql().query(deletePlayerMute);
							deletePlayerMute.close();
						}
						catch(SQLException e)
						{
							e.printStackTrace();
						}
					}
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void muteSigns(BlockPlaceEvent event)
	{
		if(event.getPlayer() == null)
			return;
		
		if(event.getBlock().getType() == Material.SIGN || event.getBlock().getType() == Material.WALL_SIGN || event.getBlock().getType() == Material.SIGN_POST)	
		{
			for(AwesomeBansMutedPlayer p : plugin.getMutedPlayers())
			{
				if(p.getPlayerName().equalsIgnoreCase(event.getPlayer().getName()))
				{
					if(p.getMutedTil() > System.currentTimeMillis())
					{
						event.setCancelled(true);
						AwesomeBansMessages.muted(event.getPlayer(), p.getMutedBy(), p.getReason(), p.getMutedTil());
						return;
					}
					else
					{
						plugin.getMutedPlayers().remove(p);
						if(plugin.checkMySQL())
						{
							try
							{
								setPlayerMuted = plugin.getMysql().prepare(setPlayerMutedQuery);
								deletePlayerMute = plugin.getMysql().prepare(deletePlayerMuteQuery);
								setPlayerMuted.setString(1, event.getPlayer().getName());
								AwesomeBansMessages.debug(setPlayerMuted.toString());
								plugin.getMysql().query(setPlayerMuted);
								setPlayerMuted.close();
								deletePlayerMute.setString(1, event.getPlayer().getName());
								AwesomeBansMessages.debug(deletePlayerMute.toString());
								plugin.getMysql().query(deletePlayerMute);
								deletePlayerMute.close();
							}
							catch(SQLException e)
							{
								e.printStackTrace();
							}
						}
						return;
					}
				}
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event)
	{		
		String ip = event.getAddress().getHostAddress();
		String player = event.getName();
		String issuer = "";
		String reason = "";
		boolean isBanned = false;
		boolean isMuted = false;
		boolean isLifeBanned = false;
		int lives = 0;
		int warningLevel = 0;		
		AwesomeBansMessages.debug(AwesomeBansSettings.getChatPrefix() +"player login listener triggered for "+ player);
		if(plugin.checkMySQL())
		{
			try 
			{
				addPlayerIp = plugin.getMysql().prepare(addPlayerIpQuery);
				addPlayerIp.setString(1, player);
				addPlayerIp.setString(2, ip);
				AwesomeBansMessages.debug(addPlayerIp.toString());
				plugin.getMysql().query(addPlayerIp);
				addPlayerIp.close();
				selectPlayer = plugin.getMysql().prepare(getPlayerQuery);
				selectPlayer.setString(1, player);
				AwesomeBansMessages.debug(selectPlayer.toString());
				result = plugin.getMysql().query(selectPlayer);	
				if(result.next())
				{
					AwesomeBansMessages.debug("Player Found");
					date = System.currentTimeMillis();
					isBanned = result.getBoolean("isBanned");
					isMuted = result.getBoolean("isMuted");
					isLifeBanned = result.getBoolean("isLifeBanned");
					warningLevel = result.getInt("WarningLevel");
					lives = result.getInt("Lives");
					result.close();
					selectPlayer.close();
					AwesomeBansMessages.debug("printing banned, muted, and warning level "+isBanned+" "+isMuted+" "+warningLevel);
					if(isBanned)
					{
						selectPlayerBan = plugin.getMysql().prepare(getPlayerBanQuery);
						selectPlayerBan.setString(1, player);
						result = null;
						result = plugin.getMysql().query(selectPlayerBan);
						if(result.next())
						{
							banDate = result.getLong("EndTime");
							issuer = result.getString("Issuer");
							reason = result.getString("Reason");
						}
						
						result.close();
						selectPlayerBan.close();
						if(banDate != 0)
						{
							AwesomeBansMessages.debug("Right now: "+date+" Banned until: "+banDate);
							if(date < banDate)
							{
								DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
								event.setKickMessage("Banned until: "+dateFormat.format(banDate)+ " By: "+ issuer +" For: "+reason);
								AwesomeBansMessages.debug(player +" Banned until: "+dateFormat.format(banDate)+ " By: "+ issuer +" For: "+reason);
					    		event.setLoginResult(Result.KICK_BANNED);
					    		endJoin(player, warningLevel, isMuted, isBanned);
					    		return;
							}
							else
							{
								setPlayerBanned = plugin.getMysql().prepare(setPlayerBannedQuery);
								setPlayerBanned.setString(1, player);
								plugin.getMysql().query(setPlayerBanned);
								setPlayerBanned.close();
								
								deletePlayerBan = plugin.getMysql().prepare(deletePlayerBanQuery);
								deletePlayerBan.setString(1, player);
								plugin.getMysql().query(deletePlayerBan);
								deletePlayerBan.close();
								
								isBanned=false;
								endJoin(player, warningLevel, isMuted, isBanned);
								return;
							}
						}
						else
						{
							endJoin(player, warningLevel, isMuted, isBanned);
							return;
						}
					}
					else if(isLifeBanned)
					{
						selectPlayerLifeBan = plugin.getMysql().prepare(getPlayerLifeBanQuery);
						selectPlayerLifeBan.setString(1, player);						
						result = null;
						result = plugin.getMysql().query(selectPlayerLifeBan);
						if(result.next())
						{
							banDate = result.getLong("EndTime");
						}
						result.close();
						selectPlayerLifeBan.close();
							
						if(banDate!=0)
						{
							AwesomeBansMessages.debug("right nao: "+date+" banned til: "+banDate);
							if(lives > 0)
							{
								setPlayerLifeBanned = plugin.getMysql().prepare(setPlayerLifeBannedQuery);
								setPlayerLifeBanned.setString(1, player);
								plugin.getMysql().query(setPlayerLifeBanned);
								setPlayerLifeBanned.close();
								
								deletePlayerLifeBan = plugin.getMysql().prepare(deletePlayerLifeBanQuery);
								deletePlayerLifeBan.setString(1, player);
								plugin.getMysql().query(deletePlayerLifeBan);
								deletePlayerLifeBan.close();
								
								isBanned=false;
								AwesomeBansMessages.info(player +" logged in with Warning Level: "+warningLevel);
								endJoin(player, warningLevel, isMuted, isBanned);
								return;
							}
							else if(date < banDate)
							{
								DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
								event.setKickMessage("You are out of lives! Wait unti "+dateFormat.format(banDate)+ " or visit: "+AwesomeBansSettings.getBuyURL());
								AwesomeBansMessages.debug( player +" is out of lives, denied!");
					    		event.setLoginResult(Result.KICK_BANNED);
					    		return;
							}
							else
							{
								setPlayerLifeBanned.setString(1, player);
								plugin.getMysql().query(setPlayerLifeBanned);
								setPlayerLifeBanned.close();
								deletePlayerLifeBan.setString(1, player);
								plugin.getMysql().query(deletePlayerLifeBan);
								deletePlayerLifeBan.close();
								isBanned=false;
								endJoin(player, warningLevel, isMuted, isBanned);
								return;
							}
						}
					}
					else
					{
						selectPlayer.close();
						endJoin(player, warningLevel, isMuted, isBanned);
						return;
					}	
				}
				else
				{
					result.close();
					selectPlayer.close();
					
					addPlayer = plugin.getMysql().prepare(addPlayerQuery);
					addPlayer.setString(1, player);
					plugin.getMysql().query(addPlayer);
					addPlayer.close();
					AwesomeBansMessages.info("Creating new player record for: "+ player);
					
					selectBannedIps = plugin.getMysql().prepare(selectBannedIpsQuery);
					result = plugin.getMysql().query(selectBannedIpsQuery);
					String testIp;
					while(result.next())
					{
						testIp = result.getString("Ip");
						AwesomeBansMessages.debug("Ip from query:"+ testIp+" player ip: "+ip);
						if(ip.equalsIgnoreCase(testIp))
						{
							AwesomeBansMessages.info("banned ip detected on new player. DESTROY!");
							isBanned = true;
							String[] args2 = {player, "perm", "Automatic Ban Evasion Detection"};
							AwesomeBansCommandBan.ban(Bukkit.getConsoleSender(), args2, plugin);
							event.setKickMessage("Automatic Ban Evasion Detection");
							event.setLoginResult(Result.KICK_BANNED);
							break;
						}
					}
					result.close();
					selectBannedIps.close();
					
					endJoin(player, warningLevel, isMuted, isBanned);
					return;
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
			event.setKickMessage("MySQL Error, contact the server staff to report this error");
			event.setLoginResult(Result.KICK_OTHER);
			return;
		}
	}
	public void endJoin(String player, int warningLevel, boolean isMuted, boolean isBanned) throws SQLException
	{
		if (isBanned)
			showJoinBanned(player);
		//else
			//showJoin(player, warningLevel, isMuted);
		return;
	}
	/*public void showJoin(String player, int warningLevel, boolean isMuted)
	{
		AwesomeBansMessages.info(player +" logged in with Warning Level: "+warningLevel +" Muted: "+isMuted);
		for(Player p : Bukkit.getServer().getOnlinePlayers())
		{
			AwesomeBansMessages.debug("Checking permissions for "+p.getName());
			if(AwesomeBansPermissions.loginNotify(p))
				AwesomeBansMessages.loginNotify(p, player, warningLevel, isMuted);	
		}
	}*/
	public void showJoinBanned(String player)
	{
		AwesomeBansMessages.info(player +" tried to login, but is banned");
		for(Player p : Bukkit.getServer().getOnlinePlayers())
		{
			AwesomeBansMessages.debug("Checking permissions for "+p.getName());
			if(AwesomeBansPermissions.loginNotify(p))
				AwesomeBansMessages.loginNotifyBanned(p, player);
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void overrideJoinMessages(PlayerJoinEvent event)
	{
		int warninglvl=0;
		boolean muted=false;
		if(plugin.checkMySQL())
		{
			try
			{				
				selectPlayer = plugin.getMysql().prepare(getPlayerQuery);
				selectPlayer.setString(1, event.getPlayer().getName());
				ResultSet result = plugin.getMysql().query(selectPlayer);
				if(result.next())
				{
					muted = result.getBoolean("isMuted");
					warninglvl = result.getInt("WarningLevel");
				}
				result.close();
				selectPlayer.close();
			} 
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		AwesomeBansMessages.info(event.getPlayer().getDisplayName() +" logged in with Warning Level: "+warninglvl +" Muted: "+muted);
		if(AwesomeBansSettings.isOverrideJoinMessages())
			event.setJoinMessage(null);
		for(Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if(!p.getName().equalsIgnoreCase(event.getPlayer().getName()))
			{
				if(AwesomeBansPermissions.loginNotify(p))
					AwesomeBansMessages.loginNotify(p, event.getPlayer(), warninglvl, muted);
				else if(AwesomeBansSettings.isOverrideJoinMessages())
					AwesomeBansMessages.loginAnnounce(p, event.getPlayer());
			}
		}		
	}
	
	
}