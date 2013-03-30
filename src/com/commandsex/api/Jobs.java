package com.commandsex.api;

import java.util.ArrayList;
import java.util.List;

import com.commandsex.CommandsEX;
import com.commandsex.api.interfaces.DisableJob;

import com.commandsex.api.interfaces.EnableJob;
import com.commandsex.helpers.LogHelper;

/**
 * Class to handle methods that need executed when the plugin is reloaded or disabled
 * This will most commonly be used for saving configs or writing to the database
 */
public class Jobs {

    private static List<EnableJob> enableJobs = new ArrayList<EnableJob>();
    private static List<DisableJob> disableJobs = new ArrayList<DisableJob>();

    /**
     * Used when a feature needs to execute code when the plugin is enabled
     * @param enableJob The class to execute the enable job for
     */
    public static void addEnableJob(EnableJob enableJob){
        enableJobs.add(enableJob);
    }

    /**
     * Executes all enable jobs, this should ONLY be executed when the plugin is actually enabling
     */
    public static void executeEnableJobs(){
        for (EnableJob enableJob : enableJobs){
            try {
                enableJob.onEnable();
            } catch (Exception e){
                e.printStackTrace();
                LogHelper.logSevere("Error while executing enable job for class " + enableJob.getClass().getName());
            }
        }
    }

    /**
     * Used when a feature needs to execute code when the plugin is disabled
     * @param disableJob The class to execute the disable job for
     */
    public static void addDisableJob(DisableJob disableJob){
        disableJobs.add(disableJob);
    }
    
    /**
     * Executes all disable jobs, this should ONLY be executed when the plugin is actually disabling
     */
    public static void executeDisableJobs(){
        for (DisableJob disableJob : disableJobs){
            try {
                disableJob.onDisable();
            } catch (Exception e){
                e.printStackTrace();
                LogHelper.logSevere("Error while executing disable job for class " + disableJob.getClass().getName());
            }
        }
    }
    
}
