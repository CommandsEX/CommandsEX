package com.commandsex.database;

import org.jooq.SQLDialect;
import org.jooq.impl.Executor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlDatabase implements Database {

    private Connection connection = null;
    private String prefix;
    private boolean connected = false;

    public MySqlDatabase(String name, String username, String password, String host, String port, String prefix){
        this.prefix = prefix;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name, username, password);
            connected = true;
        } catch (Throwable throwable){
            throwable.printStackTrace();
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isConnected() {
        return connected;
    }

    public Connection getConnection(){
        return connection;
    }

    public Executor getExecutor() {
        return new Executor(connection, SQLDialect.MYSQL);
    }

    public void close() {
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
