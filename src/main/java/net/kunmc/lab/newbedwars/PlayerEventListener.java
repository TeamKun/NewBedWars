package net.kunmc.lab.newbedwars;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class PlayerEventListener implements Listener {
    private final NewBedWars plugin;
    private final Objective objective;

    public PlayerEventListener(NewBedWars plugin, Objective objective) {
        this.plugin = plugin;
        this.objective = objective;
    }

    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent e) {
        if(EntityType.PLAYER != e.getEntityType()) {
            return;
        }
        Player player = (Player)e.getEntity();
        Material material = e.getItem().getItemStack().getType();
        if(Config.getInstance().isNotCraftableItems(plugin).stream().noneMatch(i->i.equals(material))) {
            return;
        }
        Score score = objective.getScore(player.getName());
        score.setScore(score.getScore() + 1);
    }

    @EventHandler
    public void onEntityDropItemEvent(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        Material material = e.getItemDrop().getItemStack().getType();
        if(Config.getInstance().isNotCraftableItems(plugin).stream().noneMatch(i->i.equals(material))) {
            return;
        }
        Score score = objective.getScore(player.getName());
        score.setScore(score.getScore() - 1);
    }

    @EventHandler
    public void onCraftItemEvent(CraftItemEvent e) {
        Player player = (Player)e.getWhoClicked();
        Material material = e.getRecipe().getResult().getType();
        if(Config.getInstance().isNotCraftableItems(plugin).contains(material)) {
            TextComponent component = new TextComponent();
            component.setText("ベッドは作成できません！");
            component.setColor(ChatColor.RED);
            player.sendMessage(component);
            e.setCancelled(true);
        }
    }
}
