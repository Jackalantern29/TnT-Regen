package com.Jackalantern29.TnTRegen;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Jackalantern29.TnTRegen.Inventory.InventoryManager;

public class CommandRConfig implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("rconfig")) {
			if(sender instanceof Player && !sender.hasPermission("tntregen.command.rconfig")) {
				sender.sendMessage(ConfigManager.getNoPermMessage());
				return true;
			}
			ConfigManager.reloadConfig();
			InventoryManager.unregisterInventories();
			InventoryManager.updateInventories(null);
			for(Player player : Bukkit.getOnlinePlayers())
				InventoryManager.updateInventories(player.getUniqueId());
			ParticlePresetManager.reloadConfig();
			sender.sendMessage("§aAll files has been reloaded.");
			sender.sendMessage("§aInventories for /rparticle & /rsound has been updated.");
			return true;
		}
		return false;
	}

}
