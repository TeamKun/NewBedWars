package net.kunmc.lab.newbedwars;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Config {

    public static Config getInstance() {
        return SettingHolder.INSTANCE;
    }

    public ArrayList<Material> cannotCraftItems(NewBedWars plugin) {
        List<String> list = plugin.getConfig().getStringList("cannotCraftItems");
        ArrayList<Material> materialList = new ArrayList<>();
        list.stream().filter(l -> null != Material.getMaterial(l)).forEach(l -> materialList.add(Material.getMaterial(l)));
        return materialList;
    }

    public ArrayList<Material> cannotBreakItems(NewBedWars plugin) {
        List<String> list = plugin.getConfig().getStringList("cannotBreakItems");
        ArrayList<Material> materialList = new ArrayList<>();
        list.stream().filter(l -> null != Material.getMaterial(l)).forEach(l -> materialList.add(Material.getMaterial(l)));
        return materialList;
    }

    public int getCountDown(NewBedWars plugin) {
        return plugin.getConfig().getInt("countDownSecond");
    }

    public int getSecondPerDay(NewBedWars plugin) {
        return plugin.getConfig().getInt("secondPerDay");
    }

    private BigDecimal taskPerSecond(NewBedWars plugin) {
        int dayTick = getSecondPerDay(plugin);
        if(0 == dayTick) {
            return BigDecimal.ZERO;
        }
        // 現実時間の1秒につき何tick進める必要があるか
        return BigDecimal.valueOf(24000L).divide(BigDecimal.valueOf(dayTick),5, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal taskDelay(NewBedWars plugin) {
        BigDecimal tickPerSecond = taskPerSecond(plugin);
        if(BigDecimal.ZERO.equals(tickPerSecond)) {
            return BigDecimal.ZERO;
        }
        // 1tickあたりマイクラ内の時間をどれだけすすめるか
        BigDecimal gameLoop = BigDecimal.valueOf(20);
        return tickPerSecond.divide(gameLoop, 5, BigDecimal.ROUND_HALF_UP);
    }

    public static class SettingHolder {
        private static final Config INSTANCE = new Config();
    }
}
