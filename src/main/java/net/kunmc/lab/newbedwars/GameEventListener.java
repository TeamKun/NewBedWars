package net.kunmc.lab.newbedwars;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class GameEventListener implements Listener {
    private final NewBedWars plugin;
    private final GameScore scoreboard;

    public GameEventListener(NewBedWars plugin, GameScore scoreboard) {
        this.plugin = plugin;
        this.scoreboard = scoreboard;
    }

    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent e) {
        if(EntityType.PLAYER != e.getEntityType()) {
            return;
        }
        if(Const.BEDS.contains(e.getItem().getItemStack().getType())) {
            // 所有ベッド数をカウントアップする
            scoreboard.addBedCountScore((Player)e.getEntity(), e.getItem().getItemStack().getAmount());
        }
    }

    @EventHandler
    public void onEntityDropItemEvent(PlayerDropItemEvent e) {
        if(Const.BEDS.contains(e.getItemDrop().getItemStack().getType())) {
            // 所有ベッド数をマイナスカウントする
            scoreboard.minusBedCountScore(e.getPlayer(), e.getItemDrop().getItemStack().getAmount());
        }
    }

    @EventHandler
    public void onCraftItemEvent(CraftItemEvent e) {
        Player player = (Player)e.getWhoClicked();
        if(Const.BEDS.contains(e.getRecipe().getResult().getType()) || Const.CHESTS.contains(e.getRecipe().getResult().getType())) {
            TextComponent component = new TextComponent();
            component.setText("ベッドとチェストは作成できません！");
            component.setColor(ChatColor.RED);
            player.sendMessage(component);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDamageEvent(BlockDamageEvent e) {
        Material material = e.getBlock().getType();
        // チェストは破壊不可とする
        if(Const.CHESTS.contains(material)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDropItemEvent(BlockDropItemEvent e) {
        // チェストは破壊不可とする
        if(Const.CHESTS.contains(e.getBlock().getType())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent e) {
        if(!InventoryType.CHEST.equals(e.getInventory().getType())) {
            return;
        }
        Player player = (Player)e.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                int count = 0;
                for (ItemStack item : player.getInventory()) {
                    if(null == item) {
                        continue;
                    }
                    if(Const.BEDS.contains(item.getType())) {
                        count += item.getAmount();
                    }
                }
                scoreboard.setBedCountScore(player, count);
            }
        }.runTaskLater(plugin, 1);
    }

    @EventHandler
    public void onItemSpawnEvent(ItemSpawnEvent e) {
        // ゲームルールで削除したベッドはスポーン不可にする
        if(plugin.isRemoveBed(e.getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBedLeaveEvent(PlayerBedLeaveEvent e) {
        // 夜の場合は起床させない
        plugin.getLogger().info("onPlayerBedLeaveEvent.setCancelled: " + plugin.getTime());
        if(plugin.isNight()) {
            e.setCancelled(true);
            return;
        }
        if(plugin.isDay()) {
            // 使用していたベッドは削除
            e.getBed().setType(Material.AIR);
            plugin.removeBed(e.getPlayer());
        }

    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        if(Const.BEDS.contains(e.getItemInHand().getType())) {
            // 所有ベッド数をマイナスカウントする
            scoreboard.minusBedCountScore(e.getPlayer(), e.getItemInHand().getAmount());
        }
    }
     @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent e) {
        Location loc = plugin.getRespawnLocation();
        // ワールドボーダーの中心をリスポーン地点とする
        if(null != loc) {
            e.setRespawnLocation(loc);
        }
    }
}
