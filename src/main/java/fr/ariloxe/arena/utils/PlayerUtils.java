package fr.ariloxe.arena.utils;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.utils.inventory.InventoryConvertor;
import fr.kanao.core.utils.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ariloxe
 */
public class PlayerUtils {

    private static final ItemStack diamondSword = new ItemCreator(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 3).unbreakable(true).get();

    private static final ItemStack specialItem = new ItemCreator(Material.STAINED_GLASS_PANE).damage(11).name("§3✦ §9Objet Spécial §3✦").enchant(Enchantment.DURABILITY, 9).get();
    private static final ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, 9);
    private static final ItemStack diamondPickaxe = new ItemCreator(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 3).unbreakable(true).get();
    private static final ItemStack waterBucket = new ItemStack(Material.WATER_BUCKET);

    private static final ItemStack cobbleStone = new ItemStack(Material.COBBLESTONE, 64);
    private static final ItemStack bow = new ItemCreator(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 2).get();
    private static final ItemStack arrow = new ItemStack(Material.ARROW, 32);

    private static final ItemStack diamondHelmet = new ItemCreator(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).unbreakable(true).get();
    private static final ItemStack diamondChestplate = new ItemCreator(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).unbreakable(true).get();
    private static final ItemStack ironLeggings = new ItemCreator(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).unbreakable(true).get();
    private static final ItemStack diamondBoots = new ItemCreator(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).unbreakable(true).get();

    private static final ItemStack[] defaultInventory = new ItemStack[]{
            diamondSword, specialItem, gapple, diamondPickaxe, waterBucket, cobbleStone, arrow, bow, cobbleStone
    };

    public static ItemStack[] getDefaultInventory() {
        return defaultInventory;
    }

    public static void giveFightInventory(Player player){
        player.getInventory().clear();

        if(ArenaAPI.getInstance().getArenaStatsMap().get(player.getUniqueId()).getEditInventory().equals("")){
            player.getInventory().setItem(0, diamondSword);
            player.getInventory().addItem(specialItem);
            player.getInventory().setItem(2, gapple);
            player.getInventory().setItem(3, diamondPickaxe);
            player.getInventory().setItem(4, waterBucket);
            player.getInventory().setItem(5, cobbleStone);
            player.getInventory().setItem(6, arrow);
            player.getInventory().setItem(7, bow);
            player.getInventory().setItem(8, cobbleStone);
        } else {
            player.getInventory().setContents(InventoryConvertor.inventoryFromBase64(ArenaAPI.getInstance().getArenaStatsMap().get(player.getUniqueId()).getEditInventory()));
        }

        player.getInventory().setHelmet(diamondHelmet);
        player.getInventory().setChestplate(diamondChestplate);
        player.getInventory().setLeggings(ironLeggings);
        player.getInventory().setBoots(diamondBoots);
    }

    public static void giveEditInventory(Player player){
        player.getInventory().clear();
        player.getInventory().setItem(0, diamondSword);
        player.getInventory().addItem(specialItem);
        player.getInventory().setItem(2, gapple);
        player.getInventory().setItem(3, diamondPickaxe);
        player.getInventory().setItem(4, waterBucket);
        player.getInventory().setItem(5, cobbleStone);
        player.getInventory().setItem(6, arrow);
        player.getInventory().setItem(7, bow);
        player.getInventory().setItem(8, cobbleStone);

    }


    public static Player getTarget(Player player, int maxRange, double aiming, boolean wallHack) {
        Player target = null;
        double distance = 0.0D;
        Location playerEyes = player.getEyeLocation();
        Vector direction = playerEyes.getDirection().normalize();
        List<Player> targets = new ArrayList<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online == player ||
                    !online.getWorld().equals(player.getWorld()) || online
                    .getLocation().distanceSquared(playerEyes) > (maxRange * maxRange) || online
                    .getGameMode().equals(GameMode.SPECTATOR))
                continue;
            targets.add(online);
        }
        if (targets.size() > 0) {
            Location loc = playerEyes.clone();
            Vector progress = direction.clone().multiply(0.7D);
            maxRange = 100 * maxRange / 70;
            int loop = 0;
            while (loop < maxRange) {
                loop++;
                loc.add(progress);
                Block block = loc.getBlock();
                if (!wallHack && block.getType().isSolid())
                    break;
                double lx = loc.getX();
                double ly = loc.getY();
                double lz = loc.getZ();
                for (Player possibleTarget : targets) {
                    if (possibleTarget == player)
                        continue;
                    Location testLoc = possibleTarget.getLocation().add(0.0D, 0.85D, 0.0D);
                    double px = testLoc.getX();
                    double py = testLoc.getY();
                    double pz = testLoc.getZ();
                    boolean dX = (Math.abs(lx - px) < 0.7D * aiming);
                    boolean dY = (Math.abs(ly - py) < 1.7D * aiming);
                    boolean dZ = (Math.abs(lz - pz) < 0.7D * aiming);
                    if (dX && dY && dZ) {
                        target = possibleTarget;
                        break;
                    }
                }
                if (target != null) {
                    distance = ((double) (loop * 70) / 100);
                    break;
                }
            }
        }
        if (target != null)
            return target;
        return null;
    }

}
