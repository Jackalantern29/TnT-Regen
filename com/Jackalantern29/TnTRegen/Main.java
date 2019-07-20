package com.Jackalantern29.TnTRegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin {
	static List<BlockState> storedBlocks = new ArrayList<>();
	private static Main plugin;
	public void onLoad() {
		plugin = this;
		File configFile = new File(getDataFolder() + "/config.yml"); 
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		if(!configFile.exists()) {
			saveDefaultConfig();			
		} else {
			if(!config.contains("enablePlugin")) config.set("enablePlugin", true);
			
			if(!config.contains("enable-Regen")) config.set("enable-Regen", true);
			if(!config.contains("delay-")) config.set("delay-", 1200);
			if(!config.contains("period-")) config.set("period-", 1200);
			
			if(!config.contains("enablePRIMEDTNTRegen")) config.set("enablePRIMEDTNTRegen", true);
			if(!config.contains("delayPRIMEDTNT")) config.set("delayPRIMEDTNT", 1200);
			if(!config.contains("periodPRIMEDTNT")) config.set("periodPRIMEDTNT", 20);
			
			if(!config.contains("enableWITHERRegen")) config.set("enableWITHERRegen", true);
			if(!config.contains("delayWITHER")) config.set("delayWITHER", 1200);
			if(!config.contains("periodWITHER")) config.set("periodWITHER", 20);
			
			if(!config.contains("enableWITHERSKULLRegen")) config.set("enableWITHERSKULLRegen", true);
			if(!config.contains("delayWITHERSKULL")) config.set("delayWITHERSKULL", 1200);
			if(!config.contains("periodWITHERSKULL")) config.set("periodWITHERSKULL", 20);
			
			if(!config.contains("enableCREEPERRegen")) config.set("enableCREEPERRegen", true);
			if(!config.contains("delayCREEPER")) config.set("delayCREEPER", 1200);
			if(!config.contains("periodCREEPER")) config.set("periodCREEPER", 20);
			
			if(!config.contains("enableFIREBALLRegen")) config.set("enableFIREBALLRegen", true);
			if(!config.contains("delayFIREBALL")) config.set("delayFIREBALL", 1200);
			if(!config.contains("periodFIREBALL")) config.set("periodFIREBALL", 20);

			if(!config.contains("instantRegen")) config.set("instantRegen", false);
			if(!config.contains("disableExplosionBlockDamage")) config.set("disableExplosionBlockDamage", false);
			if(!config.contains("enableParticles")) config.set("enableParticles", true);

			if(!config.contains("particle")) config.set("particle", Particle.HEART.name().toLowerCase());
			
			if(config.getConfigurationSection("triggers") == null)
				getServer().getWorlds().forEach(world -> {config.set("triggers." + world.getName() + ".minY", 0.0); config.set("triggers." + world.getName() + ".maxY", 256.0);});
			try {
				config.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BlocksFile.update();
	}
	public void onEnable() {
		File configFile = new File(getDataFolder() + "/config.yml"); 
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		if(config.getBoolean("enablePlugin") == false) {
			setEnabled(false);
			getServer().getConsoleSender().sendMessage("[TnTRegen] Disabling TnTRegen. \"enablePlugin\" in config is set to false.");
			return;
		}
		getServer().getPluginManager().registerEvents(new EntityExplodeListener(), this);
	}
	public void onDisable() {
		for(BlockState state : storedBlocks) {
			state.update(true, false);
		}
	}
	public static void instantRegen(List<BlockState> blockStateList, long delay) {
		HashMap<Location, HashMap<Material, BlockData>> blocks = new HashMap<>();
		File configFile = new File(plugin.getDataFolder() + "/config.yml"); 
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		for(BlockState b : blockStateList) {
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
			}.runTaskLater(plugin, delay);
		}
	}
	public static BukkitTask regenSched(List<BlockState> blockStateList, long delay, long period) {
		File configFile = new File(plugin.getDataFolder() + "/config.yml"); 
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		ArrayList<BlockState> blocks = new ArrayList<>();
		for(BlockState b : blockStateList) {
			blocks.add(b);
			storedBlocks.add(b);
		}
		return new BukkitRunnable() {
			@Override
			public void run() {
				if(!blocks.isEmpty()) {
					Location location = blocks.get(blocks.size() - 1).getLocation();
					if((location.getBlock().getType() == Material.AIR) || (location.getBlock().getType() == Material.WATER) || (location.getBlock().getType() == Material.LAVA) || (location.getBlock().getType() == Material.FIRE)) {
						blocks.get(blocks.size() - 1).update(true, false);
						if(config.getBoolean("enableParticles"))
							location.getWorld().spawnParticle(Particle.valueOf(config.getString("particle").toUpperCase()), location, 3, 1, 1, 1);
					}
					
					blocks.remove(blocks.get(blocks.size() - 1));
				} else {
					storedBlocks.remove(storedBlocks.size() - 1);
					cancel();
				}
			}
		}.runTaskTimer(plugin, delay, period);
	}
	public static Main getInstance() {
		return plugin;
	}
}
