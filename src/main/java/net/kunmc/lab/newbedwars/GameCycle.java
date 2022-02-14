package net.kunmc.lab.newbedwars;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class GameCycle extends BukkitRunnable {
    private final NewBedWars plugin;
    private final World world;
    private double time;
    private final double dayMagnification;
    private double sunsetMagnification;
    private CountdownTask countDownTask;
    private boolean isCountdown = true;
    enum TIME {
        dayStart(0d),
        sunsetStart(12517d),
        nightStart(13000d),
        nightEnd(24000d);
        final double tick;
        TIME(double tick) {
            this.tick = tick;
        }
    }

    public GameCycle(NewBedWars plugin, World world) {
        this.plugin = plugin;
        this.world = world;
        dayMagnification = plugin.taskDelay();

        time = TIME.dayStart.tick;
    }

    @Override
    public void run() {
        try {
            if(null == world) {
                this.cancel();
            }
            world.setTime(Math.round(time));

            // 日没までのカウントダウン開始
            if(TIME.sunsetStart.tick <= time && isCountdown) {
                int cntDownSecond = plugin.getCountDown();
                countDown(cntDownSecond);

                setSunsetSpeed(cntDownSecond);
                isCountdown = false;
            }

            if(TIME.nightStart.tick <= time && null != countDownTask && !countDownTask.isCancelled()) {
                // 設定時間によっては夜が始まってもカウントダウンが終わり切らない場合があるため
                // カウントダウンが終わるまでは日の入りの速度で時間経過させる
                time += sunsetMagnification;
            }else if(TIME.nightStart.tick <= time) {
                time = TIME.dayStart.tick;
                plugin.resetBed();
                plugin.giveOutBed();
                isCountdown = true;
                countDownTask = null;
            } else if (TIME.sunsetStart.tick <= time) {
                time += sunsetMagnification;
            } else {
                // 日中は設定した速度で時間経過
                time += dayMagnification;
            }
        }catch(IllegalStateException ise) {
            plugin.getLogger().warning("時間経過スレッドはすでにスケジュール済です");
            plugin.getLogger().warning(ise.getMessage());
        }catch(Exception e) {
            plugin.getLogger().warning("時間経過処理で例外が発生しました");
            plugin.getLogger().warning(e.getMessage());
        }
    }

    public long getTime() {
        return (long)time;
    }

    private void setSunsetSpeed(int cntDownSecond) {
        // 日の入りの時間経過速度を計算
        double cntDownSpeed = 20d * cntDownSecond;
        double untilNight = TIME.nightStart.tick - time;
        sunsetMagnification = untilNight / cntDownSpeed;
    }

    private void countDown(int cntDownSecond) {
        countDownTask = new CountdownTask(cntDownSecond);
        countDownTask.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void cancel() {
        if(null != countDownTask && !countDownTask.isCancelled()) {
            countDownTask.stop();
        }
        super.cancel();
    }

    private class CountdownTask extends BukkitRunnable {
        private int countdown;

        CountdownTask(int countdown) {
            this.countdown = countdown;
        }

        @Override
        public void run() {
            BaseComponent[] message = new ComponentBuilder("残り秒数： " + countdown).color(ChatColor.WHITE).create();
            Bukkit.getOnlinePlayers().stream().filter(p -> !p.isDead()).forEach(p -> p.sendActionBar(message));
            countdown --;
            if(countdown < 0) {
                BaseComponent[] timeupMessage = new ComponentBuilder("").color(ChatColor.WHITE).create();
                Bukkit.getOnlinePlayers().stream().filter(p -> !p.isDead()).forEach(p -> p.sendActionBar(timeupMessage));
                this.cancel();
            }
        }

        @Override
        public void cancel() {
            super.cancel();
            plugin.kill();
        }

        public void stop() {
            super.cancel();
        }
    }
}
