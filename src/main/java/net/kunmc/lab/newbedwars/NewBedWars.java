package net.kunmc.lab.newbedwars;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public final class NewBedWars extends JavaPlugin {

    private NBWScoreboard board;
    private PlayerEventListener listener;
    private DaylightTask task;

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
    public void start(Player commander) {
        task = new DaylightTask(this, commander);

        task.runTaskTimer(this, 0, 1);
        board = new NBWScoreboard();
        listener = new PlayerEventListener(this, board);
        getServer().getPluginManager().registerEvents(listener, this);

        Bukkit.getOnlinePlayers().forEach(this::initPlayer);
    }

    public void stop() {
        HandlerList.unregisterAll(listener);
        listener = null;
        board = null;
        task.cancel();
        task = null;
    }

    private void initPlayer(Player player) {
        // ゲーム開始時に所有しているベッドをカウントする
        int count = 0;
        for (ItemStack item : player.getInventory()) {
            if(null == item) {
                continue;
            }
            if(Config.getInstance().cannotCraftItems(this).contains(item.getType()) ){
                count += item.getAmount();
            }
        }
        board.setBedCountScore(player, count);
        board.setAliveTurnScore(player, 0);
        board.setShowPlayer(player);
    }

    public void killPlayer() {
        //Bukkit.getOnlinePlayers().stream().filter(p->!(p.isSleeping())).forEach(p->p.sendMessage("早く寝なきゃだめでしょ！"));
    }

    public long getTime() {
        return task.getTime();
    }

    public void addAliveTurn() {
        Bukkit.getOnlinePlayers().stream().filter(p->!p.isDead()).forEach(p->board.addAliveTurnScore(p, 1));
    }
}
