package fr.ariloxe.arena.commands;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.utils.inventory.InventoryConvertor;
import fr.kanao.core.utils.ItemCreator;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ariloxe
 */
public class ConfirmCommand extends Command {

    private final ItemStack joinArena = new ItemCreator(Material.DIAMOND_SWORD).name("§6Rejoindre l'Arène §8§l▪ §7Clic-droit").get();
    private final ItemStack leave = new ItemCreator(Material.BED).name("§cRetour au Hub §8§l▪ §7Clic-droit").get();

    public ConfirmCommand() {
        super("confirm");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        Player player = ((Player) commandSender);
        if(player.getLocation().getY() < 200 || !player.getInventory().contains(Material.GOLDEN_APPLE)){
            player.sendMessage("§d§lKanao §8» §cVous n'êtes pas entrain d'éditer un kit.");
            return false;
        }

        player.sendMessage("");
        player.sendMessage("§7§l❘ §eVous avez §aenregistré §evos changements avec succès.");
        player.sendMessage(" §7• §eBon jeu à vous ! ❤");
        player.sendMessage("");

        ArenaAPI.getInstance().getArenaStatsMap().get(player.getUniqueId()).setEditInventory(InventoryConvertor.inventoryToBase64(player.getInventory().getContents()));

        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().setItem(4, joinArena);
        player.getInventory().setItem(7, leave);
        return false;
    }
}
