package com.commandsex.database;

import com.commandsex.helpers.LogHelper;
import org.jooq.SQLDialect;
import org.jooq.impl.Executor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqLiteDatabase implements Database {

    private Connection connection = null;
    private String prefix;
    private boolean connected = false;

    public SqLiteDatabase(String location, String prefix){
        this.prefix = prefix;

        try {
            Class.forName("org.sqlite.JDBC").newInstance();
            connection = DriverManager.getConnection("jdbc:sqlite:" + location);
            connected = true;
        } catch (Throwable throwable){
            throwable.printStackTrace();
            LogHelper.logSevere("Failed to connect to SQLite database\n" + throwable.getMessage());
        }
    }

    public String getPrefix(){
        return prefix;
    }

    public boolean isConnected() {
        return connected;
    }

    public Executor getExecutor() {
        return new Executor(connection, SQLDialect.SQLITE);
    }

    public void close(){
        if (isConnected()){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
