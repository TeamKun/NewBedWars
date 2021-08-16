package net.kunmc.lab.newbedwars;

import org.bukkit.plugin.java.JavaPlugin;

public final class NewBedWars extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        NewBedWarsExecutor executor = new NewBedWarsExecutor(this);
        getCommand(Const.MAIN_COMMAND).setExecutor(executor);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void start() {

    }
    public void stop() {

    }
}
