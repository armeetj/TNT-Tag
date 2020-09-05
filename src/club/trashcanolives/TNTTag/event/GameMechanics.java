package club.trashcanolives.TNTTag.event;

import club.trashcanolives.TNTTag.Main;
import club.trashcanolives.TNTTag.player.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

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

    public void tntCheck(Player player)
    {

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
}
