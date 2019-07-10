package com.beefbytes.puffix;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.misc.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

public class Puffix extends JavaPlugin {

    private static List<PrefixSuffix> prefixes = new ArrayList<>();
    private static List<PrefixSuffix> suffixes = new ArrayList<>();

    @Override
    public void onEnable(){
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
	        PlaceholderAPI.registerPlaceholderHook("puffix", new PuffixPlaceholderHook(this));
            getLogger().log(Level.INFO, "PlaceholderAPI Hooked");
        } else {
            throw new RuntimeException("PlaceholderAPI not found!");
        }

        getCommand("puffix").setExecutor((sender, command, label, args) -> {
            if(args.length == 0){
                sender.sendMessage("Correct usage: /puffix reload");
                return true;
            }

            switch(args[0]){
                case "reload":
                    loadConfig();
                    sender.sendMessage("Config Reloaded");
                    getLogger().log(Level.INFO, "Config Reloaded");
                    break;
                default:
                    sender.sendMessage("Correct usage: /puffix reload");
                    break;
            }

            return true;
        });

        loadConfig();
        getLogger().log(Level.INFO, "Config Loaded");
    }

    public void loadConfig(){
        if(!this.getDataFolder().exists()){
            this.getDataFolder().mkdirs();
        }

        File configFile = new File(this.getDataFolder(), "config.json");
        if(!configFile.exists()){
            try{
                byte[] data = IOUtils.readFully(this.getResource("config.json"), -1, false);
                Files.write(configFile.toPath(), data);
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        }

        String configJson;
        try{
            byte[] configData = Files.readAllBytes(configFile.toPath());
            configJson = new String(configData, StandardCharsets.UTF_8);
        }catch(IOException e){
            throw new RuntimeException(e);
        }

        prefixes.clear();
        suffixes.clear();

        JSONObject config = new JSONObject(configJson);

        JSONArray prefix = config.getJSONArray("prefix");
        prefix.forEach((p) -> prefixes.add(new PrefixSuffix((JSONObject) p)));

        JSONArray suffix = config.getJSONArray("suffix");
        suffix.forEach((p) -> suffixes.add(new PrefixSuffix((JSONObject) p)));

        prefixes.sort(Comparator.comparingInt(p -> -p.priority));
        suffixes.sort(Comparator.comparingInt(s -> -s.priority));
    }

    public String getPrefix(Player p){
        for(PrefixSuffix prefix : prefixes){
            if(p.hasPermission(prefix.permission)){
                return prefix.string;
            }
        }

        return "";
    }

    public String getSuffix(Player p){
        for(PrefixSuffix suffix : suffixes){
            if(p.hasPermission(suffix.permission)){
                return suffix.string;
            }
        }

        return "";
    }

    public static class PrefixSuffix {

        public String string;
        public String permission;
        public Integer priority;

        public PrefixSuffix(JSONObject data){
            this.string = data.getString("string");
            this.permission = data.getString("permission");
            this.priority = data.getInt("priority");
        }

        @Override
        public String toString(){
            return "PrefixSuffix{" +
                    "string='" + string + '\'' +
                    ", permission='" + permission + '\'' +
                    ", priority=" + priority +
                    '}';
        }
    }
}
