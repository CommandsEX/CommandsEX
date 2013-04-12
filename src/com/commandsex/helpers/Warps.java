package com.commandsex.helpers;

import com.commandsex.CommandsEX;
import com.commandsex.database.Database;
import com.commandsex.database.MySqlDatabase;
import com.commandsex.database.SqLiteDatabase;
import com.commandsex.interfaces.EnableJob;
import org.bukkit.plugin.PluginManager;
import org.jooq.Record;
import org.jooq.Result;

public class Warps implements EnableJob {



    private Database database;

    @Override
    public void onEnable(PluginManager pluginManager) {
        database = CommandsEX.database;
        database.getExecutor().execute("CREATE TABLE IF NOT EXISTS " + database.getPrefix() + "warps (id integer NOT NULL" + (database instanceof MySqlDatabase ? " AUTO_INCREMENT" : " PRIMARY KEY") + ", name varchar(50) NOT NULL, owner varchar(50) NOT NULL" + (database instanceof MySqlDatabase ? "" : " COLLATE 'NOCASE'") + ", private BOOLEAN NOT NULL, world VARCHAR(32) NOT NULL, x DOUBLE NOT NULL, y DOUBLE NOT NULL, z DOUBLE NOT NULL" + (database instanceof MySqlDatabase ? ", PRIMARY_KEY (id)" : "") + ", UNIQUE " + (database instanceof MySqlDatabase ? "KEY name " : "") + "(name))" + (database instanceof MySqlDatabase ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='stores warps' AUTO_INCREMENT=1" : ""));

        Result<Record> results = database.getExecutor().select().from(database.getPrefix() + "warps").fetch();

        for (Record record : results){

        }
    }
}

class Warp {

    private String name;
    private String owner;
    private boolean isPrivate;
    private String world;
    private double x;
    private double y;
    private double z;

    public Warp (String name, String owner, boolean isPrivate, String world, double x, double y, double z){
        this.name = name;
        this.owner = owner;
        this.isPrivate = isPrivate;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;


    }

}
