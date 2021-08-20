package net.kunmc.lab.newbedwars;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerEventListener implements Listener {
    private final NewBedWars plugin;
    private final NBWScoreboard scoreboard;

    public PlayerEventListener(NewBedWars plugin, NBWScoreboard scoreboard) {
        this.plugin = plugin;
        this.scoreboard = scoreboard;
    }

    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent e) {
        if(EntityType.PLAYER != e.getEntityType()) {
            return;
        }
        Player player = (Player)e.getEntity();
        Material material = e.getItem().getItemStack().getType();
        if(Config.getInstance().nonCraftableItems(plugin).stream().noneMatch(i->i.equals(material))) {
            return;
        }
        scoreboard.addBedCountScore(player, e.getItem().getItemStack().getAmount());
    }

    @EventHandler
    public void onEntityDropItemEvent(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        Material material = e.getItemDrop().getItemStack().getType();
        if(Config.getInstance().nonCraftableItems(plugin).stream().noneMatch(i->i.equals(material))) {
            return;
        }
        scoreboard.minusBedCountScore(player, e.getItemDrop().getItemStack().getAmount());
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onCraftItemEvent(CraftItemEvent e) {
        Player player = (Player)e.getWhoClicked();
        Material material = e.getRecipe().getResult().getType();
        if(Config.getInstance().nonCraftableItems(plugin).contains(material)) {
            TextComponent component = new TextComponent();
            component.setText("ベッドは作成できません！");
            component.setColor(ChatColor.RED);
            player.sendMessage(component);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDamageEvent(BlockDamageEvent e) {
        Material material = e.getBlock().getType();
        if(Config.getInstance().nonBreakable(plugin).contains(material)) {
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
                    if(Config.getInstance().nonCraftableItems(plugin).contains(item.getType()) ){
                        count += item.getAmount();
                    }
                }
                scoreboard.setBedCountScore(player, count);
            }
        }.runTaskLater(plugin, 1);
    }
}
