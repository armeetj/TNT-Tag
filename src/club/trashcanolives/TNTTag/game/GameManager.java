package club.trashcanolives.TNTTag.game;

import club.trashcanolives.TNTTag.Main;
import club.trashcanolives.TNTTag.player.PlayerManager;
import club.trashcanolives.TNTTag.player.PlayerScoreboard;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

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
        if (plugin.getConfig().contains("GameSpawn"))
        {
            this.gameSpawn = new Location(Bukkit.getServer().getWorld(
                    plugin.getConfig().getString("GameSpawn.world")),
                    plugin.getConfig().getDouble("GameSpawn.x"),
                    plugin.getConfig().getDouble("GameSpawn.y"),
                    plugin.getConfig().getDouble("GameSpawn.z"),
                    (float) plugin.getConfig().getDouble("GameSpawn.yaw"),
                    (float) plugin.getConfig().getDouble("GameSpawn.pitch"));
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Game spawn found in config!");
        }

        if (plugin.getConfig().contains("LobbySpawn"))
        {
            this.gameSpawn = new Location(Bukkit.getServer().getWorld(
                    plugin.getConfig().getString("LobbySpawn.world")),
                    plugin.getConfig().getDouble("LobbySpawn.x"),
                    plugin.getConfig().getDouble("LobbySpawn.y"),
                    plugin.getConfig().getDouble("LobbySpawn.z"),
                    (float) plugin.getConfig().getDouble("LobbySpawn.yaw"),
                    (float) plugin.getConfig().getDouble("LobbySpawn.pitch"));
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Lobby spawn found in config!");
        }

        for (Player player : Bukkit.getOnlinePlayers())
        {
            plugin.playersInGame.add(player);
            plugin.playerManager.put(player.getUniqueId(), new PlayerManager(player.getUniqueId(), false, 0, false, false));
            lobbyWait(player);

            player.setFoodLevel(20);
            player.setHealth(20);
            player.setGameMode(GameMode.ADVENTURE);

        }
    }

    public void lobbyWait(Player player)
    {
        int online = Bukkit.getOnlinePlayers().size();
        Bukkit.getServer().broadcastMessage("§d§l(§f§l" + online + "§d§l/" + playersNeeded);
        playerCheck(online);
    }

    public boolean playerCheck(int online)
    {
        if (online >= playersNeeded)
        {
            if (!isStarted)
            {
                lobbyCountdown();
                setStarted(true);
            }
            return true;
        }
        return false;
    }

    public void gameStart()
    {
        explosionCountdown();
        plugin.gameMechanics.tntPlacer();
        for (Player player : Bukkit.getOnlinePlayers())
        {
            player.teleport(gameSpawn);
            player.setWalkSpeed(0.5f);
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
        }
    }

    public void gameStop(Player player)
    {
        //implements bungeecord
        player.setWalkSpeed(.2f);
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        player.getInventory().setHelmet(null);
        player.getInventory().clear();

        player.setGameMode(GameMode.ADVENTURE);
        plugin.playersInGame.clear();
        plugin.playerManager.clear();
        player.setPlayerListName(ChatColor.WHITE + player.getName());

    }

    public void explosionCountdown()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (explosionCountdown > 0)
                {
                    explosionCountdown--;
                    for (Player player : Bukkit.getOnlinePlayers())
                    {
                        PlayerScoreboard.scoreGame(player, explosionCountdown);
                    }
                } else
                {
                    if (plugin.playersInGame.size() == 1)
                    {
                        Player player = plugin.playersInGame.get(0);
                        player.sendTitle("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "WINNER", "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + "Last one standing \\o/ !");
                        for (Player online : Bukkit.getOnlinePlayers())
                        {
                            gameStop(online);

                        }
                        this.cancel();
                        return;
                    }else
                    {
                        plugin.gameMechanics.tntCheck();
                        Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.RED + "BOOM! Players have been eliminated and tnt has been placed!");
                        for (Player online : Bukkit.getOnlinePlayers())
                        {
                            online.sendTitle(ChatColor.RED + "" + ChatColor.RED + "BOOM!", "Players eliminated, TNT Placed");
                            online.sendTitle(ChatColor.RED + "" + ChatColor.MAGIC + ";;;" + ChatColor.DARK_RED + ChatColor.BOLD + " RUN " + ChatColor.RED + ChatColor.MAGIC + ";;;", "");
                            online.playSound(online.getLocation(), Sound.EXPLODE, 1, 1);
                        }
                        explosionCountdown = 30;
                        plugin.gameMechanics.tntPlacer();
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
                    if(playerCheck(plugin.playersInGame.size()))
                    {
                        lobbyCountdown--;
                        for (Player online : Bukkit.getOnlinePlayers())
                        {
                            online.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + lobbyCountdown, ChatColor.ITALIC + "game starting...");
                            online.playSound(online.getLocation(), Sound.ARROW_HIT, 2, 2);
                        }

                    } else
                    {
                        Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + playersNeeded + " players needed to start!");
                        this.cancel();
                        lobbyCountdown = 5;
                    }
                }else
                {
                    this.cancel();
                    gameStart();
                    this.cancel();
                    for (Player online : Bukkit.getOnlinePlayers())
                    {
                        online.sendTitle(ChatColor.RED + "" + ChatColor.RED + "Good Luck!", "Try not to explode ;)");
                        online.playSound(online.getLocation(), Sound.EXPLODE, 1, 1);
                    }
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
    public int getPlayersNeeded()
    {
        return playersNeeded;
    }

    public void setPlayersNeeded(int playersNeeded)
    {
        this.playersNeeded = playersNeeded;
    }
}
