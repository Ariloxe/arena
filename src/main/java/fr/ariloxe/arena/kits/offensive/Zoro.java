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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * @author Ariloxe
 */
public class Zoro extends ArenaKit {

    private ExecutorTask dragonSlashCooldown;

    public Zoro(Player player) {
        super("Zoro", player);
    }

    @Override
    public void onEquip() {
        getPlayer().getInventory().setItem(0, new ItemCreator(Material.DIAMOND_SWORD).unbreakable(true).enchant(Enchantment.DAMAGE_ALL, 4).name("§aEnma §8§l▪ §7Objet").get());
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§2Dragon Slash §8§l▪ §7Clic-droit").get());
    }

    @Override
    public void onDeath() {
        if(dragonSlashCooldown != null){
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(dragonSlashCooldown);
            dragonSlashCooldown = null;
        }
    }

    @Override
    public void onRightClick(ItemStack itemStack) {
        if(itemStack.getType() == Material.NETHER_STAR){
            if(dragonSlashCooldown == null){
                getPlayer().sendMessage("§7§l❘ §eVous avez utilisé votre compétence §bDragonSlash§e.");

                getPlayer().getWorld().getNearbyEntities(getPlayer().getLocation(), 5, 5, 5).stream().filter(entity -> entity instanceof Player).filter(entity -> entity != getPlayer()).forEach(entity -> {
                    Vector v = getVectorForPoints(entity.getLocation(), getPlayer().getLocation());
                    v = v.clone().multiply(-2);
                    v = v.clone().setY(1D);
                    entity.setVelocity(v);
                    if(((Player) entity).getHealth() <= 4)
                        getPlayer().setHealth(Math.min(getPlayer().getHealth() + 4, getPlayer().getMaxHealth()));

                    ((Player) entity).damage(4);
                });


                dragonSlashCooldown = new ExecutorTask("DragonSlash (Zoro)", 30, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eDragonSlash §a!");
                    dragonSlashCooldown = null;
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(dragonSlashCooldown);
            } else
                getPlayer().sendMessage("§7§l❘ §cVous devez attendre §l" + (dragonSlashCooldown.getDuration()- dragonSlashCooldown.getCurrentDuration()) + "§cs avant de réutiliser ce pouvoir.");
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(entityDamageByEntityEvent.getEntity() instanceof Player && entityDamageByEntityEvent.getDamager() == getPlayer() && getPlayer().getItemInHand().getType() == Material.DIAMOND_SWORD){
            if(ArenaAPI.getInstance().getRandom().nextInt(20) <= 1){
                ((Player) entityDamageByEntityEvent.getEntity()).damage(2);
                getPlayer().sendMessage("§7§l❘ §eVous avez infligé §f1§c❤ §esupplémentaire.");
            }
        }
    }

    private Vector getVectorForPoints(Location l1, Location l2) {
        double vX = (1.0D + 0.07D * l2.distance(l1)) * (l2.getX() - l1.getX()) / l2.distance(l1);

        double vY = 1D;
        double vZ = (1.0D + 0.07D * l2.distance(l1)) * (l2.getZ() - l1.getZ()) / l2.distance(l1);
        return new Vector(vX, vY, vZ);
    }
}
