package net.kunmc.lab.newbedwars;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.Objects;

public final class NewBedWars extends JavaPlugin {

    private GameScore board;
    private GameEventListener listener;
    private GameCycle task;
    private World world;
    private ArrayList<Location> chestList = new ArrayList<>();
    private ArrayList<Location> bedList = new ArrayList<>();

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

    public boolean start(Player commander) {
        if(null != task) {
            return false;
        }
        world = commander.getWorld();
        task = new GameCycle(this, world);

        task.runTaskTimer(this, 0, 1);
        board = new GameScore();
        listener = new GameEventListener(this, board);
        getServer().getPluginManager().registerEvents(listener, this);

        giveOutBed();
        Bukkit.getOnlinePlayers().forEach(this::initializePlayer);
        return true;
    }

    public boolean stop() {
        if(null == task) {
            return false;
        }
        HandlerList.unregisterAll(listener);
        listener = null;
        board = null;
        task.cancel();
        task = null;
        world = null;
        return true;
    }

    public boolean set(Player commander) {
        Block block = getPlayerFacedBlock(commander);
        if(!Const.CHESTS.contains(block.getType())) {
            return false;
        }
        chestList.add(block.getLocation());
        return true;
    }

    public boolean unset(Player commander) {
        Block block = getPlayerFacedBlock(commander);
        if (!chestList.contains(block.getLocation())) {
            return false;
        }

        chestList.removeIf(l -> l.equals(block.getLocation()));
        return true;
    }

    public boolean isStart() {
        return null == task;
    }

    public Location getChestLocation() {
        if(chestList.isEmpty()) {
            return null;
        }
        return chestList.get(chestList.size() - 1);
    }

    private Block getPlayerFacedBlock(Player player) {
        return player.getTargetBlock(5);
    }

    public ArrayList<Location> getDistributionChest(){
        return chestList;
    }

    public boolean containsDistributionChest(Player commander) {
        Block block = getPlayerFacedBlock(commander);
        return chestList.contains(block.getLocation());
    }

    private void initializePlayer(Player player) {
        resetBed();
        removeBed(player);

        // ゲーム開始時に所有しているベッドをカウントする
        board.setBedCountScore(player, 0);
        board.setAliveTurnScore(player, 0);
        board.setShowPlayer(player);
    }

    public void kill() {
        Bukkit.getOnlinePlayers().forEach(this::damagePlayer);
    }
    public void damagePlayer(Player player) {
        if(player.isDead()) {
            return;
        }
        removeBed(player);
        if(player.isSleeping()) {
            board.addAliveTurnScore(player, 1);
        } else {
            // 寝ていない場合はkillする
            player.damage(Const.killDamage);
        }
    }

    public void removeBed(Player player) {
        PlayerInventory inventory = player.getInventory();
        Const.BEDS.forEach(bed->{
            if(inventory.containsAtLeast(new ItemStack(bed),1)) {
                inventory.remove(bed);
            }
            // オフハンドに所持していても削除する
            if(bed == inventory.getItemInOffHand().getType()) {
                inventory.setItemInOffHand(new ItemStack(Const.replaceBlock));
            }
        });
    }

    public long getTime() {
        return task.getTime();
    }

    public boolean isNight() {
        return GameCycle.TIME.nightStart.tick <= task.getTime() && task.getTime() <= GameCycle.TIME.nightEnd.tick;
    }

    public boolean isDay() {
        return GameCycle.TIME.dayStart.tick <= task.getTime() && task.getTime() <= GameCycle.TIME.sunsetStart.tick;
    }

    public void giveOutBed() {
        long alivePLayer = Bukkit.getOnlinePlayers().stream().filter(p->!p.isDead()).count();
        int totalAmount = (int)alivePLayer - getLessChair();
        if(0 >= totalAmount) {
            return;
        }
        int chestCnt = chestList.size();
        if(0 == chestCnt) {
            return;
        }
        // チェスト内のベッドを削除
        chestList.stream().filter(loc->Const.CHESTS.contains(world.getBlockAt(loc).getType())).forEach(loc-> {
            Chest chest = (Chest) world.getBlockAt(loc).getState(false);
            Const.BEDS.forEach(bed->{
                chest.getBlockInventory().remove(bed);
                chest.update(true);
            });
        });

        // 1チェストあたりの配給数を指定
        int[] cnt = new int[chestCnt];
        for(int i=0; i<cnt.length; i++) {
            cnt[i] = (i == cnt.length -1) ?
                    (totalAmount/chestCnt) + (totalAmount%chestCnt) : totalAmount/chestCnt;
        }

        for(int i =0; i<chestList.size(); i++) {
            Block block = world.getBlockAt(chestList.get(i));
            if(Const.CHESTS.contains(block.getType())) {
                Chest chest = (Chest) block.getState(false);
                ItemStack[] stack = new ItemStack[cnt[i]];

                // スタックさせずに配給する
                for(int j=0; j <cnt[i] ; j++) {
                    stack[j] = new ItemStack(Const.getRandomBed());
                }
                chest.getBlockInventory().addItem(stack);
                chest.update(true);
            }
        }
    }

    public void resetBed() {
        WorldBorder border = world.getWorldBorder();

        // ワールドボーダー上に配置した場合を考慮して+1マス広くチェックする
        double borderSize = (border.getSize() / 2) + 1;

        double maxBorderX = border.getCenter().getX() + borderSize;
        double maxBorderZ = border.getCenter().getZ() + borderSize;
        double minBorderX = border.getCenter().getX() - borderSize;
        double minBorderZ = border.getCenter().getZ() - borderSize;

        // ワールドボーダー内に存在するベッドを削除する
        for(double y=0; y<Const.maxBorderY; y++) {
            for(double x=minBorderX ; x<maxBorderX ; x++) {
                for(double z=minBorderZ; z<maxBorderZ; z++) {
                    Location loc = new Location(world, x, y, z);
                    Block block = loc.getBlock();
                    if(Const.BEDS.contains(block.getType())) {
                        // ドロップさせないために削除対象の位置情報を保存する
                        bedList.add(block.getLocation());

                        block.setType(Const.replaceBlock);
                    }
                }
            }
        }
        bedList.clear();
    }

    private boolean nearBlock(Location a, Location spawnBedLoc) {
        boolean nearX = false;
        boolean nearY = false;
        boolean nearZ = false;

        // X, Z軸ともに+-1の範囲で同じLocationで有ることをチェックする
        if(a.getX() == spawnBedLoc.getBlockX() || a.getX() + 1 == spawnBedLoc.getBlockX() || a.getX() == spawnBedLoc.getBlockX() + 1) {
            nearX = true;
        }
        if(a.getY() == spawnBedLoc.getBlockY()) {
            nearY = true;
        }
        if(a.getZ() == spawnBedLoc.getBlockZ() || a.getZ() + 1 == spawnBedLoc.getBlockZ() || a.getZ() == spawnBedLoc.getBlockZ() + 1) {
            nearZ = true;
        }
        return nearX && nearY && nearZ;
    }

    public boolean isRemoveBed(Location loc) {
        if(null == loc) return false;
        // 削除時と同等のスポーン位置に発生した場合はtrueとする
        for(Location bed : bedList) {
            if(nearBlock(bed, loc)) {
                return true;
            }
        }
        return false;
    }

    public int getCountDown() {
        return getConfig().getInt("countdown");
    }

    public int getDaySecond() {
        return getConfig().getInt("daysecond");
    }

    public int getLessChair() {
        return getConfig().getInt("lessbed");
    }

    private double taskPerSecond() {
        int daySecond = getDaySecond();
        if(0 == daySecond) {
            return 0d;
        }
        // 現実時間の1秒につき何tick進める必要があるか
        return 12517d / daySecond ;
    }

    public double taskDelay() {
        double tickPerSecond = taskPerSecond();
        if(0 == tickPerSecond) {
            return 0d;
        }
        // 1tickあたりマイクラ内の時間をどれだけすすめるか算出
        double gameLoop = 20d;
        return tickPerSecond / gameLoop;
    }

    public Location getRespawnLocation() {
        if(null == world) {
            return null;
        }
        return world.getWorldBorder().getCenter();
    }
}
