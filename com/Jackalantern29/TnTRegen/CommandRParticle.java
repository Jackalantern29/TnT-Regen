package com.Jackalantern29.TnTRegen;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
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

public class CommandRParticle implements CommandExecutor {
	InventoryPageSystem page = new InventoryPageSystem();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("rparticle")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("Only players can use this command.");
				return true;
			}
			if(sender.hasPermission("tntregen.command.rparticle")) {
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
		
		Particle[] particles = Particle.values();
		Particle bRParticle = Particle.valueOf(config.getString("particles.blockRegen.particle").toUpperCase());
		Particle bTRParticle = Particle.valueOf(config.getString("particles.blockToBeRegen.particle").toUpperCase());
		int maxPagesForParticles = Integer.parseInt(String.valueOf((particles.length / 45.0) + 1.0).split("\\.")[0]);
		if(String.valueOf((particles.length / 45.0) + 1.0).split("\\.")[1].equals("0"))
			maxPagesForParticles--;
		if(page.getTotalPages() == 0) {
			for(int i = 1; i <= maxPagesForParticles; i++) {
				page.createPage(new InventoryManager(page, 9*6, "§cParticles [" + i + "]"));				
			}
		}
		int c = 0;
		int dc = 0;
		for(InventoryManager inventory : page.getInventoryPages()) {
			c++;
			inventory.setCancelled(true);
			inventory.cancelBottomInventory(true);
			inventory.fillSlots(new ItemCreator(Material.PAPER).setDisplayName(" ").create(), new EventFunction().setClickFunction(() -> {}, FunctionCancelType.TRUE, FunctionClickType.ANY), new int[] {46, 47, 48, 50, 51, 52});
			ItemStack fws = new ItemCreator(Material.FIREWORK_STAR).setDisplayName(" ").create();
			inventory.setFunction(fws, new EventFunction().setClickFunction(()->{}, FunctionCancelType.TRUE, FunctionClickType.ANY));
			if(c < maxPagesForParticles) {
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
			inventory.setItem(49, new ItemCreator(Material.COMPASS).setDisplayName("§aParticles").setLore(new String[] {
					"§7blockRegen: §l" + StringUtils.capitalize(bRParticle.toString().toLowerCase()), 
					"§eLeft click to " + (config.getBoolean("particles.blockRegen.enable")  ? "§cdisable" : "§aenable"),
					"§7blockToBeRegen: §l" + StringUtils.capitalize(bTRParticle.toString().toLowerCase()),
					"§eRight click to " + (config.getBoolean("particles.blockToBeRegen.enable") ? "§cdisable" : "§aenable")}).create());
			inventory.setFunction(49, new EventFunction().setClickFunction(()-> {
				config.set("particles.blockRegen.enable", config.getBoolean("particles.blockRegen.enable") ? false : true);
				try {
					config.save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				openMenu(player, startingPage);
			}, FunctionCancelType.TRUE, FunctionClickType.LEFT).setClickFunction(() -> {
				config.set("particles.blockToBeRegen.enable", config.getBoolean("particles.blockToBeRegen.enable") ? false : true);
				try {
					config.save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				openMenu(player, startingPage);
			}, FunctionCancelType.TRUE, FunctionClickType.RIGHT));
			int s = -45;
			for(int i = 1; i <= c; i++) {
				s = s + 45;
			}
			int cc = 0;
			for(int i = 0; i <= 44; i++) {
				Particle p;
				try {
					p = particles[i+s+dc];
					if(p.getDataType() != Void.class) {
						i--;
						s++;
						cc++;
						continue;
					}
				} catch(ArrayIndexOutOfBoundsException e) {
					break;
				}
				if(bRParticle.equals(p)) {
					inventory.setItem(i, new ItemCreator(Material.LIME_DYE).setDisplayName("§a" + StringUtils.capitalize(bRParticle.toString().toLowerCase())).setLore(new String[] {"§eCurrently set for blockRegen", "§eRight click to set blockToBeRegen"}).create());
					EventFunction function = new EventFunction();
					function.setClickFunction(() -> {
						config.set("particles.blockToBeRegen.particle", p.toString().toLowerCase());
						try {
							config.save(configFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
						player.sendMessage("§bblockToBeRegen particle is now set to §l" + StringUtils.capitalize(p.toString().toLowerCase()));
						openMenu(player, startingPage);
					}, FunctionCancelType.TRUE, FunctionClickType.RIGHT);
					function.setClickFunction(() -> {}, FunctionCancelType.TRUE, FunctionClickType.ANY);
					inventory.setFunction(i, function);
				} 
				if(bTRParticle.equals(p)) {
					if(bTRParticle.equals(bRParticle)) {
						inventory.setItem(i, new ItemCreator(Material.MAGENTA_DYE).setDisplayName("§d" + StringUtils.capitalize(bTRParticle.toString().toLowerCase())).setLore(new String[] {"§eCurrently set for blockRegen", "§eCurrently set for blockToBeRegen"}).create());
						EventFunction function = new EventFunction();
						function.setClickFunction(() -> {}, FunctionCancelType.TRUE, FunctionClickType.ANY);
						inventory.setFunction(i, function);
					} else {
						inventory.setItem(i, new ItemCreator(Material.LIGHT_BLUE_DYE).setDisplayName("§b" + StringUtils.capitalize(bTRParticle.toString().toLowerCase())).setLore(new String[] {"§eLeft click to set blockRegen", "§eCurrently set for blockToBeRegen"}).create());
						EventFunction function = new EventFunction();
						function.setClickFunction(() -> {
							config.set("particles.blockRegen.particle", p.toString().toLowerCase());
							try {
								config.save(configFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
							player.sendMessage("§ablockRegen particle is now set to §l" + StringUtils.capitalize(p.toString().toLowerCase()));
							openMenu(player, startingPage);
						}, FunctionCancelType.TRUE, FunctionClickType.LEFT);
						function.setClickFunction(() -> {}, FunctionCancelType.TRUE, FunctionClickType.ANY);
						inventory.setFunction(i, function);
					}
				} 
				if(!bRParticle.equals(p) && !bTRParticle.equals(p)) {
					inventory.setItem(i, new ItemCreator(Material.GRAY_DYE).setDisplayName("§7" + StringUtils.capitalize(p.toString().toLowerCase())).setLore(new String[] {"§eLeft click to set blockRegen", "§eRight click to set blockToBeRegen"}).create());
					EventFunction function = new EventFunction();
					function.setClickFunction(() -> {
						config.set("particles.blockRegen.particle", p.toString().toLowerCase());
						try {
							config.save(configFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
						player.sendMessage("§ablockRegen particle is now set to §l" + StringUtils.capitalize(p.toString().toLowerCase()));
						openMenu(player, startingPage);
					}, FunctionCancelType.TRUE, FunctionClickType.LEFT);
					function.setClickFunction(() -> {
						config.set("particles.blockToBeRegen.particle", p.toString().toLowerCase());
						try {
							config.save(configFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
						player.sendMessage("§bblockToBeRegen particle is now set to §l" + StringUtils.capitalize(p.toString().toLowerCase()));
						openMenu(player, startingPage);
					}, FunctionCancelType.TRUE, FunctionClickType.RIGHT);
					function.setClickFunction(() -> {}, FunctionCancelType.TRUE, FunctionClickType.ANY);
					inventory.setFunction(i, function);
				}
			}
			dc = dc + cc;
		}
		player.openInventory(page.getInventoryPage(startingPage).getInventory());
	}

}
