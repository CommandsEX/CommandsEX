package com.commandsex.helpers;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.commandsex.interfaces.EnableJob;
import static com.commandsex.Language._;

public class VaultHelper implements EnableJob {
    private static Permission p = null;
    private static Economy e = null;
    private static Chat c = null;

    @Override
    public void onEnable(PluginManager pluginManager) {
        if(pluginManager.getPlugin("Vault") == null) {
            LogHelper.logInfo(_(Bukkit.getConsoleSender(), "vaultNotFound"));
            return;
        }
        
        // Setup permission
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if(permissionProvider != null) {
            p = permissionProvider.getProvider();
            LogHelper.logDebug(_(Bukkit.getConsoleSender(), "vaultPermission", p.getName()));
        }
        
        // Setup economy
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if(economyProvider != null) {
            e = economyProvider.getProvider();
            LogHelper.logDebug(_(Bukkit.getConsoleSender(), "vaultEconomy", e.getName()));
        }
        
        // Setup chat
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if(chatProvider != null) {
            c = chatProvider.getProvider();
            LogHelper.logDebug(_(Bukkit.getConsoleSender(), "vaultChat", c.getName()));
        }
    }
    
    /**
     * Gets the current Permission provider
     * @return the provider or <code>null</code> if not found
     */
    public static Permission getPermission() {
        return p;
    }
    
    /**
     * Gets the current Economy provider
     * @return the provider or <code>null</code> if not found
     */
    public static Economy getEconomy() {
        return e;
    }
    
    /**
     * Gets the current Chat provider
     * @return the provider or <code>null</code> if not found
     */
    public static Chat getChat() {
        return c;
    }

}
