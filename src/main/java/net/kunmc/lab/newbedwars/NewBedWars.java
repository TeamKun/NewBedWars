package net.kunmc.lab.newbedwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.Objects;

public final class NewBedWars extends JavaPlugin {

    private NBWScoreboard board;
    private PlayerEventListener listener;
    private DaylightTask task;
    private World world;
    private ArrayList<Location> chestList = new ArrayList<>();

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
        world = commander.getWorld();
        task = new DaylightTask(this, world);

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
        world = null;
    }

    public void set(Player commander) {
        Block block = getPlayerFacedBlock(commander);
        if(Material.CHEST != block.getType()) {
            return;
        }
        chestList.add(block.getLocation());
    }

    public ArrayList<Location> getChestList(){
        return chestList;
    }

    public Location getChestLocation() {
        ArrayList<Location> list = getChestList();
        if(list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    public boolean unset(Player commander) {
        Block block = getPlayerFacedBlock(commander);
        if(Material.CHEST != block.getType()) {
            return false;
        }
        if (!chestList.contains(block)) {
            return false;
        }
        getChestList().stream().filter(l->l.equals(block)).forEach(l->chestList.remove(l));
        return true;
    }

    private Block getPlayerFacedBlock(Player player) {
        Location loc = player.getLocation().add(player.getLocation().getDirection().multiply(1));
        Block block = world.getBlockAt(loc);
        return block;
    }

    public boolean isContainsChest(Player commander) {
        Block block = getPlayerFacedBlock(commander);
        if(chestList.contains(block)){
            return true;
        }
        return false;
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

    public void fillChest() {
        // TODO: チェストの座標を取得
        //Block b = world.getBlockAt(xPos, yPos, zPos);
        //Chest c = (Chest) b.getState();
        // TODO: 配給アイテムを追加
        //c.getBlockInventory().addItem(items);
        //c.update();
    }
}
