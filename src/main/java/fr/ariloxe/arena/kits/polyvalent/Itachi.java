package fr.ariloxe.arena.kits.polyvalent;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.kits.ArenaKit;
import fr.ariloxe.arena.tasks.ExecutorTask;
import fr.kanao.core.utils.ItemCreator;
import fr.kanao.core.utils.mojang.Tasks;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Ariloxe
 */
public class Itachi extends ArenaKit {

    private ExecutorTask rageTask;
    private boolean enabled = false;

    public Itachi(Player player) {
        super("Itachi", player);
    }


    @Override
    public void onEquip() {
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§5Susano §8§l▪ §7Clic-droit").get());
    }

    @Override
    public void onDeath() {
        if(rageTask != null){
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(rageTask);
        }
    }


    @Override
    public void onRightClick(ItemStack itemStack) {
        if(itemStack.getType() == Material.NETHER_STAR){
            if(rageTask == null){
                getPlayer().sendMessage("§7§l❘ §eVous avez activé votre compétence §bSusano§e.");
                if(getPlayer().getHealth() <= 10){
                    enabled = true;
                    Tasks.runLater(()-> enabled = false, 20*2);
                    getPlayer().setHealth(Math.min(getPlayer().getMaxHealth(), getPlayer().getHealth()+4));
                } else {
                    Player nearestPlayer = null;
                    double distance = 0;
                    for(Player player : getPlayer().getWorld().getNearbyEntities(getPlayer().getLocation(), 5, 5, 5).stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toList())){
                        if(player == getPlayer())
                            continue;
                        if(nearestPlayer == null){
                            nearestPlayer = player;
                            distance = player.getLocation().distance(getPlayer().getLocation());
                        } else {
                            if(player.getLocation().distance(getPlayer().getLocation()) < distance){
                                nearestPlayer = player;
                                distance = player.getLocation().distance(getPlayer().getLocation());
                            }
                        }
                    }

                    if(nearestPlayer == null)
                        return;

                    nearestPlayer.setHealth(nearestPlayer.getHealth() - 4);
                    nearestPlayer.getWorld().strikeLightningEffect(nearestPlayer.getLocation());
                }


                int c = getPlayer().getInventory().first(Material.NETHER_STAR);
                getPlayer().getInventory().setItem(c, new ItemCreator(Material.NETHER_STAR).name("§5Susano §8§l▪ §7Clic-droit").get());
                rageTask = new ExecutorTask("Susano (Itachi)", 37, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    int a = getPlayer().getInventory().first(Material.NETHER_STAR);
                    getPlayer().getInventory().setItem(a, new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§5Susano §8§l▪ §7Clic-droit").get());
                    rageTask = null;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eSusano §a!");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(rageTask);
            } else {
                getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (rageTask.getDuration()-rageTask.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(entityDamageByEntityEvent.getDamager() instanceof Player && entityDamageByEntityEvent.getDamager() == getPlayer()){
            int c = new Random().nextInt(10);
            if(c <= 3)
                entityDamageByEntityEvent.getEntity().setFireTicks(20*3);
        } else if(entityDamageByEntityEvent.getEntity() instanceof Player && entityDamageByEntityEvent.getEntity() == getPlayer() && enabled){
            entityDamageByEntityEvent.setCancelled(true);
        }
    }

}
