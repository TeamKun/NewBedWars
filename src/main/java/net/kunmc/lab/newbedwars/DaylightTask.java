package net.kunmc.lab.newbedwars;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.math.BigDecimal;

public class DaylightTask extends BukkitRunnable {
    private final NewBedWars plugin;
    private final World world;
    private long time;
    private final long dayMagnification;
    private long nightMagnification = 120L;
    private CountdownTask countDownTask;
    private boolean isCountdown = true;
    private long nightStart = 13000L;
    enum TIME {
        dayStart(0L),
        bedIn(12517L),
        nightEnd(24000L);
        private final long tick;
        TIME(long tick) {
            this.tick = tick;
        }
    }

    public DaylightTask(NewBedWars plugin, Player commander) {
        this.plugin = plugin;
        this.world = commander.getWorld();

        BigDecimal b = Config.getInstance().taskDelay(plugin);
        dayMagnification = b.longValue();

        time = TIME.dayStart.tick;
    }

    @Override
    public void run() {
        try {
            if(null == world) {
                this.cancel();
            }
            world.setTime(time);
            if(TIME.bedIn.tick <= time && isCountdown) {
                // カウントダウン
                int cntDownSecond = Config.getInstance().getCountDown(plugin);
                doCountDown(cntDownSecond);

                updNightMagnification(cntDownSecond);
                plugin.getLogger().info("updNightMagnification: " + nightMagnification);

                isCountdown = false;
            }
            if(nightStart <= time) {
                if(countDownTask.isCancelled()) {
                    // フライング起床のためキル
                    plugin.killPlayer();
                }
                // 夜間は固定速度でスキップ
                time += nightMagnification;
                plugin.getLogger().info("world time(night): " + time);
            } else {
                // 日中は設定した速度で時間経過
                time += dayMagnification;
                plugin.getLogger().info("world time(day): " + time);
            }

            if(TIME.nightEnd.tick <= time) {
                time = TIME.dayStart.tick;
                nightMagnification = 120L;
                nightStart = 13000L;
                isCountdown = true;
                plugin.getLogger().info("world time(リセット): " + time);
                countDownTask = null;
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
        return false;
    }

    public boolean isNight() {
        return false;
    }

    public long getTime() {
        return time;
    }

    private void doCountDown(int cntDownSecond) {
        countDownTask = new CountdownTask(cntDownSecond);
        countDownTask.runTaskTimer(plugin, 0, 20);
    }

    private void updNightMagnification(int cntDownSecond) {
        // 全員就寝～朝までの経過速度を計算・更新する
        BigDecimal afterCountdownTick = BigDecimal.valueOf(time + (dayMagnification * 20 * cntDownSecond));
        nightStart = afterCountdownTick.longValue();

        BigDecimal untilDayStart = BigDecimal.valueOf(24000L).subtract(afterCountdownTick);
        nightMagnification = untilDayStart.divide(BigDecimal.valueOf(20L).multiply(BigDecimal.valueOf(cntDownSecond)), 5, BigDecimal.ROUND_HALF_UP).longValue();
    }

    private class CountdownTask extends BukkitRunnable {
        private int countdown;

        CountdownTask(int countdown) {
            this.countdown = countdown;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            if(countdown >= 0) {
                BaseComponent[] message = new ComponentBuilder("残り秒数： " + countdown ).color(ChatColor.WHITE).create();
                Bukkit.getOnlinePlayers().stream().filter(p->!p.isDead()).forEach(p-> p.sendActionBar(message));
                countdown --;
                return;
            }
            plugin.killPlayer();
            this.cancel();
        }
    }
}
