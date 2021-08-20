package net.kunmc.lab.newbedwars;

import net.kunmc.lab.newbedwars.command.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NewBedWarsExecutor implements CommandExecutor, TabCompleter {
    private final NewBedWars plugin;
    public NewBedWarsExecutor(NewBedWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            return true;
        }
        BaseCommand nbwCommand = build(args);
        if(null == nbwCommand) {
            sender.sendMessage(new ComponentBuilder("error: コマンドが間違っています \nusage: /nbw " + Const.START + " | " + Const.STOP + " | " + Const.CONF + " | " + Const.INFO + " | " + Const.SET).color(ChatColor.RED).create());
            return true;
        }

        BaseComponent[] message = nbwCommand.check(args);
        if(message != null) {
            sender.sendMessage(message);
            return true;
        }

        nbwCommand.execute(args, ((Player) sender).getPlayer());
        sender.sendMessage(nbwCommand.message());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(!Const.MAIN_COMMAND.equals(command.getName())) {
            return new ArrayList<>();
        }
        if(1 == args.length) {
            return Stream.of(Const.START, Const.STOP, Const.CONF, Const.INFO, Const.SET)
                    .filter(e->e.startsWith(args[0])).collect(Collectors.toList());
        }
        BaseCommand nbwCommand = build(args);
        if(null == nbwCommand) {
            return new ArrayList<>();
        }
        return nbwCommand.onTabComplete(args);
    }

    private BaseCommand build(String[] args) {
        if(1 > args.length) {
            return null;
        }
        switch (args[0]) {
            case Const.START:
                return new Start(plugin, args);
            case Const.STOP:
                return new Stop(plugin, args);
            case Const.CONF:
                return new Conf(plugin, args);
            case Const.INFO:
                return new Info(plugin, args);
            case Const.SET:
                return new Set(plugin, args);
        }
        return null;
    }
}