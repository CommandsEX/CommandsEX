package com.commandsex.database;

import org.jooq.impl.Executor;

import java.sql.Connection;

public interface Database {

    /**
     * Gets the string to prefix all tables with
     * @return The string to prefix all tables with
     */
    public String getPrefix();

    /**
     * Checks if the database is connected
     * @return Is the database connected
     */
    public boolean isConnected();

    /**
     * Gets the {@link Connection} to the database
     * @return The {@link Connection} to the database
     */
    public Connection getConnection();

    /**
     * Gets the database {@link Executor} from the JOOQ api
     * @return The database {@link Executor}
     */
    public Executor getExecutor();

    /**
     * Closes the connection to the database, this is usually down when CommandsEX is disabled
     */
    public void close();

}