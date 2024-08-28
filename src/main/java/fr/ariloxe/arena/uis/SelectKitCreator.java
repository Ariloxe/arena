package fr.ariloxe.arena.uis;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.kits.ArenaKit;
import fr.ariloxe.arena.kits.ArenaKitKind;
import fr.ariloxe.arena.kits.ArenaKitList;
import fr.ariloxe.arena.utils.PlayerUtils;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import fr.kanao.core.utils.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ariloxe
 */
public class SelectKitCreator {

    private KInventory kInventory;
    private final ArenaAPI arenaAPI;
    private final List<Integer> integerList = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 45, 46, 47, 48, 49, 50, 51, 52, 53);
    private final ArenaKitKind kitType;

    public SelectKitCreator(ArenaAPI instance, ArenaKitKind kitType){
        this.arenaAPI = instance;
        this.kitType = kitType;
        this.kInventory = new KInventory(KItem.DEFAULT, 54, "§8│ " + kitType.getName());

        KItem glassPane = new KItem(new ItemCreator(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7)).name("§c").get());
        integerList.forEach(integer -> this.kInventory.setElement(integer, glassPane));

        KItem retour = new KItem(new ItemCreator(Material.ARROW).name("§8§l» §7Fermer ce menu §8§l«").lore("", "§8§l» §7Cette option vous permet de §cfermer§7 ce menu.", "§7Et ainsi retourner au menu principal.", "", "§8§l» §7Cliquez pour fermer").get());
        retour.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> ArenaAPI.getInstance().getSelectMainInventory().open(player));
        this.kInventory.setElement(53, retour);
    }

    public void open(Player kPlayer){
        AtomicInteger p = new AtomicInteger(9);
        Arrays.stream(ArenaKitList.values()).filter(kit -> kit.getArenaKitKind() == this.kitType).forEach(kit -> {
            KItem kItem = new KItem(new ItemCreator(kit.getIcon()).name("§8§l» §7§l" + kit.getName()).lore(kit.getDescription()).get());
            kItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
                try {
                    ArenaKit arenaKit = kit.getClazz().getConstructor(Player.class).newInstance(player);
                    ArenaAPI.getInstance().getArenaKitMap().put(player.getUniqueId(), arenaKit);
                    player.sendMessage("§7§l❘ §7Vous avez §e§léquipé avec succès§7 le kit " + kit.getName());


                    PlayerUtils.giveFightInventory(player);
                    int x = ArenaAPI.getInstance().getRandom().nextInt(50 * 2) - 50 + Bukkit.getWorld("world").getWorldBorder().getCenter().getBlockX();
                    int z = ArenaAPI.getInstance().getRandom().nextInt(50 * 2) - 50 + Bukkit.getWorld("world").getWorldBorder().getCenter().getBlockZ();
                    Location loc = Bukkit.getWorld("world").getHighestBlockAt(x, z).getLocation();
                    loc.getChunk().load();
                    player.teleport(loc.clone().add(0, 2, 0));
                    player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

                    player.getInventory().remove(Material.STAINED_GLASS_PANE);
                    arenaKit.equip();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            });
            this.kInventory.setElement(p.get(), kItem);
            p.getAndIncrement();
        });

        this.kInventory.open(kPlayer);
    }

}
