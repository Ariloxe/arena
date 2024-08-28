package fr.ariloxe.arena.utils.scoreboard;

import fr.ariloxe.arena.ArenaAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ScoreboardManager implements Listener {

    public final Map<UUID, PersonalScoreboard> scoreboards;
    public final ScheduledFuture glowingTask;
    public int ipCharIndex;
    public int cooldown;
    private final ArenaAPI uhc;

    public ScoreboardManager(ArenaAPI hub) {
        this.uhc = hub;

        this.scoreboards = new HashMap<>();
        this.ipCharIndex = 0;
        this.cooldown = 0;

        this.glowingTask = this.uhc.getScheduledExecutorService().scheduleAtFixedRate(() -> {
            String ip = this.colorIpAt();

            for (PersonalScoreboard scoreboard : this.scoreboards.values())
                this.uhc.getExecutorMonoThread().execute(()-> scoreboard.setLines("§d" + ip));
        }, 80, 80, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        if (this.scoreboards.containsKey(player.getUniqueId())) {
            ArenaAPI.getInstance().getLogger().log(Level.WARNING, "The player '" + player.getUniqueId().toString() + "' already have a scoreboard!");
            return;
        }

        this.scoreboards.put(player.getUniqueId(), new PersonalScoreboard(this.uhc, player));
        ArenaAPI.getInstance().getLogger().log(Level.INFO, "Added scoreboard to '" + player.getUniqueId() + "'.");
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();

        if (this.scoreboards.containsKey(player.getUniqueId())) {
            this.scoreboards.get(player.getUniqueId()).onLogout();
            this.scoreboards.remove(player.getUniqueId());

            ArenaAPI.getInstance().getLogger().log(Level.INFO, "Removed scoreboard to '" + player.getUniqueId() + "'.");
        }
    }


    private String colorIpAt() {
        String ip = "kanao.fr/discord";

        if (this.cooldown > 0) {
            this.cooldown--;
            return ip;
        }

        StringBuilder formattedIp = new StringBuilder();

        if (this.ipCharIndex > 0) {
            formattedIp.append(ip, 0, this.ipCharIndex - 1);
            formattedIp.append(ChatColor.GRAY).append(ip.charAt(this.ipCharIndex - 1));
        }
        else {
            formattedIp.append(ip, 0, this.ipCharIndex);
        }

        formattedIp.append(ChatColor.WHITE).append(ip.charAt(this.ipCharIndex));

        if (this.ipCharIndex + 1 < ip.length()) {
            formattedIp.append(ChatColor.GRAY).append(ip.charAt(this.ipCharIndex + 1));

            if (this.ipCharIndex + 2 < ip.length())
                formattedIp.append(ChatColor.LIGHT_PURPLE).append(ip.substring(this.ipCharIndex + 2));

            this.ipCharIndex++;
        } else {
            this.ipCharIndex = 0;
            this.cooldown = 50;
        }

        return formattedIp.toString();
    }
}


