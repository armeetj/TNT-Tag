package club.trashcanolives.TNTTag.player;

import club.trashcanolives.TNTTag.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class PlayerScoreboard
{
    public static ScoreboardManager manager;
    public static Scoreboard scoreboard;
    public static Objective objective;
    public static Score time;
    public static Score coins;
    public static Score alive;
    public static Score dead;
    public static Score ip;
    public static Score div1;
    public static Score div2;

    private static Main plugin = Main.getPlugin(Main.class);

    public static void scoreGame(Player player, int timeLeft)
    {
        manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("tnttag", "");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "TNT TAG" );

        div1 = objective.getScore(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------------------");
        div1.setScore(6);

        if (plugin.playersInGame.size() == 1)
        {
            time = objective.getScore(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Time: " + ChatColor.WHITE + "Complete");
            time.setScore(5);
            return;
        } else
        {
            time = objective.getScore(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Time: " + ChatColor.WHITE + timeLeft);
            time.setScore(5);
        }

        coins = objective.getScore(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Coins: " + ChatColor.WHITE + "100");
        coins.setScore(4);

        alive = objective.getScore(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Alive: " + ChatColor.WHITE + plugin.playersInGame.size());
        alive.setScore(3);

        dead = objective.getScore(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Dead: " + ChatColor.WHITE + (Bukkit.getOnlinePlayers().size() - plugin.playersLeftGame.size()));
        dead.setScore(2);

        ip = objective.getScore("" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + ChatColor.BOLD + "3d4h.world");
        ip.setScore(1);

        div2 = objective.getScore(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------------------");
        div2.setScore(0);

        player.setScoreboard(scoreboard);
    }

    public static void scoreLobby(Player player)
    {
        manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("tnttag", "");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "TNT TAG" );

        div1 = objective.getScore(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------------------");
        div1.setScore(4);

        time = objective.getScore(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Waiting: " + (ChatColor.WHITE + "" + Bukkit.getOnlinePlayers().size()) + "/" + ChatColor.LIGHT_PURPLE + plugin.gameManager.getPlayersNeeded());
        time.setScore(3);

        coins = objective.getScore(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Coins: " + ChatColor.WHITE + "100");
        coins.setScore(2);

        ip = objective.getScore("" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + ChatColor.BOLD + "3d4h.world");
        ip.setScore(1);

        div2 = objective.getScore(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------------------");
        div2.setScore(0);
        player.setScoreboard(scoreboard);
    }
}
