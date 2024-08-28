package fr.ariloxe.arena.kits.utilitary;

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
public class Warwick extends ArenaKit {

    private ExecutorTask rueeTask;
    private boolean canStun = false;
    private Player stunnedPlayer;

    public Warwick(Player player) {
        super("Warwick", player);
    }

    @Override
    public void onEquip() {
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§cRuée §8§l▪ §7Clic-droit").get());
    }

    @Override
    public void onDeath() {
        if(rueeTask != null){
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(rueeTask);
        }
    }

    @Override
    public void onRightClick(ItemStack itemStack) {
        if(itemStack.getType() == Material.NETHER_STAR){
            if(rueeTask == null){
                getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*5, 0, false, false));
                canStun = true;

                getPlayer().sendMessage("§7§l❘ §eVous avez activé votre compétence §bRuée§e.");

                int c = getPlayer().getInventory().first(Material.NETHER_STAR);
                getPlayer().getInventory().setItem(c, new ItemCreator(Material.NETHER_STAR).name("§cRuée §8§l▪ §7Clic-droit").get());
                rueeTask = new ExecutorTask("Ruée (Warwick)", 40, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    int a = getPlayer().getInventory().first(Material.NETHER_STAR);
                    getPlayer().getInventory().setItem(a, new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§cRuée §8§l▪ §7Clic-droit").get());
                    rueeTask = null;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eRuée §a!");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(rueeTask);

            } else {
                getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (rueeTask.getDuration()- rueeTask.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(entityDamageByEntityEvent.getDamager() == getPlayer() && entityDamageByEntityEvent.getEntity() instanceof Player){
            if(ArenaAPI.getInstance().getRandom().nextInt(100) <= 35)
                ((Player) entityDamageByEntityEvent.getDamager()).setHealth(Math.min(((Player) entityDamageByEntityEvent.getDamager()).getMaxHealth(), ((Player) entityDamageByEntityEvent.getDamager()).getHealth()+(entityDamageByEntityEvent.getDamage()*0.2)));
            if(canStun){
                ((Player) entityDamageByEntityEvent.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20*2, 240, false, false));
                ((Player) entityDamageByEntityEvent.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*2, 200, false, false));
                stunnedPlayer = (Player) entityDamageByEntityEvent.getEntity();
                canStun = false;
                Tasks.runLater(()-> stunnedPlayer = null, 20*2);
            }
        } else if(entityDamageByEntityEvent.getDamager() == stunnedPlayer && entityDamageByEntityEvent.getEntity() instanceof Player){
            entityDamageByEntityEvent.setCancelled(true);
        }
    }


}
