package com.Jackalantern29.TnTRegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin implements Listener {
	FileConfiguration config = getConfig();
	public void onLoad() {
		saveDefaultConfig();
		if(!config.contains("enablePlugin")) config.set("enablePlugin", true);
		if(!config.contains("enableTnTRegen")) config.set("enableTnTRegen", true);
		if(!config.contains("enableCreeperRegen")) config.set("enableCreeperRegen", true);
		if(!config.contains("delayTnT")) config.set("delayTnT", 1200);
		if(!config.contains("periodTnT")) config.set("periodTnT", 20);
		if(!config.contains("delayCreeper")) config.set("delayCreeper", 1200);
		if(!config.contains("periodCreeper")) config.set("periodCreeper", 20);
		List<String> worlds = new ArrayList<>();
		worlds.add(getServer().getWorlds().get(0).getName());
		if(!config.contains("worlds")) config.set("worlds", worlds);
		saveConfig();
	}
	public void onEnable() {
		if(config.getBoolean("enablePlugin") == false) {
			setEnabled(false);
			getServer().getConsoleSender().sendMessage("[TnTRegen] Disabling TnTRegen. \"enablePlugin\" in config is set to false.");
			return;
		}
		getServer().getPluginManager().registerEvents(this, this);
	}
	public BukkitTask regenSched(List<Block> blockList, long delay, long period) {
		HashMap<Location, HashMap<Material, BlockData>> blocks = new HashMap<>();
		for(Block b : blockList) {
			HashMap<Material, BlockData> map = new HashMap<>();
			map.put(b.getType(), b.getBlockData());
			blocks.put(b.getLocation(), map);
		}
		return new BukkitRunnable() {
			
			@Override
			public void run() {
				if(blocks.keySet().iterator().hasNext()) {
					Material material = blocks.get(blocks.keySet().iterator().next()).keySet().iterator().next();
					Location location = blocks.keySet().iterator().next();
					BlockData blockData = blocks.get(blocks.keySet().iterator().next()).get(material);
					if((location.getBlock().getType() == Material.AIR) || (location.getBlock().getType() == Material.WATER) || (location.getBlock().getType() == Material.LAVA)) {
						location.getBlock().setType(material, false);
						location.getBlock().setBlockData(blockData, false);
					}
					
					blocks.remove(blocks.keySet().iterator().next());
				} else {
					cancel();
				}
			}
		}.runTaskTimer(this, delay, period);
	}
	@EventHandler
	public void onTnTExplode(EntityExplodeEvent event) {
		Entity e = event.getEntity();
		for(String worlds : config.getStringList("worlds")) {
			if(e.getWorld().getName().equals(worlds)) {
				if(config.getBoolean("enableTnTRegen")) {
					if(e instanceof TNTPrimed) {
						event.setYield(0);
						regenSched(event.blockList(), config.getInt("delayTnT"), config.getInt("periodTnT"));
					}
				}
				if(config.getBoolean("enableCreeperRegen")) {
					if(e instanceof Creeper) {
						event.setYield(0);
						regenSched(event.blockList(), config.getInt("delayTnT"), config.getInt("periodTnT"));	
					}
				}
			}
		}
	}
}
