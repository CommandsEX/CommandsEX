package com.commandsex.commands;

import static com.commandsex.Language._;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Fly;
import com.commandsex.helpers.Players;
import com.commandsex.interfaces.Command;

/**
 * Fly, lets the user fly
 * @author Kezz101
 */
@Builder(name = "fly", description = "Lets the user fly", type = "COMMAND")
@Cmd(command = "fly", description = "Lets the user fly", usage = "%c% [player [on|off]]")
public class Command_fly implements Command {

	@Override
	public boolean run(CommandSender sender, String[] args, String alias) {
		if(!Players.checkIsPlayer(sender)) {
			return true;
		}
		
		if(args == null || args.length == 0) { //Toggle self
			Fly.toggle((Player) sender);
			sender.sendMessage(_(sender, (((Player)sender).getAllowFlight() ? "flySetOn" : "flySetOff"), sender.getName()));
			return true;
		}
		
		if(args.length == 1) { //Toggle another
			if(!Players.hasPermission(sender, Fly.TOGGLE_OTHERS))
				return true;
			
			Player player = Players.getPlayer(args[0], sender);
			if(player != null) {
				Fly.toggle(player);
				sender.sendMessage(_(sender, "flyToggledAnother", player.getName()));
			}
			return true;
		}
		
		if(args.length == 2) { //Set another
			if(Players.hasPermission(sender, Fly.TOGGLE_OTHERS))
				return true;
			
			if(!args[1].toLowerCase().matches("on|off")) {
				Player player = Players.getPlayer(args[0], sender);
				if(player != null) {
					Fly.set(player, args[1].equalsIgnoreCase("on"));
					sender.sendMessage(_(sender, args[1].equalsIgnoreCase("on") ? "flySetOn" : "flySetOff", player.getName()));
				}
			}
			return false;
		}
		
		return false;
	}

}
