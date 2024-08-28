package fr.ariloxe.arena.kits.utilitary;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.kits.ArenaKit;
import fr.ariloxe.arena.tasks.ExecutorTask;
import fr.kanao.core.cosmetics.utils.particles.ParticleEffect;
import fr.kanao.core.cosmetics.utils.particles.ParticleUtils;
import fr.kanao.core.utils.ItemCreator;
import fr.kanao.core.utils.mojang.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.stream.Collectors;

/**
 * @author Ariloxe
 */
public class Deidara extends ArenaKit {

    private ExecutorTask dragonSlashCooldown;
    private boolean bakuton = false;

    public Deidara(Player player) {
        super("Deidara", player);
    }

    @Override
    public void onEquip() {
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§cBakuton §8§l▪ §7Clic-droit").get());
    }

    @Override
    public void onDeath() {
        ParticleUtils.display(ParticleEffect.EXPLOSION_HUGE, getPlayer().getLocation());

        if(getPlayer().getKiller() == null){
            getPlayer().getNearbyEntities(5, 5, 5).stream().filter(entity -> entity instanceof Player && entity != getPlayer()).forEach(entity -> {
                ((Player) entity).damage(6);
                ((Player) entity).playSound(getPlayer().getLocation(), Sound.EXPLODE, 1, 1);
            });
        } else {
            getPlayer().getKiller().damage(6);
            getPlayer().getKiller().playSound(getPlayer().getLocation(), Sound.EXPLODE, 1, 1);
        }

        if(dragonSlashCooldown != null){
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(dragonSlashCooldown);
            dragonSlashCooldown = null;
        }
    }

    @Override
    public void onRightClick(ItemStack itemStack) {
        if(itemStack.getType() == Material.NETHER_STAR){
            if(dragonSlashCooldown == null){
                getPlayer().sendMessage("§7§l❘ §eVous avez utilisé votre compétence §bBakuton§e.");
                bakuton = true;
                Tasks.runLater(()-> bakuton = false, 20*12);

                dragonSlashCooldown = new ExecutorTask("DragonSlash (Zoro)", 42, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eBakuton §a!");
                    dragonSlashCooldown = null;
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(dragonSlashCooldown);
            } else
                getPlayer().sendMessage("§7§l❘ §cVous devez attendre §l" + (dragonSlashCooldown.getDuration()- dragonSlashCooldown.getCurrentDuration()) + "§cs avant de réutiliser ce pouvoir.");
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent projectileHitEvent) {
        if(projectileHitEvent.getEntity().getShooter() instanceof Player){
            Player player = (Player) projectileHitEvent.getEntity().getShooter();
            if(getPlayer() == player && bakuton){
                if (projectileHitEvent.getEntity() instanceof Arrow) {
                    ParticleEffect.FLAME.display(0, 0, 0, 0.3F, 15, projectileHitEvent.getEntity().getLocation(), 30);
                    ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.05F, 1, projectileHitEvent.getEntity().getLocation(), 30);

                    for (Entity entity : projectileHitEvent.getEntity().getNearbyEntities(5, 5, 5).stream().filter(entity -> entity instanceof Player && entity != getPlayer()).collect(Collectors.toList())) {
                        if(!(entity instanceof Player))
                            continue;

                        double c = entity.getLocation().distance(projectileHitEvent.getEntity().getLocation());
                        ((Player) entity).damage(c <= 1 ? 8 : c <= 3 ? 6 : 4);
                    }
                }
            }
        }
    }
}
