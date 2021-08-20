package net.kunmc.lab.newbedwars;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;
import java.util.Objects;

public final class NewBedWars extends JavaPlugin {

    private ScoreboardManager manager;
    private Scoreboard board;
    private Objective objective;
    private PlayerEventListener listener;

    @Override
    public void onEnable() {
        // Plugin startup logic
        NewBedWarsExecutor executor = new NewBedWarsExecutor(this);
        Objects.requireNonNull(getCommand(Const.MAIN_COMMAND)).setExecutor(executor);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @SuppressWarnings("deprecation")
    public void start() {
        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        objective = board.registerNewObjective("bedCount", "dummy");
        objective.setDisplayName(Const.BED_COUNT);
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);

        listener = new PlayerEventListener(this, objective);
        getServer().getPluginManager().registerEvents(listener, this);

        Bukkit.getOnlinePlayers().forEach(this::initPlayer);
    }
    public void stop() {
        HandlerList.unregisterAll(listener);
        listener = null;

        objective = null;
        board = null;
        manager =null;
    }

    private void initPlayer(Player player) {
        // ゲーム開始時に所有しているベッドをカウントする
        int count = 0;
        for (ItemStack item : player.getInventory()) {
            if(null == item) {
                continue;
            }
            if(Config.getInstance().nonCraftableItems(this).contains(item.getType()) ){
                count += item.getAmount();
            }
        }
        Score score = objective.getScore(player.getName());
        score.setScore(count);

        player.setScoreboard(board);
    }
}
