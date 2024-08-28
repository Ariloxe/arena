package fr.ariloxe.arena.kits.offensive;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.kits.ArenaKit;
import fr.ariloxe.arena.tasks.ExecutorTask;
import fr.ariloxe.arena.utils.BlockUtils;
import fr.kanao.core.utils.ItemCreator;
import fr.kanao.core.utils.mojang.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ariloxe
 */
public class DemonRequin extends ArenaKit {

    private ExecutorTask rotateTask;

    public DemonRequin(Player player) {
        super("Démon Requin", player);
    }


    @Override
    public void onEquip() {
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§bBubulle d'Eau §8§l▪ §7Clic-droit").get());
        getPlayer().getInventory().setBoots(new ItemCreator(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DEPTH_STRIDER, 3).unbreakable(true).get());
    }

    @Override
    public void onDeath() {
        if(rotateTask != null)
            ArenaAPI.getInstance().getGlobalExecutor().removeTask(rotateTask);

    }


    @Override
    public void onRightClick(ItemStack itemStack) {
        if(itemStack.getType() == Material.NETHER_STAR){
            if(rotateTask == null){
                List<Location> blocksLocation = new ArrayList<>();
                for(Block block : BlockUtils.circle(getPlayer().getLocation(), 10.0, 10.0, false, true, 3)){
                    if(block.getType() == Material.AIR){
                       blocksLocation.add(block.getLocation());
                       block.setType(Material.STATIONARY_WATER);
                    }
                }

                Tasks.runLater(()->{
                    for(Location blockLocation : blocksLocation)
                        blockLocation.getBlock().setType(Material.AIR);

                }, 20*10);

                rotateTask = new ExecutorTask("Souffle du Son (Tengen)", 31, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    rotateTask = null;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §bBubulle d'Eau §a!");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(rotateTask);
            } else {
                getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (rotateTask.getDuration() - rotateTask.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
            }
        }
    }

}
