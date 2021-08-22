package net.kunmc.lab.newbedwars.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class BaseAttribute {
    enum TYPE {
        DAYTRIK {
            @Override
            BaseComponent[] check(String s) {
                try {
                    int trik = Integer.parseInt(s);
                    if (trik < 10 || 3000 < trik) {
                        return new ComponentBuilder("error: daytrikは 10~3000 の間で設定してください").color(ChatColor.RED).create();
                    }
                } catch (Exception e) {
                    return new ComponentBuilder("error: daytrik が数値に変換できませんでした").color(ChatColor.RED).create();
                }
                return null;
            }
        },
        COUNTDOWN {
            @Override
            BaseComponent[] check(String s) {
                try {
                    int count = Integer.parseInt(s);
                    if (count < 1 || 3000 < count) {
                        return new ComponentBuilder("error: countdownは 1~3000 の間で設定してください").color(ChatColor.RED).create();
                    }
                } catch (Exception e) {
                    return new ComponentBuilder("error: countdown が数値に変換できませんでした").color(ChatColor.RED).create();
                }
                return null;
            }
        },
        DISTRIBUTION {
            @Override
            BaseComponent[] check(String s) {
                try {
                    int distribution = Integer.parseInt(s);
                    if (distribution < 0 || 100 < distribution) {
                        return new ComponentBuilder("error: distributionは 0~100 の間で設定してください").color(ChatColor.RED).create();
                    }
                } catch (Exception e) {
                    return new ComponentBuilder("error: distribution が数値に変換できませんでした").color(ChatColor.RED).create();
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