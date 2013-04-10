package com.commandsex.commands;

import com.commandsex.CommandsEX;
import com.commandsex.Language;
import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.WebHelper;
import com.commandsex.interfaces.Command;
import com.commandsex.interfaces.EnableJob;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

import java.io.IOException;

@Builder(name = "define", description = "Lookup the definition of any word", type = "COMMAND")
@Cmd(command = "define", description = "Lookup the definition of any word", usage = "%c% <word> [dictionary id]")
public class Command_define implements Command, EnableJob {

    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if(args.length == 1) {
            WebHelper.Dictionary.Definition def;
            sender.sendMessage(Language.getTranslationForSender(sender, "defineWaiting", args[0]));
            try {
                def = WebHelper.getDefinition(args[0]);
            } catch (IOException e) {
                sender.sendMessage(Language.getTranslationForSender(sender, "commandError", "define", e.getMessage()));
                return true;
            }
            if(def != null)
                sender.sendMessage(Language.getTranslationForSender(sender, "definition", args[0], def.getDefinition(), def.getUrl()));
            else
                sender.sendMessage(Language.getTranslationForSender(sender, "definitionNotFound", args[0]));
            return true;
        }

        if(args.length == 2) {
            int id;

            try{
                id = Integer.parseInt(args[1]);
            } catch(NumberFormatException e) {
                sender.sendMessage(Language.getTranslationForSender(sender, "dictionaryInvalidId", args[1]));
                return true;
            }

            if(WebHelper.Dictionary.getDictionaryFromID(id) == null)
                sender.sendMessage(Language.getTranslationForSender(sender, "dictionaryNotFound"));
            else {
                sender.sendMessage(Language.getTranslationForSender(sender, "defineWaiting", args[0]));
                WebHelper.Dictionary.Definition def;
                try {
                    def = WebHelper.getDefinition(args[0], WebHelper.Dictionary.getDictionaryFromID(id));
                } catch (IOException e) {
                    sender.sendMessage(Language.getTranslationForSender(sender, "commandError", "define", e.getMessage()));
                    return true;
                }
                if(def != null)
                    sender.sendMessage(Language.getTranslationForSender(sender, "definition", args[0], def.getDefinition(), def.getUrl()));
                else
                    sender.sendMessage(Language.getTranslationForSender(sender, "definitionNotFound", args[0]));
            }
            return true;
        }

        return false;
    }

    @Override
    public void onEnable(PluginManager pluginManager) {
        CommandsEX.config.addDefault("defaultDictionary", "DUCK_DUCK_GO");
    }
}
