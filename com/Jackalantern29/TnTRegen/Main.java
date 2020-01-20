package com.Jackalantern29.TnTRegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
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

			if(!config.contains("shiftGravityUp")) {config.set("shiftGravityUp", true); csave = true;}
			if(!config.contains("maxShiftGravityUp")) {config.set("maxShiftGravityUp", 5); csave = true;}
			
			if(!config.contains("particles.blockRegen.particle")) {config.set("particles.blockRegen.particle", Particle.HEART.name().toLowerCase()); csave = true;}
			if(!config.contains("particles.blockRegen.enable")) {config.set("particles.blockRegen.enable", true); csave = true;}
			if(!config.contains("particles.blockToBeRegen.particle")) {config.set("particles.blockToBeRegen.particle", Particle.FLAME.name().toLowerCase()); csave = true;}
			if(!config.contains("particles.blockToBeRegen.enable")) {config.set("particles.blockToBeRegen.enable", true); csave = true;}
			
			if(!config.contains("forceBlockToRegen")) {config.set("forceBlockToRegen", false); csave = true;}
			if(!config.contains("griefPreventionPluginAllowExplosionRegen")) {config.set("griefPreventionPluginAllowExplosionRegen", false); csave = true;}
			if(!config.contains("sound.enable")) {config.set("sound.enable", true); csave = true;}
			if(!config.contains("sound.sound")) {config.set("sound.sound", Sound.BLOCK_GRASS_PLACE.name().toLowerCase()); csave = true;}
			if(!config.contains("sound.volume")) {config.set("sound.volume", 1.0); csave = true;}
			if(!config.contains("sound.pitch")) {config.set("sound.pitch", 2.0); csave = true;}
			
			if(!config.contains("NoPermMsg")) {config.set("NoPermMsg", "&c[TNTRegen] You do not have permission to use this command!"); csave = true;}
			
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
		getCommand("rparticle").setExecutor(new CommandRParticle());
		getCommand("rsound").setExecutor(new CommandRSound());
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
					if(config.getBoolean("particles.blockRegen.enable")) {
						if(config.getString("particles.blockRegen.particle").equals("lightning")) {
							location.getWorld().strikeLightningEffect(location);
						} else
							location.getWorld().spawnParticle(Particle.valueOf(config.getString("particles.blockRegen.particle").toUpperCase()), location, 3, 1, 1, 1);
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
							if(config.getBoolean("particles.blockRegen.enable")) {
								if(config.getString("particles.blockRegen.particle").equals("lightning")) {
									location.getWorld().strikeLightningEffect(location);
								} else
									location.getWorld().spawnParticle(Particle.valueOf(config.getString("particles.blockRegen.particle").toUpperCase()), location, 3, 1, 1, 1);
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
		new BukkitRunnable() {
			@Override
			public void run() {
				if(blocks.isEmpty())
					this.cancel();
				else {
					if(config.getBoolean("particles.blockToBeRegen.enable")) {
						Random r = new Random();
						BlockState block = blocks.get(r.nextInt(blocks.size()));
						block.getWorld().spawnParticle(Particle.valueOf(config.getString("particles.blockToBeRegen.particle").toUpperCase()), block.getLocation(), 5, 0.5, 0.5, 0.5, 0);											
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
		return new BukkitRunnable() {
			@Override
			public void run() {
				if(!blocks.isEmpty()) {
					Location location = blocks.get(blocks.size() - 1).getLocation();
					if((location.getBlock().getType().hasGravity()) || (location.getBlock().getType() == Material.AIR) || (location.getBlock().getType() == Material.WATER) || (location.getBlock().getType() == Material.LAVA) || (location.getBlock().getType() == Material.FIRE)) {
						regen(location);
					} else {
						if(!config.getBoolean("forceBlockToRegen")) {
							final BlockState save = location.getBlock().getState();
							blocks.get(blocks.size() - 1).update(true, false);
							location.getBlock().breakNaturally();
							save.update(true, false);
						} else {
							if(location.getBlock().getState() instanceof Container) {
								Container container = (Container)location.getBlock().getState();
								for(ItemStack items : container.getInventory().getContents()) {
									if(items != null) {
										location.getWorld().dropItemNaturally(location, items);
									}
								}
								container.getInventory().clear();
							}
							location.getWorld().dropItemNaturally(location, new ItemStack(location.getBlock().getType()));
							regen(location);
						}
					}
					
					blocks.remove(blocks.get(blocks.size() - 1));
				} else {
					cancel();
				}
			}
			public void regen(Location location) {
				if(location.getBlock().getType().hasGravity()) {							
					if(config.getBoolean("shiftGravityUp")) {
						for (int i = config.getInt("maxShiftGravityUp"); i > 0; i--) {
							if(location.clone().add(0, i, 0).getBlock().getType().hasGravity()) {
								if((location.clone().add(0, i+1, 0).getBlock().getType() == Material.AIR) || (location.clone().add(0, i+1, 0).getBlock().getType() == Material.WATER) || (location.clone().add(0, i+1, 0).getBlock().getType() == Material.LAVA) || (location.clone().add(0, i+1, 0).getBlock().getType() == Material.FIRE)) {
									location.clone().add(0, i+1, 0).getBlock().setBlockData(location.clone().add(0, i, 0).getBlock().getBlockData());
								}
							}
						}
						//TODO Investigate this line below.
						//TODO Prevent entities from suffocating.
						location.clone().add(0, 1, 0).getBlock().setBlockData(location.getBlock().getBlockData());
					}
				}
				blocks.get(blocks.size() - 1).update(true);
				CoreProtectAPI api = Main.getInstance().getCoreProtect();
				if(api != null)
					api.logPlacement("#tntregen", location, location.getBlock().getType(), location.getBlock().getBlockData());
				storedBlocks.remove(blocks.get(blocks.size() - 1));
				if(config.getBoolean("particles.blockRegen.enable")) {
					if(config.getString("particles.blockRegen.particle").equals("lightning")) {
						location.getWorld().strikeLightningEffect(location);
					} else
						location.getWorld().spawnParticle(Particle.valueOf(config.getString("particles.blockRegen.particle").toUpperCase()), location, 3, 1, 1, 1);
				}
				if(config.getBoolean("sound.enable")) {
					location.getWorld().playSound(location, Sound.valueOf(config.getString("sound.sound").toUpperCase()), Float.valueOf(config.getString("sound.volume")), Float.valueOf(config.getString("sound.pitch")));
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
	public GriefPrevention getGriefPrevention() {
		Plugin p = getServer().getPluginManager().getPlugin("GriefPrevention");
		if(p == null || !(p instanceof GriefPrevention))
			return null;
		GriefPrevention grief = GriefPrevention.instance;
		if(grief.isEnabled() == false)
			return null;
		return grief;
	}
}
