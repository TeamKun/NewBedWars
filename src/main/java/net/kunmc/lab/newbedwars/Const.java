package net.kunmc.lab.newbedwars;

import org.bukkit.Material;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

public class Const {
    public final static String MAIN_COMMAND = "nbw";

    // command
    public final static String START = "start";
    public final static String STOP = "stop";
    public final static String INFO = "info";
    public final static String CONF = "conf";
    public final static String SET = "set";
    public final static String UNSET = "unset";

    // bed
    public final static Set<Material> BEDS = EnumSet.of(
            Material.BLACK_BED, Material.WHITE_BED, Material.ORANGE_BED
            , Material.MAGENTA_BED, Material.LIGHT_BLUE_BED, Material.YELLOW_BED
            , Material.LIME_BED, Material.PINK_BED, Material.GRAY_BED
            , Material.LIGHT_GRAY_BED, Material.CYAN_BED, Material.PURPLE_BED
            , Material.BLUE_BED, Material.BROWN_BED, Material.GREEN_BED
            , Material. RED_BED
    );
    public static Material getRandomBed() {
        int item = new Random().nextInt(BEDS.size());
        int i = 0;
        for(Material bed : BEDS) {
            if(i == item) {
                return bed;
            }
            i ++;
        }
        return null;
    }

    // chest
    public final static Set<Material> CHESTS = EnumSet.of(
            Material.CHEST
    );

    // game setting
    public final static double maxBorderY = 256;
    public final static double killDamage = 100d;
    public final static Material replaceBlock = Material.AIR;

}