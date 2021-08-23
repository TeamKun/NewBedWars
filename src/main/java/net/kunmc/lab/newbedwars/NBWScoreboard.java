package net.kunmc.lab.newbedwars;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class NBWScoreboard {
    private final Scoreboard scoreboard;
    private final static String BED_COUNT = "bedCount";
    private final static String BED_NAME = "個のベッドを所有";

    private final static String ALIVE_TURN_COUNT = "aliveTurn";
    private final static String ALIVE_TURN_NAME = "生存日数";

    public NBWScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        // ベッドの所有数
        Objective objective1 = scoreboard.registerNewObjective(BED_COUNT, "dummy");
        objective1.setDisplayName(BED_NAME);
        objective1.setDisplaySlot(DisplaySlot.BELOW_NAME);

        // 生き残りターン数
        Objective objective2 = scoreboard.registerNewObjective(ALIVE_TURN_COUNT, "dummy");
        objective2.setDisplayName(ALIVE_TURN_NAME);
        objective2.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private int getScore(Player player, String name) {
        Score score = null;
        try {
            Objective objective = scoreboard.getObjective(name);
            score = objective.getScore(player);
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
        return score.getScore();
    }

    @SuppressWarnings("deprecation")
    private void setScore(Player player, String name, int i) {
        Score score;
        try {
            Objective objective = scoreboard.getObjective(name);
            score = objective.getScore(player);
        } catch(NullPointerException e) {
            e.printStackTrace();
            return;
        }
        score.setScore(i);
    }

    public void setBedCountScore(Player player, int i) {
        setScore(player, BED_COUNT, i);
    }

    public int getBedCountScore(Player player) {
        return getScore(player, BED_COUNT);
    }

    public void addBedCountScore(Player player, int i) {
        int score = getBedCountScore(player);
        setBedCountScore(player, score + i);
    }

    public void minusBedCountScore(Player player, int i) {
        int score = getBedCountScore(player);
        setBedCountScore(player, score - i);
    }

    public int getAliveTurnScore(Player player){
        return getScore(player, ALIVE_TURN_COUNT);
    }

    public void setAliveTurnScore(Player player, int i) {
        setScore(player, ALIVE_TURN_COUNT, i);
    }

    public void addAliveTurnScore(Player player, int i) {
        int score = getAliveTurnScore(player);
        setAliveTurnScore(player, score + i);
    }

    public void setShowPlayer(Player player) {
        player.setScoreboard(scoreboard);
    }
}
