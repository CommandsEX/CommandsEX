package com.commandsex.database;

import org.jooq.impl.Executor;

public interface Database {

    public String getPrefix();
    public boolean isConnected();
    public Executor getExecutor();
    public void close();

}