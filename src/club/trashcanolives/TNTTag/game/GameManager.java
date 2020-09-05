package club.trashcanolives.TNTTag.game;

import club.trashcanolives.TNTTag.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class GameManager implements Listener
{
    private Main plugin = Main.getPlugin(Main.class);
    private int lobbyCountdown = 5;
    private int explosionCountdown = 30;
    private int playersNeeded = 2;
    private boolean isStarted;

    Location lobbySpawn;
    Location gameSpawn;

    public void setupGame()
    {
        this.gameSpawn = new Location(Bukkit.getServer().getWorld(plugin.getConfig().getString("GameSpawn.world")), plugin.getConfig().getDouble("GameSpawn.x"),  plugin.getConfig().getDouble("GameSpawn.y"),  plugin.getConfig().getDouble("GameSpawn.z"));
        this.lobbySpawn = new Location(Bukkit.getServer().getWorld(plugin.getConfig().getString("LobbySpawn.world")), plugin.getConfig().getDouble("LobbySpawn.x"),  plugin.getConfig().getDouble("LobbySpawn.y"),  plugin.getConfig().getDouble("LobbySpawn.z"));
    }

    public void lobbyWait(Player player)
    {
        int online = Bukkit.getOnlinePlayers().size();
        Bukkit.getServer().broadcastMessage("§d§l(§f§l" + online + "§d§l/" + playersNeeded);
        playerCheck(online);
    }

    public void playerCheck(int online)
    {
        if (online == playersNeeded)
        {
            lobbyCountdown();
            setStarted(true);
        }
    }

    public void gameStart()
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            player.teleport(gameSpawn);
            player.setWalkSpeed(3);
        }
        explosionCountdown();
    }

    public void gameStop()
    {
        //implements bungeecord
    }

    public void explosionCountdown()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    if (explosionCountdown > 0)
                    {
                        explosionCountdown--;
                    }else
                    {
                        plugin.gameMechanics.tntCheck(player);
                        explosionCountdown = 30;
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20l);
    }

    public void lobbyCountdown()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (lobbyCountdown > 0)
                {
                    lobbyCountdown--;
                }else
                {
                    gameStart();
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20l);
    }

    public boolean isStarted()
    {
        return isStarted;
    }

    public void setStarted(boolean started)
    {
        isStarted = started;
    }
}
