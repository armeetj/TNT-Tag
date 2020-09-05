package club.trashcanolives.TNTTag;

import club.trashcanolives.TNTTag.event.GameMechanics;
import club.trashcanolives.TNTTag.game.GameManager;
import club.trashcanolives.TNTTag.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin
{
    public HashMap<UUID, PlayerManager> playerManager = new HashMap<UUID, PlayerManager>();
    public ArrayList<Player> playersInGame = new ArrayList<Player>();
    public ArrayList<Player> playersLeftGame = new ArrayList<Player>();

    public GameMechanics gameMechanics;
    public GameManager gameManager;

    @Override
    public void onEnable()
    {
        loadConfig();
        instanceClasses();
        getServer().getPluginManager().registerEvents(new GameMechanics(), this);
        getServer().getConsoleSender().sendMessage("§c§lT§f§lN§c§lT Tag §a§lEnabled!");
    }

    @Override
    public void onDisable()
    {
        getServer().getConsoleSender().sendMessage("§c§lT§f§lN§c§lT Tag §c§lDisabled!");
    }

    public void loadConfig()
    {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void instanceClasses()
    {
        gameMechanics = new GameMechanics();
        gameManager = new GameManager();
    }
}
