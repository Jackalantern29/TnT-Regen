package com.Jackalantern29.TnTRegen;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class CommandRExplode implements TabExecutor {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String lavel, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("rexplode")) {
			if(!sender.hasPermission("tntregen.command.rexplode")) {
				sender.sendMessage(ConfigManager.getNoPermMessage());
				return true;
			}
			if(!(sender instanceof Player)) {
				sender.sendMessage("[TnTRegen] Only players can use this command.");
				return true;
			}
			Player player = (Player)sender;
			if(args.length == 0) {
				player.sendMessage("§aCreating explosion.");
				player.getWorld().createExplosion(player.getLocation(), 4f);
				return true;
			} else if(args.length >= 1) {
				float value = 0.0f;
				try {
					value = Float.parseFloat(args[0]);
				} catch(NumberFormatException e) {
					player.sendMessage("§cError: '" + args[0] + "' is not an accept argument.");
					return true;
				}
				player.sendMessage("§aCreating explosion.");
				player.getWorld().createExplosion(player.getLocation(), Float.parseFloat(value + ""));
				return true;
			}
			
		}
		return false;
	}

}
