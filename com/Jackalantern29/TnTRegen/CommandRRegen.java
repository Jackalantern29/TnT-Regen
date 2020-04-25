package com.Jackalantern29.TnTRegen;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.Jackalantern29.TnTRegen.Explosion.ExplosionManager;
import com.google.common.collect.Iterables;

public class CommandRRegen implements CommandExecutor, TabCompleter {
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("rregen")) {
			if(!sender.hasPermission("tntregen.command.rregen")) {
				sender.sendMessage(ConfigManager.getNoPermMessage());
				return true;
			}
			if(args.length == 0) {
				sender.sendMessage("§cUsage: §f/rregen §c<§fall§c|§fnear§c|§frecent§c|§ffirst§c> [§f-f[entityTypes...]§c|§f-r[radius]§c|§f-l[worldName,x,y,z] §cor §f-l[onlinePlayer]§c|§f-c[count]§c|§f-i§c]...");
				sender.sendMessage("§cFlags:");
				sender.sendMessage("  §f-f[entityTypes...]§c: Filters explosions from entities. Use ',' in between entities to filter multiple entities.");
				sender.sendMessage("  §f-r[radius]§c: Filters explosions within a radius.");
				sender.sendMessage("  §f-l[worldName,x,y,z] OR -l[onlinePlayer]§c: Sets the location the execution point.");
				sender.sendMessage("  §f-c[count]§c: Performs the tasks X amount of times, where X = count.");
				sender.sendMessage("  §f-i§c: Gets information from explosions retrieved. Explosions will not regenerate if this flag is used.");
				return true;
			} else if(args.length == 1) {
				String sub = args[0];
				sub1(sender, sub, null, null, -1, 1, false);
				return true;
			} else if(args.length > 1) {
				List<EntityType> filteredTypes = null;
				Location location = null;
				double radius = -1;
				int count = 1;
				boolean info = false;
				for(int i = 1; i < args.length; i++) {
					String sub = args[i];
					if(sub.substring(0, 2).equalsIgnoreCase("-f") || sub.substring(0, 2).equalsIgnoreCase("-r") || sub.substring(0, 2).equalsIgnoreCase("-l") || sub.substring(0, 2).equalsIgnoreCase("-c") || sub.substring(0, 2).equalsIgnoreCase("-i")) {
						if(sub.length() >= 3 && sub.substring(2, 3).equals("[") && sub.substring(sub.length()-1, sub.length()).equals("]")) {
							String value = sub.substring(3).replace("]", "");
							if(value.equals("")) {
								sender.sendMessage("§cMissing values for flag §l" + sub.substring(0, 2) + "§c.");
								return true;
							}
							if(sub.substring(0, 2).equalsIgnoreCase("-f")) {
								filteredTypes = new ArrayList<>();
								if(value.contains(",")) {
									for(String type : value.split(",")) {
										try {
											filteredTypes.add(EntityType.valueOf(type.toUpperCase()));
										} catch(IllegalArgumentException e) {
											sender.sendMessage("§cType §l" + type + "§c does not exist.");
											return true;
										}
									}
								} else {
									try {
										filteredTypes.add(EntityType.valueOf(value.toUpperCase()));
									} catch(IllegalArgumentException e) {
										sender.sendMessage("§cType §l" + value + "§c does not exist.");
										return true;
									}
								}
							} else if(sub.substring(0, 2).equalsIgnoreCase("-r")) {
								if(!(sender instanceof Player)) {
									for(String s : args) {
										if(!s.contains("-l")) {
											sender.sendMessage("§cYou must include the flag -l with -r when on console.");
											return true;
										}
									}
								}
								try {
									radius = Double.parseDouble(value);
									if(radius < 0.0) {
										sender.sendMessage("§cRadius cannot be less than 0.0");
										return true;
									}
									if(location == null)
										location = ((Player)sender).getLocation();
								} catch(NumberFormatException e) {
									sender.sendMessage("§cValue §l" + sub.replace(sub.subSequence(0, 3), "").replace("]", "") + "§c is not a number.");
								}
							} else if(sub.substring(0, 2).equalsIgnoreCase("-l")) {
								if(value.contains(",")) {
									if(value.split(",").length < 4) {
										sender.sendMessage("§cYou do not have enough values. -l[worldName,x,y,z]");
										return true;
									} else if(value.split(",").length > 4) {
										sender.sendMessage("§cYou have too many values. -l[worldName,x,y,z]");
										return true;
									} else {
										World world;
										double x;
										double y;
										double z;
										world = Bukkit.getWorld(value.split(",")[0]);
										try {
											x = Double.parseDouble(value.split(",")[1]);
										} catch(NumberFormatException e) {
											sender.sendMessage("§cLocation value X §l" + value.split(",")[1] + "§c is not a number.");
											return true;
										}
										try {
											y = Double.parseDouble(value.split(",")[2]);
										} catch(NumberFormatException e) {
											sender.sendMessage("§cLocation value Y §l" + value.split(",")[2] + "§c is not a number.");
											return true;
										}
										try {
											z = Double.parseDouble(value.split(",")[3]);
										} catch(NumberFormatException e) {
											sender.sendMessage("§cLocation value Z §l" + value.split(",")[3] + "§c is not a number.");
											return true;
										}
										location = new Location(world, x, y, z);
									}	
								} else {
									Player target = Bukkit.getPlayerExact(value) != null ? Bukkit.getPlayerExact(value) : null;
									if(target != null) {
										location = new Location(target.getWorld(), target.getLocation().getBlockX(), target.getLocation().getBlockY(), target.getLocation().getBlockZ());
									} else {
										sender.sendMessage("§cCoult not fetch location from Player §l" + value + "§c. Player is not online.");
										return true;
									} 
								}
							} else if(sub.substring(0, 2).equalsIgnoreCase("-c")) {
								try {
									count = Integer.parseInt(value);
									if(count < 1) {
										sender.sendMessage("§cCount cannot be less than 1.");
										return true;
									}
								} catch(NumberFormatException e) {
									sender.sendMessage("§cValue §l" + value + "§c is not a number.");
									return true;
								}
							}
						} else if(sub.substring(0, 2).equalsIgnoreCase("-i")) {
							info = true;
						} else if(sub.length() <= 2 || (sub.length() >= 3 && !sub.substring(2, 3).equals("["))) {
							sender.sendMessage("§cMissing opening square bracket '[' for §l" + sub + ".");
							return true;
						} else if(!sub.substring(sub.length()-1, sub.length()).equals("]")) {
							sender.sendMessage("§cMissing closing square bracket ']' for §l" + sub + ".");
							return true;
						}
					}
				}
				sub1(sender, args[0], filteredTypes, location, radius, count, info);
				return true;
			}
		}
		return false;
	}
	
	private List<ExplosionManager> sub1(CommandSender sender, String sub, List<EntityType> filteredTypes, Location location, double radius, int count, boolean info) {
		if(sub.equalsIgnoreCase("all")) {
			if(info) {
				int amount = ExplosionManager.getExplosions(filteredTypes, location, radius).size();
				sender.sendMessage("§aCurrently §n" + amount + "§a explosions are in the process of regenerating.");
			} else {
				if(ExplosionManager.getExplosions(filteredTypes, location, radius).isEmpty())
					sender.sendMessage("§aThere are no explosions currently being regenerated" + (filteredTypes == null ? "." : " for filtered types."));
				else {
					int amount = ExplosionManager.getExplosions(filteredTypes, location, radius).size();
					ExplosionManager.regenerateAll(filteredTypes, location, radius);
					sender.sendMessage("§aSuccessfully regenerated §n" + amount + "§a explosion" + (amount != 1 ? "s" : "") + ".");
				}
			}
		} else if(sub.equalsIgnoreCase("near")) {
			if(!(sender instanceof Player)) {
				if(location == null) {
					sender.sendMessage("§cYou must use the -l flag when using this command on a console.");
					return null;
				}
			} else {
				if(location == null)
					location = ((Player)sender).getLocation();
			}
			for(int i = 0; i < count; i++) {
				double distance = 0;
				ExplosionManager ex = null;
				if(ExplosionManager.getExplosions(filteredTypes, location, radius).isEmpty()) {
					sender.sendMessage("§aThere are no explosions currently being regenerated" + (filteredTypes == null ? "." : " for filtered types."));
					break;
				}
				else {
					for(ExplosionManager explosion : new ArrayList<>(ExplosionManager.getExplosions(filteredTypes, location, radius))) {
						if(explosion.getLocation().getWorld().equals(location.getWorld())) {
							if(ex == null) {
								distance = location.distance(explosion.getLocation());
								ex = explosion;
							} else {
								if(distance >= location.distance(explosion.getLocation())) {
									distance = location.distance(explosion.getLocation());
									ex = explosion;
								}
							}
						}
					}
					ex.regenerate();
				}	
			}
			sender.sendMessage("§aSuccesfully regenerated nearest explosion" + (count == 1 ? "" : "s") + " from §nX: " + location.getBlockX() + "§a, §nY: " + location.getBlockY() + "§a, §nZ: " + location.getBlockZ() + "§a.");
		} else if(sub.equalsIgnoreCase("recent")) {
			for(int i = 0; i < count; i++) {
				if(ExplosionManager.getExplosions(filteredTypes, location, radius).isEmpty()) {
					sender.sendMessage("§aThere are no explosions currently being regenerated" + (filteredTypes == null ? "." : " for filtered types."));
					break;
				} else {
					ExplosionManager explosion = Iterables.getLast(ExplosionManager.getExplosions(filteredTypes, location, radius));
					explosion.regenerate();
					sender.sendMessage("§aSuccessfully regenerated recent explosion at §nX: " + explosion.getLocation().getBlockX() + "§a, §nY: " + explosion.getLocation().getBlockY() + "§a, §nZ: " + explosion.getLocation().getBlockZ() + "§a.");
				}
			}
		} else if(sub.equalsIgnoreCase("first")) {
			for(int i = 0; i < count; i++) {
				if(ExplosionManager.getExplosions(filteredTypes, location, radius).isEmpty()) {
					sender.sendMessage("§aThere are no explosions currently being regenerated" + (filteredTypes == null ? "." : " for filtered types."));
					break;
				} else {
					ExplosionManager explosion = Iterables.getFirst(ExplosionManager.getExplosions(filteredTypes, location, radius), null);
					explosion.regenerate();
					sender.sendMessage("§aSuccessfully regenerated first explosion at §nX: " + explosion.getLocation().getBlockX() + "§a, §nY: " + explosion.getLocation().getBlockY() + "§a, §nZ: " + explosion.getLocation().getBlockZ() + "§a.");
				}
			}
		} else {
			sender.sendMessage("§cThat is not a valid sub command.");
			sender.sendMessage("§cUsage: §f/rregen §c<§fall§c|§fnear§c|§frecent§c|§ffirst§c>");
		}
		return null;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		return list;
	}
}
