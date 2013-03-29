package com.commandsex.handlers;

import java.util.Set;

import com.commandsex.api.interfaces.Event;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import com.commandsex.CommandsEX;

public class EventManager implements Listener {

    public int registerEvents(){
        int ret = 0;
        Reflections reflections = new Reflections("com.commandsex.handlers");
        Set<Class<? extends Event>> commandClasses = reflections.getSubTypesOf(Event.class);

        for (Class<? extends Event> clazz : commandClasses){
            try {
                Bukkit.getPluginManager().registerEvents((Event) clazz.newInstance(), CommandsEX.plugin);
                ret++;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        
        return ret;
    }
}
