package com.commandsex.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

/**
 * Required for any command class
 */
public @interface Cmd {
    
    /**
     * @return The primary command
     */
    String command();
    
    /**
     * @return A brief description of the command
     */
    String description();
    
    /**
     * @return A comma separated list of command aliases, cex_<command> will be automatically added
     */
    String aliases() default "";
    
    /**
     * @return The permission node required for the command, don't forget to add this permission in an EnableJob
     */
    String permission() default "";

    /**
     * @return The {@link org.bukkit.permissions.PermissionDefault} that the {@link org.bukkit.permissions.Permission} should use
     */
    String permissionDefault() default "OP";

    /**
     * @return This only needs to be used if you have 1 or more paremeters for the command, %c% is replaced by /<command>
     */
    String usage() default "%c%";
    
}
