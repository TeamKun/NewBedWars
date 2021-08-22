package net.kunmc.lab.newbedwars.command;

import net.kunmc.lab.newbedwars.NewBedWars;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Conf extends BaseCommand{

    public Conf(NewBedWars plugin, String[] args) {
        super(plugin, args, 3);
    }

    @Override
    public void execute(String[] args, Player player) {
        plugin.getConfig().set(args[1], Integer.parseInt(args[2]));
    }
    public BaseComponent[] message() {
        return new ComponentBuilder("info: " + args[1] + "の設定変更が完了しました").color(ChatColor.GREEN).create();
    }

    @Override
    public ArrayList onTabComplete(String[] args) {
        if(length - 1 != args.length) {
            return new ArrayList<>();
        }
        return Arrays.stream(BaseAttribute.TYPE.values())
                .filter(e -> e.name().toLowerCase().startsWith(args[1]))
                .map(e -> e.name().toLowerCase()).collect(Collectors.toCollection((Supplier<ArrayList>) ArrayList::new));
    }

    @Override
    public BaseComponent[] check(String[] args) {
        if(length != args.length) {
            return new ComponentBuilder("error: 引数が間違っています \nusage: /nbw conf").color(ChatColor.RED).create();
        }
        // 属性の値チェック
        if(Arrays.stream(BaseAttribute.TYPE.values()).noneMatch(attr -> attr.name().toLowerCase().equals(args[1]))) {
            return new ComponentBuilder("error: コマンドが間違っています \nusage: /nbw conf").color(ChatColor.RED).create();
        }

        // 引数の範囲チェック
        BaseComponent[] c = BaseAttribute.getType(args[1]).check(args[2]);
        if(null != c) {
            return c;
        }
        return null;
    }
}