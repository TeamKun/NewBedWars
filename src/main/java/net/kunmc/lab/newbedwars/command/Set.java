package net.kunmc.lab.newbedwars.command;

import net.kunmc.lab.newbedwars.NewBedWars;
import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

public class Set extends BaseCommand{
    public Set(NewBedWars plugin, String[] args) {
        super(plugin, args, 1);
    }

    @Override
    public void execute(String[] args, Player player) {
        //plugin.set(player);
    }
    @Override
    public BaseComponent[] message() {
        return new ComponentBuilder("info: 配給用のチェストが設定されました").color(ChatColor.GREEN).create();
    }

    @Override
    public ArrayList onTabComplete(String[] args) {
        return new ArrayList<>();
    }

    @Override
    public BaseComponent[] check(String[] args) {
        if(length != args.length) {
            return new ComponentBuilder("error: 引数が間違っています \nusage: /nbw set").color(ChatColor.RED).create();
        }
        return null;
    }
}
