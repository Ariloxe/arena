package fr.ariloxe.arena.kits.offensive;

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

/**
 * @author Ariloxe
 */
public class Homelander extends ArenaKit {

    private ExecutorTask rageTask;
    private int combo = 0;

    public Homelander(Player player) {
        super("Homelander", player);
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(entityDamageByEntityEvent.getDamager() == getPlayer() && entityDamageByEntityEvent.getEntity() instanceof Player){
            combo++;
            if(combo == 1)
                entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage());
            else if(combo == 2)
                entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage()*1.5);
            else
                entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage()*1.60);
        } else if(entityDamageByEntityEvent.getEntity() == getPlayer()){
            combo = 0;
        }
    }

    @Override
    public void onEquip() {
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§6Rage §8§l▪ §7Clic-droit").get());
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
                getPlayer().setAllowFlight(true);
                Tasks.runLater(()->{
                    if(getPlayer().isOnline())
                        getPlayer().setAllowFlight(false);
                }, 20*7);

                getPlayer().sendMessage("§7§l❘ §eVous avez activé votre compétence §bRage§e.");



                int c = getPlayer().getInventory().first(Material.NETHER_STAR);
                getPlayer().getInventory().setItem(c, new ItemCreator(Material.NETHER_STAR).name("§6Rage §8§l▪ §7Clic-droit").get());
                rageTask = new ExecutorTask("Rage (Homelander)", 37, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    int a = getPlayer().getInventory().first(Material.NETHER_STAR);
                    getPlayer().getInventory().setItem(a, new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§6Rage §8§l▪ §7Clic-droit").get());
                    rageTask = null;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eRage §a!");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(rageTask);
            } else {
                getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (rageTask.getDuration()-rageTask.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent entityDamageEvent){
        if(entityDamageEvent.getEntity() == getPlayer() && entityDamageEvent.getCause() == EntityDamageEvent.DamageCause.FALL){
            entityDamageEvent.setCancelled(true);
        }
    }

}
