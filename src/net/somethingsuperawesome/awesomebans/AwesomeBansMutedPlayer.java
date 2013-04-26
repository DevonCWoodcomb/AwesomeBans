package net.somethingsuperawesome.awesomebans;

import lombok.Getter;
import lombok.Setter;

public class AwesomeBansMutedPlayer 
{
	@Getter
	private String PlayerName;
	@Getter
	@Setter
	private String mutedBy;
	@Getter
	@Setter
	private long mutedTil;
	@Getter
	@Setter
	private String reason;
	
	public AwesomeBansMutedPlayer(String name, String muter, long date, String reason)
	{
		PlayerName = name;
		mutedTil = date;
		mutedBy = muter;
		this.reason = reason;
	}
	@Override
	public boolean equals(Object o)
	{
		AwesomeBansMutedPlayer p;
		if(o instanceof AwesomeBansMutedPlayer)
		{
			p = (AwesomeBansMutedPlayer)o;
		}
		else
		{
			return false;
		}
		if(this.PlayerName.equalsIgnoreCase(p.getPlayerName()))
		{
			return true;
		}
		return false;
	}
}
