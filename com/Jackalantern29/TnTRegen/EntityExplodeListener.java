package com.Jackalantern29.TnTRegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class EntityExplodeListener implements Listener {

	  private static HashMap<BlockState, Integer> sortByValues(HashMap<BlockState, Integer> map) { 
	       List<Entry<BlockState, Integer>> list = new LinkedList<>(map.entrySet());
	       Collections.sort(list, new Comparator<Entry<BlockState, Integer>>() {
	            public int compare(Entry<BlockState, Integer> o1, Entry<BlockState, Integer> o2) {
	            	return ((Comparable<Integer>) ((Map.Entry<BlockState, Integer>) (o2)).getValue()).compareTo(((Map.Entry<BlockState, Integer>) (o1)).getValue());
	            }
	       });
	       HashMap<BlockState, Integer> sortedHashMap = new LinkedHashMap<>();
	       for (Iterator<Entry<BlockState, Integer>> it = list.iterator(); it.hasNext();) {
	              Entry<BlockState, Integer> entry = it.next();
	              sortedHashMap.put(entry.getKey(), entry.getValue());
	       } 
	       return sortedHashMap;
	  }
	@SuppressWarnings("unchecked")
	@EventHandler(priority=EventPriority.HIGHEST)
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
				ArrayList<BlockState> blockStates2 = new ArrayList<BlockState>();
				HashMap<BlockState, Integer> newBlockStates = new HashMap<BlockState, Integer>();
				blockStates.forEach(block -> newBlockStates.put(block, block.getY()));
				Map<BlockState, Integer> map = sortByValues(newBlockStates);
				for(BlockState blocks : map.keySet()) {
					if(blocks.getType() != Material.TNT)
						blockStates2.add(blocks);
				}
				for(String worlds : config.getConfigurationSection("triggers").getKeys(false)) {
					if(entity.getWorld().getName().equals(worlds)) {
						if(entity.getLocation().getY() >= config.getDouble("triggers." + worlds + ".minY") && entity.getLocation().getY() <= config.getDouble("triggers." + worlds + ".maxY")) {
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
								if(config.getBoolean("enable" + entity.getType().name().replace("_", "") + "Regen")) {
									for(BlockState states : blockStates) {
										if(states.getType() != Material.TNT && states.getType() != Material.PISTON_HEAD) {										
											Block blocka = states.getLocation().getBlock();
											ConfigurationSection blockSection = blockConfig.getConfigurationSection(blocka.getType().name().toLowerCase());
											if(blockSection.getBoolean("doPreventDamage")) {
												event.blockList().remove(blocka);
												blockStates2.remove(blocka.getState());
												if(blockSection.getBoolean("replace.doReplace"))
													blocka.setType(Material.valueOf(blockSection.getString("replace.replaceWith").toUpperCase()));
											} else if(blockSection.getBoolean("regen")) {
												if(states instanceof Container && !(states instanceof ShulkerBox)) {
													if(blockSection.getBoolean("saveItems")) {
														((Container) states).getInventory().clear();
														ArrayList<ItemStack> items = new ArrayList<>();
														for(ItemStack i : ((Container)states).getInventory().getContents())
															items.add(i);
														if(states.getMetadata("drops").isEmpty())
															states.setMetadata("drops", new FixedMetadataValue(Main.getInstance(), items));
														else
															((ArrayList<ItemStack>)states.getMetadata("drops").get(states.getMetadata("drops").size() - 1).value()).addAll(items);
													} else {
														int index = blockStates2.indexOf(states);
														blockStates2.remove(index);
														BlockData save = blocka.getState().getBlockData();
														blocka.setBlockData(save);
														((Container) blocka.getState()).getInventory().clear();
														blockStates2.add(index, blocka.getState());
													}
												} else if(blockSection.getBoolean("replace.doReplace")) {
													blockStates2.get(blockStates2.indexOf(states)).setType(Material.valueOf(blockSection.getString("replace.replaceWith").toUpperCase()));
												} if(blocka.getBlockData() instanceof Bisected)
													blocka.setType(Material.AIR, false);
												else
													blocka.setType(Material.AIR);
												if(!states.getBlock().hasMetadata("drops"))
													states.getBlock().setMetadata("drops", new FixedMetadataValue(Main.getInstance(), blocka.getDrops()));
												else
													states.getBlock().getMetadata("drops").get(states.getBlock().getMetadata("drops").size() - 1);
											} else {
												Random r = new Random();
												int random = r.nextInt(99);
												event.blockList().remove(blocka);
												blockStates2.remove(blocka.getState());
												if(random <= blockSection.getInt("chance")-1)
													blocka.breakNaturally();
												else {
													if(blocka.getBlockData() instanceof Bisected)
														blocka.setType(Material.AIR, false);
													else
														blocka.setType(Material.AIR);
												}
											}
										}
									}
									if(Main.getInstance().getCoreProtect() != null)
										blockStates2.forEach(block -> Main.getInstance().getCoreProtect().logRemoval("#" + entity.getType().name().toLowerCase(), block.getLocation(), block.getType(), block.getBlockData()));
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
}
