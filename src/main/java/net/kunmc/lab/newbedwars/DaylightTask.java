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
    private long sunsetMagnification = 120L;
    private long nightMagnification = 120L;
    private CountdownTask countDownTask;
    private boolean isCountdown = true;
    enum TIME {
        dayStart(0L),
        sunsetStart(12517L),
        nightStart(13000L),
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
            if(TIME.sunsetStart.tick <= time && isCountdown) {
                // カウントダウン
                int cntDownSecond = Config.getInstance().getCountDown(plugin);
                doCountDown(cntDownSecond);

                updNightMagnification(cntDownSecond);
                plugin.getLogger().info("updNightMagnification: " + nightMagnification);

                isCountdown = false;
            }

            if(TIME.nightStart.tick <= time) {
                if(countDownTask.isCancelled()) {
                    // フライング起床のためキル
                    plugin.killPlayer();
                }
                // 夜間は固定速度でスキップ
                time += nightMagnification;
                plugin.getLogger().info("world time(night): " + time);
            } else if (TIME.sunsetStart.tick <= time) {
                // 日の入りは固定速度でスキップ
                time += sunsetMagnification;
                plugin.getLogger().info("world time(sunset): " + time);
            } else {
                // 日中は設定した速度で時間経過
                time += dayMagnification;
                plugin.getLogger().info("world time(day): " + time);
            }

            if(TIME.nightEnd.tick <= time) {
                time = TIME.dayStart.tick;
                nightMagnification = 120L;
                sunsetMagnification = 120L;
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
        // 日の入りの時間経過速度を計算
        BigDecimal cntDownSpeed = BigDecimal.valueOf(20L).multiply(BigDecimal.valueOf(cntDownSecond));
        BigDecimal untilNight = BigDecimal.valueOf(TIME.nightStart.tick).subtract(BigDecimal.valueOf(time));
        sunsetMagnification = untilNight.divide(cntDownSpeed, 5, BigDecimal.ROUND_HALF_UP).longValue();

        // 夜の時間経過を速度を計算
        BigDecimal nightSpeed = BigDecimal.valueOf(20L).multiply(BigDecimal.valueOf(5L));
        BigDecimal untilDayStart = BigDecimal.valueOf(24000L).subtract(BigDecimal.valueOf(TIME.nightStart.tick));
        nightMagnification = untilDayStart.divide(nightSpeed, 5, BigDecimal.ROUND_HALF_UP).longValue();
    }

    private class CountdownTask extends BukkitRunnable {
        private int countdown;

        CountdownTask(int countdown) {
            this.countdown = countdown;
        }

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
