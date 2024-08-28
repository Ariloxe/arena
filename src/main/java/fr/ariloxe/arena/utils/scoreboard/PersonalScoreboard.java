package fr.ariloxe.arena.utils.scoreboard;

import fr.ariloxe.arena.ArenaAPI;
import fr.ariloxe.arena.utils.scoreboard.tools.ObjectiveSign;
import fr.kanao.core.PyraliaCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

/*
 * This file is part of Hub.
 *
 * Hub is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hub is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hub.  If not, see <http://www.gnu.org/licenses/>.
 */
public class PersonalScoreboard {
    private final ArenaAPI uhc;
    private final UUID player;
    private final ObjectiveSign objectiveSign;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.0");


    public PersonalScoreboard(ArenaAPI uhc, Player player) {
        this.uhc = uhc;
        this.player = player.getUniqueId();

        objectiveSign = new ObjectiveSign("UHCHost", "UHCHost");
        objectiveSign.addReceiver(player);
    }

    public void onLogout() {
        this.objectiveSign.removeReceiver(this.uhc.getServer().getOfflinePlayer(this.player));
    }

    public ObjectiveSign getObjectiveSign() {
        return objectiveSign;
    }

    public void setLines(String ip){
        ObjectiveSign objectiveSign = this.objectiveSign;
        objectiveSign.setDisplayName("§8• §d§lKANAO §8- §d§lARENE §8•");
        objectiveSign.setLine(0, "§a");
        objectiveSign.setLine(1, "§8» §fKit : §6" + (ArenaAPI.getInstance().getArenaKitMap().containsKey(player) ? ArenaAPI.getInstance().getArenaKitMap().get(player).getName() : "§cAucun"));
        objectiveSign.setLine(2, "§b");
        objectiveSign.setLine(3, "§8» §fKills : §a" + ArenaAPI.getInstance().getArenaStatsMap().get(player).getKills());
        objectiveSign.setLine(4, "§8» §fMorts : §c" + ArenaAPI.getInstance().getArenaStatsMap().get(player).getDeaths());
        objectiveSign.setLine(5, "§8» §fK/D : §6" + decimalFormat.format(ArenaAPI.getInstance().getArenaStatsMap().get(player).getKD()));
        objectiveSign.setLine(6, "§c");
        objectiveSign.setLine(7, "§8» §fJoueur(s): §b" + (Bukkit.getOnlinePlayers().size()- PyraliaCore.getInstance().getModerationManager().getModeratorsList().size()));
        objectiveSign.setLine(8, "§d");
        objectiveSign.setLine(9, "§8§l❯ §d" + ip);
        objectiveSign.updateLines();
    }

    public UUID getUUID() {
        return player;
    }
}
