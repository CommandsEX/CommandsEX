package com.commandsex.commands;

import com.commandsex.Language;
import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.WebHelper;
import com.commandsex.interfaces.Command;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;

/**
 * ListDictionaries, gets a list of all the dictionaries available to a server
 * @author kezz101
 */
@Builder(name = "listdictionaries", description = "Displays a list of all available online dictionaries", type = "COMMAND")
@Cmd(command = "listdictionaries", description = "Displays a list of all available online dictionaries")
public class Command_listdictionaries implements Command {

    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        for(WebHelper.Dictionary dict : WebHelper.Dictionary.values())
            sender.sendMessage(Language.getTranslationForSender(sender, "listDictionaries", WordUtils.capitalize(dict.name().replace("_", " ")), dict.getID() + ""));
        return true;
    }

}
