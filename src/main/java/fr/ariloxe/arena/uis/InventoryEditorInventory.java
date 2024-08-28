package fr.ariloxe.arena.uis;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.utils.PlayerUtils;
import fr.ariloxe.arena.utils.inventory.InventoryConvertor;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import fr.kanao.core.utils.ItemCreator;
import fr.kanao.core.utils.skull.SkullList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ariloxe
 */
public class InventoryEditorInventory {

    public InventoryEditorInventory(Player player) {
        String inventory = ArenaAPI.getInstance().getArenaStatsMap().get(player.getUniqueId()).getEditInventory();
        KInventory kInventory = new KInventory(54, "§f┃ §d§lKanao §8- §7Inventaire");

        KItem modify = new KItem(new ItemCreator(SkullList.CHECK_MARK.getItemStack()).name("§8» §aCréer un kit").lore("", "§fPermet de modifier le kit", "§factuel.", "", "§8» §eClique-Gauche §7pour changer le kit.").get());
        modify.addCallback((kInventoryRepresentation, itemStack, player1, kInventoryClickContext) -> {
            player1.closeInventory();
            player1.playSound(player1.getLocation(), Sound.HORSE_ARMOR, 1, 1);

            player1.sendMessage("");
            player1.sendMessage("§d§lKanao §8» §7Bienvenue dans l'éditeur de kit de l'arène !");
            player1.sendMessage(" §8• §7Une fois les changements terminés, faites §a/confirm§7 !");
            player1.sendMessage("");
            ArenaAPI.getInstance().getSelectMainInventory().getInteractiveMessage().sendMessage(player1);

            if(ArenaAPI.getInstance().getArenaStatsMap().get(player1.getUniqueId()).getEditInventory().equals(""))
                PlayerUtils.giveEditInventory(player1);
            else
                player1.getInventory().setContents(InventoryConvertor.inventoryFromBase64(ArenaAPI.getInstance().getArenaStatsMap().get(player1.getUniqueId()).getEditInventory()));
        });

        KItem chest = new KItem(new ItemCreator(SkullList.BEDROCK.getItemStack()).name("§8» §cProchainement..").lore("", "§fCette fonctionnalité arrive bientôt !", "§fRestez à l'affût :D", "").get());

        KItem cancel = new KItem(new ItemCreator(SkullList.ARROW_RIGHT.getItemStack()).name("§8» §cRetour").lore("", "§fPermet de revenir au menu", "§fde séléction.", "", "§8» §eClique-Gauche §7pour revenir.").get());
        cancel.addCallback((kInventoryRepresentation, itemStack, player1, kInventoryClickContext) -> ArenaAPI.getInstance().getSelectMainInventory().open(player1));

        kInventory.setElement(0, modify);
        kInventory.setElement(1, chest);
        kInventory.setElement(8, cancel);

        KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE).damage(10).name("§a").get());

        for (int i = 9; i < 18; i++)
            kInventory.setElement(i, glass);

        if(inventory.equals("")){
            int c = 45;
            for (ItemStack itemStack : PlayerUtils.getDefaultInventory()) {
                KItem kItem = new KItem(itemStack);
                kInventory.setElement(c, kItem);
                c++;
                if(c == 54)
                    c = 36;
            }
        } else {
            ItemStack[] itemStacks = InventoryConvertor.inventoryFromBase64(inventory);
            int c = 45;
            for (ItemStack itemStack : itemStacks) {
                if(itemStack == null || itemStack.getType() == Material.AIR){
                    c++;
                    if(c == 54)
                        c = 18;
                    continue;
                }
                KItem kItem = new KItem(itemStack);
                kInventory.setElement(c, kItem);
                c++;
                if(c == 54)
                    c = 18;
            }
        }


        kInventory.open(player);
    }

}
