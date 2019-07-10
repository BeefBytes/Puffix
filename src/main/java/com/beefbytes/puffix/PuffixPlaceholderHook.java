package com.beefbytes.puffix;

import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.entity.Player;

public class PuffixPlaceholderHook extends PlaceholderHook {

	private Puffix plugin;

	public PuffixPlaceholderHook(Puffix plugin){
		this.plugin = plugin;
	}

	@Override
	public String onPlaceholderRequest(Player p, String identifier){
		if(p == null){
			return "";
		}

		switch(identifier){
			case "prefix":
				return plugin.getPrefix(p);
			case "suffix":
				return plugin.getSuffix(p);
		}

		return null;
	}

}
