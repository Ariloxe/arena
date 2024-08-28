package fr.ariloxe.arena.kits.polyvalent;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.kits.ArenaKit;
import fr.ariloxe.arena.tasks.ExecutorTask;
import fr.kanao.core.cosmetics.utils.particles.ParticleEffect;
import fr.kanao.core.cosmetics.utils.particles.ParticleUtils;
import fr.kanao.core.utils.ItemCreator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ariloxe
 */
public class Mettaton extends ArenaKit {

    private ExecutorTask killswitchTask;
    private ExecutorTask rayonRouge;
    private ExecutorTask tntRayonTask;
    private boolean exForm = false;
    private float defaultWalkSpeed = 0;

    public Mettaton(Player player) {
        super("Mettaton", player);
    }

    @Override
    public void onEquip() {
        this.defaultWalkSpeed = getPlayer().getWalkSpeed();
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§cKill Switch §8§l▪ §7Clic-droit").get());
    }

    @Override
    public void onDeath() {
        getPlayer().setWalkSpeed(defaultWalkSpeed);

        if(killswitchTask != null){
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(killswitchTask);
        }
        if(rayonRouge != null){
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(rayonRouge);
        }
        if(tntRayonTask != null){
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(tntRayonTask);
        }
    }

    @Override
    public void onRightClick(ItemStack itemStack) {
        if(itemStack.getType() == Material.NETHER_STAR){
            if(!exForm){
                if(rayonRouge == null){
                    Player nearestPlayer = getPlayer().getNearbyEntities(20, 20, 20).stream().filter(entity -> entity instanceof Player && entity != getPlayer() && entity.getLocation().getY() < 100).map(entity -> (Player) entity).findFirst().orElse(null);
                    if(nearestPlayer == null)
                        return;

                    drawParticleBeam(getPlayer().getLocation(), nearestPlayer.getLocation());
                    nearestPlayer.setHealth(nearestPlayer.getHealth()-4);
                    nearestPlayer.damage(0, getPlayer());
                    getPlayer().sendMessage("§7§l❘ §eVous avez utilisé votre pouvoir sur §b" + nearestPlayer.getName() + "§e.");


                    rayonRouge = new ExecutorTask("Rayon Rouge (Mettaton)", 40, ()->{
                        if(!getPlayer().isOnline())
                            return;
                        rayonRouge = null;
                        getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eRayon Rouge §a!");
                    });
                    ArenaAPI.getInstance().getGlobalExecutor().addTask(rayonRouge);
                } else {
                    getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (rayonRouge.getDuration()- rayonRouge.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
                }
            } else {
                if(tntRayonTask == null){
                    Player nearestPlayer = getPlayer().getNearbyEntities(20, 20, 20).stream().filter(entity -> entity instanceof Player && entity != getPlayer() && entity.getLocation().getY() < 100).map(entity -> (Player) entity).findFirst().orElse(null);
                    if(nearestPlayer == null)
                        return;

                    for(int c = 0; c < 2; c++){
                        TNTPrimed tnt = getPlayer().getWorld().spawn(nearestPlayer.getLocation(), TNTPrimed.class);
                        tnt.setFuseTicks(25);
                    }

                    getPlayer().sendMessage("§7§l❘ §eVous avez utilisé votre pouvoir sur §b" + nearestPlayer.getName() + "§e.");

                    tntRayonTask = new ExecutorTask("TnTRayon (Mettaton)", 20, ()->{
                        if(!getPlayer().isOnline())
                            return;
                        tntRayonTask = null;
                        getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eTnT §a!");
                    });
                    ArenaAPI.getInstance().getGlobalExecutor().addTask(tntRayonTask);
                } else {
                    getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (tntRayonTask.getDuration()- tntRayonTask.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
                }
            }
        }
    }

    @Override
    public void onLeftclick(ItemStack itemStack) {
        if(itemStack.getType() == Material.NETHER_STAR){
            if(killswitchTask == null){
                exForm = !exForm;
                if(!exForm){
                    getPlayer().sendMessage("§7§l❘ §eVous avez pris votre forme §bSimple§e.");
                } else {
                    getPlayer().sendMessage("§7§l❘ §eVous avez pris votre forme §bEX§e.");
                }

                killswitchTask = new ExecutorTask("Killswitch (Mettaton)", 15, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    killswitchTask = null;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eKill Switch §a!");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(killswitchTask);
            } else {
                getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (killswitchTask.getDuration()- killswitchTask.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(entityDamageByEntityEvent.getEntity() instanceof Player && entityDamageByEntityEvent.getEntity() == getPlayer() && !exForm){
            entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage()*0.8);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            event.setCancelled(true);
            ParticleUtils.display(ParticleEffect.EXPLOSION_HUGE, event.getLocation());
            ParticleUtils.display(ParticleEffect.FLAME, event.getLocation(), 3);

            for (Player player : event.getLocation().getWorld().getPlayers()) {
                if (player.getLocation().distance(event.getLocation()) <= 5) {
                    player.setHealth(player.getHealth() - 4);
                }
            }
        }
    }


    private void drawParticleBeam(Location loc1, Location loc2) {
        double distance = loc1.distance(loc2);
        double step = 0.2; // Step size for particles

        double dx = (loc2.getX() - loc1.getX()) / distance;
        double dy = (loc2.getY() - loc1.getY()) / distance;
        double dz = (loc2.getZ() - loc1.getZ()) / distance;

        for (double i = 0; i <= distance; i += step) {
            double x = loc1.getX() + dx * i;
            double y = loc1.getY() + dy * i;
            double z = loc1.getZ() + dz * i;

            ParticleUtils.display(ParticleEffect.REDSTONE, new Location(loc1.getWorld(), x, y, z));
        }
    }

}
