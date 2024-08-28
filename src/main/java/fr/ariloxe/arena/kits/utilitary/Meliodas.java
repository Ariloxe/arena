package fr.ariloxe.arena.kits.utilitary;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.kits.ArenaKit;
import fr.ariloxe.arena.tasks.ExecutorTask;
import fr.kanao.core.utils.ItemCreator;
import fr.kanao.core.utils.mojang.Tasks;
import fr.kanao.core.utils.packets.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ariloxe
 */
public class Meliodas extends ArenaKit {

    private ExecutorTask rotateTask;
    private boolean contreTotal = false;
    private final Map<Player, Double> playerDoubleMap = new HashMap<>();

    public Meliodas(Player player) {
        super("Meliodas", player);
    }


    @Override
    public void onEquip() {
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§aContre Total §8§l▪ §7Clic-droit").get());
    }

    @Override
    public void onDeath() {
        if(rotateTask != null)
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(rotateTask);

    }


    @Override
    public void onRightClick(ItemStack itemStack) {
        if(itemStack.getType() == Material.NETHER_STAR){
            if(rotateTask == null){
                getPlayer().playSound(getPlayer().getLocation(), Sound.ZOMBIE_WOODBREAK, 1, 1);
                new Title("", "§a§lINVINCIBLE...").sendToPlayer(getPlayer());
                contreTotal = true;
                Tasks.runLater(()->{
                    playerDoubleMap.forEach((player, aDouble) -> {
                        if(player.getLocation() != null)
                            player.damage(aDouble, getPlayer());
                    });

                    playerDoubleMap.clear();
                    new Title("", "§c§lCONTRE TOTAL !!").sendToPlayer(getPlayer());
                    contreTotal = false;
                }, 20*3);

                rotateTask = new ExecutorTask("Souffle du Son (Tengen)", 31, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    rotateTask = null;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eContre Total §a!");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(rotateTask);
            } else {
                getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (rotateTask.getDuration() - rotateTask.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(entityDamageByEntityEvent.getEntity() == getPlayer() && entityDamageByEntityEvent.getDamager() instanceof Player){
            if(contreTotal){
                playerDoubleMap.put((Player) entityDamageByEntityEvent.getDamager(), playerDoubleMap.getOrDefault(((Player) entityDamageByEntityEvent.getDamager()), 0.0) + entityDamageByEntityEvent.getDamage()/2);
                entityDamageByEntityEvent.setCancelled(true);
            }
        }

    }

}
