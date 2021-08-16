package net.kunmc.lab.newbedwars.command;

import net.kunmc.lab.newbedwars.NewBedWars;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.ArrayList;
import java.util.List;

public class Info extends BaseCommand {

    public Info(NewBedWars plugin, String[] args) {
        super(plugin, args, 1);
    }

    @Override
    public BaseComponent[] execute(String[] args) {
        plugin.start();
        return new ComponentBuilder("info: 設定値を表示します").color(ChatColor.GREEN).create();
    }

    @Override
    public ArrayList onTabComplete(String[] args) {
        return new ArrayList<>();
    }

    @Override
    public BaseComponent[] check(String[] args) {
        if(length != args.length) {
            return new ComponentBuilder("error: 引数が間違っています \nusage: /nbw info").color(ChatColor.RED).create();
        }
        return null;
    }
}
