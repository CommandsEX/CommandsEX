package com.commandsex;

import java.io.InputStream;

/**
 * Functions needed by the Builder
 * This class MUST NOT use/reference anything to do with Bukkit, CraftBukkit or any included libraries
 * Unless those libraries are included in the Builder jar
 */
public class BuilderAccess {

    public static InputStream getPluginYML(){
        return BuilderAccess.class.getClassLoader().getResourceAsStream("plugin.yml");
    }
    
}
