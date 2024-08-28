
package fr.ariloxe.arena.kits.polyvalent;

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
public class FatGum extends ArenaKit {

    private ExecutorTask sonTask;

    private int hits = 0;
    private float walkSpeed = 0;
    private double strenghtMultiplicator = 1;
    private final PotionEffect potionEffect = new PotionEffect(PotionEffectType.SPEED, 8*20, 0, false, false);
    public FatGum(Player player) {
        super("Fat Gum", player);
    }


    @Override
    public void onEquip() {
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§eLargage §8§l▪ §7Clic-droit").get());
        this.walkSpeed = getPlayer().getWalkSpeed();
    }

    @Override
    public void onDeath() {
        if(sonTask != null)
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(sonTask);

        getPlayer().setWalkSpeed(this.walkSpeed);
        getPlayer().setLevel(0);
    }


    @Override
    public void onRightClick(ItemStack itemStack) {
        if(itemStack.getType() == Material.NETHER_STAR){
            if(sonTask == null){
                if(hits <= 10){
                    this.strenghtMultiplicator = 1.1;
                    Tasks.runLater(()-> this.strenghtMultiplicator = 1, 20*5);
                } else if(hits <= 20){
                    this.strenghtMultiplicator = 1.2;
                    Tasks.runLater(()-> this.strenghtMultiplicator = 1, 20*7);
                } else if(hits <= 35){
                    this.strenghtMultiplicator = 1.2;
                    getPlayer().addPotionEffect(potionEffect);
                    Tasks.runLater(()-> this.strenghtMultiplicator = 1, 20*7);
                } else {
                    this.strenghtMultiplicator = 1.3;
                    getPlayer().setWalkSpeed((float) (getPlayer().getWalkSpeed()*2.3));
                    Tasks.runLater(()-> {
                        this.strenghtMultiplicator = 1;
                        if(getPlayer() == null)
                            return;
                        getPlayer().setWalkSpeed(this.walkSpeed);
                    }, 200);
                }

                hits = 0;
                getPlayer().setLevel(hits);

                int c = getPlayer().getInventory().first(Material.NETHER_STAR);
                getPlayer().getInventory().setItem(c, new ItemCreator(Material.NETHER_STAR).name("§eLargage §8§l▪ §7Clic-droit").get());
                sonTask = new ExecutorTask("Largage (FatGum)", 20, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    int a = getPlayer().getInventory().first(Material.NETHER_STAR);
                    getPlayer().getInventory().setItem(a, new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§eLargage §8§l▪ §7Clic-droit").get());
                    sonTask = null;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eLargage §a!");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(sonTask);
            } else {
                getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (sonTask.getDuration()- sonTask.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(entityDamageByEntityEvent.getEntity() == getPlayer() && entityDamageByEntityEvent.getDamager() instanceof Player){
            hits++;
            getPlayer().setLevel(hits);
        } else if(entityDamageByEntityEvent.getDamager() instanceof Player && entityDamageByEntityEvent.getDamager() == getPlayer())
            entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage()*this.strenghtMultiplicator);


    }

}
