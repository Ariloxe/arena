package fr.ariloxe.arena.kits;

/**
 * @author Ariloxe
 */
public enum ArenaKitKind {

    OFF("§cOffensifs"),
    DEF("§bDéfensifs"),
    POLY("§ePolyvalents"),
    UTIL("§aUtilitaire"),

    ;

    private final String name;

    ArenaKitKind(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
