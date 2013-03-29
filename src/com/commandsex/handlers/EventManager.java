package com.commandsex.handlers;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import com.commandsex.CommandsEX;
import com.commandsex.api.IEvent;

public class EventManager implements Listener {

    public int registerEvents(){
        int ret = 0;
        Reflections reflections = new Reflections("com.commandsex.handlers");
        Set<Class<? extends IEvent>> commandClasses = reflections.getSubTypesOf(IEvent.class);

        for (Class<? extends IEvent> clazz : commandClasses){
            try {
                Bukkit.getPluginManager().registerEvents((IEvent) clazz.newInstance(), CommandsEX.plugin);
                ret++;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        
        return ret;
    }
}
