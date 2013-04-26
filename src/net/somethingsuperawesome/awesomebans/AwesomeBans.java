package net.somethingsuperawesome.awesomebans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.MySQL;
import lombok.Getter;

import org.bukkit.plugin.java.JavaPlugin;


public final class AwesomeBans extends JavaPlugin
{
	private Logger log = Logger.getLogger("Minecraft");

	@Getter
	private MySQL mysql;
	public ResultSet result = null;
	
	private String query;
	@Getter
	private ArrayList<AwesomeBansMutedPlayer> mutedPlayers;
	
	@Override
	public void onEnable() 
	{
		AwesomeBansMessages.setLog(log);
		AwesomeBansSettings.init(this);
		AwesomeBansSettings.loadSettings();

		if (AwesomeBansSettings.getDbHost().equals(null)) 
		{
			this.log.severe(AwesomeBansSettings.getChatPrefix() + "mySQL is on, but host is not defined, dying");
		}
		if (AwesomeBansSettings.getDbUser().equals(null)) 
		{
			this.log.severe(AwesomeBansSettings.getChatPrefix() + "mySQL is on, but username is not defined, dying");
		}
		if (AwesomeBansSettings.getDbPass().equals(null)) 
		{
			this.log.severe(AwesomeBansSettings.getChatPrefix() + "mySQL is on, but password is not defined, dying");
		}
		if (AwesomeBansSettings.getDbDatabase().equals(null)) 
		{
			this.log.severe(AwesomeBansSettings.getChatPrefix() + "mySQL is on, but database is not defined, dying");
		}
		
		// Declare mySQL Handler
		this.mysql = new MySQL(this.log, AwesomeBansSettings.getChatPrefix(), AwesomeBansSettings.getDbHost(), AwesomeBansSettings.getDbPort(), AwesomeBansSettings.getDbDatabase(), AwesomeBansSettings.getDbUser(), AwesomeBansSettings.getDbPass());
		AwesomeBansMessages.debug("mySQL Initializing");
		// Initialize mySQL Handler
		this.mysql.open();
		// Check if the Connection was successful
		if (checkMySQL()) 
		{ 
			AwesomeBansMessages.debug("mySQL connection successful");
			// Check if the tables exists in the database if not create them	
			if (!this.mysql.isTable(AwesomeBansSettings.getPlayers())) 
			{ 
				AwesomeBansMessages.info("Creating table "+AwesomeBansSettings.getPlayers());
				query = "CREATE TABLE "+AwesomeBansSettings.getPlayers()+"(" +
						"PlayerName VARCHAR(25) NOT NULL, " +
						"isBanned TINYINT(1) NOT NULL, " +
						"isLifeBanned TINYINT(1) NOT NULL, " +
						"isMuted TINYINT(1) NOT NULL, " +
						"Lives INT NOT NULL, " +
						"LastLives LONG NOT NULL, " +
						"WarningLevel INT NOT NULL, " +
						"PRIMARY KEY (PlayerName));";
				try 
				{
					this.mysql.query(query);
				} 
				catch (SQLException e) 
				{
					e.printStackTrace();
				}
			}
			if (!this.mysql.isTable(AwesomeBansSettings.getPunRec())) 
			{ 
				AwesomeBansMessages.info("Creating table "+AwesomeBansSettings.getPunRec());
				query = "CREATE TABLE "+AwesomeBansSettings.getPunRec()+" (" +
						"PunishmentRecordId INT NOT NULL AUTO_INCREMENT, " +
						"PunishedPlayer VARCHAR(25) NOT NULL, " +
						"Issuer VARCHAR(25) NOT NULL, " +
						"Type VARCHAR(15) NOT NULL, " +
						"Reason VARCHAR(255) NOT NULL, " +
						"ServerName VARCHAR(45) NOT NULL, " +
						"TimeIssued LONG NOT NULL, " +
						"EndTime LONG NOT NULL, " +
						"WarnLevel INT NOT NULL, " +
						"PRIMARY KEY (PunishmentRecordId));";
				try 
				{
					this.mysql.query(query);
				} 
				catch (SQLException e) 
				{
					e.printStackTrace();
				}
			}
			if (!this.mysql.isTable(AwesomeBansSettings.getBans()))
			{ 
				AwesomeBansMessages.info("Creating table "+AwesomeBansSettings.getBans());
				query = "CREATE TABLE "+AwesomeBansSettings.getBans()+"(" +
						"PlayerName VARCHAR(25) NOT NULL, " +
						"EndTime LONG NOT NULL, " +
						"Issuer VARCHAR(25) NOT NULL, " +
						"Reason VARCHAR(255) NOT NULL, " +
						"PunId INT NOT NULL, " +
						"PRIMARY KEY (PlayerName));";
				try 
				{
					this.mysql.query(query);
				} 
				catch (SQLException e) 
				{
					e.printStackTrace();
				}
			}
			if (!this.mysql.isTable(AwesomeBansSettings.getLifeBans())) 
			{ 
				AwesomeBansMessages.info("Creating table "+AwesomeBansSettings.getLifeBans());
				query = "CREATE TABLE "+AwesomeBansSettings.getLifeBans()+"(" +
						"PlayerName VARCHAR(25) NOT NULL, " +
						"EndTime LONG NOT NULL, " +
						"PRIMARY KEY (PlayerName));";
				try 
				{
					this.mysql.query(query);
				} 
				catch (SQLException e) 
				{
					e.printStackTrace();
				}
			}
			if (!this.mysql.isTable(AwesomeBansSettings.getMutes())) 
			{ 
				AwesomeBansMessages.info("Creating table "+AwesomeBansSettings.getMutes());
				query = "CREATE TABLE "+AwesomeBansSettings.getMutes()+"(" +
						"PlayerName VARCHAR(25) NOT NULL, " +
						"EndTime LONG NOT NULL, " +
						"Issuer VARCHAR(25) NOT NULL, " +
						"Reason VARCHAR(255) NOT NULL, " +
						"PunId INT NOT NULL, " +
						"PRIMARY KEY (PlayerName));";
				try 
				{
					this.mysql.query(query);
				} 
				catch (SQLException e) 
				{
					e.printStackTrace();
				}
			}
			if (!this.mysql.isTable(AwesomeBansSettings.getIps())) 
			{ 
				AwesomeBansMessages.info("Creating table "+AwesomeBansSettings.getIps());
				query = "CREATE TABLE "+AwesomeBansSettings.getIps()+"(" +
						"PlayerName VARCHAR(25) NOT NULL, " +
						"Ip VARCHAR(45) NOT NULL, " +
						"PRIMARY KEY (PlayerName, Ip));";
				try 
				{
					this.mysql.query(query);
				} 
				catch (SQLException e) 
				{
					e.printStackTrace();
				}
			}
		} 
		else 
		{
			this.log.severe(AwesomeBansSettings.getChatPrefix() + "mySQL connection failed");
		}
		
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() 
		{
		    @Override  
		    public void run() 
		    {
		    	AwesomeBansMessages.debug(AwesomeBansSettings.getChatPrefix() + "import mutes every 30 seconds");
		    	importMutes();
		    }
		}, 10L, 600L);
		getServer().getPluginManager().registerEvents(new AwesomeBansPlayerListener(this), this);
		getCommand("warn").setExecutor(new AwesomeBansCommands(this));
		getCommand("check").setExecutor(new AwesomeBansCommands(this));
		getCommand("checkdetail").setExecutor(new AwesomeBansCommands(this));
		getCommand("ban").setExecutor(new AwesomeBansCommands(this));
		getCommand("kick").setExecutor(new AwesomeBansCommands(this));
		getCommand("mute").setExecutor(new AwesomeBansCommands(this));
		getCommand("unban").setExecutor(new AwesomeBansCommands(this));
		getCommand("unmute").setExecutor(new AwesomeBansCommands(this));
		getCommand("addlives").setExecutor(new AwesomeBansCommands(this));
		getCommand("importfc").setExecutor(new AwesomeBansCommands(this));
		getCommand("awesomebans").setExecutor(new AwesomeBansCommands(this));
	}
		
	
	public void importMutes()
	{
    	if(checkMySQL())
    	{
    		if(mutedPlayers == null)
    			mutedPlayers = new ArrayList<AwesomeBansMutedPlayer>();
    		try 
	    	{
    			query = "SELECT * FROM "+ AwesomeBansSettings.getMutes() + ";";
    			result = mysql.query(query);
	    		while(result.next())
	    		{
	    			String tName = result.getString("PlayerName").toLowerCase();
	    			long tDate = result.getLong("EndTime");
	    			String tIssuer = result.getString("Issuer".toLowerCase());
					String tReason = result.getString("Reason");	    			
	    			addMuted(tName, tIssuer, tDate, tReason);	    			
	    		}	
			} 
	    	catch (SQLException e) 
			{
				e.printStackTrace();
			}
    	}
    	for(int k=0;k<mutedPlayers.size();k++)
    	{
    		AwesomeBansMessages.debug("Muted player: "+mutedPlayers.get(k).getPlayerName());
    	}
	}
	public void addMuted(String name, String muter, long date, String reason)
	{
		AwesomeBansMutedPlayer steve = new AwesomeBansMutedPlayer(name.toLowerCase(), muter.toLowerCase(), date, reason);
		if(mutedPlayers.indexOf(steve)>=0)
		{
			mutedPlayers.get(mutedPlayers.indexOf(steve)).setMutedTil(date);
			mutedPlayers.get(mutedPlayers.indexOf(steve)).setMutedBy(muter);
			mutedPlayers.get(mutedPlayers.indexOf(steve)).setReason(reason);
		}
		else
		{
			mutedPlayers.add(steve);
		}	
	}
	public void removeMuted(String name)
	{
		for(AwesomeBansMutedPlayer p : mutedPlayers)
		{
			if(p.getPlayerName().equalsIgnoreCase(name))
			{
				mutedPlayers.remove(p);
				return;
			}
		}	
	}
	public boolean checkMySQL()
	{
		AwesomeBansMessages.debug("checking mySQL");
		boolean open = mysql.isOpen();
		if(!open)
		{
			AwesomeBansMessages.debug("mySQL failed, attempting to reopen");
			mysql.open();
			open = mysql.isOpen();
			if(open)
			{
				AwesomeBansMessages.debug("mySQL reopen");
			}
			else
			{
				AwesomeBansMessages.debug("mySQL reopen failed");
			}
		}		
		return open;
	}
}


	