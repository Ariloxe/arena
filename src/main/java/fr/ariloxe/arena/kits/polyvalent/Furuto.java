package fr.ariloxe.arena.kits.polyvalent;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.kits.ArenaKit;
import fr.ariloxe.arena.tasks.ExecutorTask;
import fr.ariloxe.arena.utils.PlayerUtils;
import fr.kanao.core.utils.ItemCreator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author Ariloxe
 */
public class Furuto extends ArenaKit {

    private ExecutorTask rotateTask;

    public Furuto(Player player) {
        super("Furûto", player);
    }


    @Override
    public void onEquip() {
        getPlayer().getInventory().addItem(new ItemCreator(Material.NETHER_STAR).enchant(Enchantment.DURABILITY, 10).name("§bFlûte de Furûto §8§l▪ §7Clic-droit").get());
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
                Player target = PlayerUtils.getTarget(getPlayer(), 15, 1.4D, true);
                if(target == null)
                    return;

                swapInv(target);
                target.playSound(target.getLocation(), Sound.ENDERMAN_SCREAM, 1, 1);
                getPlayer().sendMessage("§8§l❘ §fVous avez utilisé votre §bFlûte§f sur §b" + target.getName() + "§f.");

                rotateTask = new ExecutorTask("Souffle du Son (Tengen)", 22, ()->{
                    if(!getPlayer().isOnline())
                        return;
                    rotateTask = null;
                    getPlayer().sendMessage("§7§l❘ §aVous pouvez désormais réutiliser la compétence §bFlûte de Fûruto §a!");
                });
                ArenaAPI.getInstance().getGlobalExecutor().addTask(rotateTask);
            } else {
                getPlayer().sendMessage("§7§l❘ §cVeuillez attendre §e" + (rotateTask.getDuration() - rotateTask.getCurrentDuration()) + "s §cavant de pouvoir utiliser cette compétence.");
            }
        }
    }

    private void swapInv(Player player) {
        Random random = new Random();
        List<Integer> slots = new ArrayList<>();
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 8; i++)
            items.add(player.getInventory().getItem(i));

        for (int i = 0; i < 8; i++)
            slots.add(i);

        Map<Integer, ItemStack> map = new HashMap<>();
        while (!items.isEmpty())
            map.put(slots.remove(random.nextInt(slots.size())), items.remove(random.nextInt(items.size())));
        for (Map.Entry<Integer, ItemStack> entry : map.entrySet())
            player.getInventory().setItem(entry.getKey(), entry.getValue());
    }


}
