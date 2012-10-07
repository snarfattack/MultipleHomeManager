package com.gmail.scottmwoodward.multiplehomemanagement;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHome {

	private HelperArgumentChecker checker;
	private HelperTeleport teleporter;
	private String perm;
	private MultipleHomeManagement plugin;
	
	public CommandHome(MultipleHomeManagement plugin){
		this.plugin = plugin;
		this.checker = new HelperArgumentChecker();
		this.teleporter = new HelperTeleport(plugin);
	}
	
	public boolean execute(CommandSender sender, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			int homeNumber = checker.check(1, args, player, "Usage: /home <home number>" );
			if(homeNumber<1){
				return true;
			}
			perm = getPerm(homeNumber);
			if(player.hasPermission(perm)&&player.hasPermission("multihomes.home")){
				if(plugin.getHomeCharge()){
					if(plugin.getUseEcon()){
						if(!plugin.getEconHandler().checkBalance(player, plugin.getHomeCost())){
							player.sendMessage("You do not have enough money to teleport to a home");
							return true;
						}
					}
				}
				if(plugin.getPending().contains(player.getDisplayName())){
					player.sendMessage("You already have a pending teleport");
					return true;
				}
				teleporter.teleport(player, homeNumber);
				return true;
			}			
			else{
				player.sendMessage("You do not have permission to teleport to home number "+args[0]);
				return true;
			}
			
			
				
		}
		else{
			sender.sendMessage("You must be logged in, as a player, to teleport to a home");
			return true;
		}
	}
	
	private String getPerm(int homeNumber){
		double tier = (double)homeNumber/(double)plugin.getTelePerTier();
		return "multihomes.sethome.tier"+String.valueOf((int)Math.ceil(tier));
	}
}
