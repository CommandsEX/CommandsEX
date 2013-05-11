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
    private static Permission permission = null;
    private static Economy economy = null;
    private static Chat chat = null;
    private static boolean vaultAvailable = false;

    @Override
    public void onEnable(PluginManager pluginManager) {
        if(pluginManager.getPlugin("Vault") == null) {
            LogHelper.logInfo(_(Bukkit.getConsoleSender(), "vaultNotFound"));
            return;
        }

        vaultAvailable = true;
        
        // Setup permission
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if(permissionProvider != null) {
            permission = permissionProvider.getProvider();
            LogHelper.logDebug(_(Bukkit.getConsoleSender(), "vaultPermission", permission.getName()));
        }
        
        // Setup economy
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if(economyProvider != null) {
            economy = economyProvider.getProvider();
            LogHelper.logDebug(_(Bukkit.getConsoleSender(), "vaultEconomy", economy.getName()));
        }
        
        // Setup chat
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if(chatProvider != null) {
            chat = chatProvider.getProvider();
            LogHelper.logDebug(_(Bukkit.getConsoleSender(), "vaultChat", chat.getName()));
        }
    }

    /**
     * Checks if the Vault plugin is enabled
     * @return Is the Vault plugin available
     */
    public static boolean isVaultAvailable(){
        return vaultAvailable;
    }

    /**
     * Gets the current Permission provider
     * @return the provider or <code>null</code> if not found
     */
    public static Permission getPermission() {
        return permission;
    }

    /**
     * Checks if permissions are available and returns the result
     * @return Are Permissions available?
     */
    public static boolean isPermissionsAvailable(){
        return permission != null;
    }
    
    /**
     * Gets the current Economy provider
     * @return the provider or <code>null</code> if not found
     */
    public static Economy getEconomy() {
        return economy;
    }

    /**
     * Checks if economy is available and returns the result
     * @return Is Economy available?
     */
    public static boolean isEconomyAvailable(){
        return economy != null;
    }
    
    /**
     * Gets the current Chat provider
     * @return the provider or <code>null</code> if not found
     */
    public static Chat getChat() {
        return chat;
    }

    /**
     * Checks if Chat is available and returns the result
     * @return Is Chat available?
     */
    public static boolean isChatAvailable(){
        return chat != null;
    }

}
