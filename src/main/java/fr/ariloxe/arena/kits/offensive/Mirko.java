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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Ariloxe
 */
public class Mirko extends ArenaKit {

    private ExecutorTask rushTalk;
    private float defaultWalkSpeed = 0;
    private boolean enabled = false;

    public Mirko(Player player) {
        super("Mirko", player);
    }

    @Override
    public void onEquip() {
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§bRush §8§l▪ §7Clic-droit").get());
        this.defaultWalkSpeed = getPlayer().getWalkSpeed();
        getPlayer().setWalkSpeed((float) (getPlayer().getWalkSpeed()*1.75));
    }

    @Override
    public void onDeath() {
        getPlayer().setWalkSpeed(defaultWalkSpeed);
        if(rushTalk != null){
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(rushTalk);
        }
    }

    @Override
    public void onRightClick(ItemStack itemStack) {
        if(itemStack.getType() == Material.NETHER_STAR){
            if(rushTalk == null){
                getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*7, 1, false, false));
                enabled = true;
                getPlayer().setWalkSpeed(defaultWalkSpeed);
                getPlayer().sendMessage("§7§l❘ §eVous avez activé votre compétence §bRush§e.");
                Tasks.runLater(()-> {
                    enabled = false;
                    if(getPlayer().isOnline()){
                        getPlayer().setWalkSpeed((float) (defaultWalkSpeed*1.75));
                    }
                }, 20*7);

                int c = getPlayer().getInventory().first(Material.NETHER_STAR);
                getPlayer().getInventory().setItem(c, new ItemCreator(Material.NETHER_STAR).name("§cRush §8§l▪ §7Clic-droit").get());
                rushTalk = new ExecutorTask("Rush (Mirko)", 27, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    int a = getPlayer().getInventory().first(Material.NETHER_STAR);
                    getPlayer().getInventory().setItem(a, new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§bRush §8§l▪ §7Clic-droit").get());
                    rushTalk = null;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eRush §a!");
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
            if(enabled)
                entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage()*1.2);
        }
    }


}
