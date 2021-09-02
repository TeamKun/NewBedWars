package net.kunmc.lab.newbedwars.command;

import net.kunmc.lab.newbedwars.NewBedWars;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class BaseCommand {
    protected NewBedWars plugin;
    protected String[] args;
    protected int length;

    public BaseCommand(NewBedWars plugin, String[] args, int length) {
        this.plugin = plugin;
        this.args = args;
        this.length = length;
    }
    public abstract BaseComponent[] execute(String[] args, Player player);
    public abstract ArrayList onTabComplete(String[] args);
    public abstract BaseComponent[] check(String[] args, Player player);
}
