package com.Jackalantern29.TnTRegen;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.Jackalantern29.TnTRegen.Explosion.ExplosionManager.ExplosionType;
import com.Jackalantern29.TnTRegen.Inventory.InventoryManager;
import com.Jackalantern29.TnTRegen.Inventory.InventoryManager.TypeCommand;

public class CommandRParticleSound implements TabExecutor {	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("rparticle") || cmd.getName().equalsIgnoreCase("rsound")) {
			if(!sender.hasPermission("tntregen.command." + cmd.getName().toLowerCase())) {
				sender.sendMessage(ConfigManager.getNoPermMessage());
				return true;
			}
			TypeCommand command = TypeCommand.valueOf(cmd.getName().substring(1, cmd.getName().length()).toUpperCase());
			if(!(sender instanceof Player)) {
				sender.sendMessage("§cOnly players can use this command.");
				return true;
			}
			Player player = (Player)sender;
			if(args.length == 0) {
				if(!(sender instanceof Player)) {
					sender.sendMessage("§cOnly players can use this command.");
				} else {
					if(ConfigManager.isPlayerSettingsEnabled()) {
						sender.sendMessage("§aOpening /" + cmd.getName().toLowerCase() + " GUI.");
						player.openInventory(InventoryManager.getInventoryCategory(player.getUniqueId(), command));
					} else if(player.hasPermission("tntregen.command.r" + command.toString().toLowerCase() + ".args")) {
						sender.sendMessage("§aOpening /" + cmd.getName().toLowerCase() + " GUI.");
						player.openInventory(InventoryManager.getInventoryCategory(null, command));
					} else {
						player.sendMessage(ConfigManager.getNoPermMessage());
					}
				}
				return true;
			} else if(args.length <= 5) {
				if(!sender.hasPermission("tntregen.command.r" + command.toString().toLowerCase() + ".args")) {
					sender.sendMessage(ConfigManager.getNoPermMessage());
					return true;
				}
				String sub1 = args[0].toLowerCase();
				String sub2 = args.length >= 2 ? args[1].toLowerCase() : "";
				String sub3 = args.length >= 3 ? args[2].toLowerCase() : "";
				List<String> subs3 = new ArrayList<>();
				if(sub1.equals("server")) {
					if(!sub2.equals("")) {
						if(sub2.equals("help")) {
							sender.sendMessage("§cUsage: §f/" +  cmd.getName().toLowerCase() + " §c[§fentity§c|§fblock§c] [§ftype§c]");
							return true;
						} else if(sub2.equals("entity") || sub2.equals("block")) {
							ExplosionType type = ExplosionType.valueOf(sub2.toUpperCase());
							if(sub3.equals("")) {
								((Player)sender).openInventory(InventoryManager.getSubCategory(null, command, type));
								return true;
							}
							if(type == ExplosionType.ENTITY) {
								for(EntityType entity : ConfigManager.getSupportedEntities()) {
									subs3.add(entity.toString().toLowerCase());
								}
							} else if(type == ExplosionType.BLOCK) { 
								for(Material material : ConfigManager.getSupportedBlocks()) {
									subs3.add(material.toString().toLowerCase());
								}
							}
							if(!sub3.equals("")) {
								if(subs3.contains(sub3)) {
									if(InventoryManager.getSubCatType(null, command, null, type, sub3, 1) != null)
										((Player)sender).openInventory(InventoryManager.getSubCatType(null, command, null, type, sub3, 1));
									else
										sender.sendMessage("§cAn inventory for that sub category was not created.");
									return true;
								} else {
									sender.sendMessage("§cType '" + sub3 + "' is not a supported " + type.toString().toLowerCase() + ".");
									return true;
								}
							}
						} else {
							sender.sendMessage("§aOpening /" + cmd.getName().toLowerCase() + " GUI.");
							sender.sendMessage("§cThat is not a valid category. Can be either 'entity' or 'block'.");
							return true;
						}
					} else {
						player.openInventory(InventoryManager.getInventoryCategory(null, command));
						return true;
					}
				} else {
					player.openInventory(InventoryManager.getInventoryCategory(null, command));
					return true;
				}
			} else {
				sender.sendMessage("§cUsage: §f/" +  cmd.getName().toLowerCase() + " §c[§fentity§c|§fblock§c] [§ftype§c] [§fpage|set§c] <§fpage|" + command.toString().toLowerCase() + "§c>.");
				return true;
			}	
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		if(cmd.getName().equalsIgnoreCase("rparticle") || cmd.getName().equalsIgnoreCase("rsound")) {
			if(sender.hasPermission("tntregen.command." + cmd.getName().toLowerCase() + ".args")) {	
				if(args.length == 1) {
					list.add("server");
					return StringUtil.copyPartialMatches(args[0], list, new ArrayList<>(list.size()));
				} else if(args.length == 2) {
					list.add("block");
					list.add("entity");
					return StringUtil.copyPartialMatches(args[1], list, new ArrayList<>(list.size()));
				} else if(args.length == 3) {
					String sub = args[0];
					String sub1 = args[1];
					if(sub1.equalsIgnoreCase("entity")) {
						if(sub.equalsIgnoreCase("server")) {
							for(EntityType type : ConfigManager.getSupportedEntities()) {
								list.add(type.toString().toLowerCase());
							}
						}
					} else if(sub1.equalsIgnoreCase("block")) {
						if(sub.equalsIgnoreCase("server")) {
							for(Material material : ConfigManager.getSupportedBlocks()) {
								list.add(material.toString().toLowerCase());
							}
						}
					}
					return StringUtil.copyPartialMatches(args[2], list, new ArrayList<String>(list.size()));
				}
			}
		}
		return list;
	}
	
	
}
