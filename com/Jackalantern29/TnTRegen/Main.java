package com.Jackalantern29.TnTRegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.Jackalantern29.TnTRegen.Explosion.BlockManager;
import com.Jackalantern29.TnTRegen.Explosion.ExplosionManager;
import com.Jackalantern29.TnTRegen.Explosion.ExplosionManager.ExplosionType;
import com.Jackalantern29.TnTRegen.Inventory.InventoryManager;
import com.Jackalantern29.TnTRegen.Inventory.InventoryManager.InventoryManagerListener;
import com.Jackalantern29.TnTRegen.Inventory.InventoryManager.TypeCommand;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

public class Main extends JavaPlugin {
	static ArrayList<BlockState> storedBlocks = new ArrayList<>();
	static HashMap<UUID, HashMap<String, Boolean>> pluginPerms = new HashMap<>();
	private static Main plugin;
	protected static boolean debugMode = false;
	public void onEnable() {
		plugin = this;
		ConfigManager.updateConfig();
		if(ConfigManager.isPluginEnable() == false) {
			setEnabled(false);
			getServer().getConsoleSender().sendMessage("[TnTRegen] Disabling TnTRegen. \"enablePlugin\" in config is set to false.");
			return;
		}
		getServer().getPluginManager().registerEvents(new EntityExplodeListener(), this);
		getServer().getPluginManager().registerEvents(new InventoryManagerListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(), this);
		
		getCommand("rparticle").setExecutor(new CommandRParticleSound());
		getCommand("rsound").setExecutor(new CommandRParticleSound());
		getCommand("rparticle").setTabCompleter(new CommandRParticleSound());
		getCommand("rsound").setTabCompleter(new CommandRParticleSound());
		
		getCommand("rconfig").setExecutor(new CommandRConfig());
		getCommand("rregen").setExecutor(new CommandRRegen());
		getCommand("rregen").setTabCompleter(new CommandRRegen());
		
		getCommand("rexplode").setExecutor(new CommandRExplode());
		
		//getCommand("rlogger").setExecutor(new CommandRLogger());
		BlocksFile.update();
		InventoryManager.updateInventories(null);
		if(ConfigManager.isPlayerSettingsEnabled()) {	
			for(Player player : Bukkit.getOnlinePlayers())
				new PlayerSettingsManager(player.getUniqueId()).updateFile();
//			Bukkit.getScheduler().scheduleSyncDelayedTask(this, ()-> {
//				for(Player player : Bukkit.getOnlinePlayers()) {
//					InventoryManager.updateInventories(player);
//				}
//			}, 20);
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				@Override
				public void run() {
					for(Player player : Bukkit.getOnlinePlayers()) {
						if(!pluginPerms.containsKey(player.getUniqueId()))
							pluginPerms.put(player.getUniqueId(), new HashMap<String, Boolean>());
						HashMap<String, Boolean> map = pluginPerms.get(player.getUniqueId());
						if(!InventoryManager.hasInventories(player.getUniqueId())) {
							InventoryManager.updateInventories(player.getUniqueId());
						}
						boolean update = false;
						for(TypeCommand command : TypeCommand.values()) {
							for(ExplosionType explosion : ExplosionType.values()) {
								String p1 = "tntregen.command.r" + command.toString().toLowerCase() + "." + explosion.toString().toLowerCase();
								if(!map.containsKey(p1))
									map.put(p1, player.hasPermission(p1));
								boolean check = map.get(p1);
								if(check != player.hasPermission(p1)) {
									update = true;
									map.put(p1, player.hasPermission(p1));
								}
								if(explosion == ExplosionType.ENTITY) {
									for(EntityType type : ConfigManager.getSupportedEntities()) {
										String p2 = p1 + "." + type.toString().toLowerCase();
										if(!map.containsKey(p2))
											map.put(p2, player.hasPermission(p2));
										check = map.get(p2);
										if(check != player.hasPermission(p2)) {
											update = true;
											map.put(p2, player.hasPermission(p2));
										}
										if(!map.containsKey(p2 + ".presets"))
											map.put(p2 + ".presets", player.hasPermission(p2 + ".presets"));
										check = map.get(p2 + ".presets");
										if(check != player.hasPermission(p2 + ".presets")) {
											update = true;
											map.put(p2 + ".presets", player.hasPermission(p2 + ".presets"));
										}
										if(command == TypeCommand.PARTICLE) {
											for(Particle particle : Particle.values()) {
												if(particle.getDataType() == Void.class) {
													String p3 = p2 + ".particle." + particle.toString().toLowerCase();
													if(!map.containsKey(p3))
														map.put(p3, player.hasPermission(p3));
													check = map.get(p3);
													if(check != player.hasPermission(p3)) {
														update = true;
														map.put(p3, player.hasPermission(p3));
													}
												}
											}
											for(ParticlePresetManager preset : ParticlePresetManager.getPresetParticles()) {
												String p3 = p2 + ".particle." + preset.getName().toLowerCase();
												if(!map.containsKey(p3))
													map.put(p3, player.hasPermission(p3));
												check = map.get(p3);
												if(check != player.hasPermission(p3)) {
													update = true;
													map.put(p3, player.hasPermission(p3));
												}
											}
										} else if(command == TypeCommand.SOUND) {
											for(Sound sound : Sound.values()) {
												String p3 = p2 + ".sound." + sound.toString().toLowerCase();
												if(!map.containsKey(p3))
													map.put(p3, player.hasPermission(p3));
												check = map.get(p3);
												if(check != player.hasPermission(p3)) {
													update = true;
													map.put(p3, player.hasPermission(p3));
												}
											}
										}
									}
								} else if(explosion == ExplosionType.BLOCK) {
									for(Material material : ConfigManager.getSupportedBlocks()) {
										String p2 = p1 + "." + material.toString().toLowerCase();
										if(!map.containsKey(p2))
											map.put(p2, player.hasPermission(p2));
										check = map.get(p2);
										if(check != player.hasPermission(p2)) {
											update = true;
											map.put(p2, player.hasPermission(p2));
										}
										if(command == TypeCommand.PARTICLE) {
											for(Particle particle : Particle.values()) {
												if(particle.getDataType() == Void.class) {
													String p3 = p2 + ".particle." + particle.toString().toLowerCase();
													if(!map.containsKey(p3))
														map.put(p3, player.hasPermission(p3));
													check = map.get(p3);
													if(check != player.hasPermission(p3)) {
														update = true;
														map.put(p3, player.hasPermission(p3));
													}
												}
											}
											for(ParticlePresetManager preset : ParticlePresetManager.getPresetParticles()) {
												String p3 = p2 + ".particle." + preset.getName().toLowerCase();
												if(!map.containsKey(p3))
													map.put(p3, player.hasPermission(p3));
												check = map.get(p3);
												if(check != player.hasPermission(p3)) {
													update = true;
													map.put(p3, player.hasPermission(p3));
												}
											}
										} else if(command == TypeCommand.SOUND) {
											for(Sound sound : Sound.values()) {
												String p3 = p2 + ".sound." + sound.toString().toLowerCase();
												if(!map.containsKey(p3))
													map.put(p3, player.hasPermission(p3));
												check = map.get(p3);
												if(check != player.hasPermission(p3)) {
													update = true;
													map.put(p3, player.hasPermission(p3));
												}
											}
										}
									}
								}
							}
						}
						if(update)
							InventoryManager.updateInventories(player.getUniqueId());
					}
				}
			}, 20, 20);
		}
	}
	public void onDisable() {
		for(ExplosionManager explosion : new ArrayList<>(ExplosionManager.getExplosions())) {
			for(BlockManager block : new ArrayList<>(explosion.getBlocks())) {
				explosion.rrun(block);
			}
			ExplosionManager.getExplosions().remove(explosion);
		}
		InventoryManager.unregisterInventories();
		pluginPerms.clear();
		ExplosionManager.clearBlocksDurability();
	}
	public static Main getInstance() {
		return plugin;
	}
	public CoreProtectAPI getCoreProtect() {
		Plugin p = getServer().getPluginManager().getPlugin("CoreProtect");
		if(p == null)
			return null;
		try {
			if(!(p instanceof CoreProtect))
				return null;
		} catch (NoClassDefFoundError e) {
			return null;
		}
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
