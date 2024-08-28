package fr.ariloxe.arena.kits.defensive;

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
public class Absalom extends ArenaKit {

    private ExecutorTask rushTalk;
    private ExecutorTask endInvisibilityTask;
    private boolean enabled = false;

    private final ItemStack diamondHelmet = new ItemCreator(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).unbreakable(true).get();
    private final ItemStack diamondChestplate = new ItemCreator(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).unbreakable(true).get();
    private final ItemStack ironLeggings = new ItemCreator(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).unbreakable(true).get();
    private final ItemStack diamondBoots = new ItemCreator(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).unbreakable(true).get();

    public Absalom(Player player) {
        super("Absalom", player);
    }

    @Override
    public void onEquip() {
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§9Suke-Suke §8§l▪ §7Clic-droit").get());
        getPlayer().setMaxHealth(24);
        getPlayer().setHealth(24);
    }

    @Override
    public void onDeath() {
        getPlayer().setMaxHealth(20);
        getPlayer().setHealth(20);

        if(rushTalk != null){
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(rushTalk);
        }

        if(endInvisibilityTask != null){
            getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(endInvisibilityTask);
        }
    }

    @Override
    public void onRightClick(ItemStack itemStack) {
        if(itemStack.getType() == Material.NETHER_STAR){
            if(rushTalk == null){
                getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*10, 9, false, false));
                enabled = true;
                getPlayer().sendMessage("§7§l❘ §eVous avez activé votre compétence §bInvisibilité§e.");
                getPlayer().getInventory().setHelmet(null);
                getPlayer().getInventory().setChestplate(null);
                getPlayer().getInventory().setLeggings(null);
                getPlayer().getInventory().setBoots(null);


                int c = getPlayer().getInventory().first(Material.NETHER_STAR);
                getPlayer().getInventory().setItem(c, new ItemCreator(Material.NETHER_STAR).name("§9Suke-Suke §8§l▪ §7Clic-droit").get());
                rushTalk = new ExecutorTask("SukeSuke (Absalom)", 40, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    int a = getPlayer().getInventory().first(Material.NETHER_STAR);
                    getPlayer().getInventory().setItem(a, new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§9Suke-Suke §8§l▪ §7Clic-droit").get());
                    rushTalk = null;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §9Suke-Suke" +
                            " §a!");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(rushTalk);

                endInvisibilityTask = new ExecutorTask("endInvisibility (Absalom)", 10, ()->{
                    endInvisibilityTask = null;
                    enabled = false;
                    getPlayer().getInventory().setHelmet(diamondHelmet);
                    getPlayer().getInventory().setChestplate(diamondChestplate);
                    getPlayer().getInventory().setLeggings(ironLeggings);
                    getPlayer().getInventory().setBoots(diamondBoots);
                    getPlayer().sendMessage("§7§l❘ §eVous êtes de nouveau §bvisible§e.");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(endInvisibilityTask);

            } else {
                getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (rushTalk.getDuration()- rushTalk.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(entityDamageByEntityEvent.getDamager() == getPlayer() || entityDamageByEntityEvent.getEntity() == getPlayer()){
            if(enabled)
                entityDamageByEntityEvent.setCancelled(true);
        }
    }


}
