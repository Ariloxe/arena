package fr.ariloxe.arena.kits.polyvalent;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.kits.ArenaKit;
import fr.ariloxe.arena.tasks.ExecutorTask;
import fr.kanao.core.utils.ItemCreator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ariloxe
 */
public class Nidalee extends ArenaKit {

    private ExecutorTask metamorphoseTask;
    private boolean tigre = false;
    private float defaultWalkSpeed = 0;

    public Nidalee(Player player) {
        super("Nidalee", player);
    }

    @Override
    public void onEquip() {
        this.defaultWalkSpeed = getPlayer().getWalkSpeed();
        getPlayer().setMaxHealth(24);
        getPlayer().setHealth(24);
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§eMétamorphose §8§l▪ §7Clic-droit").get());
    }

    @Override
    public void onDeath() {
        getPlayer().setWalkSpeed(defaultWalkSpeed);

        if(metamorphoseTask != null){
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(metamorphoseTask);
        }
    }

    @Override
    public void onRightClick(ItemStack itemStack) {
        if(itemStack.getType() == Material.NETHER_STAR){
            if(metamorphoseTask == null){
                tigre = !tigre;
                if(!tigre){
                    getPlayer().setMaxHealth(24);
                    if(getPlayer().getHealth() >= 15)
                        getPlayer().setHealth(24);

                    getPlayer().setWalkSpeed(defaultWalkSpeed);
                    getPlayer().sendMessage("§7§l❘ §eVous avez pris votre forme §bChasseuse§e.");
                } else {
                    getPlayer().setMaxHealth(16);
                    getPlayer().setWalkSpeed((float) (getPlayer().getWalkSpeed()*1.5));
                    getPlayer().sendMessage("§7§l❘ §eVous avez pris votre forme §bTigre§e.");
                }



                int c = getPlayer().getInventory().first(Material.NETHER_STAR);
                getPlayer().getInventory().setItem(c, new ItemCreator(Material.NETHER_STAR).name("§eMétamorphose §8§l▪ §7Clic-droit").get());
                metamorphoseTask = new ExecutorTask("Métamorphose (Nidalee)", 12, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    int a = getPlayer().getInventory().first(Material.NETHER_STAR);
                    getPlayer().getInventory().setItem(a, new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§eMétamorphose §8§l▪ §7Clic-droit").get());
                    metamorphoseTask = null;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eMétamorphose §a!");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(metamorphoseTask);
            } else {
                getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (metamorphoseTask.getDuration()-metamorphoseTask.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(entityDamageByEntityEvent.getEntity() instanceof Player){
            if(entityDamageByEntityEvent.getDamager() instanceof Player){
                Player damager = (Player) entityDamageByEntityEvent.getDamager();
                if(damager == getPlayer()){
                    if(tigre){
                        entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage()*1.25);
                    } else {
                        entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage()*0.8);
                    }
                }
            } else if(entityDamageByEntityEvent.getDamager() instanceof Arrow){
                Arrow arrow = (Arrow) entityDamageByEntityEvent.getDamager();
                if(arrow.getShooter() instanceof Player){
                    Player damager = (Player) arrow.getShooter();
                    if(damager == getPlayer()){
                        if(!tigre){
                            entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage()*1.3);
                        }
                    }
                } else if(entityDamageByEntityEvent.getEntity() == getPlayer()){
                    if(!tigre){
                        entityDamageByEntityEvent.setDamage(ArenaAPI.getInstance().getRandom().nextInt(10) <= 3 ? (entityDamageByEntityEvent.getDamage()*0.5) : entityDamageByEntityEvent.getDamage());
                    } else {
                        entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage()*0.75);
                    }
                }
            }
        }
    }


}
