package net.kunmc.lab.newbedwars.command;

import net.kunmc.lab.newbedwars.NewBedWars;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import java.util.ArrayList;

public class Stop extends BaseCommand{
    public Stop(NewBedWars plugin, String[] args) {
        super(plugin, args, 1);
    }

    @Override
    public BaseComponent[] execute(String[] args, Player player) {
        if(plugin.stop()) {
            return new ComponentBuilder("info: 新ベッドウォーズが終了しました").color(ChatColor.GREEN).create();
        } else {
            return new ComponentBuilder("error: 新ベッドウォーズが起動していません \nusage: /nbw start").color(ChatColor.RED).create();
        }

    }

    @Override
    public ArrayList onTabComplete(String[] args) {
        return new ArrayList<>();
    }

    @Override
    public BaseComponent[] check(String[] args, Player player) {
        if(length != args.length) {
            return new ComponentBuilder("error: 引数が間違っています \nusage: /nbw stop").color(ChatColor.RED).create();
        }
        return null;
    }
}
