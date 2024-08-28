package fr.ariloxe.arena.kits.defensive;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.kits.ArenaKit;
import fr.ariloxe.arena.tasks.ExecutorTask;
import fr.kanao.core.utils.ItemCreator;
import fr.kanao.core.utils.mojang.Tasks;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Ariloxe
 */
public class MarkEvans extends ArenaKit {

    private ExecutorTask rushTalk;
    private boolean enabled = false;

    public MarkEvans(Player player) {
        super("Mark Evans", player);
    }

    @Override
    public void onEquip() {
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§2Main Magique §8§l▪ §7Clic-droit").get());
        getPlayer().setMaxHealth(24);
        getPlayer().setHealth(24);
    }

    @Override
    public void onDeath() {
        getPlayer().setMaxHealth(20);
        getPlayer().setHealth(20);

        if(rushTalk != null){
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(rushTalk);
        }
    }

    @Override
    public void onRightClick(ItemStack itemStack) {
        if(itemStack.getType() == Material.NETHER_STAR){
            if(rushTalk == null){
                enabled = true;
                Tasks.runLater(()-> enabled = false, 20*5);
                getPlayer().sendMessage("§7§l❘ §eVous avez activé votre compétence §2Main Magique§e.");

                int c = getPlayer().getInventory().first(Material.NETHER_STAR);
                getPlayer().getInventory().setItem(c, new ItemCreator(Material.NETHER_STAR).name("§2Main Magique §8§l▪ §7Clic-droit").get());
                rushTalk = new ExecutorTask("Shield (Absalom)", 35, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    int a = getPlayer().getInventory().first(Material.NETHER_STAR);
                    getPlayer().getInventory().setItem(a, new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§2Main Magique §8§l▪ §7Clic-droit").get());
                    rushTalk = null;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §bMain Magique" +
                            " §a!");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(rushTalk);

            } else {
                getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (rushTalk.getDuration()- rushTalk.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
            }
        }
    }


    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(entityDamageByEntityEvent.getDamager() instanceof Arrow && entityDamageByEntityEvent.getEntity() == getPlayer()){
            if(!enabled)
                entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage()*0.8);
            else {
                ((Player) ((Arrow) entityDamageByEntityEvent.getDamager()).getShooter()).damage(entityDamageByEntityEvent.getDamage());
                entityDamageByEntityEvent.setCancelled(true);
            }
        }
    }


}
