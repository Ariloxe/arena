package fr.ariloxe.arena.kits.utilitary;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.kits.ArenaKit;
import fr.ariloxe.arena.tasks.ExecutorTask;
import fr.kanao.core.utils.ItemCreator;
import fr.kanao.core.utils.mojang.Tasks;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Ariloxe
 */
public class Tsunade extends ArenaKit {

    private ExecutorTask ninjutsuCooldown;
    private boolean canHeal = true;

    public Tsunade(Player player) {
        super("Tsunade", player);
    }

    @Override
    public void onEquip() {
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§dNinjutsu §8§l▪ §7Clic-droit").get());
    }

    @Override
    public void onDeath() {
        if(ninjutsuCooldown != null){
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(ninjutsuCooldown);
            ninjutsuCooldown = null;
        }
    }

    @Override
    public void onRightClick(ItemStack itemStack) {
        if(itemStack.getType() == Material.NETHER_STAR){
            if(ninjutsuCooldown == null){
                getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 70, 2, false, false));
                getPlayer().sendMessage("§7§l❘ §eVous avez utilisé votre compétence §bNinjutsu§e.");

                ninjutsuCooldown = new ExecutorTask("Ninjutsu (Tsunade)", 34, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eNinjutsu §a!");
                    ninjutsuCooldown = null;
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(ninjutsuCooldown);
            } else
                getPlayer().sendMessage("§7§l❘ §cVous devez attendre §l" + (ninjutsuCooldown.getDuration()-ninjutsuCooldown.getCurrentDuration()) + "§cs avant de réutiliser ce pouvoir.");
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent entityDamageEvent){
        if(entityDamageEvent.getEntity() == getPlayer()){
            if(canHeal && ((Player) entityDamageEvent.getEntity()).getHealth() <= 4){
                ((Player) entityDamageEvent.getEntity()).setHealth(8);
                ((Player) entityDamageEvent.getEntity()).playSound(entityDamageEvent.getEntity().getLocation(), Sound.DRINK, 1, 1);
                canHeal = false;
                Tasks.runLater(()-> canHeal = true, 20*60);
            }
        }
    }
}
