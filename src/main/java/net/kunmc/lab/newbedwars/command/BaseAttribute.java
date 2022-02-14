package net.kunmc.lab.newbedwars.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;

public class BaseAttribute {
    enum TYPE {
        DAYSECOND {
            @Override
            BaseComponent[] check(String s) {
                try {
                    int trik = Integer.parseInt(s);
                    if (trik < 10 || 300 < trik) {
                        return new ComponentBuilder("error: daysecondは 10~300 の間で設定してください").color(ChatColor.RED).create();
                    }
                } catch (Exception e) {
                    return new ComponentBuilder("error: daysecond は数字で入力してください").color(ChatColor.RED).create();
                }
                return null;
            }
        },
        COUNTDOWN {
            @Override
            BaseComponent[] check(String s) {
                try {
                    int count = Integer.parseInt(s);
                    if (count < 1 || 30 < count) {
                        return new ComponentBuilder("error: countdownは 1~30 の間で設定してください").color(ChatColor.RED).create();
                    }
                } catch (Exception e) {
                    return new ComponentBuilder("error: countdown は数字で入力してください").color(ChatColor.RED).create();
                }
                return null;
            }
        },
        LESSBED {
            @Override
            BaseComponent[] check(String s) {
                try {
                    int lessChair = Integer.parseInt(s);
                    if (lessChair < 0 || Bukkit.getOnlinePlayers().size() <= lessChair) {
                        return new ComponentBuilder("error: lessbedは 参加人数以下の整数で設定してください").color(ChatColor.RED).create();
                    }
                } catch (Exception e) {
                    return new ComponentBuilder("error: lessbed は数字で入力してください").color(ChatColor.RED).create();
                }
                return null;
            }
        };

        abstract BaseComponent[] check(String s);
    }

    public static TYPE getType(String arg) {
        return TYPE.valueOf(arg.toUpperCase());
    }
}