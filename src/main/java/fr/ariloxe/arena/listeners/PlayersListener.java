package fr.ariloxe.arena.listeners;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.kits.ArenaKit;
import fr.ariloxe.arena.tasks.ExecutorTask;
import fr.ariloxe.arena.utils.PlayerUtils;
import fr.ariloxe.arena.utils.tech.PlayerStats;
import fr.kanao.core.PyraliaCore;
import fr.kanao.core.cosmetics.uis.CosmeticsMainInventory;
import fr.kanao.core.tech.redis.messaging.ChannelType;
import fr.kanao.core.utils.ItemCreator;
import fr.kanao.core.utils.mojang.Tasks;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;


/**
 * @author Ariloxe
 */
public class PlayersListener implements Listener {

    private final ItemStack cosmetics = new ItemCreator(Material.EMERALD).name("§bCosmétiques §8§l▪ §7Clic-droit").get();
    private final ItemStack joinArena = new ItemCreator(Material.DIAMOND_SWORD).name("§6Rejoindre l'Arène §8§l▪ §7Clic-droit").get();
    private final ItemStack leave = new ItemCreator(Material.BED).name("§cRetour au Hub §8§l▪ §7Clic-droit").get();

    private final CosmeticsMainInventory cosmeticsMainInventory = new CosmeticsMainInventory();


    private final ArenaAPI instance;
    private final List<Location> locationList = new ArrayList<>();

    public PlayersListener(ArenaAPI instance){
        this.instance = instance;

        ExecutorTask executorTask = new ExecutorTask("Suppréssion des blocs", 60, ()->{
            locationList.forEach(location -> location.getBlock().setType(Material.AIR));
            locationList.clear();
        });
        executorTask.setRepeatable();

        ArenaAPI.getInstance().getGlobalExecutor().addTask(executorTask);
    }

    @EventHandler
    public void onLiquid(BlockFromToEvent blockSpreadEvent) {
        blockSpreadEvent.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent playerDropItemEvent){
        playerDropItemEvent.setCancelled(true);

        if(playerDropItemEvent.getItemDrop() != null && playerDropItemEvent.getItemDrop().getItemStack().getType() == Material.NETHER_STAR){
            ArenaAPI.getInstance().getArenaKitMap().get(playerDropItemEvent.getPlayer().getUniqueId()).onLeftclick(playerDropItemEvent.getItemDrop().getItemStack());
        }
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent weatherChangeEvent){
        weatherChangeEvent.setCancelled(true);
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent entitySpawnEvent){
        if(!(entitySpawnEvent.getEntity() instanceof Item))
            entitySpawnEvent.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(entityDamageByEntityEvent.getEntity().getLocation().getY() > 200){
            entityDamageByEntityEvent.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onRightclick(PlayerInteractEvent playerInteractEvent){
        if(playerInteractEvent.getItem() == null)
            return;

        if(playerInteractEvent.getItem().getType() == Material.NETHER_STAR){
            if(ArenaAPI.getInstance().getArenaKitMap().containsKey(playerInteractEvent.getPlayer().getUniqueId())){
                ArenaKit arenaKit = ArenaAPI.getInstance().getArenaKitMap().get(playerInteractEvent.getPlayer().getUniqueId());
                if(playerInteractEvent.getAction().name().contains("RIGHT"))
                    arenaKit.onRightClick(playerInteractEvent.getItem());
                else
                    arenaKit.onLeftclick(playerInteractEvent.getItem());
            }
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent foodLevelChangeEvent){
        foodLevelChangeEvent.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent playerInteractEvent){
        if(playerInteractEvent.getItem() == null)
            return;

        if(playerInteractEvent.getPlayer().getLocation().getY() >= 200 && !playerInteractEvent.getPlayer().getInventory().contains(Material.GOLDEN_APPLE)){
            if(playerInteractEvent.getItem().getType() == Material.DIAMOND_SWORD)
                instance.getSelectMainInventory().open(playerInteractEvent.getPlayer());
            else if(playerInteractEvent.getItem().getType() == Material.BED)
                Tasks.runAsync(()-> PyraliaCore.getInstance().getPubSub().send(ChannelType.INFRA.getName(), "sendToHub " + playerInteractEvent.getPlayer().getName()));
            else if(playerInteractEvent.getItem().getType() == Material.EMERALD)
                cosmeticsMainInventory.open(playerInteractEvent.getPlayer());

        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent blockPlaceEvent){
        if(blockPlaceEvent.getBlockPlaced().getLocation().getY() > 130){
            blockPlaceEvent.setCancelled(true);
            return;
        }

        if(blockPlaceEvent.getPlayer().getInventory().getItemInHand().hasItemMeta()){
            blockPlaceEvent.setCancelled(true);
            return;
        }

        locationList.add(blockPlaceEvent.getBlock().getLocation());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent blockBreakEvent){
        if(blockBreakEvent.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;


        if(!locationList.contains(blockBreakEvent.getBlock().getLocation())){
            blockBreakEvent.getPlayer().sendMessage("§7§l❘ §cVous ne pouvez pas casser un bloc qui n'a pas été posé par un joueur.");
            blockBreakEvent.setCancelled(true);
            return;
        }

        locationList.remove(blockBreakEvent.getBlock().getLocation());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent playerRespawnEvent){
        Player player = playerRespawnEvent.getPlayer();
        player.getInventory().clear();

        player.teleport(new Location(Bukkit.getWorld("world"), 0, 202, 0));

        player.getInventory().setHeldItemSlot(4);

        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));


        player.setMaxHealth(20);
        player.setHealth(player.getMaxHealth());
        player.setAllowFlight(false);
        player.setFoodLevel(20);

        player.getInventory().addItem(cosmetics);
    //    player.getInventory().setItem(2, new ItemCreator(Material.SKULL_ITEM, 1, (short) 3).name("§aOptions §8§l▪ §7Clic-droit").owner(player.getName()).get());
        player.getInventory().setItem(4, joinArena);
        player.getInventory().setItem(7, leave);
    }


    @EventHandler
    public void onDeath(PlayerDeathEvent playerDeathEvent){
        playerDeathEvent.setDeathMessage(null);
        playerDeathEvent.getEntity().getWorld().playEffect(playerDeathEvent.getEntity().getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);

        Player player = playerDeathEvent.getEntity();
        ArenaAPI.getInstance().getArenaStatsMap().get(player.getUniqueId()).setDeaths(ArenaAPI.getInstance().getArenaStatsMap().get(player.getUniqueId()).getDeaths() + 1);
        if(ArenaAPI.getInstance().getArenaKitMap().containsKey(player.getUniqueId())){
            ArenaKit arenaKit = ArenaAPI.getInstance().getArenaKitMap().get(player.getUniqueId());
            arenaKit.dead();

            arenaKit.deletePlayer();
        }

        if(player.getKiller() != null){
            Bukkit.broadcastMessage("§7§l❘ §c" + player.getName() + "§7 est mort par §a" + player.getKiller().getName() + " §8[§f" + ((int) player.getKiller().getHealth()) / 2 + "§f ❤§8]");

            ArenaAPI.getInstance().getArenaStatsMap().get(player.getKiller().getUniqueId()).setKills(ArenaAPI.getInstance().getArenaStatsMap().get(player.getKiller().getUniqueId()).getKills() + 1);
            player.getKiller().getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 3));
            player.getKiller().getInventory().addItem(new ItemStack(Material.COBBLESTONE, 20));
            player.getKiller().getInventory().addItem(new ItemStack(Material.ARROW, 18));
            //player.getKiller().setHealth(Math.min(player.getKiller().getHealth() + 4, player.getKiller().getMaxHealth()));
        } else
            Bukkit.broadcastMessage("§7§l❘ §3" + player.getName() + "§7 est mort tout seul !");

        playerDeathEvent.getDrops().clear();
        Bukkit.getScheduler().runTaskLater(instance, ()->{
            player.spigot().respawn();
            player.teleport(new Location(Bukkit.getWorld("world"), 0, 202, 0));
            player.getInventory().clear();
            player.getInventory().setHeldItemSlot(4);

            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));


            player.setMaxHealth(20);
            player.setHealth(player.getMaxHealth());
            player.setAllowFlight(false);
            player.setFoodLevel(20);

            player.getInventory().addItem(cosmetics);
        //    player.getInventory().setItem(2, new ItemCreator(Material.SKULL_ITEM, 1, (short) 3).name("§aOptions §8§l▪ §7Clic-droit").owner(player.getName()).get());
            player.getInventory().setItem(4, joinArena);
            player.getInventory().setItem(7, leave);

        }, 3);
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent){
        playerQuitEvent.setQuitMessage(null);
        Player player = playerQuitEvent.getPlayer();
        PlayerStats playerStats = ArenaAPI.getInstance().getArenaStatsMap().remove(player.getUniqueId());
        playerStats.disconnect();
        if(ArenaAPI.getInstance().getArenaKitMap().containsKey(player.getUniqueId())){
            ArenaKit arenaKit = ArenaAPI.getInstance().getArenaKitMap().get(player.getUniqueId());
            arenaKit.dead();
            arenaKit.deletePlayer();
        }
    }


    @EventHandler
    public void onPlayerDamaging(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(entityDamageByEntityEvent.getDamager() instanceof Arrow && entityDamageByEntityEvent.getEntity() instanceof Player && ((Arrow)entityDamageByEntityEvent.getDamager()).getShooter() instanceof Player){
            Player player = ((Player) ((Arrow) entityDamageByEntityEvent.getDamager()).getShooter());
            Player victim = ((Player) entityDamageByEntityEvent.getEntity());
            Bukkit.getScheduler().runTaskLater(instance, ()-> player.sendMessage("§7§l❘ §7" + victim.getName() + " §8§l» " + makePercentColor(victim.getHealth()) + "% §8[§6" + makePercent(entityDamageByEntityEvent.getDamage()) + "%§8]"), 2);
        }
    }

    private String makePercentColor(double health) {
        double hearts = health / 2;
        double percent = hearts * 10;

        if (percent >= 66) {
            return "§a" + ((int) percent);
        } else if (percent >= 33) {
            return "§e" + ((int) percent);
        } else if (percent == 0) {
            return "§0" + (0);
        } else {
            return "§c" + ((int) percent);
        }
    }

    private String makePercent(double health) {
        double hearts = health / 2;
        double percent = hearts * 10;

        if (percent == 0) {
            return "" + (0);
        } else {
            return "" + ((int) percent);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(event.getEntity().getLocation().getY() > 130)
            event.setCancelled(true);
    }


}
