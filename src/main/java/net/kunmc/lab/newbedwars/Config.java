package net.kunmc.lab.newbedwars;

import org.bukkit.Material;
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

    public static class SettingHolder {
        private static final Config INSTANCE = new Config();
    }
}
