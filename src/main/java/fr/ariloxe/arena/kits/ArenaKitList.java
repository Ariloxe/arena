package fr.ariloxe.arena.kits;

import fr.ariloxe.arena.kits.defensive.Absalom;
import fr.ariloxe.arena.kits.defensive.Crust;
import fr.ariloxe.arena.kits.defensive.MarkEvans;
import fr.ariloxe.arena.kits.defensive.Mordekaiser;
import fr.ariloxe.arena.kits.polyvalent.*;
import fr.ariloxe.arena.kits.offensive.*;
import fr.ariloxe.arena.kits.utilitary.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ariloxe
 */
public enum ArenaKitList {

    NIDALEE("§eNidalee", ArenaKitKind.POLY, new ItemStack(Material.BOW), Nidalee.class, "§e┃ §fPassez de la forme §eTigre§f à la", "§e┃ §fforme §eHumaine§f en utilisant votre", "§e┃ §fcompétence passive."),
    TENGEN("§aTengen", ArenaKitKind.UTIL, new ItemStack(Material.RED_ROSE), Tengen.class, "§b┃ §fClic-droit avec votre §cobjet§f pour", "§b┃ §fvous téléporter dans le §ados§f du", "§b┃ §fjoueur visé."),
    WARWICK("§aWarwick", ArenaKitKind.UTIL, new ItemStack(Material.DIAMOND_SWORD), Warwick.class, "§b┃ §fObtenez l'effet de §bRapidité§f et", "§b┃ §fla possibilité de §cStun§f un joueur", "§b┃ §fen utilisant votre compétence."),
    WEREWOLF("§cLoup-Garou", ArenaKitKind.OFF, new ItemStack(Material.LAVA_BUCKET), Werewolf.class, "§c┃ §fFaites de plus en plus de dégâts", "§c┃ §fau fur et à mesure que la vie de", "§c┃ §fvotre ennemi descends."),
    HOMELANDER("§cHomelander", ArenaKitKind.OFF, new ItemStack(Material.FEATHER), Homelander.class, "§c┃ §fObtenez le pouvoir de voler dans", "§c┃ §fles airs grâce à votre objet spécial."),
    MIRKO("§cMirko", ArenaKitKind.OFF, new ItemStack(Material.SUGAR), Mirko.class, "§c┃ §fVous allez plus vite de base.", "§c┃ §fObtenez §bRapidité II§f avec votre ultimate."),
    TSUNADE("§aTsunade", ArenaKitKind.UTIL, new ItemStack(Material.PAPER), Tsunade.class, "§b┃ §fVotre ultimate vous permet d'avoir", "§b┃ §fl'effet de §drégénération §fpendnat", "§b┃ §fquelques instants.", "§b┃ §fVous serez heal la première fois que", "§b┃ §fvous passerez sous 4§c❤"),
    SAITAMA("§cSaitama", ArenaKitKind.OFF, new ItemStack(Material.WOOD_SWORD), Saitama.class, "§c┃ §fFrappez avec vos §6poings§f et éjectez", "§c┃ §fles joueurs au loin."),
    ZORO("§cZoro", ArenaKitKind.OFF, new ItemStack(Material.BOAT), Zoro.class, "§c┃ §fFonçez sur vos adversaires et", "§c┃ §féjectez-les grâce à votre §2Dash§f."),
    DEIDARA("§aDeidara", ArenaKitKind.UTIL, new ItemStack(Material.TNT), Deidara.class, "§b┃ §fLancez des flèches explosives lors de", "§b┃ §fl'activation de votre pouvoir."),
    MORDEKAISER("§bMordekaiser", ArenaKitKind.DEF, new ItemStack(Material.DIAMOND_CHESTPLATE), Mordekaiser.class, "§d┃ §fGros stuff, insensibilité aux flèches", "§d┃ §fet heal de quelques coeurs à l'ult."),
    ABSALOM("§bAbsalom", ArenaKitKind.DEF, new ItemStack(Material.FERMENTED_SPIDER_EYE), Absalom.class, "§d┃ §fInvisibilité à l'ult ainsi que 2 coeurs", "§d┃ §fsupplémentaires."),
    CRUST("§bCrust", ArenaKitKind.DEF, new ItemStack(Material.SPRUCE_DOOR_ITEM), Crust.class, "§d┃ §fInfligez 10% de dégâts en moins et", "§d┃ §fsoyez insensible aux dégâts à l'ult."),
    EVAN("§bMark Evans", ArenaKitKind.DEF, new ItemStack(Material.STICK), MarkEvans.class, "§d┃ §fLorsque l'ult est activé vous renvoyez", "§d┃ §fles dégâts des flèches."),
    ITACHI("§eItachi", ArenaKitKind.POLY, new ItemStack(Material.IRON_CHESTPLATE), Itachi.class, "§e┃ §fMoins de 5§c❤ §f: soyez heal", "§e┃ §fet insensible aux dégâts.", "§e┃ §fPlus de 5§c❤ §f: faites tomber", "§e┃ §fdes éclairs sur le joueur le", "§e┃ §fplus proche de vous."),
    FAT_GUM("§eFat Gum", ArenaKitKind.POLY, new ItemStack(Material.COOKED_BEEF), FatGum.class, "§e┃ §fPlus vous êtes §cfrappé§f, plus", "§e┃ §fvous ferez de dégâts pendant votre", "§e┃ §fultime."),
    METTATON("§eMettaton", ArenaKitKind.POLY, new ItemStack(Material.REDSTONE), Mettaton.class, "§e┃ §fPlacez une TnT explosive ou alors", "§e┃ §fémettez un rayon rouge."),
    KYOGAI("§eKyogai", ArenaKitKind.POLY, new ItemStack(Material.STICK), Kyogai.class, "§e┃ §fRetournez de §c180 degrés§f les", "§e┃ §fjoueurs autour de vous."),
    MELIODAS("§eMeliodas", ArenaKitKind.DEF, new ItemStack(Material.GOLD_SWORD), Meliodas.class, "§e┃ §fVous pouvez renvoyer §c50%§f des", "§e┃ §fdégâts pendant 3 secondes."),
    DEMON_SHARK("§cDémon Requin", ArenaKitKind.OFF, new ItemStack(Material.WATER_BUCKET), DemonRequin.class, "§c┃ §fCreéz une §cbulle d'eau§f autour de", "§e┃ §fvous et ayez §dDepth Strider III§f."),
    FURUTO("§bFurûto", ArenaKitKind.POLY, new ItemStack(Material.GOLDEN_CARROT), Furuto.class, "§b┃ §fMélangez l'inventaire du joueur", "§b┃ §fvisé. §7(22 secondes)"),
    RUI("§eRui", ArenaKitKind.UTIL, new ItemStack(Material.WEB), Rui.class, "§e┃ §fCréez une toile d'araignée autour", "§e┃ §fde vous. §7(27 secondes)");

    private final String name;
    private final ArenaKitKind arenaKitKind;
    private final ItemStack icon;
    private final Class<?extends ArenaKit> clazz;
    private final List<String> description = new ArrayList<>();

    ArenaKitList(String name, ArenaKitKind arenaKitKind, ItemStack icon, Class<?extends ArenaKit> clazz, String... description) {
        this.name = name;
        this.clazz = clazz;
        this.arenaKitKind = arenaKitKind;
        this.icon = icon;
        this.description.addAll(Arrays.asList(description));
    }

    public String getName() {
        return name;
    }

    public Class<? extends ArenaKit> getClazz() {
        return clazz;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public ArenaKitKind getArenaKitKind() {
        return arenaKitKind;
    }

    public List<String> getDescription() {
        return description;
    }
}



