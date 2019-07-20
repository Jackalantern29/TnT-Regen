package com.Jackalantern29.TnTRegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeListener implements Listener {

	@EventHandler(priority=EventPriority.NORMAL)
	public void onExplode(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		File configFile = new File(Main.getInstance().getDataFolder() + "/config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		if(config.getBoolean("disableExplosionBlockDamage")) {
			new ArrayList<Block>(event.blockList()).forEach(block -> event.blockList().remove(block));
		} else {
			if(!event.isCancelled()) {
				ArrayList<BlockState> blockStates = new ArrayList<BlockState>();
				File blockFile = new File(Main.getInstance().getDataFolder() + "/blocks.yml");
				Configuration blockConfig = YamlConfiguration.loadConfiguration(blockFile);
				event.blockList().forEach(block -> blockStates.add(block.getState()));
				ArrayList<BlockState> blockStates2 = new ArrayList<BlockState>(blockStates);
				for(String worlds : config.getConfigurationSection("triggers").getKeys(false)) {
					if(entity.getWorld().getName().equals(worlds)) {
						if(entity.getLocation().getY() >= config.getDouble("triggers." + worlds + ".minY") && entity.getLocation().getY() <= config.getDouble("triggers." + worlds + ".maxY")) {
							//TODO if entity instanceof wither, end crystal, creeper, tnt, ghast fireball, wither skulls
							if(!config.contains("enable" + entity.getType().name().replace("_", "") + "Regen")) {
								Bukkit.getConsoleSender().sendMessage("Type " + entity.getType().name().replace("_", "") + " is not in the config. Adding support for this entity.");
								config.set("enable" + entity.getType().name().replace("_", "") + "Regen", config.getBoolean("enable-Regen"));
								config.set("delay" + entity.getType().name().replace("_", ""), config.getInt("delay-"));
								config.set("period" + entity.getType().name().replace("_", ""), config.getInt("period-"));
								try {
									config.save(configFile);
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else {
								event.blockList().clear();
								for(BlockState states : blockStates) {
									Block blocka = states.getLocation().getBlock();
									ConfigurationSection blockSection = blockConfig.getConfigurationSection(blocka.getType().name().toLowerCase());
									if(blockSection.getBoolean("doPreventDamage")) {
										event.blockList().remove(blocka);
										blockStates2.remove(blocka.getState());
									} else if(blockSection.getBoolean("regen")) {
										if(states instanceof Container && !(states instanceof ShulkerBox)) {
											if(blockSection.getBoolean("saveItems"))
												((Container) states).getInventory().clear();
											else {
												int index = blockStates2.indexOf(states);
												blockStates2.remove(index);
												BlockData save = blocka.getState().getBlockData();
												
												blocka.setType(Material.AIR);
												blocka.setBlockData(save);
												((Container) blocka.getState()).getInventory().clear();
												blockStates2.add(index, blocka.getState());
											}
										}
										blocka.setType(Material.AIR);
									} else {
										Random r = new Random();
										int random = r.nextInt(99);
										event.blockList().remove(blocka);
										blockStates2.remove(blocka.getState());
										if(random <= blockSection.getInt("chance")-1)
											blocka.breakNaturally();
										else blocka.setType(Material.AIR);
									}
								}
								if(!config.getBoolean("instantRegen"))
									Main.regenSched(blockStates2, config.getInt("delay" + entity.getType().name().replace("_", "")), config.getInt("period" + entity.getType().name().replace("_", "")));
								else
									Main.instantRegen(blockStates2, config.getInt("delay" + entity.getType().name().replace("_", "")));
							}
						}
					}
				}
			}
		}
	}
}
