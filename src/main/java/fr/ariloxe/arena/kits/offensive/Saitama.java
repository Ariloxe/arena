package fr.ariloxe.arena.kits.offensive;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.kits.ArenaKit;
import fr.ariloxe.arena.tasks.ExecutorTask;
import fr.kanao.core.utils.ItemCreator;
import fr.kanao.core.utils.mojang.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Ariloxe
 */
public class Saitama extends ArenaKit {

    private ExecutorTask rushTalk;
    private boolean enabled = false;

    public Saitama(Player player) {
        super("Saitama", player);
    }

    @Override
    public void onEquip() {
        getPlayer().getInventory().remove(Material.DIAMOND_SWORD);
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§eOne Punch §8§l▪ §7Clic-droit").get());
    }

    @Override
    public void onDeath() {
        if(rushTalk != null){
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(rushTalk);
        }
    }

    @Override
    public void onRightClick(ItemStack itemStack) {
        if(itemStack.getType() == Material.NETHER_STAR){
            if(rushTalk == null){

                enabled = true;
                getPlayer().sendMessage("§7§l❘ §eVous avez activé votre compétence §bOne Punch§e.");

                int c = getPlayer().getInventory().first(Material.NETHER_STAR);
                getPlayer().getInventory().setItem(c, new ItemCreator(Material.NETHER_STAR).name("§eOne Punch §8§l▪ §7Clic-droit").get());
                rushTalk = new ExecutorTask("OnePunch (Saitama)", 27, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    int a = getPlayer().getInventory().first(Material.NETHER_STAR);
                    getPlayer().getInventory().setItem(a, new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§eOne Punch  §8§l▪ §7Clic-droit").get());
                    rushTalk = null;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eOne Punch §a!");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(rushTalk);

            } else {
                getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (rushTalk.getDuration()- rushTalk.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(entityDamageByEntityEvent.getDamager() == getPlayer() && entityDamageByEntityEvent.getEntity() instanceof Player){
            if(((Player) entityDamageByEntityEvent.getDamager()).getItemInHand() == null || ((Player) entityDamageByEntityEvent.getDamager()).getItemInHand().getType() == Material.AIR){
                entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage()*14);
                if(enabled){
                    getPlayer().playSound(getPlayer().getLocation(), Sound.ZOMBIE_WOODBREAK, 3.0F, 1.0F);
                    Player victim = (Player) entityDamageByEntityEvent.getEntity();
                    entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage()*2);
                    victim.setVelocity(victim.getLocation().getDirection().clone().multiply(-3));
                    enabled = false;
                }
            }
        }
    }


}
