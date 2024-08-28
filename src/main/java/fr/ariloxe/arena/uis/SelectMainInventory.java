package fr.ariloxe.arena.uis;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.kits.ArenaKitKind;
import fr.ariloxe.arena.utils.PlayerUtils;
import fr.ariloxe.arena.utils.inventory.InventoryConvertor;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import fr.kanao.core.utils.ItemCreator;
import fr.kanao.core.utils.messages.InteractiveMessage;
import fr.kanao.core.utils.messages.TextComponentBuilder;
import fr.kanao.core.utils.skull.SkullList;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author Ariloxe
 */
public class SelectMainInventory {

    private KInventory kInventory;
    private final ArenaAPI arenaAPI;
    private final InteractiveMessage interactiveMessage = new InteractiveMessage().add(new TextComponentBuilder("§7§l❘ §eCliquez sur §bce §bmessage §epour §esauvegarder.").setHoverMessage("\n§d§lKANAO §8- §f§lARENA \n\n §fCliquez sur ce message afin de\n§fsauvegarder votre inventaire !\n").setClickAction(ClickEvent.Action.RUN_COMMAND, "/confirm").build());

    private final Map<ArenaKitKind, SelectKitCreator> arenaKitKindSelectKitCreatorMap = new HashMap<>();

    public SelectMainInventory(ArenaAPI instance){
        this.arenaAPI = instance;
        this.kInventory = new KInventory(KItem.DEFAULT, 54, "§f┃ §d§lKanao §8- §7Arène");

        List<Integer> integerList = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 46, 47, 48, 49, 50, 51, 52, 53);
        KItem glassPane = new KItem(new ItemCreator(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7)).name("§c").get());
        integerList.forEach(integer -> this.kInventory.setElement(integer, glassPane));

        KItem retour = new KItem(new ItemCreator(Material.ARROW).name("§8§l» §7Fermer ce menu §8§l«").lore("", "§8§l» §7Cette option vous permet de §cfermer§7 ce menu.", "", "§8§l» §7Cliquez pour fermer").get());
        retour.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> player.closeInventory());
        this.kInventory.setElement(53, retour);


        KItem polyvalent = new KItem(new ItemCreator(SkullList.YELLOW_BALL.getItemStack()).name("§8» §e§lPolyvalent").lore("", "§fDécouvrez des §6kits §fplutôt", "§eéquilibrés §fentre attaque", "§fet défense !", "", "§8» §eClique-Gauche §7pour ouvrir ce menu.").get());
        polyvalent.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if(!arenaKitKindSelectKitCreatorMap.containsKey(ArenaKitKind.POLY))
                arenaKitKindSelectKitCreatorMap.put(ArenaKitKind.POLY, new SelectKitCreator(arenaAPI, ArenaKitKind.POLY));
            arenaKitKindSelectKitCreatorMap.get(ArenaKitKind.POLY).open(player);
        });

        KItem defensif = new KItem(new ItemCreator(SkullList.BLUE_BALL.getItemStack()).name("§8» §b§lDéfensifs").lore("", "§fLes kits défensifs sont, éponymement", "§fà leur nom, axé sur la §bdéfense§f.", "", "§8» §eClique-Gauche §7pour ouvrir ce menu.").get());
        defensif.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if(!arenaKitKindSelectKitCreatorMap.containsKey(ArenaKitKind.DEF))
                arenaKitKindSelectKitCreatorMap.put(ArenaKitKind.DEF, new SelectKitCreator(arenaAPI, ArenaKitKind.DEF));
            arenaKitKindSelectKitCreatorMap.get(ArenaKitKind.DEF).open(player);
        });

        KItem offensifs = new KItem(new ItemCreator(SkullList.ORANGE_BALL.getItemStack()).name("§8» §6§lOffensifs").lore("", "§fInfligez de lourds dégâts à vos adversaires", "§favec les kits axés §6DPS§f !", "", "§8» §eClique-Gauche §7pour ouvrir ce menu.").get());
        offensifs.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if(!arenaKitKindSelectKitCreatorMap.containsKey(ArenaKitKind.OFF))
                arenaKitKindSelectKitCreatorMap.put(ArenaKitKind.OFF, new SelectKitCreator(arenaAPI, ArenaKitKind.OFF));
            arenaKitKindSelectKitCreatorMap.get(ArenaKitKind.OFF).open(player);
        });

        KItem utilitaires = new KItem(new ItemCreator(SkullList.GREEN_BALL.getItemStack()).name("§8» §a§lUtilitaires").lore("", "§fJouez de manière intelligente avec les", "§fkits axés §astratégie§f !", "", "§8» §eClique-Gauche §7pour ouvrir ce menu.").get());
        utilitaires.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if(!arenaKitKindSelectKitCreatorMap.containsKey(ArenaKitKind.UTIL))
                arenaKitKindSelectKitCreatorMap.put(ArenaKitKind.UTIL, new SelectKitCreator(arenaAPI, ArenaKitKind.UTIL));
            arenaKitKindSelectKitCreatorMap.get(ArenaKitKind.UTIL).open(player);
        });

        KItem kit = new KItem(new ItemCreator(Material.CHEST).name("§8» §c§lChanger son inventaire").lore("", "§fPermet de changer son inventaire par défaut", "§flors de rejoindre l'arène.", "", "§8» §eCliquez pour changer.").get());
        kit.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> new InventoryEditorInventory(player));

        this.kInventory.setElement(20, polyvalent);
        this.kInventory.setElement(21, defensif);
        this.kInventory.setElement(23, offensifs);
        this.kInventory.setElement(24, utilitaires);
        this.kInventory.setElement(45, kit);
    }

    public void open(Player kPlayer){
        this.kInventory.open(kPlayer);
    }

    public InteractiveMessage getInteractiveMessage() {
        return interactiveMessage;
    }
}
