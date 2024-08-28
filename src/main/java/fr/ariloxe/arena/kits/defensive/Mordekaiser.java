package fr.ariloxe.arena.kits.defensive;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.kits.ArenaKit;
import fr.ariloxe.arena.tasks.ExecutorTask;
import fr.kanao.core.utils.ItemCreator;
import fr.kanao.core.utils.mojang.Tasks;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Ariloxe
 */
public class Mordekaiser extends ArenaKit {

    private ExecutorTask indestructibleTalk;

    public Mordekaiser(Player player) {
        super("Mordekaiser", player);
    }

    public void onEquip(){
        getPlayer().getInventory().setHelmet(new ItemCreator(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).get());
        getPlayer().getInventory().setChestplate(new ItemCreator(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).get());
        getPlayer().getInventory().setBoots(new ItemCreator(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).get());

        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§bIndestructible §8§l▪ §7Clic-droit").get());
    }

    public void onRightClick(ItemStack itemStack){
        if (itemStack.getType() == Material.NETHER_STAR){
            if (indestructibleTalk == null) {
                getPlayer().setHealth(Math.min(getPlayer().getHealth() + 4, getPlayer().getMaxHealth()));

                getPlayer().sendMessage("§7§l❘ §eVous avez activé votre compétence §bIndestructible§e.");

                int c = getPlayer().getInventory().first(Material.NETHER_STAR);
                getPlayer().getInventory().setItem(c, new ItemCreator(Material.NETHER_STAR).name("§cIndestructible §8§l▪ §7Clic-droit").get());
                indestructibleTalk = new ExecutorTask("Indestructible (Mordekaiser)", 40, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    int a = getPlayer().getInventory().first(Material.NETHER_STAR);
                    getPlayer().getInventory().setItem(a, new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§bIndestructible §8§l▪ §7Clic-droit").get());
                    indestructibleTalk = null;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §eIndestructible §a!");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(indestructibleTalk);

            } else {
                getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (indestructibleTalk.getDuration()- indestructibleTalk.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
            }
        }

    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(entityDamageByEntityEvent.getEntity() == getPlayer() && entityDamageByEntityEvent.getDamager() instanceof Arrow){
            entityDamageByEntityEvent.setCancelled(true);
        }
    }


}
