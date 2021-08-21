package net.kunmc.lab.newbedwars;

import org.bukkit.Material;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Config {

    public static Config getInstance() {
        return SettingHolder.INSTANCE;
    }

    public ArrayList<Material> nonCraftableItems(NewBedWars plugin) {
        List<String> list = plugin.getConfig().getStringList("noncraftable");
        ArrayList<Material> materialList = new ArrayList<>();
        list.stream().filter(l -> null != Material.getMaterial(l)).forEach(l -> materialList.add(Material.getMaterial(l)));
        return materialList;
    }

    public ArrayList<Material> nonBreakable(NewBedWars plugin) {
        List<String> list = plugin.getConfig().getStringList("nonbreakable");
        ArrayList<Material> materialList = new ArrayList<>();
        list.stream().filter(l -> null != Material.getMaterial(l)).forEach(l -> materialList.add(Material.getMaterial(l)));
        return materialList;
    }

    public int dayTrik(NewBedWars plugin) {
        return plugin.getConfig().getInt("secondperday");
    }

    public long taskPerSecond(NewBedWars plugin) {
        int dayTrik = dayTrik(plugin);
        if(0 == dayTrik) {
            return 0;
        }
        // 現実時間の1秒につき何trikを必要とするか
        long trikPerSecond = 24000L / dayTrik;
        return trikPerSecond;
    }

    public BigDecimal taskDelay(NewBedWars plugin) {
        BigDecimal trikPerSecond = BigDecimal.valueOf(taskPerSecond(plugin));
        if(BigDecimal.ZERO == trikPerSecond) {
            return BigDecimal.ZERO;
        }
        // 1秒=20tick と比較して何倍の速さですすめる必要があるか
        BigDecimal gameLoop = BigDecimal.valueOf(20);
        BigDecimal i = trikPerSecond.divide(gameLoop, 5, BigDecimal.ROUND_HALF_UP);
        return i;
    }

    public static class SettingHolder {
        private static final Config INSTANCE = new Config();
    }
}
