package fr.ariloxe.arena.listeners;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.utils.tech.PlayerStats;
import fr.kanao.core.PyraliaCore;
import fr.kanao.core.player.CorePlayer;
import fr.kanao.core.player.Rank;
import fr.kanao.core.utils.ItemCreator;
import fr.kanao.core.utils.mojang.Tasks;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * @author Ariloxe
 */
public class JoinListener implements Listener {

    private final Location location;
    private final ItemStack joinArena = new ItemCreator(Material.DIAMOND_SWORD).name("§6Rejoindre l'Arène §8§l▪ §7Clic-droit").get();
    private final ItemStack leave = new ItemCreator(Material.BED).name("§cRetour au Hub §8§l▪ §7Clic-droit").get();
    private final ItemStack cosmetics = new ItemCreator(Material.EMERALD).name("§bCosmétiques §8§l▪ §7Clic-droit").get();

    public JoinListener(){
        World world = Bukkit.getWorld("world");
        world.loadChunk(0, 1);
        world.getWorldBorder().setCenter(0, 0);
        world.getWorldBorder().setSize(200);
        Location loc = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        loc.setY(200);
        this.location = loc.clone().add(0, 1, 0);


        Material m = Material.BARRIER;
        Material g = Material.BARRIER;
        for (int i = -15; i <= 15; i++) {

            for (int h = 1; h <= 3; h++) {
                loc.clone().add(-15, h, i).getBlock().setType(m);
                loc.clone().add(15, h, i).getBlock().setType(m);

                loc.clone().add(i, h, 15).getBlock().setType(m);
                loc.clone().add(i, h, -15).getBlock().setType(m);
            }

            for (int j = -15; j <= 15; j++) {
                loc.clone().add(i, 0, j).getBlock().setType(g);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        playerJoinEvent.setJoinMessage(null);
        Player player = playerJoinEvent.getPlayer();
        ArenaAPI.getInstance().getArenaStatsMap().putIfAbsent(player.getUniqueId(), new PlayerStats(player.getUniqueId()));
        Tasks.runLater(()-> player.teleport(location), 3);
        if(PyraliaCore.getInstance().getModerationManager().getModeratorsList().contains(player.getUniqueId()))
            return;

        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);

        //    player.getInventory().addItem(cosmetics);
        player.getInventory().addItem(cosmetics);
        player.getInventory().setItem(4, joinArena);
        player.getInventory().setItem(7, leave);

        CorePlayer corePlayer = PyraliaCore.getInstance().getPlayerManager().getPlayer(player);
        if(corePlayer.getRank() != Rank.PLAYER && !corePlayer.isNick())
            Bukkit.broadcastMessage("§8» " + corePlayer.getRank().getName() + " " + player.getName() + "§f a rejoint l'arène !");
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent){
        playerQuitEvent.setQuitMessage(null);
    }

}
