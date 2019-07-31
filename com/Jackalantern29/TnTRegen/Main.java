package com.Jackalantern29.TnTRegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

public class Main extends JavaPlugin {
	static ArrayList<BlockState> storedBlocks = new ArrayList<>();
	private static Main plugin;
	public void onLoad() {
		plugin = this;
		File configFile = new File(getDataFolder() + "/config.yml"); 
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		if(!configFile.exists()) {
			saveDefaultConfig();			
		} else {
			boolean csave = false;
			if(!config.contains("enablePlugin")) {config.set("enablePlugin", true); csave = true;}
			
			if(!config.contains("enable-Regen")) {config.set("enable-Regen", true); csave = true;}
			if(!config.contains("delay-")) {config.set("delay-", 1200); csave = true;}
			if(!config.contains("period-")) {config.set("period-", 1200); csave = true;}
			
			if(!config.contains("enablePRIMEDTNTRegen")) {config.set("enablePRIMEDTNTRegen", true); csave = true;}
			if(!config.contains("delayPRIMEDTNT")) {config.set("delayPRIMEDTNT", 1200); csave = true;}
			if(!config.contains("periodPRIMEDTNT")) {config.set("periodPRIMEDTNT", 20); csave = true;}
			
			if(!config.contains("enableWITHERRegen")) {config.set("enableWITHERRegen", true); csave = true;}
			if(!config.contains("delayWITHER")) {config.set("delayWITHER", 1200); csave = true;}
			if(!config.contains("periodWITHER")) {config.set("periodWITHER", 20); csave = true;}
			
			if(!config.contains("enableWITHERSKULLRegen")) {config.set("enableWITHERSKULLRegen", true); csave = true;}
			if(!config.contains("delayWITHERSKULL")) {config.set("delayWITHERSKULL", 1200); csave = true;}
			if(!config.contains("periodWITHERSKULL")) {config.set("periodWITHERSKULL", 20); csave = true;}
			
			if(!config.contains("enableCREEPERRegen")) {config.set("enableCREEPERRegen", true); csave = true;}
			if(!config.contains("delayCREEPER")) {config.set("delayCREEPER", 1200); csave = true;}
			if(!config.contains("periodCREEPER")) {config.set("periodCREEPER", 20); csave = true;}
			
			if(!config.contains("enableFIREBALLRegen")) {config.set("enableFIREBALLRegen", true); csave = true;}
			if(!config.contains("delayFIREBALL")) {config.set("delayFIREBALL", 1200); csave = true;}
			if(!config.contains("periodFIREBALL")) {config.set("periodFIREBALL", 20); csave = true;}

			if(!config.contains("instantRegen")) {config.set("instantRegen", false); csave = true;}
			if(!config.contains("disableExplosionBlockDamage")) {config.set("disableExplosionBlockDamage", false); csave = true;}
			if(!config.contains("enableParticles")) {config.set("enableParticles", true); csave = true;}

			if(!config.contains("shiftGravityUp")) {config.set("shiftGravityUp", true); csave = true;}
			if(!config.contains("maxShiftGravityUp")) {config.set("maxShiftGravityUp", 5); csave = true;}
			
			if(!config.contains("particle")) {config.set("particle", Particle.HEART.name().toLowerCase()); csave = true;}
			if(!config.contains("sound.enable")) {config.set("sound.enable", true); csave = true;}
			if(!config.contains("sound.sound")) {config.set("sound.sound", Sound.BLOCK_GRASS_PLACE.name().toLowerCase()); csave = true;}
			if(!config.contains("sound.volume")) {config.set("sound.volume", 1.0); csave = true;}
			if(!config.contains("sound.pitch")) {config.set("sound.pitch", 2.0); csave = true;}
			
			if(config.getConfigurationSection("triggers") == null) {
				getServer().getWorlds().forEach(world -> {config.set("triggers." + world.getName() + ".minY", 0.0); config.set("triggers." + world.getName() + ".maxY", 256.0);});
				csave = true;
			}
			if(csave == true) {				
				try {
					config.save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
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
		getServer().getPluginManager().registerEvents(new EntityExplodeListener(), this);
		BlocksFile.update();
	}
	public void onDisable() {
		File configFile = new File(plugin.getDataFolder() + "/config.yml"); 
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		if(!storedBlocks.isEmpty()) {
			for(BlockState blocks : storedBlocks) {
				Location location = blocks.getLocation();
				if((location.getBlock().getType() == Material.AIR) || (location.getBlock().getType() == Material.WATER) || (location.getBlock().getType() == Material.LAVA) || (location.getBlock().getType() == Material.FIRE)) {
					blocks.update(true, false);
					CoreProtectAPI api = Main.getInstance().getCoreProtect();
					if(api != null)
						api.logPlacement("#tntregen", location, location.getBlock().getType(), location.getBlock().getBlockData());
					if(config.getBoolean("enableParticles")) {
						if(config.getString("particle").equals("lightning")) {
							location.getWorld().strikeLightningEffect(location);
						} else
							location.getWorld().spawnParticle(Particle.valueOf(config.getString("particle").toUpperCase()), location, 3, 1, 1, 1);
					}
					if(config.getBoolean("sound.enable")) {
						location.getWorld().playSound(location, Sound.valueOf(config.getString("sound.sound").toUpperCase()), Float.valueOf(config.getString("sound.volume")), Float.valueOf(config.getString("sound.pitch")));
					}
				}	
			}
		}
	}
	public static void instantRegen(ArrayList<BlockState> blockStateList, long delay) {
		File configFile = new File(plugin.getDataFolder() + "/config.yml"); 
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(!blockStateList.isEmpty()) {
					for(BlockState blocks : blockStateList) {
						Location location = blocks.getLocation();
						if((location.getBlock().getType() == Material.AIR) || (location.getBlock().getType() == Material.WATER) || (location.getBlock().getType() == Material.LAVA) || (location.getBlock().getType() == Material.FIRE)) {
							blocks.update(true, false);
							CoreProtectAPI api = Main.getInstance().getCoreProtect();
							if(api != null)
								api.logPlacement("#tntregen", location, location.getBlock().getType(), location.getBlock().getBlockData());
							if(config.getBoolean("enableParticles")) {
								if(config.getString("particle").equals("lightning")) {
									location.getWorld().strikeLightningEffect(location);
								} else
									location.getWorld().spawnParticle(Particle.valueOf(config.getString("particle").toUpperCase()), location, 3, 1, 1, 1);
							}
							if(config.getBoolean("sound.enable")) {
								location.getWorld().playSound(location, Sound.valueOf(config.getString("sound.sound").toUpperCase()), Float.valueOf(config.getString("sound.volume")), Float.valueOf(config.getString("sound.pitch")));
							}
						}	
					}
				} else {
					cancel();
				}
			}
		}.runTaskLater(plugin, delay);
	}
	public static BukkitTask regenSched(ArrayList<BlockState> blockStateList, long delay, long period) {
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
					if((location.getBlock().getType().hasGravity()) || (location.getBlock().getType() == Material.AIR) || (location.getBlock().getType() == Material.WATER) || (location.getBlock().getType() == Material.LAVA) || (location.getBlock().getType() == Material.FIRE)) {
						if(location.getBlock().getType().hasGravity()) {							
							if(config.getBoolean("shiftGravityUp")) {
								for (int i = config.getInt("maxShiftGravityUp"); i > 0; i--) {
									if(location.clone().add(0, i, 0).getBlock().getType().hasGravity()) {
										if((location.clone().add(0, i+1, 0).getBlock().getType() == Material.AIR) || (location.clone().add(0, i+1, 0).getBlock().getType() == Material.WATER) || (location.clone().add(0, i+1, 0).getBlock().getType() == Material.LAVA) || (location.clone().add(0, i+1, 0).getBlock().getType() == Material.FIRE)) {
											location.clone().add(0, i+1, 0).getBlock().setBlockData(location.clone().add(0, i, 0).getBlock().getBlockData());
										}
									}
								}
								location.clone().add(0, 1, 0).getBlock().setBlockData(location.getBlock().getBlockData());
							}
						}
						blocks.get(blocks.size() - 1).update(true);
						CoreProtectAPI api = Main.getInstance().getCoreProtect();
						if(api != null)
							api.logPlacement("#tntregen", location, location.getBlock().getType(), location.getBlock().getBlockData());
						storedBlocks.remove(blocks.get(blocks.size() - 1));
						if(config.getBoolean("enableParticles")) {
							if(config.getString("particle").equals("lightning")) {
								location.getWorld().strikeLightningEffect(location);
							} else
								location.getWorld().spawnParticle(Particle.valueOf(config.getString("particle").toUpperCase()), location, 3, 1, 1, 1);
						}
						if(config.getBoolean("sound.enable")) {
							location.getWorld().playSound(location, Sound.valueOf(config.getString("sound.sound").toUpperCase()), Float.valueOf(config.getString("sound.volume")), Float.valueOf(config.getString("sound.pitch")));
						}
					} else {
						final BlockState save = location.getBlock().getState();
						blocks.get(blocks.size() - 1).update(true, false);
						location.getBlock().breakNaturally();
						save.update(true, false);
					}
					
					blocks.remove(blocks.get(blocks.size() - 1));
				} else {
					cancel();
				}
			}
		}.runTaskTimer(plugin, delay, period);
	}
	public static Main getInstance() {
		return plugin;
	}
	public CoreProtectAPI getCoreProtect() {
		Plugin p = getServer().getPluginManager().getPlugin("CoreProtect");
		if(p == null || !(p instanceof CoreProtect))
			return null;
		CoreProtectAPI CoreProtect = ((CoreProtect)p).getAPI();
		if(CoreProtect.isEnabled() == false)
			return null;
		if(CoreProtect.APIVersion() < 6)
			return null;
		return CoreProtect;
	}
}
