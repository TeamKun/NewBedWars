package net.kunmc.lab.newbedwars;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.math.BigDecimal;

public class DaylightTask extends BukkitRunnable {
    NewBedWars plugin;
    Player commander;
    World world;
    long magnification;
    long time;

    public DaylightTask(NewBedWars plugin, Player commander) {
        this.plugin = plugin;
        this.commander = commander;
        this.world = commander.getWorld();

        BigDecimal b = Config.getInstance().taskDelay(plugin);
        magnification = b.longValue();

        time = 0L;
    }

    @Override
    public void run() {
        try {
            if(null == world) {
                this.cancel();
            }
            world.setTime(time);
            plugin.getLogger().info("world time: " + time);
            time += magnification;
            if(24000 <= time) {
                time =  0L;
                plugin.getLogger().info("world time(リセット): " + time);
            }
        }catch(IllegalStateException ise) {
            plugin.getLogger().warning("時間経過スレッドはすでにスケジュール済です");
            plugin.getLogger().warning(ise.getMessage());
        }catch(Exception e) {
            plugin.getLogger().warning("時間経過処理で例外が発生しました");
            plugin.getLogger().warning(e.getMessage());
        }
    }

    public boolean isDay() {
        if(time >= 0 && time <= 12517) {
            return true;
        }
        return false;
    }

    public boolean isNight() {
        return false;
    }

}
