package net.somethingsuperawesome.awesomebans;

public class AwesomeBansDurationParser
{
	public static long getDuration(String textDur)
	{
		long duration = 0;
		int currentPos = 0;
		String small = "";
		char[] chatDur = textDur.toCharArray();
		try
		{
			for(int k=0; k<chatDur.length; k++)
			{
				if(chatDur[k] == 's')
				{
					for(int j = currentPos; j<k; j++)
						small += chatDur[j];
					currentPos = k+1;
					duration += (Integer.valueOf(small)*1000);
					small = "";
				}
				else if(chatDur[k] == 'm')
				{
					for(int j = currentPos; j<k; j++)
						small += chatDur[j];
					currentPos = k+1;
					duration += (Integer.valueOf(small)*1000*60);
					small = "";
				}
				else if(chatDur[k] == 'h')
				{
					for(int j = currentPos; j<k; j++)
						small += chatDur[j];
					currentPos = k+1;
					duration += (Integer.valueOf(small)*1000*60*60);
					small = "";
				}
				else if(chatDur[k] == 'd')
				{
					for(int j = currentPos; j<k; j++)
						small += chatDur[j];
					currentPos = k+1;
					duration += (Integer.valueOf(small)*1000*60*60*24);
					small = "";
				}
				else if(chatDur[k] == 'w')
				{
					for(int j = currentPos; j<k; j++)
						small += chatDur[j];
					currentPos = k+1;
					duration += (Integer.valueOf(small)*1000*60*60*24*7);
					small = "";
				}
			}
		}
		catch(Exception e)
		{
			duration = 0;
		}		
		return duration;
	}
}
