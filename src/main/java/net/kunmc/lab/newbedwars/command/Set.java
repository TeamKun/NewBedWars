package net.kunmc.lab.newbedwars.command;

import net.kunmc.lab.newbedwars.NewBedWars;
import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Set extends BaseCommand{
    public Set(NewBedWars plugin, String[] args) {
        super(plugin, args, 1);
    }

    @Override
    public BaseComponent[] execute(String[] args, Player player) {
        if(plugin.set(player)) {
            Location loc = plugin.getChestLocation();
            return new ComponentBuilder("info: 配給用のチェストを設定しました(座標：" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ")").color(ChatColor.GREEN).create();
        }
        return new ComponentBuilder("error: 配給用チェストの設定に失敗しました。指定したブロックがチェストではありません。").color(ChatColor.RED).create();
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
        if(plugin.containsDistributionChest(player)) {
            return new ComponentBuilder("error: すでに配給用チェストに設定済です").color(ChatColor.RED).create();
        }
        return null;
    }
}
