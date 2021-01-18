package me.loadxbare.bossbartimer;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Bar {
	
	public int taskID = -1;
	private final Main plugin;
	private BossBar bar;
	public String barTitle;
	public int barDuration;
	public String barDurationMessage;
	public String barColor;
	
	public Bar(Main plugin) {
		this.plugin = plugin;
	}
	
	public void addPlayer(Player player) {
		bar.addPlayer(player);
	}
	
	public BossBar getBar() {
		return bar;
	}
	
	public void createBar() {
		barTitle = "null";
		bar = Bukkit.createBossBar(format(barTitle), BarColor.RED, BarStyle.SOLID);
		bar.setVisible(false);
	}
	
	public void setBarColor(String color) {
		bar.setColor(BarColor.valueOf(color));
		barColor = color;
	}
	
	public void setBarTitle(String title) {
		barTitle = title;
	}
	
	public void setBarDuration(int duration) {
		barDuration = duration;
	}
	
	public void setBarVisibility(boolean value) {
		bar.setVisible(value);
	}
	
	public void start() {
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			int timeInSeconds = barDuration;
			double progress = 1.0;
			double interval = 1.0 / timeInSeconds;
			int daysRemaining = 0;
			int hoursRemaining = 0;
			int minutesRemaining = 0;
			int secondsRemaining = 0;
			
			@Override
			public void run() {
				secondsRemaining = timeInSeconds % 60;
				minutesRemaining = (timeInSeconds / 60) % 60;
				hoursRemaining = (timeInSeconds / (60 * 60)) % 24;
				daysRemaining = (timeInSeconds / (60 * 60)) / 24;
				
				bar.setProgress(progress);
				bar.setTitle(format(barTitle + " &f[" + daysRemaining + "d " + hoursRemaining + "h " + minutesRemaining + "m " + secondsRemaining + "s]"));
				bar.setVisible(true);
				
				progress = progress - interval;
				timeInSeconds -= 1;
				if (progress <= 0) {
					bar.setVisible(false);
					Bukkit.getScheduler().cancelTask(taskID);
				}
				
			}
			
		}, 0, 20);
	}
	
	
	public String format(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}