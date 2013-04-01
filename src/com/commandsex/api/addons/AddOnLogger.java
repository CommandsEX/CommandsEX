package com.commandsex.api.addons;

import com.commandsex.CommandsEX;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Modified {@link Logger} used to prefix all logged messages sent by the add-on
 * With CommandsEX-(Add-on Name)
 */
public class AddOnLogger extends Logger  {

    private String addonName;

    /**
     * Constructs a new {@link AddOnLogger}
     * @param addOn The addOn to create this AddOnLogger for
     */
    public AddOnLogger(AddOn addOn) {
        super(addOn.getClass().getCanonicalName(), null);
        this.addonName = "[" + addOn.getName() + "]";
        setParent(CommandsEX.plugin.getLogger());
        setLevel(Level.ALL);
    }

    /**
     * Logs a message to the console
     * @param logRecord The {@link LogRecord} to log
     */
    @Override
    public void log(LogRecord logRecord){
        logRecord.setMessage(addonName + logRecord.getMessage());
        super.log(logRecord);
    }
}
