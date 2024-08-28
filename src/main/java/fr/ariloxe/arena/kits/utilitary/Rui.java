package fr.ariloxe.arena.kits.utilitary;

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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ariloxe
 */
public class Rui extends ArenaKit {

    private ExecutorTask rotateTask;

    public Rui(Player player) {
        super("Rui", player);
    }


    @Override
    public void onEquip() {
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§cCage de l'Araignée §8§l▪ §7Clic-droit").get());
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

                getPlayer().playSound(getPlayer().getLocation(), Sound.SPIDER_IDLE, 1, 1);
                for(Location block : getSphere(getPlayer(), 5, true)){
                    if(block.getBlock().getType() == Material.AIR || block.getBlock().getType() == Material.LONG_GRASS){
                       blocksLocation.add(block);
                       block.getBlock().setType(Material.WEB);
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
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §cCage de l'Araignée §a!");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(rotateTask);
            } else {
                getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (rotateTask.getDuration() - rotateTask.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
            }
        }
    }

    private List<Location> getSphere(Player kPlayer, int radius, boolean hollow){
        List<Location> circleBlocks = new ArrayList<>();

        Location centerBlock = kPlayer.getLocation();
        int bX = centerBlock.getBlockX();
        int bY = centerBlock.getBlockY();
        int bZ = centerBlock.getBlockZ();

        for(int x = bX - radius; x <= bX + radius; x++){
            for(int y = bY - radius; y <= bY + radius; y++){
                for(int z = bZ - radius; z <= bZ + radius; z++){
                    double distance = ((bX - x) * (bX - x) + ((bZ - z) * (bZ - z)) + ((bY - y) * (bY - y)));
                    if(distance < radius * radius && !(hollow && distance < ((radius -1) * (radius -1)))){
                        Location block = new Location(centerBlock.getWorld(), x, y, z);
                        circleBlocks.add(block);
                    }
                }
            }
        }
        return circleBlocks;
    }

}
