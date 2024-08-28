package fr.ariloxe.arena.kits.utilitary;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.kits.ArenaKit;
import fr.ariloxe.arena.tasks.ExecutorTask;
import fr.ariloxe.arena.utils.PlayerUtils;
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
public class Tengen extends ArenaKit {

    private ExecutorTask sonTask;
    private final PotionEffect potionEffect = new PotionEffect(PotionEffectType.SPEED, 4*20, 2, false, false);

    public Tengen(Player player) {
        super("Tengen", player);
    }


    @Override
    public void onEquip() {
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§bSouffle du Son §8§l▪ §7Clic-droit").get());
    }

    @Override
    public void onDeath() {
        if(sonTask != null)
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(sonTask);

    }


    @Override
    public void onRightClick(ItemStack itemStack) {
        if(itemStack.getType() == Material.NETHER_STAR){
            if(sonTask == null){
                Player target = PlayerUtils.getTarget(getPlayer(), 15, 1.4D, true);
                if(target == null)
                    return;

                Location targetLocation = target.getLocation().clone();
                targetLocation.add(targetLocation.getDirection().clone().multiply(-1));
                getPlayer().teleport(targetLocation);
                getPlayer().playSound(getPlayer().getLocation(), Sound.ENDERMAN_TELEPORT, 3.0F, 1.0F);
                getPlayer().addPotionEffect(potionEffect);

                getPlayer().sendMessage("§7§l❘ §eVous-vous êtes téléporté(e) sur §b" + target.getName() + "§e.");

                int c = getPlayer().getInventory().first(Material.NETHER_STAR);
                getPlayer().getInventory().setItem(c, new ItemCreator(Material.NETHER_STAR).name("§dSouffle du Son §8§l▪ §7Clic-droit").get());
                sonTask = new ExecutorTask("Souffle du Son (Tengen)", 35, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    int a = getPlayer().getInventory().first(Material.NETHER_STAR);
                    getPlayer().getInventory().setItem(a, new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§dSouffle du Son §8§l▪ §7Clic-droit").get());
                    sonTask = null;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eSouffle du Son §a!");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(sonTask);
            } else {
                getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (sonTask.getDuration()- sonTask.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(entityDamageByEntityEvent.getDamager() == getPlayer() && entityDamageByEntityEvent.getEntity() instanceof Player)
            entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage()*1.5);

    }

}
