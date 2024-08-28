package fr.ariloxe.arena.kits.polyvalent;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.kits.ArenaKit;
import fr.ariloxe.arena.tasks.ExecutorTask;
import fr.kanao.core.utils.ItemCreator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ariloxe
 */
public class Kyogai extends ArenaKit {

    private ExecutorTask rotateTask;

    public Kyogai(Player player) {
        super("Kyogai", player);
    }


    @Override
    public void onEquip() {
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§6Retournement 180° §8§l▪ §7Clic-droit").get());
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
                for(Object target : getPlayer().getNearbyEntities(7, 7, 7).stream().filter(entity -> entity instanceof Player).toArray()){
                    if(target instanceof Player){
                        Player targetPlayer = ((Player) target);
                        if(targetPlayer == getPlayer())
                            continue;

                        Location tp = targetPlayer.getLocation();
                        tp.setYaw(tp.getYaw() + 180);
                        targetPlayer.teleport(tp);
                    }
                }
                rotateTask = new ExecutorTask("Souffle du Son (Tengen)", 18, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    rotateTask = null;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eRetournement §a!");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(rotateTask);
            } else {
                getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (rotateTask.getDuration() - rotateTask.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
            }
        }
    }


}
