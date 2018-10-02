package com.Jackalantern29.TnTRegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin implements Listener {
	static List<HashMap<Location, HashMap<Material, BlockData>>> storedBlocks = new ArrayList<>();
	public void onLoad() {
		File configFile = new File(getDataFolder() + "/config.yml"); 
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		if(!configFile.exists()) {
			saveDefaultConfig();			
		} else {
			if(!config.contains("enablePlugin")) config.set("enablePlugin", true);
			if(!config.contains("enableTnTRegen")) config.set("enableTnTRegen", true);
			if(!config.contains("enableCreeperRegen")) config.set("enableCreeperRegen", true);
			if(!config.contains("delayTnT")) config.set("delayTnT", 1200);
			if(!config.contains("periodTnT")) config.set("periodTnT", 20);
			if(!config.contains("delayCreeper")) config.set("delayCreeper", 1200);
			if(!config.contains("periodCreeper")) config.set("periodCreeper", 20);
			List<String> worlds = new ArrayList<>();
			if(!getServer().getWorlds().isEmpty())
				worlds.add(getServer().getWorlds().get(0).getName());
			else
				worlds.add("world");
			if(!config.contains("worlds")) config.set("worlds", worlds);
			if(!config.contains("instantRegen")) config.set("instantRegen", false);
			if(!config.contains("disableExplosionBlockDamage")) config.set("disableExplosionBlockDamage", false);
			if(!config.contains("enableParticles")) config.set("enableParticles", true);
			if(!config.contains("particle")) config.set("particle", "heart");
			if(!config.contains("triggers.minY")) config.set("triggers.minY", 0.0);
			if(!config.contains("triggers.maxY")) config.set("triggers.maxY", 256.0);
			try {
				config.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void onEnable() {
		File configFile = new File(getDataFolder() + "/config.yml"); 
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		if(config.getBoolean("enablePlugin") == false) {
			setEnabled(false);
			getServer().getConsoleSender().sendMessage("[TnTRegen] Disabling TnTRegen. \"enablePlugin\" in config is set to false.");
			return;
		}
		getServer().getPluginManager().registerEvents(this, this);
	}
	public void onDisable() {
		for(HashMap<Location, HashMap<Material, BlockData>> map : storedBlocks) {
			for(Location locs : map.keySet()) {
				for(Material mat : map.get(locs).keySet()) {
					locs.getBlock().setType(mat);
					locs.getBlock().setBlockData(map.get(locs).get(mat));
				}
			}
		}
	}
	public void instantRegen(List<Block> blockList, long delay) {
		HashMap<Location, HashMap<Material, BlockData>> blocks = new HashMap<>();
		File configFile = new File(getDataFolder() + "/config.yml"); 
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		for(Block b : blockList) {
			HashMap<Material, BlockData> map = new HashMap<>();
			map.put(b.getType(), b.getBlockData());
			blocks.put(b.getLocation(), map);
			
			new BukkitRunnable() {

				@Override
				public void run() {
					for(Location locs : blocks.keySet()) {
						for(Material mat : blocks.get(locs).keySet()) {
							locs.getBlock().setType(mat);
							locs.getBlock().setBlockData(blocks.get(locs).get(mat));
							if(config.getBoolean("enableParticles"));
								locs.getWorld().spawnParticle(Particle.valueOf(config.getString("particle").toUpperCase()), locs, 1, 1, 1, 1);
						}
					}
				}
			}.runTaskLater(this, delay);
		}
	}
	public BukkitTask regenSched(List<Block> blockList, long delay, long period) {
		File configFile = new File(getDataFolder() + "/config.yml"); 
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		HashMap<Location, HashMap<Material, BlockData>> blocks = new HashMap<>();
		for(Block b : blockList) {
			HashMap<Material, BlockData> map = new HashMap<>();
			map.put(b.getType(), b.getBlockData());
			blocks.put(b.getLocation(), map);
		}
		storedBlocks.add(blocks);
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
						if(config.getBoolean("enableParticles"))
							location.getWorld().spawnParticle(Particle.valueOf(config.getString("particle").toUpperCase()), location, 3, 1, 1, 1);
					}
					
					blocks.remove(blocks.keySet().iterator().next());
				} else {
					storedBlocks.remove(storedBlocks.size() - 1);
					cancel();
				}
			}
		}.runTaskTimer(this, delay, period);
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onTnTExplode(EntityExplodeEvent event) {
		Entity e = event.getEntity();
		File configFile = new File(getDataFolder() + "/config.yml"); 
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		if(config.getBoolean("disableExplosionBlockDamage")) {
			event.setCancelled(true);
			return;
		}
		for(String worlds : config.getStringList("worlds")) {
			if(e.getWorld().getName().equals(worlds)) {
				if(e.getLocation().getY() >= config.getDouble("triggers.minY") && e.getLocation().getY() <= config.getDouble("triggers.maxY")) {
					if(config.getBoolean("enableTnTRegen")) {
						if(e instanceof TNTPrimed) {
							event.setYield(0);
							if(!config.getBoolean("instantRegen"))
								regenSched(event.blockList(), config.getInt("delayTnT"), config.getInt("periodTnT"));
							else
								instantRegen(event.blockList(), config.getInt("delayTnT"));
						}
					}
					if(config.getBoolean("enableCreeperRegen")) {
						if(e instanceof Creeper) {
							event.setYield(0);
							if(!config.getBoolean("instantRegen"))
								regenSched(event.blockList(), config.getInt("delayCreeper"), config.getInt("periodCreper"));	
							else
								instantRegen(event.blockList(), config.getInt("delayCreeper"));
						}
					}	
				}
			}
		}
	}
}
