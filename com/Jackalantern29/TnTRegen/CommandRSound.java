package com.Jackalantern29.TnTRegen;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Jackalantern29.MegaPixelNetwork.EventFunction;
import com.Jackalantern29.MegaPixelNetwork.FunctionCancelType;
import com.Jackalantern29.MegaPixelNetwork.FunctionClickType;
import com.Jackalantern29.MegaPixelNetwork.InventoryManager;
import com.Jackalantern29.MegaPixelNetwork.InventoryPageSystem;
import com.Jackalantern29.MegaPixelNetwork.ItemCreator;

public class CommandRSound implements CommandExecutor {
	InventoryPageSystem page = new InventoryPageSystem();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("rsound")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("Only players can use this command.");
				return true;
			}
			if(sender.hasPermission("tntregen.command.rsound")) {
				openMenu((Player)sender, 1);
			} else {
				File configFile = new File(Main.getInstance().getDataFolder() + "/config.yml"); 
				YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
				sender.sendMessage(config.getString("NoPermMsg").replace("&", "§"));
				return true;
			}
				
		}
		return false;
	}
	public void openMenu(Player player, int startingPage) {
		File configFile = new File(Main.getInstance().getDataFolder() + "/config.yml"); 
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		
		Sound[] sounds = Sound.values();
		Sound sound = Sound.valueOf(config.getString("sound.sound").toUpperCase());
		int maxPagesForSounds = Integer.parseInt(String.valueOf((sounds.length / 45.0) + 1.0).split("\\.")[0]);
		if(String.valueOf((sounds.length / 45.0) + 1.0).split("\\.")[1].equals("0"))
			maxPagesForSounds--;
		if(page.getTotalPages() == 0) {
			for(int i = 1; i <= maxPagesForSounds; i++) {
				page.createPage(new InventoryManager(page, 9*6, "§cSounds [" + i + "]"));				
			}
		}
		int c = 0;
		for(InventoryManager inventory : page.getInventoryPages()) {
			c++;
			inventory.setCancelled(true);
			inventory.cancelBottomInventory(true);
			inventory.fillSlots(new ItemCreator(Material.PAPER).setDisplayName(" ").create(), new EventFunction().setClickFunction(() -> {}, FunctionCancelType.TRUE, FunctionClickType.ANY), new int[] {46, 47, 48, 50, 51, 52});
			ItemStack fws = new ItemCreator(Material.FIREWORK_STAR).setDisplayName(" ").create();
			inventory.setFunction(fws, new EventFunction().setClickFunction(()->{}, FunctionCancelType.TRUE, FunctionClickType.ANY));
			if(c < maxPagesForSounds) {
				inventory.setItem(53, new ItemCreator(Material.EMERALD).setDisplayName("§a§lNext Page").create());
				EventFunction function = new EventFunction();
				function.setClickFunction(() -> {
					openMenu(player, startingPage + 1);
				}, FunctionCancelType.TRUE, FunctionClickType.ANY);
				inventory.setFunction(53, function);
			} else
				inventory.setItem(53, fws);
			if(c != 1) {
				inventory.setItem(45, new ItemCreator(Material.EMERALD).setDisplayName("§a§lPrevious Page").create());
				EventFunction function = new EventFunction();
				function.setClickFunction(() -> {
					openMenu(player, startingPage - 1);
				}, FunctionCancelType.TRUE, FunctionClickType.ANY);
				inventory.setFunction(45, function);
			} else
				inventory.setItem(45, fws);
			inventory.setItem(49, new ItemCreator(Material.COMPASS).setDisplayName("§aSound").setLore(new String[] {
					"§7Sound: §l" + StringUtils.capitalize(sound.toString().toLowerCase()), 
					"§eMiddle click to " + (config.getBoolean("sound.enable") ? "§cdisable" : "§aenable"),
					"§7Volume: §l" + config.getDouble("sound.volume"),
					"§eLeft click to increase volume",
					"§eRight click to decrease volume",
					"§7Pitch: §l" + config.getDouble("sound.pitch"),
					"§eShift + Left click to increase pitch",
					"§eShift + Right click to decrease pitch"}).create());
			int v0 = Integer.parseInt(config.getString("sound.volume").split("\\.")[0]);
			int v1 = Integer.parseInt(config.getString("sound.volume").split("\\.")[1]);
			int p0 = Integer.parseInt(config.getString("sound.pitch").split("\\.")[0]);
			int p1 = Integer.parseInt(config.getString("sound.pitch").split("\\.")[1]);
			inventory.setFunction(49, new EventFunction().setClickFunction(()-> {
				config.set("sound.enable", config.getBoolean("sound.enable") ? false : true);
				try {
					config.save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				openMenu(player, startingPage);
			}, FunctionCancelType.TRUE, FunctionClickType.MIDDLE).setClickFunction(() -> {
				config.set("sound.volume",v1 == 9 ? Double.parseDouble((v0 + 1) + ".0") : Double.parseDouble(v0 + "." + (v1 + 1)));
				try {
					config.save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				openMenu(player, startingPage);
			}, FunctionCancelType.TRUE, FunctionClickType.LEFT).setClickFunction(() -> {
				config.set("sound.volume", config.getDouble("sound.volume") > 0.0 ? v1 == 0 ? Double.parseDouble((v0 - 1) + ".9") : Double.parseDouble(v0 + "." + (v1 - 1)) : config.getDouble("sound.volume"));
				try {
					config.save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				openMenu(player, startingPage);
			}, FunctionCancelType.TRUE, FunctionClickType.RIGHT).setClickFunction(() -> {
				config.set("sound.pitch", p1 == 9 ? Double.parseDouble((p0 + 1) + ".0") : Double.parseDouble(p0 + "." + (p1 + 1)));
				try {
					config.save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				openMenu(player, startingPage);
			}, FunctionCancelType.TRUE, FunctionClickType.SHIFT_LEFT).setClickFunction(() -> {
				config.set("sound.pitch", config.getDouble("sound.pitch") > 0.0 ? p1 == 0 ? Double.parseDouble((p0 - 1) + ".9") : Double.parseDouble(p0 + "." + (p1 - 1)) : config.getDouble("sound.pitch"));
				try {
					config.save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				openMenu(player, startingPage);
			}, FunctionCancelType.TRUE, FunctionClickType.SHIFT_RIGHT));
			int p = -45;
			for(int i = 1; i <= c; i++) {
				p = p + 45;
			}
			for(int i = 0; i <= 44; i++) {
				Sound s;
				try {
					s = sounds[i+p];
				} catch(ArrayIndexOutOfBoundsException e) {
					break;
				}
				if(sound.equals(s)) {
					inventory.setItem(i, new ItemCreator(Material.LIME_DYE).setDisplayName("§a" + StringUtils.capitalize(sound.toString().toLowerCase())).setLore(new String[] {"§eCurrently set"}).create());
					EventFunction function = new EventFunction();
					function.setClickFunction(() -> {
						player.playSound(player.getLocation(), s, Float.parseFloat(config.getString("sound.volume")), Float.parseFloat(config.getString("sound.pitch")));
					}, FunctionCancelType.TRUE, FunctionClickType.ANY);
					inventory.setFunction(i, function);
				} 
				if(!sound.equals(s)) {
					inventory.setItem(i, new ItemCreator(Material.GRAY_DYE).setDisplayName("§7" + StringUtils.capitalize(s.toString().toLowerCase())).setLore(new String[] {"§eClick to set sound"}).create());
					EventFunction function = new EventFunction();
					function.setClickFunction(() -> {
						config.set("sound.sound", s.toString().toLowerCase());
						try {
							config.save(configFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
						player.sendMessage("§aRegen Sound is now set to §l" + StringUtils.capitalize(s.toString().toLowerCase()));
						player.playSound(player.getLocation(), s, Float.parseFloat(config.getString("sound.volume")), Float.parseFloat(config.getString("sound.pitch")));
						openMenu(player, startingPage);
					}, FunctionCancelType.TRUE, FunctionClickType.ANY);
					inventory.setFunction(i, function);
				}
			}
		}
		player.openInventory(page.getInventoryPage(startingPage).getInventory());
	}

}
