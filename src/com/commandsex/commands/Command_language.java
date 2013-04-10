package com.commandsex.commands;

import com.commandsex.Language;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Players;
import com.commandsex.helpers.Utils;
import com.commandsex.interfaces.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Cmd(command = "language", description = "Set a players language", aliases = "lang, setlang", permissionDefault = "ALL", usage = "%c% OR %c% [player] <language>")
public class Command_language implements Command {
    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if (args.length != 1 && args.length != 2){
            sender.sendMessage(Language.getTranslationForSender(sender, "availableLanguages", Utils.join(Language.getAvailableLanguages(), ChatColor.GOLD + ", ", ChatColor.GOLD + " & ")));
            return true;
        }

        Player target;
        String language;

        if (args.length == 1){
            if (!Players.checkIsPlayer(sender)){
                return true;
            }

            target = (Player) sender;
            language = args[0];
        } else {
            target = Players.getPlayer(args[0]);

            if (target == null){
                return true;
            }

            language = args[1];
        }

        if (!Language.isLanguageAvailable(language)){
            sender.sendMessage(Language.getTranslationForSender(sender, "invalidLanguage", language));
            return true;
        }

        String tName = target.getName();
        Language.setUserLanguage(tName, language);

        if (sender == target){
            sender.sendMessage(Language.getTranslationForSender(sender, "languageSet", language));
        } else {
            sender.sendMessage(Language.getTranslationForSender(sender, "languageSetOther", tName, language));
            target.sendMessage(Language.getTranslationForSender(target, "languageSetOtherNotify", sender.getName(), language));
        }

        return true;
    }
}
