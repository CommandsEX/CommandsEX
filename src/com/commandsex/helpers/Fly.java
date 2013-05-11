package com.commandsex.helpers;

import static com.commandsex.Language._;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import com.commandsex.interfaces.EnableJob;

public class Fly implements EnableJob, Listener {
	public static final Permission TOGGLE_OTHERS = new Permission("cex.fly.others");
	public static final Permission SAVE = new Permission("cex.fly.save");
		
	@Override
	public void onEnable(PluginManager pluginManager) {
		pluginManager.addPermission(TOGGLE_OTHERS);
		pluginManager.addPermission(SAVE);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		Material block = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
		if(e.getPlayer().hasPermission(SAVE)) {
			if(block == Material.AIR) {
				set(player, true);
				player.sendMessage(_(player, "flySavedAir"));
			} else if(block == Material.LAVA) {
				Block b = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
				while(b.getRelative(BlockFace.UP).getType() == Material.LAVA) {
					b = b.getRelative(BlockFace.UP);
				}
				player.teleport(b.getRelative(BlockFace.UP).getLocation(), TeleportCause.PLUGIN);
				player.sendMessage(_(player, "flySavedAir"));
			}
		}
	}
	
	public static void toggle(Player player) {
		set(player, !player.getAllowFlight());
	}
	
	public static void set(Player player, boolean on) {
		player.setAllowFlight(on);
		if(!on)
			player.setFlying(false);
	}
	
}
