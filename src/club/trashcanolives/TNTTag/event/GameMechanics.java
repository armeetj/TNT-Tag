package club.trashcanolives.TNTTag.event;

import club.trashcanolives.TNTTag.Main;
import club.trashcanolives.TNTTag.player.PlayerManager;
import club.trashcanolives.TNTTag.player.PlayerScoreboard;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class GameMechanics implements Listener
{
    private Main plugin = Main.getPlugin(Main.class);
    private int taggersSelected;

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        if (!plugin.gameManager.isStarted())
        {
            Player player = event.getPlayer();
            if (plugin.playersLeftGame.contains(player))
            {
                //TODO bungee push back
            }
            event.setJoinMessage("");
            plugin.playerManager.put(player.getUniqueId(), new PlayerManager(player.getUniqueId(), false, 0, false, false));
            plugin.playersInGame.add(player);
            plugin.gameManager.lobbyWait(player);
            PlayerScoreboard.scoreLobby(player);

        } else
        {
            //TODO: bungeecord push back (game has already started)
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        event.setQuitMessage("");
        plugin.playerManager.remove(player.getUniqueId());
        plugin.playersInGame.remove(player);
        plugin.playersLeftGame.add(player);
    }

    public void tntCheck()
    {
        for (PlayerManager playerManager : plugin.playerManager.values())
        {
            if (playerManager.isHasTNT())
            {
                playerManager.setHasTNT(false);
                playerManager.setDead(true);

                Player player = Bukkit.getPlayer(playerManager.getUuid());
                plugin.playersInGame.remove(player);
                Location playerLocation = player.getLocation();
                playerLocation.getWorld().createExplosion(playerLocation.getX(), playerLocation.getY(), playerLocation.getZ(), 1, false, false);
                player.setPlayerListName(ChatColor.GRAY + player.getName());
                player.getInventory().clear();
                player.getInventory().setHelmet(null);
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    @EventHandler
    public void playerTNTTag(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player)
        {
            event.setDamage(0);
            Player tagger = (Player) event.getDamager();
            PlayerManager taggerManager = plugin.playerManager.get(tagger.getUniqueId());

            Player tagged = (Player) event.getEntity();
            PlayerManager taggedManager = plugin.playerManager.get(tagged.getUniqueId());

            if (taggerManager.isHasTNT() && !taggedManager.isHasTNT())
            {
                taggerManager.setHasTNT(false);
                taggedManager.setHasTNT(true);
                tagger.getInventory().setHelmet(null);
                tagger.getInventory().clear();

                tagged.getInventory().setHelmet(new ItemStack(Material.TNT));
                tagged.getInventory().addItem(new ItemStack(Material.TNT));
                tagged.playSound(tagger.getLocation(), Sound.CREEPER_HISS, 1, 1);
                tagged.sendTitle(ChatColor.RED + "" + "TAGGED", ChatColor.DARK_RED + "You've been tagged!");
            } else if (taggerManager.isHasTNT() && taggedManager.isHasTNT())
            {
                tagger.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "This player already has TNT!");
                event.setCancelled(true);
                return;
            }
        }
    }


    public void tntPlacer()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (taggersSelected != 3)
                {
                    Player randomPlayer = plugin.playersInGame.get(ThreadLocalRandom.current().nextInt(plugin.playersInGame.size()));
                    PlayerManager randomPlayerManager = plugin.playerManager.get(randomPlayer.getUniqueId());

                    if (!randomPlayerManager.isDead() && !randomPlayerManager.isHasTNT())
                    {
                        randomPlayerManager.setHasTNT(true);
                        randomPlayer.getInventory().setHelmet(new ItemStack(Material.TNT));
                        randomPlayer.setPlayerListName(ChatColor.RED + "" + ChatColor.BOLD + randomPlayer.getName());
                        randomPlayer.getInventory().addItem(new ItemStack(Material.TNT));
                        randomPlayer.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are IT!! Tag someone before the timer runs out!");
                    }else
                    {
                        this.cancel();
                        taggersSelected = 0;
                    }

                }
            }
        }.runTaskTimer(plugin, 20, 20);

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event)
    {
        if (plugin.gameManager.isStarted())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventory(InventoryOpenEvent event)
    {
        if (plugin.gameManager.isStarted())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void damageEvent(EntityDamageEvent event)
    {
        if (plugin.gameManager.isStarted())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void foodLevelChange(FoodLevelChangeEvent event)
    {
        if (plugin.gameManager.isStarted())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event)
    {
        if (plugin.gameManager.isStarted())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event)
    {
        if (plugin.gameManager.isStarted())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockBurn(BlockBurnEvent event)
    {
        if (plugin.gameManager.isStarted())
        {
            event.setCancelled(true);
        }
    }


}
