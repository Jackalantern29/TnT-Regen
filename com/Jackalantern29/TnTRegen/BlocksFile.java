package com.Jackalantern29.TnTRegen;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.file.YamlConfiguration;

public class BlocksFile {

	public static void update() {
		File blockFile = new File(Main.getInstance().getDataFolder() + "/blocks.yml"); 
		YamlConfiguration config = YamlConfiguration.loadConfiguration(blockFile);
		if(!blockFile.exists()) {
			try {
				blockFile.createNewFile();
				Bukkit.getConsoleSender().sendMessage("Generating blocks.yml file.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Block block = null;
		if(!Bukkit.getWorlds().isEmpty())
		 block = Bukkit.getWorlds().get(0).getBlockAt(0, 1, 0);
		Material save = block.getType();
		boolean csave = false;
		for(Material materials : Material.values()) {
			if(materials.isBlock()) {
				if(!config.contains(materials.name().toLowerCase() + ".doPreventDamage")) {config.set(materials.name().toLowerCase() + ".doPreventDamage", false); csave = true;}
				if(!config.contains(materials.name().toLowerCase() + ".regen")) {config.set(materials.name().toLowerCase() + ".regen", true); csave = true;}
				if(block != null && !materials.name().contains("BED") && materials != Material.COMPARATOR) {
					block.setType(materials);
					if(block.getState() instanceof Container && !(block.getState() instanceof ShulkerBox) && !config.contains(materials.name().toLowerCase() + ".saveItems")) {
						config.set(materials.name().toLowerCase() + ".saveItems", true);
						csave = true;
					} else {
						if(!config.contains(materials.name().toLowerCase() + ".replace.doReplace")) {config.set(materials.name().toLowerCase() + ".replace.doReplace", false); csave = true;}
						if(!config.contains(materials.name().toLowerCase() + ".replace.replaceWith")) {config.set(materials.name().toLowerCase() + ".replace.replaceWith", materials.name().toLowerCase()); csave = true;}
					}
				} else {
					if(!config.contains(materials.name().toLowerCase() + ".replace.doReplace")) {config.set(materials.name().toLowerCase() + ".replace.doReplace", false); csave = true;}
					if(!config.contains(materials.name().toLowerCase() + ".replace.replaceWith")) {config.set(materials.name().toLowerCase() + ".replace.replaceWith", materials.name().toLowerCase()); csave = true;}
				}
				if(!config.contains(materials.name().toLowerCase() + ".chance")) {config.set(materials.name().toLowerCase() + ".chance", 30); csave = true;}
				if(csave == true) {
					try {
						config.save(blockFile);
					} catch (IOException e) {
						e.printStackTrace();
					}										
				}
			}
		}
		if(block != null)
			block.setType(save);
	}
}
