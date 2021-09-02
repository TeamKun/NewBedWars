package net.kunmc.lab.newbedwars.command;

import net.kunmc.lab.newbedwars.NewBedWars;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.ArrayList;

public class Info extends BaseCommand {

    public Info(NewBedWars plugin, String[] args) {
        super(plugin, args, 1);
    }

    @Override
    public BaseComponent[] execute(String[] args, Player player) {
        ComponentBuilder chestList = new ComponentBuilder();
        ArrayList<Location> list = plugin.getChestList();
        list.stream().forEach(l->chestList.append("座標： " + l.getX() + "," + l.getY() + "," + l.getZ() + "\n"));

        if(chestList.getCursor() == -1) {
            return new ComponentBuilder("----- 設定値一覧 -----\n").color(ChatColor.GREEN)
                    .append("配給用チェスト: なし\n")
                    .append("-------------------").color(ChatColor.GREEN).create();
        }

        return new ComponentBuilder("----- 設定値一覧 -----\n").color(ChatColor.GREEN)
                .append("配給用チェスト:\n").append(chestList.create())
                .append("-------------------").color(ChatColor.GREEN).create();
    }

    @Override
    public ArrayList onTabComplete(String[] args) {
        return new ArrayList<>();
    }

    @Override
    public BaseComponent[] check(String[] args, Player player) {
        if(length != args.length) {
            return new ComponentBuilder("error: 引数が間違っています \nusage: /nbw info").color(ChatColor.RED).create();
        }
        return null;
    }
}
