package com.Jackalantern29.TnTRegen;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {
	FileConfiguration config = getConfig();
	public void onLoad() {
		saveDefaultConfig();
	}
	public void onEnable() {
		if(config.getBoolean("enablePlugin") == false) {
			setEnabled(false);
			getServer().getConsoleSender().sendMessage("[TnTRegen] Disabling TnTRegen. \"enablePlugin\" in config is set to false.");
			return;
		}
		getServer().getPluginManager().registerEvents(this, this);
	}
	@EventHandler
	public void onTnTExplode(EntityExplodeEvent event) {
		Entity e = event.getEntity();
		HashMap<Location, Material> blocks = new HashMap<>();
		for(Block b : event.blockList())
			blocks.put(b.getLocation(), b.getType());
		if(config.getBoolean("enableTnTRegen")) {
			if(e instanceof TNTPrimed) {
				event.setYield(0);
				new BukkitRunnable() {
					
					@Override
					public void run() {
						if(blocks.keySet().iterator().hasNext()) {
							Material material = blocks.get(blocks.keySet().iterator().next());
							Location location = blocks.keySet().iterator().next();
							if(location.getBlock().getType() == Material.AIR)
								location.getBlock().setType(material, false);
							
							blocks.remove(blocks.keySet().iterator().next());
						} else {
							cancel();
						}
					}
				}.runTaskTimer(this, config.getInt("delayTnT"), config.getInt("periodTnT"));
			}
		}
		if(config.getBoolean("enableCreeperRegen")) {
			if(e instanceof Creeper) {
				event.setYield(0);
				new BukkitRunnable() {
					
					@Override
					public void run() {
						if(blocks.keySet().iterator().hasNext()) {
							Material material = blocks.get(blocks.keySet().iterator().next());
							Location location = blocks.keySet().iterator().next();
							if(location.getBlock().getType() == Material.AIR)
								location.getBlock().setType(material, false);
							
							blocks.remove(blocks.keySet().iterator().next());
						} else {
							cancel();
						}
					}
				}.runTaskTimer(this, config.getInt("delayCreeper"), config.getInt("periodCreeper"));
			}	
		}
	}
}
