package fr.ariloxe.arena;

import fr.ariloxe.arena.commands.ConfirmCommand;
import fr.ariloxe.arena.kits.ArenaKit;
import fr.ariloxe.arena.listeners.JoinListener;
import fr.ariloxe.arena.listeners.PlayersListener;
import fr.ariloxe.arena.tasks.GlobalExecutor;
import fr.ariloxe.arena.uis.SelectMainInventory;
import fr.ariloxe.arena.utils.scoreboard.ScoreboardManager;
import fr.ariloxe.arena.utils.tech.ArenaStatsCollection;
import fr.ariloxe.arena.utils.tech.PlayerStats;
import fr.ariloxe.arena.utils.worlds.BiomeSwapper;
import fr.ariloxe.arena.utils.worlds.PatchBiomes;
import fr.blendman974.kinventory.KInventoryManager;
import fr.kanao.core.PyraliaCore;
import fr.kanao.core.cosmetics.kind.CosmeticsKind;
import fr.kanao.core.utils.java.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Score;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Ariloxe
 */
public class ArenaAPI extends JavaPlugin {

    private static ArenaAPI instance;
    private GlobalExecutor globalExecutor;
    private SelectMainInventory selectMainInventory;
    private final Map<UUID, ArenaKit> arenaKitMap = new HashMap<>();
    private final Map<UUID, PlayerStats> arenaStatsMap = new HashMap<>();
    private final Random random = new Random();

    private ScheduledExecutorService executorMonoThread;
    private ScheduledExecutorService scheduledExecutorService;

    private final Map<String, String> hashMap = new HashMap<String, String>() {{
        put("OCEAN", "PLAINS");
        put("DEEP_OCEAN", "PLAINS");
        put("FROZEN_OCEAN", "PLAINS");
        put("BEACH", "PLAINS");
        put("COLD_BEACH", "PLAINS");
        put("STONE_BEACH", "PLAINS");
        put("JUNGLE", "PLAINS");
        put("JUNGLE_HILLS", "PLAINS");
        put("JUNGLE_EDGE", "PLAINS");
        put("EXTREME_HILLS_PLUS", "PLAINS");
        put("EXTREME_HILLS", "PLAINS");
        put("BIRCH_FOREST_HILLS", "PLAINS");
    }};

    @Override
    public void onLoad() {
        instance = this;
        BiomeSwapper.patchBiomes(hashMap);
        try {
            new PatchBiomes().patchBiomes();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        this.globalExecutor = new GlobalExecutor();
        this.selectMainInventory = new SelectMainInventory(this);
        List<CosmeticsKind> cosmeticsKindList = Arrays.asList(CosmeticsKind.GADGETS, CosmeticsKind.FAMILIERS, CosmeticsKind.PARTICLES, CosmeticsKind.TITLES, CosmeticsKind.SUITS);
        PyraliaCore.getInstance().getCosmeticsManager().getDisabledKinds().addAll(cosmeticsKindList);
        PyraliaCore.getInstance().getMongoManager().registerObject(ArenaStatsCollection.class);

        Bukkit.getWorld("world").setGameRuleValue("naturalRegeneration", "false");

        this.scheduledExecutorService = Executors.newScheduledThreadPool(16);
        this.executorMonoThread = Executors.newScheduledThreadPool(1);

        CommandUtils.registerCommand("kanaoarena", new ConfirmCommand());

        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayersListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ScoreboardManager(this), this);
    }

    public static ArenaAPI getInstance() {
        return instance;
    }

    public Map<UUID, ArenaKit> getArenaKitMap() {
        return arenaKitMap;
    }

    public Map<UUID, PlayerStats> getArenaStatsMap() {
        return arenaStatsMap;
    }

    public GlobalExecutor getGlobalExecutor() {
        return globalExecutor;
    }

    public Random getRandom() {
        return random;
    }

    public SelectMainInventory getSelectMainInventory() {
        return selectMainInventory;
    }

    public ScheduledExecutorService getExecutorMonoThread() {
        return this.executorMonoThread;
    }
    public ScheduledExecutorService getScheduledExecutorService() {
        return this.scheduledExecutorService;
    }
}
