package me.loadxbare.bossbartimer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener, TabCompleter {
	
	public Bar bar;
	
	@Override
	public void onEnable() {
		this.getCommand("bbt").setTabCompleter(new BBTTabCompleter());
		
		this.getServer().getPluginManager().registerEvents(this, this);
		bar = new Bar(this);
		bar.createBar();
		
		if (Bukkit.getOnlinePlayers().size() > 0) {
			for (Player on : Bukkit.getOnlinePlayers()) {
				bar.addPlayer(on);
			}
		}
	}
	
	@Override
	public void onDisable() {
		bar.getBar().removeAll();
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!(bar.getBar().getPlayers().contains(event.getPlayer()))) {
			bar.addPlayer(event.getPlayer());
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("bbt")) {
			if (!(sender.hasPermission("bbt.run"))) {
				sender.sendMessage("§cYou do not have permission to perform /bbt!");
				return true;
			} else if (args.length == 0) {
				sender.sendMessage("§cUsage: /bbt <color/title/duration/start/stop>");
				return true;
			}
			

			int time = 0;
			int finalTime = 0;
			String durationMessage = "";
			String barTitle = "";
			
			if (args[0].equalsIgnoreCase("color")) {
				if (!(sender.hasPermission("bbt.color"))) {
					sender.sendMessage("§cYou do not have permission to perform /bbt color!");
					return true;
				}
				try {
					bar.setBarColor(args[1].toUpperCase());
					sender.sendMessage("§7[§9BBT§7] Color updated to " + textToColor(args[1].toUpperCase()));
				} catch (Exception e) {
					sender.sendMessage("§cUsage: /bbt color <BLUE/GREEN/PINK/PURPLE/RED/WHITE/YELLOW>");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("title")) {
				if (!(sender.hasPermission("bbt.title"))) {
					sender.sendMessage("§cYou do not have permission to perform /bbt title!");
					return true;
				}
				try {
					for (int i = 1; i < args.length; i++) {
						barTitle += args[i] + " ";
					}
					bar.setBarTitle(barTitle);
					sender.sendMessage("§7[§9BBT§7] Title updated to §f" + bar.format(barTitle));
				} catch (Exception e) {
					sender.sendMessage("§cUsage: /bbt title <title>");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("duration")) {
				if (!(sender.hasPermission("bbt.duration"))) {
					sender.sendMessage("§cYou do not have permission to perform /bbt duration!");
					return true;
				}
				try {
					int i = 1;
					do {
						time = Integer.parseInt(args[i].substring(0, args[i].length() - 1));
						if (args[i].contains("d")) {
							time *= 86400;
						} else if (args[i].contains("h")) {
							time *= 3600;
						} else if (args[i].contains("m")) {
							time *= 60;
						} else if (args[i].contains("s")) {
							time = time;
						}
						
						durationMessage += args[i];
						finalTime += time;
						i++;
					} while (i < args.length);
					bar.setBarDuration(finalTime);
					sender.sendMessage("§7[§9BBT§7] Duration updated to §6" + durationMessage);
					bar.barDurationMessage = durationMessage;
				} catch (Exception e) {
					sender.sendMessage("§cUsage: /bbt duration <duration>");
					sender.sendMessage("§7Example: /bbt duration 1h 30m");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("start")) {
				if (!(sender.hasPermission("bbt.start"))) {
					sender.sendMessage("§cYou do not have permission to perform /bbt start!");
					return true;
				}
				if (bar.barDuration == 0) {
					sender.sendMessage("§cPlease set a valid duration first!");
					return true;
				} else if (bar.barTitle == "null") {
					sender.sendMessage("§cPlease set a valid title first!");
					return true;
				}
				bar.start();
				sender.sendMessage("§7[§9BBT§7] BossBar Timer started!");
				return true;
			} else if (args[0].equalsIgnoreCase("stop")) {
				if (!(sender.hasPermission("bbt.stop"))) {
					sender.sendMessage("§cYou do not have permission to perform /bbt stop!");
					return true;
				}
				Bukkit.getScheduler().cancelTask(bar.taskID);
				bar.setBarVisibility(false);
				sender.sendMessage("§7[§9BBT§7] Any active BossBar Timers have been stopped!");
				return true;
			} else if (args[0].equalsIgnoreCase("info")) {
				if (!(sender.hasPermission("bbt.info"))) {
					sender.sendMessage("§cYou do not have permission to perform /bbt info!");
					return true;
				}
				String infoMessage = "§9§nBossBar Timer info";
				try {
					infoMessage += "\n§7BossBar Color is set to: " + textToColor(bar.barColor);
				} catch (Exception e) {
					sender.sendMessage("§cPlease set a valid color first!");
					return true;
				}
				if (bar.barDurationMessage == null) {
					sender.sendMessage("§cPlease set a valid duration first!");
					return true;
				} else {
					infoMessage += "\n§7BossBar Duration is set to: §6" + bar.barDurationMessage;
				}
				if (bar.barTitle == "null") {
					sender.sendMessage("§cPlease set a valid title first!");
					return true;
				} else {
					infoMessage += "\n§7BossBar Title is set to: §f" + bar.format(bar.barTitle);
					sender.sendMessage(infoMessage);
					return true;
				}
			} else if (args[0].equalsIgnoreCase("help")) {
				if (!(sender.hasPermission("bbt.help"))) {
					sender.sendMessage("§cYou do not have permission to perform /bbt help!");
					return true;
				}
				sender.sendMessage("§9§nBossBar Timer commands\n"
								+ "§9/bbt color §7- Changes the colour of the BossBar\n"
								+ "§9/bbt duration §7- Sets the duration of the BossBar Timer\n"
								+ "§9/bbt help §7- Displays this commands list\n"
								+ "§9/bbt info §7- Displays the current configuration for the BossBar Timer\n"
								+ "§9/bbt start §7- Starts the BossBar Timer\n"
								+ "§9/bbt stop §7- Stops any active BossBar Timers\n"
								+ "§9/bbt title §7- Changes the bar title for the BossBar");
				return true;
			}
		}
		return false;
	}
	
	public String textToColor(String text) {
		
		if (text.equals("BLUE")) {
			return "§9" + text;
		} else if (text.equals("GREEN")) {
			return "§a" + text;
		} else if (text.equals("PINK")) {
			return "§d" + text;
		} else if (text.equals("PURPLE")) {
			return "§5" + text;
		} else if (text.equals("RED")) {
			return "§c" + text;
		} else if (text.equals("WHITE")) {
			return "§f" + text;
		} else if (text.equals("YELLOW")) {
			return "§e" + text;
		} else {
			return text;
		}
	}
}
//  TODO
//  /bbt help - Display commands list
//  /bbt pause - Pauses current timer
//  /bbt resume - Resumes current timer