package net.kunmc.lab.newbedwars.command;

import net.kunmc.lab.newbedwars.NewBedWars;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import java.util.ArrayList;

public class Start extends BaseCommand{
    public Start(NewBedWars plugin, String[] args) {
        super(plugin, args, 1);
    }

    @Override
    public BaseComponent[] execute(String[] args, Player player) {
        if(plugin.start(player)) {
            return new ComponentBuilder("info: 新ベッドウォーズが開始しました").color(ChatColor.GREEN).create();
        } else {
            return new ComponentBuilder("error: 新ベッドウォーズの起動に失敗しました。起動済のため停止してから再度起動してください \n/nbw stop").color(ChatColor.RED).create();
        }

    }

    @Override
    public ArrayList onTabComplete(String[] args) {
        return new ArrayList<>();
    }

    @Override
    public BaseComponent[] check(String[] args, Player player) {
        if(length != args.length) {
            return new ComponentBuilder("error: 引数が間違っています \nusage: /nbw start").color(ChatColor.RED).create();
        }
        if(plugin.getDistributionChest().size() < 1) {
            return new ComponentBuilder("error: 配給用チェストが未設定です。先にsetコマンドで設定してください \nusage: /nbw set").color(ChatColor.RED).create();
        }
        return null;
    }
}
