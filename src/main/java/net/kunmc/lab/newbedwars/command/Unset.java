package net.kunmc.lab.newbedwars.command;

import net.kunmc.lab.newbedwars.NewBedWars;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Unset extends BaseCommand{
    public Unset(NewBedWars plugin, String[] args) {
        super(plugin, args, 1);
    }

    @Override
    public BaseComponent[] execute(String[] args, Player player) {
        if(plugin.unset(player)){
            return new ComponentBuilder("info: 配給用チェストを解除しました").color(ChatColor.GREEN).create();
        }
        return new ComponentBuilder("error: 配給用チェストの解除に失敗しました。配給用チェストではありません。").color(ChatColor.RED).create();
    }

    @Override
    public ArrayList onTabComplete(String[] args) {
        return new ArrayList<>();
    }

    @Override
    public BaseComponent[] check(String[] args, Player player) {
        if(length != args.length) {
            return new ComponentBuilder("error: 引数が間違っています \nusage: /nbw set").color(ChatColor.RED).create();
        }
        if(plugin.getDistributionChest().size() == 0) {
            return new ComponentBuilder("error: 配給用のチェストが1件も設定されていません。先にsetコマンドで設定してください。 \nusage: /nbw set").color(ChatColor.RED).create();
        }
        return null;
    }
}
