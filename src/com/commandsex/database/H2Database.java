package com.commandsex.database;

import com.commandsex.helpers.LogHelper;
import org.jooq.SQLDialect;
import org.jooq.impl.Executor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2Database implements Database {

    private Connection connection = null;
    private String prefix;
    private boolean connected = false;

    public H2Database(String location, String prefix) {
        this.prefix = prefix;

        try {
            Class.forName("org.h2.Driver").newInstance();
            connection = DriverManager.getConnection("jdbc:h2:~" + location, "sa", "");
            connected = true;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            LogHelper.logSevere("Failed to connect to H2 database\n" + throwable.getMessage());
        }
    }

    public String getPrefix(){
        return prefix;
    }

    public boolean isConnected(){
        return connected;
    }

    public Executor getExecutor(){
        return new Executor(connection, SQLDialect.H2);
    }

    public void close(){
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
