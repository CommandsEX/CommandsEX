package com.commandsex;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.commandsex.helpers.LogHelper;
import com.commandsex.helpers.Utils;

public class Database {

    public enum DatabaseType {
        SQLITE(0),
        MYSQL(1);

        private int id;

        private DatabaseType(int id){
            this.id = id;
        }

        /**
         * Returns the ID of the database type
         * @return The database ID
         */
        public int getId(){
            return id;
        }

        /**
         * Matches a string to a DatabaseType
         * @param string The DatabaseType name or id
         * @return The DatabaseType, null if not found
         */
        public static DatabaseType fromString(String string){
            DatabaseType databaseType = null;

            if (Utils.isInt(string)){
                databaseType = values()[Integer.parseInt(string)];
            } else {
                for (DatabaseType type : values()){
                    if (string.equalsIgnoreCase(type.name())){
                        databaseType = type;
                        break;
                    }
                }
            }

            return databaseType;
        }

        public static DatabaseType fromId(int id){
            return values()[id];
        }
    }

    private CommandsEX commandsEX = CommandsEX.plugin;
    private transient Connection conn;
    private String prefix = "cex_";
    private String databaseName;
    private DatabaseType databaseType;
    private boolean connected = false;

    /**
     * Creates a new SQLITE database connection
     * @param databaseName The name of the database, default commandsex
     * @param prefix All tables created by CommandsEX will be prefix with this, default cex_
     */
    public Database(String databaseName, String prefix){
        databaseType = DatabaseType.SQLITE;
        this.databaseName = databaseName;

        try {
            // this will throw an error if for some reason the JDBC class is unavailable
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + commandsEX.getDataFolder() + File.separatorChar + databaseName + ".db");
            connected = true;
        } catch (Exception e){
            e.printStackTrace();
            LogHelper.logSevere("Error while connecting to SQLITE database " + databaseName);
        }

        this.prefix = prefix;
    }

    /**
     * Creates a new MYSQL database connection
     * @param databaseName The name of the database, default commandsex
     * @param username The username to the database, default root
     * @param password The password to the database, default ""
     * @param host The host/address to the database, default localhost
     * @param port The port of the database, default 3306
     * @param prefix All tables created by CommandsEX will be prefix with this, default cex_
     */
    public Database(String databaseName, String username, String password, String host, String port, String prefix){
        databaseType = DatabaseType.MYSQL;

        try {
            // this will throw an error if for some reason the Driver class is unavailable
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName, username, password);
            connected = true;
        } catch (Exception e){
            e.printStackTrace();
            LogHelper.logSevere("Error while connecting to MYSQL database " + databaseName);
        }

        this.prefix = prefix;
    }

    /**
     * Is the database connected or did it fail?
     * @return Is the database connected
     */
    public boolean isConnected(){
        return connected;
    }

    /**
     * Gets the type of database this is
     * @return The type of database this is
     */
    public DatabaseType getType(){
        return databaseType;
    }

    /**
     * Gets the database tables prefix
     * @return The database tables prefix
     */
    public String getPrefix(){
        return prefix;
    }

    /**
     * Executes a query on the database that does not return a ResultSet
     * If %prefix% is used in the query, it will be replaced with the query
     *
     * @param query The query to execute
     * @param params Additional parameters needed
     * @return Did the query execute successfully?
     */
    public boolean query(String query, Object... params){
        if (!isConnected()){
            LogHelper.logSevere("Could not run query because database is not connected QUERY = " + query);
            return false;
        }

        query = query.replaceAll("%prefix%", getPrefix());

        if (params.length == 0){
            try {
                Statement statement = conn.createStatement();
                statement.executeUpdate(query);
                statement.close();
            } catch (Exception e){
                e.printStackTrace();
                LogHelper.logSevere("Error while writing to database, QUERY = " + query);
            }
        } else {
            // if we have only 1 parameter that is an ArrayList, make an array of objects out of it
            // this allows us to pass in params in a List easily
            if ((params.length == 1) && ((params[0] instanceof List) || (params[0] instanceof ArrayList))) {
                params = ((List<?>) params[0]).toArray();
            }

            try {
                PreparedStatement prep = conn.prepareStatement(query);
                int i = 1;
                for (Object o : params) {
                    if (o instanceof Integer) {
                        prep.setInt(i, (Integer)o);
                    } else if (o instanceof String) {
                        prep.setString(i, (String)o);
                    } else if (o instanceof Double) {
                        prep.setDouble(i, (Double)o);
                    } else if (o instanceof Float) {
                        prep.setFloat(i, (Float)o);
                    } else if (o instanceof Long) {
                        prep.setLong(i, (Long)o);
                    } else if (o instanceof Boolean) {
                        prep.setBoolean(i, (Boolean)o);
                    } else if (o instanceof Date) {
                        prep.setTimestamp(i, new Timestamp(((Date) o).getTime()));
                    } else if (o instanceof Timestamp) {
                        prep.setTimestamp(i, (Timestamp) o);
                    } else if (o == null) {
                        prep.setNull(i, 0);
                    } else {
                        // unhandled variable type
                        LogHelper.logSevere(query);
                        LogHelper.logSevere("Unhandled variable when writing to database");
                        LogHelper.logSevere(o.toString());

                        prep.clearBatch();
                        prep.close();
                        return false;
                    }

                    i++;
                }

                prep.addBatch();
                conn.setAutoCommit(false);
                prep.executeBatch();
                conn.commit();
                prep.close();
            } catch (Exception e) {
                e.printStackTrace();
                LogHelper.logSevere("Error while writing to the database " + databaseName + " QUERY = " + query);
                return false;
            }
        }

        return true;
    }

    /**
     * Executes a query and returns a ResultSet
     * If %prefix% is used in the query, it will be replaced with the query
     *
     * @param query The query to execute
     * @param params Additional parameters needed
     * @return The ResultSet, null if failed
     */
    public ResultSet query_res(String query, Object... params){
        if (!isConnected()){
            LogHelper.logSevere("Could not run query because database is not connected");
            LogHelper.logSevere(query);
            return null;
        }

        query = query.replaceAll("%prefix%", prefix);

        if (params.length == 0){
            try {
                Statement statement = conn.createStatement();
                return statement.executeQuery(query);
            } catch (Exception e){
                e.printStackTrace();
                LogHelper.logSevere("Error while writing to the database " + databaseName + " QUERY = " + query);
            }
        } else {
            // if we have only 1 parameter that is an ArrayList, make an array of objects out of it
            // this allows us to pass in params in a List easily
            if ((params.length == 1) && ((params[0] instanceof List) || (params[0] instanceof ArrayList))) {
                params = ((List<?>) params[0]).toArray();
            }

            try {
                PreparedStatement prep = conn.prepareStatement(query);
                int i = 1;
                for (Object o : params) {
                    if (o instanceof Integer) {
                        prep.setInt(i, (Integer)o);
                    } else if (o instanceof String) {
                        prep.setString(i, (String)o);
                    } else if (o instanceof Double) {
                        prep.setDouble(i, (Double)o);
                    } else if (o instanceof Float) {
                        prep.setFloat(i, (Float)o);
                    } else if (o instanceof Long) {
                        prep.setLong(i, (Long) o);
                    } else if (o == null) {
                        prep.setNull(i, 0);
                    } else {
                        // unhandled variable type
                        LogHelper.logSevere(query);
                        LogHelper.logSevere("Unhandled variable when writing to database");
                        LogHelper.logSevere(o.toString());

                        prep.close();
                        return null;
                    }

                    i++;
                }

                return prep.executeQuery();
            } catch (Throwable throwable){
                throwable.printStackTrace();
                LogHelper.logSevere("Error while writing to the database " + databaseName + " QUERY = " + query);
                return null;
            }
        }

        return null;
    }

    /**
     * Closes the database connection if it is connected
     */
    public void close(){
        if (isConnected()){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}