package com.commandsex.helpers;

import com.commandsex.CommandsEX;
import com.commandsex.database.Database;
import com.commandsex.database.MySqlDatabase;
import com.commandsex.database.SqLiteDatabase;
import com.commandsex.interfaces.EnableJob;
import org.bukkit.plugin.PluginManager;

public class Warps implements EnableJob {

    private Database database;

    @Override
    public void onEnable(PluginManager pluginManager) {
        database = CommandsEX.database;
        database.getExecutor().execute("CREATE TABLE IF NOT EXISTS " + database.getPrefix() + "warps (id integer NOT NULL" + (database instanceof MySqlDatabase ? "AUTO_INCREMENT" : "") + ", name varchar(50) NOT NULL, owner varchar(50) NOT NULL" + (database instanceof SqLiteDatabase ? " COLLATE 'NOCASE'" : "") + ", private BOOLEAN NOT NULL, world VARCHAR(32) NOT NULL, x DOUBLE NOT NULL, y DOUBLE NOT NULL, z DOUBLE NOT NULL, PRIMARY_KEY (id), UNIQUE " + (database instanceof MySqlDatabase ? "KEY name " : "") + "(name))" + (database instanceof MySqlDatabase ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='stores warps' AUTO_INCREMENT=1" : ""));
    }
}
